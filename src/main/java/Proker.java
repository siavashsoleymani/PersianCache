import com.google.gson.Gson;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static org.zeromq.ZMQ.SNDMORE;

public class Proker {

    private static String lastJob = "";
    private static String lastName = "";
    private static String lastValue = "";
    private static String lastKey = "";
    private static final Map<String, Map<String, String>> caches = new HashMap<>();
    private static final Gson gson = new Gson();

    public static void main(String[] args) {
        try (ZContext context = new ZContext()) {
            ZMQ.Socket pub = context.createSocket(SocketType.XPUB);
            ZMQ.Socket sub = context.createSocket(SocketType.XSUB);
            ZMQ.Socket rep = context.createSocket(SocketType.REP);
            pub.bind("tcp://*:8081");
            sub.bind("tcp://*:8080");
            rep.bind("tcp://*:8082");

            System.out.println("Broker listening on port 8081 and 8080 and 8082 ...");

            ZMQ.Poller items = context.createPoller(3);
            items.register(pub, ZMQ.Poller.POLLIN);
            items.register(sub, ZMQ.Poller.POLLIN);
            items.register(rep, ZMQ.Poller.POLLIN);

            boolean more;
            byte[] message;

            while (!Thread.currentThread().isInterrupted()) {
                items.poll();

                if (items.pollin(0)) {
                    do {
                        message = pub.recv(0);
                        more = pub.hasReceiveMore();
                        sub.send(message, more ? SNDMORE : 0);
                    } while (more);
                }

                if (items.pollin(1)) {
                    int i = 0;
                    do {
                        message = sub.recv(0);
                        more = sub.hasReceiveMore();
                        updateValues(message, i);
                        pub.send(message, more ? SNDMORE : 0);
                        i++;
                        if (!more) {
                            updateCache();
                            break;
                        }
                    } while (true);
                }

                if (items.pollin(2)) {
                    while (true) {
                        message = rep.recv(0);
                        String messageStr = new String(message);
                        if (messageStr.equals("hello")) {
                            String cacheStr = gson.toJson(caches);
                            rep.send(cacheStr.getBytes(), 0);
                            break;
                        }
                    }
                }
            }
        }
    }

    private static void updateCache() {
        if (lastJob.equals("pt")) {
            Map<String, String> cache = caches.get(lastName);
            if (Objects.isNull(cache))
                caches.put(lastName, new HashMap<>());
            cache = caches.get(lastName);
            cache.put(lastKey, lastValue);
        } else if (lastJob.equals("rm")) {
            Map<String, String> cache = caches.get(lastName);
            if (Objects.isNull(cache))
                return;
            cache.remove(lastKey);
        }
    }

    private static void updateValues(byte[] message, int i) {
        String messageStr = new String(message);
        if (i == 0)
            lastJob = messageStr;
        else if (i == 1)
            lastName = messageStr;
        else if (i == 2)
            lastKey = messageStr;
        else if (i == 3)
            lastValue = messageStr;
    }
}