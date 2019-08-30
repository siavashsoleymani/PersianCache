import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PersianCacheContext {
    private static Map<String, CacheMap> caches = new HashMap<>();
    private static ZMQ.Socket publisher;
    private static ZMQ.Socket subscriber;
    private static ZContext zContext;
    private static PersianCacheContext INSTANCE = null;

    private PersianCacheContext() {
        zContext = new ZContext();
        publisher = zContext.createSocket(SocketType.PUB);
        subscriber = zContext.createSocket(SocketType.SUB);
        publisher.connect("tcp://localhost:8080");
        subscriber.connect("tcp://localhost:8081");
        subscriber.subscribe("".getBytes(ZMQ.CHARSET));
        startThreadJob();
    }

    private void startThreadJob() {
        ExecutorService service = Executors.newSingleThreadExecutor();
        service.submit(() -> {
            while (true) {
                String recv = subscriber.recvStr(0);
                if (Objects.nonNull(recv)) {
                    if (recv.equals("pt")) {
                        String name = subscriber.recvStr(0);
                        String key = subscriber.recvStr(0);
                        String value = subscriber.recvStr(0);
                        CacheMap cacheMap = caches.get(name);
                        if (Objects.nonNull(cacheMap)) {
                            cacheMap.put(key, value);
                        }
                    } else if (recv.equals("rm")) {
                        String name = subscriber.recvStr(0);
                        String key = subscriber.recvStr(0);
                        CacheMap cacheMap = caches.get(name);
                        if (Objects.nonNull(cacheMap)) {
                            cacheMap.remove(key);
                        }
                    }
                }
            }
        });
    }

    public static CacheMap getCacheMap(String name) {
        if (Objects.isNull(INSTANCE))
            INSTANCE = new PersianCacheContext();
        CacheMap cacheMap = new CacheMap(INSTANCE, name);
        caches.put(name, cacheMap);
        return cacheMap;
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
}
