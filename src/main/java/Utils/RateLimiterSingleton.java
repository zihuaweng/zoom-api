package Utils;

import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

public class RateLimiterSingleton {

    private LinkedList<Long> calls = new LinkedList<>();
    private static RateLimiterSingleton rateLimiter = null;
    private final int callLimitPerSecond;

    public RateLimiterSingleton(int callLimitPerSecond) {
        this.callLimitPerSecond = callLimitPerSecond;
    }

    public RateLimiterSingleton() {
        this.callLimitPerSecond = 2;
    }

    public static RateLimiterSingleton getInstance() {
        if (rateLimiter == null) {
            synchronized (RateLimiterSingleton.class) {
                if (rateLimiter == null) {
                    rateLimiter = new RateLimiterSingleton();
                }
            }
        }
        return rateLimiter;
    }

    public void checkValid() throws InterruptedException {
        while (!calls.isEmpty() && System.currentTimeMillis() - calls.getFirst() > 1000) {
            calls.removeFirst();
        }

        if (calls.size() >= callLimitPerSecond) {
            System.out.println("Utils.RateLimiterSingleton : Warning : Only accept " + callLimitPerSecond + " requests per second. Request delay.");
            TimeUnit.MILLISECONDS.sleep(System.currentTimeMillis() - calls.getFirst() + 1000);
            calls.removeFirst();
        }
        calls.add(System.currentTimeMillis());
    }
}
