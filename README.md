# BitmapCache - currently under construction
Store Bitmaps in a cache similar to LruCache.
Max size is the max number of kilobytes of memory to be used by cache.
If a new Bitmap is added to cache that would make the cache size larger than set max the cache removes Bitmaps in order of oldest use

## How to use:
To retrieve in gradle:
`compile 'com.packruler:BitmapCache:0.9.0'`
