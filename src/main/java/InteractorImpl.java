import org.zeromq.ZMQ;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class InteractorImpl implements Interactor {

    private final ZMQ.Socket publisher;
    private final ZMQ.Socket subscriber;
    private final ZMQ.Socket requester;

    private static Map<String, CacheMap> caches = new HashMap<>();

    public InteractorImpl(ZMQ.Socket publisher, ZMQ.Socket subscriber, ZMQ.Socket requester) {
        this.publisher = publisher;
        this.subscriber = subscriber;
        this.requester = requester;
    }

    public void put(String i, String r, String name) {
        publisher.send("pt", ZMQ.SNDMORE);
        publisher.send(name, ZMQ.SNDMORE);
        publisher.send(i, ZMQ.SNDMORE);
        publisher.send(r, 0);
    }

    public void remove(Object o, String name) {
        publisher.send("rm", ZMQ.SNDMORE);
        publisher.send(name, ZMQ.SNDMORE);
        publisher.send(o.toString(), 0);
    }

    @Override
    public void startInteract() {
        ExecutorService service = Executors.newSingleThreadExecutor();
        service.submit(() -> {
            while (true) {
                String recv = subscriber.recvStr(0);
                if (Objects.nonNull(recv)) {
                    if (recv.equals("pt")) {
                        putToCacheMap();
                    } else if (recv.equals("rm")) {
                        removeFromCacheMap();
                    }
                }
            }
        });
    }

    private void removeFromCacheMap() {
        String name = subscriber.recvStr(0);
        String key = subscriber.recvStr(0);
        CacheMap cacheMap = caches.get(name);
        if (Objects.nonNull(cacheMap)) {
            cacheMap.networkRemove(key);
        }
    }

    private void putToCacheMap() {
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
        cacheMap = new CacheMap(this, name);
        caches.put(name, cacheMap);
        return cacheMap;
    }

    @Override
    public void fillCacheMapForFirstTime() {
        ExecutorService service = Executors.newSingleThreadExecutor();
        service.submit(() -> {
            requester.send("hello".getBytes(), 0);
            while (requester.hasReceiveMore()) {
                String name = requester.recvStr(0);
                String key = requester.recvStr(0);
                String value = requester.recvStr(0);
                CacheMap cacheMap = caches.get(name);
                if (Objects.isNull(cacheMap))
                    cacheMap = new CacheMap(this, name);
                cacheMap.put(key, value);
            }
        });
    }
}
