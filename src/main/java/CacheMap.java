import java.util.*;

public class CacheMap extends HashMap<String, String> {
    private final Interactor interactor;
    private final String name;

    protected CacheMap(Interactor interactor, String name) {
        super();
        this.interactor = interactor;
        this.name = name;
    }

    @Override
    public String remove(Object key) {
        if (Objects.nonNull(this.get(key)))
            interactor.remove(key, name);
        return super.remove(key);
    }

    @Override
    public String put(String key, String value) {
        if (Objects.isNull(this.get(key)) ||
                (Objects.nonNull(this.get(key)) && !this.get(key).equals(value)))
            interactor.put(key, value, name);
        return super.put(key, value);
    }

    public String networkPut(String key, String value) {
        return super.put(key, value);
    }

    public String networkRemove(Object key) {
        return super.remove(key);
    }
}
