package com.packruler.bitmapcache;

import android.util.Log;

import java.util.Calendar;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.PriorityQueue;

/**
 * Created by Packruler on 6/24/15.
 */
public class UsageQueue<K> {
    private PriorityQueue<Key<K>> queue;

    public UsageQueue() {
        queue = new PriorityQueue<>();
    }

    public UsageQueue(int initialSize) {
        queue = new PriorityQueue<>(initialSize);
    }

    private static class Key<K> implements Comparable<Key> {
        final K key;
        long lastUsed;

        public Key(K key) {
            this.key = key;
            lastUsed = Calendar.getInstance().getTimeInMillis();
        }

        public K use() {
            lastUsed = Calendar.getInstance().getTimeInMillis();
            return key;
        }

        @Override
        public boolean equals(Object o) {
//            Log.v(getClass().getSimpleName(), "Key: " + key + " | Equals: " + o.toString());
            if (o instanceof Key)
                return key.equals(((Key) o).key);

            return o.getClass().equals(key.getClass()) && o.equals(key);

        }

        @Override
        public int compareTo(Key another) {
            return (int) (lastUsed - another.lastUsed);
        }

        @Override
        public String toString() {
            return "Key: " + key + " | lastUsed: " + lastUsed;
        }
    }

    /**
     * Returns an instance of {@link Iterator} that may be used to access the
     * objects contained by this {@code Collection}. The order in which the elements are
     * returned by the {@link Iterator} is not defined unless the instance of the
     * {@code Collection} has a defined order.  In that case, the elements are returned in that
     * order.
     *
     * In this class this method is declared abstract and has to be implemented
     * by concrete {@code Collection} implementations.
     *
     * @return an iterator for accessing the {@code Collection} contents.
     */
    public Iterator<K> iterator() {
        return new Iterator<K>() {
            final Iterator<Key<K>> keyIterator = queue.iterator();

            /**
             * Returns true if there is at least one more element, false otherwise.
             *
             * @see #next
             */
            @Override

            public boolean hasNext() {
                return keyIterator.hasNext();
            }

            /**
             * Returns the next object and advances the keyIterator.
             *
             * @return the next object.
             *
             * @throws NoSuchElementException
             *         if there are no more elements.
             * @see #hasNext
             */
            @Override
            public K next() {
                return keyIterator.next().key;
            }

            /**
             * Removes the last object returned by {@code next} from the collection.
             * This method can only be called once between each call to {@code next}.
             *
             * @throws UnsupportedOperationException
             *         if removing is not supported by the collection being
             *         iterated.
             * @throws IllegalStateException
             *         if {@code next} has not been called, or {@code remove} has
             *         already been called after the last call to {@code next}.
             */
            @Override
            public void remove() {
                keyIterator.remove();
            }
        };
    }

    /**
     * Returns a count of how many objects this {@code Collection} contains.
     *
     * In this class this method is declared abstract and has to be implemented
     * by concrete {@code Collection} implementations.
     *
     * @return how many objects this {@code Collection} contains, or {@code Integer.MAX_VALUE}
     * if there are more than {@code Integer.MAX_VALUE} elements in this
     * {@code Collection}.
     */
    public int size() {
        return queue.size();
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }

    /**
     * Retrieves and removes the head of this queue,
     * or returns <tt>null</tt> if this queue is empty.
     *
     * @return the head of this queue, or <tt>null</tt> if this queue is empty
     */
    public K poll() {
        return queue.poll().key;
    }

    /**
     * Retrieves, but does not remove, the head of this queue,
     * or returns <tt>null</tt> if this queue is empty.
     *
     * @return the head of this queue, or <tt>null</tt> if this queue is empty
     */
    public K peek() {
        return queue.peek().key;
    }

    public boolean remove(K k) {
        Key<K> key=new Key<>(k);
        return queue.remove(key);
    }

    public boolean add(K k) {
        Key<K> key = new Key<>(k);
        if (!queue.contains(key))
            return queue.add(key);
        return false;
    }

    public K use(K key) {
        PriorityQueue<Key<K>> temp = new PriorityQueue<>(queue);

        Log.v(getClass().getSimpleName(), "Queue contains: ");
        while (!temp.isEmpty()) {
            Log.v(getClass().getSimpleName(), temp.poll().toString());
        }

        remove(key);
        add(key);
        return key;
    }

    public void clear() {
        queue.clear();
    }
}
