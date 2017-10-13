package cz.cuni.amis.pogamut.sposh.dbg.engine;

import cz.cuni.amis.pogamut.sposh.elements.LapPath;

/**
 * This interface is representation of AI debugger engine, you can start it, ask
 * for breakpoints, it notifies you about evaluations of plans and other stuff.
 *
 * @author Honza
 */
public interface IDebugEngine {

    /**
     * Initialize the engine, once initialized, you can do some useful stuff,
     * like disconnecting, adding breakpoints and being notified about
     * evaluation of the executing plan.
     *
     * This method should use some means to notify things like {@link IDebuggerListener},
     * that is has been initialized, so they can reflect new reality.
     */
    void initialize();

    /**
     * Add a breakpoint to the primitive at the specified path.
     *
     * @param path path to the breakpointed element
     * @return was there already breakpoint?
     */
    boolean addBreakpoint(LapPath path, boolean single);

    /**
     * Remove breakpoint from the debugged plan.
     *
     * @param path path to the breakpoint
     * @return was there a breakpoint at the spevified path?
     */
    boolean removeBreakpoint(LapPath path);

    /**
     * Disconnect engine, stop the info, remove breakpoint, clean after yourself
     * and notify displayer.
     *
     * @param reason What is the reason the debugger is being disconnected, the
     * reason will be passed to other listeners later, so it may be displayed to
     * the user.
     * @param error Should debugger disconnect because of an error (e.g. someone
     * got {@link DashPath} incompatible with recieved plan).
     */
    void disconnect(String reason, boolean error);

    /**
     * Add listener for changes in state of the engine, it is synchronized.
     *
     * @param listener new listener to add, not null
     * @return was listener already among listeners?
     */
    boolean addListener(IDebugEngineListener listener);

    /**
     * Remove listener from set of listeners of this engine.
     *
     * @param listener listener to remove
     * @return was passed listener among listeners of this engine?
     */
    boolean removeListener(IDebugEngineListener listener);
}
