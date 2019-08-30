import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.util.Objects;

public class PersianCacheContext {
    private static ZMQ.Socket publisher;
    private static ZMQ.Socket subscriber;
    private static ZContext zContext;
    private static PersianCacheContext INSTANCE = null;
    private static Interactor interactor = null;

    private PersianCacheContext() {
        zContext = new ZContext();
        publisher = zContext.createSocket(SocketType.PUB);
        subscriber = zContext.createSocket(SocketType.SUB);
        publisher.connect("tcp://localhost:8080");
        subscriber.connect("tcp://localhost:8081");
        subscriber.subscribe("".getBytes(ZMQ.CHARSET));
        interactor = new InteractorImpl(publisher, subscriber);
        startInteracting();
    }

    public static CacheMap getCacheMap(String name) {
        if (Objects.isNull(INSTANCE))
            INSTANCE = new PersianCacheContext();
        CacheMap cacheMap = interactor.getCacheMap(name);
        return cacheMap;
    }

    private void startInteracting() {
        interactor.startInteract();
    }
}
