public interface Interactor {
    void put(String i, String r, String name);

    void remove(Object o, String name);

    void startInteract();

    CacheMap getCacheMap(String name);
}
