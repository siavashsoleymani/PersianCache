import persianCache.CacheMap;
import persianCache.PersianCacheContext;

import java.util.Random;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        CacheMap first = PersianCacheContext.getCacheMap("first");
        while (true) {
            first.put("salam" + new Random().nextInt(10), "siavash");
            first.put("salam" + new Random().nextInt(10), "siavash");
            first.put("salam" + new Random().nextInt(10), "siavash");
            first.remove("salam" + new Random().nextInt(10));
            Thread.sleep(1500);
        }
    }
}
