import persianCache.CacheMap;
import persianCache.PersianCacheContext;

import java.io.IOException;
import java.util.Random;

@SuppressWarnings({"InfiniteLoopStatement"})
public class Main {
    public static void main(String[] args) throws InterruptedException, IOException {
        PersianCacheContext.initialize(8082, 8083);
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
