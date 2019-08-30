import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

public class Proker {

    public static void main(String[] args) {
        try (ZContext context = new ZContext()) {
            ZMQ.Socket pub = context.createSocket(SocketType.XPUB);
            ZMQ.Socket sub = context.createSocket(SocketType.XSUB);
            pub.bind("tcp://*:8081");
            sub.bind("tcp://*:8080");

            System.out.println("Broker listening on port 8081 and 8080 ...");

            ZMQ.Poller items = context.createPoller(2);
            items.register(pub, ZMQ.Poller.POLLIN);
            items.register(sub, ZMQ.Poller.POLLIN);

            boolean more;
            byte[] message;

            while (!Thread.currentThread().isInterrupted()) {
                items.poll();

                if (items.pollin(0)) {
                    while (true) {
                        message = pub.recv(0);
                        more = pub.hasReceiveMore();

                        sub.send(message, more ? ZMQ.SNDMORE : 0);
                        if (!more) {
                            break;
                        }
                    }
                }

                if (items.pollin(1)) {
                    while (true) {
                        message = sub.recv(0);
                        more = sub.hasReceiveMore();
                        pub.send(message, more ? ZMQ.SNDMORE : 0);
                        if (!more) {
                            break;
                        }
                    }
                }
            }
        }
    }
}