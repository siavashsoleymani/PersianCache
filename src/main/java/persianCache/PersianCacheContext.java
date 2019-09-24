package persianCache;

import discovery.Geev;
import discovery.GeevConfig;
import discovery.Node;
import gateWay.GateWay;
import gateWay.impl.GateWayImpl;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import service.CacheMapService;
import service.impl.CacheMapServiceImpl;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Objects;

public class PersianCacheContext {
    private static ZMQ.Socket publisher;
    private static ZMQ.Socket subscriber;
    private static ZMQ.Socket requester;
    private static PersianCacheContext INSTANCE = null;
    private static GateWay gateWay = null;
    private static CacheMapService cacheMapService;
    private static Geev geev;

    private PersianCacheContext() throws IOException {
        if (Objects.nonNull(INSTANCE))
            throw new IllegalStateException();
        ZContext zContext = new ZContext();
        publisher = zContext.createSocket(SocketType.PUB);
        subscriber = zContext.createSocket(SocketType.SUB);
        requester = zContext.createSocket(SocketType.REQ);
        publisher.connect("tcp://localhost:8080");
        subscriber.connect("tcp://localhost:8081");
        requester.connect("tcp://localhost:8082");
        subscriber.subscribe("".getBytes(ZMQ.CHARSET));
        gateWay = GateWayImpl.getGateWay(subscriber, requester, publisher);
        cacheMapService = new CacheMapServiceImpl();
        geev = new Geev(new GeevConfig.Builder()
                .useBroadcast()
                .onJoin(node -> gateWay.pushUpdate(node))
                .onLeave(node -> System.out.println("node " + node.toString() + " was leave!"))
                .discoveryPort(8083)
                .setMySelf(new Node(getHostAddress(), 8081))
                .build());
        fillCacheMapForFirstTime();
        startInteracting();
    }

    private String getHostAddress() throws SocketException {
        Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
        NetworkInterface nInt = null;
        while (networkInterfaces.hasMoreElements()) {
            nInt = networkInterfaces.nextElement();
            if (!nInt.isLoopback())
                break;
        }
        InetAddress defaultInetAddress = null;
        assert nInt != null;
        Enumeration<InetAddress> addresses = nInt.getInetAddresses();
        while (addresses.hasMoreElements()) {
            defaultInetAddress = addresses.nextElement();
            if (defaultInetAddress instanceof Inet4Address)
                break;
        }
        assert defaultInetAddress != null;
        return defaultInetAddress.getHostAddress();
    }

    public static CacheMap getCacheMap(String name) throws IOException {
        initialize();
        return cacheMapService.getCacheMap(name);
    }

    @SuppressWarnings("WeakerAccess")
    public static void initialize() throws IOException {
        if (Objects.isNull(INSTANCE))
            INSTANCE = new PersianCacheContext();
    }

    private void fillCacheMapForFirstTime() {
        gateWay.fillCacheMapForFirstTime();
    }

    private void startInteracting() {
        gateWay.startInteract();
    }

    static ZMQ.Socket getPublisher() {
        if (Objects.isNull(INSTANCE))
            throw new IllegalStateException("First initialize PersianContext");
        return publisher;
    }

    static ZMQ.Socket getSubscriber() {
        if (Objects.isNull(INSTANCE))
            throw new IllegalStateException("First initialize PersianContext");
        return subscriber;
    }

    static ZMQ.Socket getRequester() {
        if (Objects.isNull(INSTANCE))
            throw new IllegalStateException("First initialize PersianContext");
        return requester;
    }

    public static Geev getGeev() {
        return geev;
    }
}
