package cz.cuni.amis.pogamut.sposh.dbg.engine;

import cz.cuni.amis.pogamut.sposh.dbg.lap.LapBreakpoint;
import cz.cuni.amis.pogamut.sposh.elements.LapPath;
import cz.cuni.amis.pogamut.sposh.elements.PoshPlan;
import cz.cuni.amis.pogamut.sposh.engine.PoshEngine;

/**
 * This is an interface for displayer of debugging. Component that implements
 * this will be notified about reaching the evaluating phase of some AI engine
 * (for now, only POSH), connection, disconnection and other stuff. <br/>
 * <b>IMPORTANT:</b> All these methods are supposed to be called on EDT ( swing
 * thread, because some listeners will draw onto the screen).
 *
 * @author Honza
 */
public interface IDebugEngineListener {

    /**
     * This method is called when debugger has successfully connected to the
     * engine.
     */
    void connected();

    /**
     * Called when plan was recieved.
     *
     * @param name name used to identify the instance of the engine that is
     * running this plan
     * @param plan String representation of plan (e.g. POSH or Shade)
     */
    void planRecieved(String name, PoshPlan plan);

    /**
     * Debugger has reached {@link PoshEngine#evaluatePlan(cz.cuni.amis.pogamut.sposh.executor.IWorkExecutor) }.
     */
    void evaluationReached();

    /**
     * Notify about path that is about to be fired.
     */
    void pathReached(LapPath path);

    /**
     * Debugger is about to leave {@link PoshEngine#evaluatePlan(cz.cuni.amis.pogamut.sposh.executor.IWorkExecutor) }.
     */
    void evaluationFinished();

    /**
     * Notify listeners about new breakpoint
     * @param bp New breakpoint
     */
    void breakpointAdded(LapBreakpoint bp);

    /**
     * Notify listeners that breakpoint was removed
     * @param bp removed breakpoint
     */
    void breakpointRemoved(LapBreakpoint bp);

    /**
     * When debugger crashes or user asks to be disconnected, it will sooner or
     * later cause this.
     *
     * @param message Optional message to display the user.
     * @param error Was debugger disconnected because of an error or was it OK.
     */
    void disconnected(String message, boolean error);
}
