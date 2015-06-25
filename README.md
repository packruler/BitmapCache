# BitmapCache
Store Bitmaps in a cache similar to LruCache.
Max size is the max number of kilobytes of memory to be used by cache.
If a new Bitmap is added to cache that would make the cache size larger than set max the cache removes Bitmaps in order of oldest use

## How to use:
To retrieve in gradle:
`compile 'com.packruler:BitmapCache:1.0'`

To instanciate call `new BitmapCache(int maxSize)` 
`maxSize` is the number of kilobytes of memory to limit the cache to using.

If a new Bitmap is added to cache that would make the cache size larger than set max the cache removes Bitmaps in order of oldest last use

The usage is based off a Map for example:
`get(K key)` returns the value at that key location if it is in the map

Calling `get(K key)` updates the placement of `key` in the queue to for removal as cache reaches size limit
