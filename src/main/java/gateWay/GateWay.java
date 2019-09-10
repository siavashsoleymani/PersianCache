package gateWay;

public interface GateWay {
    void startInteract();

    void fillCacheMapForFirstTime();

    void sendPutMessage(String key, String value, String name);

    void sendRemoveMessage(Object o, String name);
}
