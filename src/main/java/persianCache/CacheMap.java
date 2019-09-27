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
        this.gateWay = GateWayImpl.getGateWay(PersianCacheContext.getPuller());
        this.name = name;
    }


    @Override
    public String remove(Object key) {
        if (Objects.nonNull(this.get(key))) {
            PersianCacheContext.getGeev().allNodes().forEach(n -> gateWay.sendRemoveMessage(key, name, n));
        }
        return super.remove(key);
    }

    @Override
    public String put(String key, String value) {
        if (Objects.isNull(this.get(key)) ||
                (Objects.nonNull(this.get(key)) && !this.get(key).equals(value))) {
            PersianCacheContext.getGeev().allNodes().forEach(n -> gateWay.sendPutMessage(key, value, name, n));
        }
        return super.put(key, value);
    }

    @SuppressWarnings("UnusedReturnValue")
    public String networkPut(String key, String value) {
        return super.put(key, value);
    }

    @SuppressWarnings("UnusedReturnValue")
    public String networkRemove(Object key) {
        return super.remove(key);
    }
}
