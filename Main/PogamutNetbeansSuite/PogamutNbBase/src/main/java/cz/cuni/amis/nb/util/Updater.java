package cz.cuni.amis.nb.util;

/**
 * Generic updater. updates can be triggered by event, timer etc.
 * @author ik
 */
public interface Updater {

    /**
     * Adds task that will be periodically called. It could be GUI updating or
     * logging etc.
     * @param task
     */
    void addUpdateTask(Runnable task);
}
