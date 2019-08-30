import java.util.Random;

public class Main2 {
    public static void main(String[] args) throws InterruptedException {
        CacheMap first = PersianCacheContext.getCacheMap("first");
        while (true) {
            System.out.println(first.size());
            Thread.sleep(1500);
        }
    }
}
