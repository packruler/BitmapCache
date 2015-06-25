package com.packruler.bitmapcache;

import android.graphics.Bitmap;
import android.util.Log;

import java.text.NumberFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by Packruler on 6/23/15.
 */
public class BitmapCache<K extends Comparable, V extends Bitmap> {
    private final String TAG = getClass().getSimpleName();

    private int max;
    private HashMap<K, V> map = new HashMap<>();
    private int size;
    //    private ArrayBlockingQueue<K> queueForRemoval = new ArrayBlockingQueue<K>(1);
    private UsageQueue<K> queueForRemoval = new UsageQueue<>();


    /**
     * Store Bitmaps in a cache with max size with automatic removal based on last access order
     *
     * @param maxSize
     *         Maximum size in kB
     */
    public BitmapCache(int maxSize) {
        max = maxSize;
    }

    /**
     * Remove all Bitmaps from cache map and recycles all of them
     */
    public void clear() {
        clear(true);
    }

    /**
     * Option to clear cache map and not recycle contained values
     *
     * @param recycle
     *         recycle contained values if true
     */
    public void clear(boolean recycle) {
        if (recycle)
            for (V val : values()) {
                val.recycle();
            }
        map.clear();
        queueForRemoval.clear();
        size = 0;
    }

    /**
     * Similar to  {@link Map#containsKey(Object)}
     */
    public boolean containsKey(Object key) {
        return map.containsValue(key);
    }

    /**
     * Similar to  {@link Map#containsValue(Object)}
     */
    public boolean containsValue(Object value) {
        return map.containsValue(value);
    }

    /**
     * Similar to  {@link Map#entrySet()}
     */
    public Set<Map.Entry<K, V>> entrySet() {
        return map.entrySet();
    }

    /**
     * Retrieves value at key and updates usage tracking.
     *
     * @param key
     *         the key
     *
     * @return the value of the mapping with the specified key, or {@code null} if no mapping for
     * the specified key is found.
     */
    public V get(K key) {
        queueForRemoval.use(key);

        return map.get(key);
    }

    /**
     * Similar to  {@link Map#isEmpty()}
     */
    public boolean isEmpty() {
        return map.isEmpty();
    }

    /**
     * Similar to  {@link Map#keySet()}
     */
    public Set<K> keySet() {
        return map.keySet();
    }

    /**
     * Maps the specified key to the specified value.
     * If the specified key is already occupied that old value is recycled.
     *
     * @param key
     *         the key
     * @param value
     *         the value
     *
     * @return value
     *
     * @throws IllegalArgumentException
     *         if value is too larger than cache size
     */
    public V put(K key, V value) {
        for (Map.Entry<K, V> entry : map.entrySet()) {
            Log.v(TAG, entry.getKey().toString() + "| Size: " + (entry.getValue().getByteCount() / 1024));
        }

        int inSize = sizeOf(value);
        if (inSize > maxSize())
            throw new IllegalArgumentException("Size too large for cache");

        V current = map.remove(key);
        if (current != null) {
            size -= sizeOf(current);
            current.recycle();
            System.gc();
        }
        size += inSize;
        trimToMax();

        queueForRemoval.remove(key);
        queueForRemoval.add(key);

        return map.put(key, value);
    }

    public void putAll(Map<? extends K, ? extends V> map) {
        for (Map.Entry<? extends K, ? extends V> entry : map.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    public V remove(K key) {
        V value = map.remove(key);
        if (value != null) {
            size -= sizeOf(value);
            Log.v(TAG, "Remove " + key + "| size: " + sizeOf(value) + " | Current size: " + size);
        }

        queueForRemoval.remove(key);

        return value;
    }

    public int size() {
        return size;
    }

    public Collection<V> values() {
        return map.values();
    }

    private int sizeOf(V value) {
        return value.getByteCount() / 1024;
    }

    public int maxSize() {
        return max;
    }

    /**
     * Change max size of cache
     */
    public void setMaxSize(int newMax) {
        max = newMax;
        trimToMax();
    }

    private boolean canAdd(V value) {
        return (size + sizeOf(value)) < maxSize();
    }

    private void trim() {
        Log.v(TAG + ".TRIM", "Current size: " +
                NumberFormat.getInstance().format(size) + "/" +
                NumberFormat.getInstance().format(max) + "KB");
        if (map.containsKey(queueForRemoval.peek())) {
            Log.v(TAG + ".TRIM", queueForRemoval.peek().toString() + " | Size: " + sizeOf(map.get(queueForRemoval.peek())));
            remove(queueForRemoval.poll());
        } else {
            Log.e(TAG, "Key: \'" + queueForRemoval.poll() + "\' is missing");
            if (!queueForRemoval.isEmpty())
                trim();
        }
    }

    private void trimToMax() {
        Log.v(TAG, "Current size: " + size + "| max: " + max + "| Trim required: " + (size < max));
        if (size > max)
            while (size > max)
                trim();
    }

    /**
     * Retrieve current underlying map of cached items
     *
     * @return map of cached {@linkplain V}
     */
    public Map<K, V> snapshot() {
        return map;
    }

//    public class Key implements Comparable<Key> {
//        final K key;
//        long lastUsed;
//        Key previous;
//        Key next;
//
//        public Key(K key) {
//            this.key = key;
//            lastUsed = Calendar.getInstance().getTimeInMillis();
//        }
//
//        public K peek() {
//            return key;
//        }
//
//        public K poll() {
//            lastUsed = Calendar.getInstance().getTimeInMillis();
//            moveDown();
//            return key;
//        }
//
//        private void moveDown() {
//            while (next != null && next.lastUsed > lastUsed) {
//                if (previous != null) {
//                    next.previous = previous;
//                    previous.next = next;
//                }
//                previous = next;
//                next = next.next;
//            }
//        }
//
//        @Override
//        public boolean equals(Object o) {
//            return key.equals(((Key) o).key);
//        }
//
//        @Override
//        public int compareTo(Key another) {
//            return (int) (lastUsed - another.lastUsed);
//        }
//    }
}
