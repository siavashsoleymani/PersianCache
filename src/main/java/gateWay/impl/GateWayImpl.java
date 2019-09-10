package gateWay.impl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import gateWay.GateWay;
import org.zeromq.ZMQ;
import persianCache.CacheMap;
import service.CacheMapService;
import service.impl.CacheMapServiceImpl;

import java.util.*;
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
        this.cacheMapService = new CacheMapServiceImpl(this.publisher, this.subscriber);
    }

    @Override
    public void startInteract() {
        ExecutorService service = Executors.newSingleThreadExecutor();
        service.submit(() -> {
            while (true) {
                String recv = subscriber.recvStr(0);
                if (Objects.nonNull(recv)) {
                    if (recv.equals("pt")) {
                        cacheMapService.putToLocalCacheMap();
                    } else if (recv.equals("rm")) {
                        cacheMapService.removeFromLocalCacheMap();
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
}