import java.util.*;

public class CacheMap extends HashMap<String, String> {
    private final PersianCacheContext persianCacheContext;
    private final String name;

    protected CacheMap(PersianCacheContext update, String name) {
        super();
        this.persianCacheContext = update;
        this.name = name;
    }

    @Override
    public String remove(Object o) {
        if (Objects.nonNull(this.get(o)))
            persianCacheContext.remove(o, name);
        return super.remove(o);
    }

    @Override
    public String put(String s, String s2) {
        if (Objects.isNull(this.get(s)))
            persianCacheContext.put(s, s2, name);
        if (Objects.nonNull(this.get(s)) && !this.get(s).equals(s2))
            persianCacheContext.put(s, s2, name);
        return super.put(s, s2);
    }
}
