package org.concurrency.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author kyan
 * @date 2019/5/13
 */
public class ConcurrentUtil {

    public static void stop(ExecutorService executor) {
        stop(executor, 60);
    }

    public static void stop(ExecutorService executor, int awaitTime) {
        try {
            System.out.println("attempt to shutdown");
            executor.shutdown();
            executor.awaitTermination(awaitTime, TimeUnit.SECONDS);
        }
        catch (InterruptedException e) {
            System.err.println("termination interrupted");
        }
        finally {
            if (!executor.isTerminated()) {
                System.err.println("killing non-finished tasks");
            }
            executor.shutdownNow();
            System.out.println("shutdown finished");
        }
    }

    public static void sleepBySec(int seconds) {
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        }
    }

    public static void sleepByMillis(long millis) {
        try {
            TimeUnit.MILLISECONDS.sleep(millis);
        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        }
    }
}
