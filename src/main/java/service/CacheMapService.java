package service;

import persianCache.CacheMap;

import java.util.Map;

public interface CacheMapService {

    void removeFromLocalCacheMap();

    void putToLocalCacheMap();

    CacheMap getCacheMap(String name);

    void appendToLocalCache(Map<String, CacheMap> cacheFromNetwork);
}
