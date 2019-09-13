package persianCache;

import gateWay.GateWay;
import gateWay.impl.GateWayImpl;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class CacheMap extends ConcurrentHashMap<String, String> {
    private final GateWay gateWay;
    private final String name;

    public CacheMap(String name) {
        super();
        this.gateWay = GateWayImpl.getGateWay(PersianCacheContext.getSubscriber(),
                PersianCacheContext.getRequester(),
                PersianCacheContext.getPublisher());
        this.name = name;
    }


    @Override
    public String remove(Object key) {
        if (Objects.nonNull(this.get(key)))
            gateWay.sendRemoveMessage(key, name);
        return super.remove(key);
    }

    @Override
    public String put(String key, String value) {
        if (Objects.isNull(this.get(key)) ||
                (Objects.nonNull(this.get(key)) && !this.get(key).equals(value)))
            gateWay.sendPutMessage(key, value, name);
        return super.put(key, value);
    }

    public String networkPut(String key, String value) {
        return super.put(key, value);
    }

    public String networkRemove(Object key) {
        return super.remove(key);
    }
}
