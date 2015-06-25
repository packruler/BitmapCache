# BitmapCache
Store Bitmaps in a cache similar to LruCache.
Max size is the max number of kilobytes of memory to be used by cache.
If a new Bitmap is added to cache that would make the cache size larger than set max the cache removes Bitmaps in order of oldest use

## How to use:
To retrieve in gradle:
`compile 'com.packruler:BitmapCache:1.0'`

To instanciate call `new BitmapCache(int maxSize)` 
`maxSize` is the number of kilobytes of memory to limit the cache to using.

If a new Bitmap is added to cache that would make the cache size larger than set max the cache removes Bitmaps in order of oldest last use.

Though not a subclass of Map most of the methods available in Map are available and are called accordingly on map containing values.

Calling `get(K key)` updates the placement of `key` in the queue to for removal as cache reaches size limit

If contained map should ever be needed for reference call `snapshot()`
