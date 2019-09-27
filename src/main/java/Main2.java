import persianCache.CacheMap;
import persianCache.PersianCacheContext;

import java.io.IOException;

@SuppressWarnings({"InfiniteLoopStatement"})
public class Main2 {
    public static void main(String[] args) throws InterruptedException, IOException {
        PersianCacheContext.initialize(8080, 8083);
        CacheMap first = PersianCacheContext.getCacheMap("first");
        while (true) {
            System.out.println(first.size());
            Thread.sleep(1500);
        }
    }
}
