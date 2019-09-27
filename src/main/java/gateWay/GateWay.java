package gateWay;

import discovery.Node;
import org.zeromq.ZMQ;

public interface GateWay {
    void startInteract();

    void sendPutMessage(String key, String value, String name, Node node);

    void sendRemoveMessage(Object o, String name, Node node);

    void pushUpdate(Node node);
}
