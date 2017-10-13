package cz.cuni.amis.utils;

import java.util.Collection;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Utility class for concurrent computations.
 * @author ik
 */
public class Concurrent {

    public static void waitForAll(Future[] futures) throws InterruptedException, ExecutionException {
        for (Future f : futures) {
            f.get();
        }
    }

    public static boolean allAreDone(Future[] futures) {
        for (Future f : futures) {
            if (!f.isDone()) {
                return false;
            }
        }
        return true;
    }


    public static boolean allAreDone(Collection<? extends Future> futures) {
        for (Future f : futures) {
            if (!f.isDone()) {
                return false;
            }
        }
        return true;
    }
}
