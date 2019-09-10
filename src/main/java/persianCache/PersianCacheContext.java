package persianCache;

import gateWay.GateWay;
import gateWay.impl.GateWayImpl;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import service.CacheMapService;
import service.impl.CacheMapServiceImpl;

import java.util.Objects;

public class PersianCacheContext {
    private static ZMQ.Socket publisher;
    private static ZMQ.Socket subscriber;
    private static ZMQ.Socket requester;
    private static ZContext zContext;
    private static PersianCacheContext INSTANCE = null;
    private static GateWay gateWay = null;
    private static CacheMapService cacheMapService;

    private PersianCacheContext() {
        if (Objects.nonNull(INSTANCE))
            throw new IllegalStateException();
        zContext = new ZContext();
        publisher = zContext.createSocket(SocketType.PUB);
        subscriber = zContext.createSocket(SocketType.SUB);
        requester = zContext.createSocket(SocketType.REQ);
        publisher.connect("tcp://localhost:8080");
        subscriber.connect("tcp://localhost:8081");
        requester.connect("tcp://localhost:8082");
        subscriber.subscribe("".getBytes(ZMQ.CHARSET));
        gateWay = new GateWayImpl(subscriber, requester, publisher);
        cacheMapService = new CacheMapServiceImpl(subscriber);
        fillCacheMapForFirstTime();
        startInteracting();
    }

    public static CacheMap getCacheMap(String name) {
        initialize();
        CacheMap cacheMap = cacheMapService.getCacheMap(name);
        return cacheMap;
    }

    public static void initialize() {
        if (Objects.isNull(INSTANCE))
            INSTANCE = new PersianCacheContext();
    }

    private void fillCacheMapForFirstTime() {
        gateWay.fillCacheMapForFirstTime();
    }

    private void startInteracting() {
        gateWay.startInteract();
    }

    public static ZMQ.Socket getPublisher() {
        if (Objects.isNull(INSTANCE))
            throw new IllegalStateException("First initialize PersianContext");
        return publisher;
    }

    public static ZMQ.Socket getSubscriber() {
        if (Objects.isNull(INSTANCE))
            throw new IllegalStateException("First initialize PersianContext");
        return subscriber;
    }

    public static ZMQ.Socket getRequester() {
        if (Objects.isNull(INSTANCE))
            throw new IllegalStateException("First initialize PersianContext");
        return requester;
    }
}
