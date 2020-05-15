package Utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

public class RateLimiterSingleton {

    private LinkedList<Long> calls = new LinkedList<>();
    private static RateLimiterSingleton rateLimiter = null;
    private final int callLimitPerSecond;
    private final Logger LOGGER = LoggerFactory.getLogger(RateLimiterSingleton.class.getName());

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
            LOGGER.warn("RateLimiterSingleton : Warning : Only accept {} requests per second. Request delay.", callLimitPerSecond);
            TimeUnit.MILLISECONDS.sleep(System.currentTimeMillis() - calls.getFirst() + 1000);
            calls.removeFirst();
        }
        calls.add(System.currentTimeMillis());
    }
}
