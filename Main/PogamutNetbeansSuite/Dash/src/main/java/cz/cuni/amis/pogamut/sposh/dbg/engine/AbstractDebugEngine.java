package cz.cuni.amis.pogamut.sposh.dbg.engine;

import cz.cuni.amis.pogamut.sposh.elements.LapPath;
import cz.cuni.amis.pogamut.sposh.elements.PoshPlan;
import java.util.HashSet;
import java.util.Set;
import javax.swing.SwingUtilities;

/**
 * Abstract class implementing {@link IDebugEngine} that is implementing some
 * common things, like listeners.
 * @author Honza
 */
public abstract class AbstractDebugEngine implements IDebugEngine {

    private final Set<IDebugEngineListener> listeners = new HashSet<IDebugEngineListener>();

    /**
     * Add listener for changes in state of the engine, it is synchronized.
     * @param listener new listener to add, not null
     * @return was listener already among listeners?
     */
    @Override
    public final boolean addListener(IDebugEngineListener listener) {
        assert listener != null;
        synchronized (listeners) {
            return listeners.add(listener);
        }
    }

    /**
     * Remove listener from set of listeners of this engine.
     * @param listener listener to remove
     * @return was passed listener among listeners of this engine?
     */
    @Override
    public final boolean removeListener(IDebugEngineListener listener) {
        assert listener != null;
        synchronized (listeners) {
            return listeners.remove(listener);
        }
    }

    /**
     * For internal use, get array of listeners that won't be affected by
     * the changes of set of listener
     * @return array of listeners of this engine
     */
    protected final IDebugEngineListener[] getListeners() {
        synchronized (listeners) {
            return listeners.toArray(new IDebugEngineListener[listeners.size()]);
        }
    }

    /**
     * Notify listeners that engine is connected and can now add breakpoints.
     */
    protected final void notifyConnected() {
        assert SwingUtilities.isEventDispatchThread();
        for (IDebugEngineListener listener : getListeners())
            listener.connected();
    }

    /**
     * Notify all listeners that plan has been recieved
     * @param name name of the engine that is running this plan
     * @param plan String representation of plan (e.g. POSH or Shade)
     */
    protected final void notifyPlanRecieved(String name, PoshPlan plan) {
        assert SwingUtilities.isEventDispatchThread();
        for (IDebugEngineListener listener : getListeners())
            listener.planRecieved(name, plan);
    }

    /**
     * Notify listeners that evaluation phase of engine has been reached
     */
    protected final void notifyEvaluationReached() {
        assert SwingUtilities.isEventDispatchThread();
        for (IDebugEngineListener listener : getListeners())
            listener.evaluationReached();
    }

    /**
     * Notify listeners that evaluation phase of engine has been finished
     */
    protected final void notifyEvaluationFinished() {
        assert SwingUtilities.isEventDispatchThread();
        for (IDebugEngineListener listener : getListeners())
            listener.evaluationFinished();
    }
    
    /**
     * Notify listeners that engine is about to fire a path.
     * @param path Path that is about to be fired
     */
    protected final void notifyPathReached(LapPath path) {
        assert SwingUtilities.isEventDispatchThread();
        for (IDebugEngineListener listener : getListeners())
            listener.pathReached(path);
    }
    
    /**
     * Notify listeners that engine has been disconnected from the JVM.
     * @param message Optional message to display the user.
     * @param error Was debugger disconnected because of an error or was it OK.
     */
    protected final void notifyDisconnected(String message, boolean error) {
        assert SwingUtilities.isEventDispatchThread();
        for (IDebugEngineListener listener : getListeners())
            listener.disconnected(message, error);
    }
}
