import persianCache.CacheMap;
import persianCache.PersianCacheContext;

public class Main2 {
    public static void main(String[] args) throws InterruptedException {
        CacheMap first = PersianCacheContext.getCacheMap("first");
        while (true) {
            System.out.println(first.size());
            Thread.sleep(1500);
        }
    }
}
