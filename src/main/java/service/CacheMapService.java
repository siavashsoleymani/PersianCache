package service;

import persianCache.CacheMap;

import java.util.Map;

public interface CacheMapService {

    void removeFromLocalCacheMap(String name, String key);

    void putToLocalCacheMap(String s, String name, String key);

    CacheMap getCacheMap(String name);

    void appendToLocalCache(Map<String, CacheMap> cacheFromNetwork);
}
