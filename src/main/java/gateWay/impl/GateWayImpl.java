package gateWay.impl;

import discovery.Node;
import gateWay.GateWay;
import org.zeromq.SocketType;
import org.zeromq.ZMQ;
import persianCache.PersianCacheContext;
import service.CacheMapService;
import service.impl.CacheMapServiceImpl;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GateWayImpl implements GateWay {

    private final ZMQ.Socket puller;
    private final CacheMapService cacheMapService;

    private static GateWay INSTANCE = null;

    private GateWayImpl(ZMQ.Socket puller) {
        if (Objects.nonNull(INSTANCE))
            throw new IllegalStateException();
        this.puller = puller;
        this.cacheMapService = new CacheMapServiceImpl();
    }

    public static GateWay getGateWay(ZMQ.Socket puller) {
        if (Objects.isNull(INSTANCE))
            INSTANCE = new GateWayImpl(puller);
        return INSTANCE;
    }

    @SuppressWarnings("InfiniteLoopStatement")
    @Override
    public void startInteract() {
        ExecutorService service = Executors.newSingleThreadExecutor();
        service.submit(() -> {
            while (true) {
                String recv = new String(puller.recv(0), ZMQ.CHARSET).trim();
                if (recv.equals("pt")) {
                    String name = new String(puller.recv(0), ZMQ.CHARSET).trim();;
                    String key = new String(puller.recv(0), ZMQ.CHARSET).trim();;
                    String value = new String(puller.recv(0), ZMQ.CHARSET).trim();;
                    cacheMapService.putToLocalCacheMap(name, key, value);
                } else if (recv.equals("rm")) {
                    String name = new String(puller.recv(0), ZMQ.CHARSET).trim();;
                    String key = new String(puller.recv(0), ZMQ.CHARSET).trim();;
                    cacheMapService.removeFromLocalCacheMap(name, key);
                }
            }
        });
    }

    @Override
    public void sendPutMessage(String key, String value, String name, Node node) {
        ZMQ.Socket pusher = PersianCacheContext.getzContext().createSocket(SocketType.PUSH);
        pusher.connect("tcp://" + node.getIp() + ":" + node.getPort());
        pusher.send("pt", ZMQ.SNDMORE);
        pusher.send(name, ZMQ.SNDMORE);
        pusher.send(key, ZMQ.SNDMORE);
        pusher.send(value, 0);
    }

    @Override
    public void sendRemoveMessage(Object o, String name, Node node) {
        ZMQ.Socket pusher = PersianCacheContext.getzContext().createSocket(SocketType.PUSH);
        pusher.connect("tcp://" + node.getIp() + ":" + node.getPort());
        pusher.send("rm", ZMQ.SNDMORE);
        pusher.send(name, ZMQ.SNDMORE);
        pusher.send(o.toString(), 0);
    }

    @Override
    public void pushUpdate(Node node) {
        cacheMapService.getCaches().forEach((name, cacheMap) -> cacheMap.forEach((k, v) -> {
            PersianCacheContext.getGeev().allNodes().forEach(n -> sendPutMessage(k, v, name, n));
        }));
    }

}
