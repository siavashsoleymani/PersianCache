package service;

import persianCache.CacheMap;

import java.util.Map;

public interface CacheMapService {
    void sendPutMessage(String key, String value, String name);

    void sendRemoveMessage(Object o, String name);

    void removeFromLocalCacheMap();

    void putToLocalCacheMap();

    CacheMap getCacheMap(String name);

    void appendToLocalCache(Map<String, CacheMap> cacheFromNetwork);
}
