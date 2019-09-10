package persianCache;

import gateWay.GateWay;
import service.CacheMapService;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class CacheMap extends ConcurrentHashMap<String, String> {
    private final CacheMapService cacheMapService;
    private final String name;

    public CacheMap(CacheMapService cacheMapService, String name) {
        super();
        this.cacheMapService = cacheMapService;
        this.name = name;
    }

    @Override
    public String remove(Object key) {
        if (Objects.nonNull(this.get(key)))
            cacheMapService.sendRemoveMessage(key, name);
        return super.remove(key);
    }

    @Override
    public String put(String key, String value) {
        if (Objects.isNull(this.get(key)) ||
                (Objects.nonNull(this.get(key)) && !this.get(key).equals(value)))
            cacheMapService.sendPutMessage(key, value, name);
        return super.put(key, value);
    }

    public String networkPut(String key, String value) {
        return super.put(key, value);
    }

    public String networkRemove(Object key) {
        return super.remove(key);
    }
}
