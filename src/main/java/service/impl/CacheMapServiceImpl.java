package service.impl;

import org.zeromq.ZMQ;
import persianCache.CacheMap;
import service.CacheMapService;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class CacheMapServiceImpl implements CacheMapService {

    private final ZMQ.Socket publisher;
    private final ZMQ.Socket subscriber;

    private static Map<String, CacheMap> caches = new ConcurrentHashMap<>();

    public CacheMapServiceImpl(ZMQ.Socket publisher, ZMQ.Socket subscriber) {
        this.publisher = publisher;
        this.subscriber = subscriber;
    }

    @Override
    public void removeFromLocalCacheMap() {
        String name = subscriber.recvStr(0);
        String key = subscriber.recvStr(0);
        CacheMap cacheMap = caches.get(name);
        if (Objects.nonNull(cacheMap)) {
            cacheMap.networkRemove(key);
        }
    }

    @Override
    public void putToLocalCacheMap() {
        String name = subscriber.recvStr(0);
        String key = subscriber.recvStr(0);
        String value = subscriber.recvStr(0);
        CacheMap cacheMap = caches.get(name);
        if (Objects.nonNull(cacheMap)) {
            cacheMap.networkPut(key, value);
        }
    }

    @Override
    public CacheMap getCacheMap(String name) {
        CacheMap cacheMap = caches.get(name);
        if (Objects.nonNull(cacheMap))
            return cacheMap;
        cacheMap = new CacheMap(name);
        caches.put(name, cacheMap);
        return cacheMap;
    }

    @Override
    public void appendToLocalCache(Map<String, CacheMap> cacheFromNetwork) {
        for (Map.Entry<String, CacheMap> bEntry : cacheFromNetwork.entrySet()) {
            CacheMap aValue = caches.get(bEntry.getKey());
            if (aValue != null) {
                aValue.putAll(bEntry.getValue());
            } else {
                caches.put(bEntry.getKey(), bEntry.getValue());
            }
        }
    }
}
