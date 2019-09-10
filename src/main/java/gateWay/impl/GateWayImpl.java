package gateWay.impl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import gateWay.GateWay;
import org.zeromq.ZMQ;
import persianCache.CacheMap;
import service.CacheMapService;
import service.impl.CacheMapServiceImpl;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GateWayImpl implements GateWay {

    private final ZMQ.Socket subscriber;
    private final ZMQ.Socket requester;
    private final ZMQ.Socket publisher;
    private final CacheMapService cacheMapService;

    private static Gson gson = new Gson();

    public GateWayImpl(ZMQ.Socket subscriber, ZMQ.Socket requester, ZMQ.Socket publisher) {
        this.subscriber = subscriber;
        this.requester = requester;
        this.publisher = publisher;
        this.cacheMapService = new CacheMapServiceImpl();
    }

    @Override
    public void startInteract() {
        ExecutorService service = Executors.newSingleThreadExecutor();
        service.submit(() -> {
            while (true) {
                String recv = subscriber.recvStr(0);
                if (Objects.nonNull(recv)) {
                    if (recv.equals("pt")) {
                        String name = subscriber.recvStr(0);
                        String key = subscriber.recvStr(0);
                        String value = subscriber.recvStr(0);
                        cacheMapService.putToLocalCacheMap(name, key, value);
                    } else if (recv.equals("rm")) {
                        String name = subscriber.recvStr(0);
                        String key = subscriber.recvStr(0);
                        cacheMapService.removeFromLocalCacheMap(name, key);
                    }
                }
            }
        });
    }

    @Override
    public void fillCacheMapForFirstTime() {
        ExecutorService service = Executors.newSingleThreadExecutor();
        service.submit(() -> {
            while (true) {
                requester.send("hello".getBytes(), 0);
                String message = requester.recvStr(0);
                Map<String, CacheMap> cacheFromNetwork =
                        gson.fromJson(message, new TypeToken<Map<String, CacheMap>>() {
                        }.getType());
                cacheMapService.appendToLocalCache(cacheFromNetwork);
                break;
            }
        });
    }

    @Override
    public void sendPutMessage(String key, String value, String name) {
        publisher.send("pt", ZMQ.SNDMORE);
        publisher.send(name, ZMQ.SNDMORE);
        publisher.send(key, ZMQ.SNDMORE);
        publisher.send(value, 0);
    }

    @Override
    public void sendRemoveMessage(Object o, String name) {
        publisher.send("rm", ZMQ.SNDMORE);
        publisher.send(name, ZMQ.SNDMORE);
        publisher.send(o.toString(), 0);
    }

}
