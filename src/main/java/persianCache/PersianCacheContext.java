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
    private static ZMQ.Socket puller;
    private static PersianCacheContext INSTANCE = null;
    private static GateWay gateWay = null;
    private static CacheMapService cacheMapService;
    private static Geev geev;
    private static ZContext zContext;

    private PersianCacheContext(Integer port, Integer discoveryPort) throws IOException {
        if (Objects.nonNull(INSTANCE))
            throw new IllegalStateException();
        zContext = new ZContext();
        puller = zContext.createSocket(SocketType.PULL);
        puller.bind("tcp://*:" + port);
        cacheMapService = new CacheMapServiceImpl();
        gateWay = GateWayImpl.getGateWay(puller);
        geev = new Geev(new GeevConfig.Builder()
                .useBroadcast()
                .onJoin(node -> {
                    System.out.println("some body come to network");
                    gateWay.pushUpdate(node);
                })
                .onLeave(node -> System.out.println("node " + node.toString() + " was leave!"))
                .discoveryPort(discoveryPort)
                .setMySelf(new Node(getHostAddress(), port))
                .build());
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
        if (Objects.isNull(INSTANCE))
            throw new IllegalStateException("First initialize PersianContext");
        return cacheMapService.getCacheMap(name);
    }

    @SuppressWarnings("WeakerAccess")
    public static void initialize(Integer port, Integer discoveryPort) throws IOException {
        if (Objects.isNull(INSTANCE))
            INSTANCE = new PersianCacheContext(port, discoveryPort);
    }

    private void startInteracting() {
        gateWay.startInteract();
    }

    static ZMQ.Socket getPuller() {
        if (Objects.isNull(INSTANCE))
            throw new IllegalStateException("First initialize PersianContext");
        return puller;
    }

    public static ZContext getzContext() {
        return zContext;
    }

    public static Geev getGeev() {
        return geev;
    }
}
