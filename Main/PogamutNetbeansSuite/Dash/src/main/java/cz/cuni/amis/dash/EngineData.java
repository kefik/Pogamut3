package cz.cuni.amis.dash;

import cz.cuni.amis.pogamut.sposh.elements.LapPath;
import cz.cuni.amis.pogamut.sposh.elements.PoshPlan;
import cz.cuni.amis.pogamut.sposh.engine.PoshEngine;
import java.util.*;

/**
 * Storage for all information provided by the {@link PoshEngine engine} that
 * will be collected. For now, it stores the {@link PoshPlan} along with all
 * reached {@link LapPath paths} and their timestamps.
 *
 * @author Honza
 */
public final class EngineData {

    /**
     * Plan the engine is executing.
     */
    private final PoshPlan plan;
    /**
     * All recieved paths and for each path list of timestamps time
     */
    private final Map<LapPath, List<Long>> storedRecords = new HashMap<LapPath, List<Long>>();
    /**
     * All listeners for new data.
     */
    private final List<IEngineDataListener> listeners = new LinkedList<IEngineDataListener>();

    /**
     * Create new storage for execution of @plan.
     *
     * @param plan Plan for which we will gather data.
     */
    public EngineData(PoshPlan plan) {
        this.plan = plan;
    }

    /**
     * Add record that engine has reached the @path.
     *
     * @param path Path the engine has reached
     * @param timestamp When did engine reached the path
     */
    public void storePath(LapPath path, long timestamp) {
        List<Long> pathTimestamps = storedRecords.get(path);
        if (pathTimestamps == null) {
            pathTimestamps = new LinkedList<Long>();
            storedRecords.put(path, pathTimestamps);
            notifynewPath(path);
        }

        long lastTimestamp = Long.MIN_VALUE;
        if (!pathTimestamps.isEmpty()) {
            lastTimestamp = pathTimestamps.get(pathTimestamps.size() - 1);
        }
        assert lastTimestamp <= timestamp;
        pathTimestamps.add(timestamp);
        notifyPathReached(path, timestamp);
    }

    /**
     * @return Get plan the engine used.
     */
    public PoshPlan getPlan() {
        return plan;
    }

    /**
     * @return Unmodifiable set containing all paths that were ever traversed by
     * the engine.
     */
    public Set<LapPath> getPaths() {
        return Collections.unmodifiableSet(storedRecords.keySet());
    }

    /**
     * Get times when engine reached the @path
     *
     * @param path For which path we want timestamps.
     * @return Unmodifiable list of all occurances of the @path in the engine in
     * ascending order.
     */
    public List<Long> getOccurrences(LapPath path) {
        if (!storedRecords.containsKey(path)) {
            throw new IllegalArgumentException("Path " + path + " hasn never been visites by the engine.");
        }
        return Collections.unmodifiableList(storedRecords.get(path));
    }

    void addListener(IEngineDataListener listener) {
        listeners.add(listener);
    }

    void removeListener(IEngineDataListener listener) {
        listeners.remove(listener);
    }

    private IEngineDataListener[] getListeners() {
        return listeners.toArray(new IEngineDataListener[listeners.size()]);
    }

    private void notifynewPath(LapPath path) {
        for (IEngineDataListener l : getListeners()) {
            l.onNewPath(path);
        }
    }

    private void notifyPathReached(LapPath path, long timestamp) {
        for (IEngineDataListener l : getListeners()) {
            l.onPathReached(path, timestamp);
        }
    }
}

/**
 * Listener on events of the {@link EngineData}.
 *
 * @author Honza
 */
interface IEngineDataListener {

    /**
     * Notification about never before seen path.
     *
     * @param path Never before traversed path
     */
    void onNewPath(LapPath path);

    /**
     * Notification that some path. The path has always been announced by {@link #onNewPath(cz.cuni.amis.pogamut.sposh.elements.LapPath)
     * } before it is reached.
     *
     * @param path Path that is being processed by engine.
     * @param timestamp When did engine reach the @path.
     */
    void onPathReached(LapPath path, long timestamp);
}