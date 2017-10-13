package cz.cuni.sposh.debugger;

import cz.cuni.amis.dash.DashWindow;
import cz.cuni.amis.dash.YaposhEngine;
import cz.cuni.amis.pogamut.sposh.dbg.engine.EngineThread;
import java.awt.event.ActionEvent;
import java.util.HashSet;
import java.util.Set;
import javax.swing.SwingUtilities;
import org.netbeans.api.debugger.jpda.JPDADebugger;
import org.netbeans.api.debugger.jpda.JPDAThread;
import org.netbeans.api.debugger.jpda.event.JPDABreakpointEvent;
import org.netbeans.api.debugger.jpda.event.JPDABreakpointListener;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.BooleanStateAction;

/**
 * High-level action (used in toolbar) will place a breakpoint at the evaluation
 * method of the sposh engine and opens new lap debugging window for each
 * different instance of an engine (=running plans).
 *
 * It is a state action, when called for odd time, it starts to watch for new
 * instances of the engine, and when called for even time, it stops the watch.
 *
 * @author HonzaH
 */
@ActionID(id = "cz.cuni.sposh.debugger.WatchLapAction", category = "Debug")
@ActionRegistration(displayName = "#CTL_WatchLapAction")
@ActionReference(path = "Toolbars/Debug")
public class WatchLapAction extends BooleanStateAction {

    /**
     * Name of group the created evaluation breakpoints will belong to. Used for
     * cleanup, when user closes NBs and reopens them, we don't want obsolete
     * BPs to hang around in the limbo. This identifies the BP created by this
     * action.
     */
    private static final String BP_GROUP_NAME = "Open windows for new engines";
    /**
     * Breakpoint listener in evaluation method. It collects all engine
     * instances it sees and when a new one appears, it creates a new debugger
     * for it. The instances of the engine are identified by the "logic" thread.
     */
    private final JPDABreakpointListener evaluateListener = new DebuggerCreator();
    /**
     * Breakpoint at the evaluation method used to catch new engine instances
     */
    private final BreakpointManager breakpointManager;

    /**
     * Create new action and make sure it is initially in the false state.
     */
    WatchLapAction() {
        setBooleanState(false);
        breakpointManager = BreakpointManager.createLapEvaluation(BP_GROUP_NAME);
        breakpointManager.purge();
    }

    /**
     * Depending on state of plan capture button either start listening for
     * debugged engines or stop listening.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        setBooleanState(!getBooleanState());

        // remove previous BPs irrespective of the boolean state
        if (getBooleanState()) {
            // add watch to the evaluation method of posh engine
            breakpointManager.addListener(evaluateListener);
        } else {
            breakpointManager.removeListener(evaluateListener);
            breakpointManager.purge();
        }
    }

    @Override
    protected String iconResource() {
        return "cz/cuni/sposh/debugger/dbg_watch.png";
    }

    @Override
    public String getName() {
        // Displayed as tooltip of the button in the menu
        return NbBundle.getMessage(WatchLapAction.class, "CTL_WatchLapAction");
    }

    /**
     * No help ctx.
     * @return null
     */
    @Override
    public HelpCtx getHelpCtx() {
        return null;
    }

    /**
     * Listener for new engines, when new engine is encountered, create and open
     * new debugger for it.
     */
    private static class DebuggerCreator implements JPDABreakpointListener {

        /**
         * Set of engines that have already reached breakpoint
         */
        private Set<JPDAThread> engines = new HashSet<JPDAThread>();

        @Override
        public void breakpointReached(JPDABreakpointEvent jpdabe) {
            JPDADebugger debugger = jpdabe.getDebugger();
            JPDAThread engineThread = jpdabe.getThread();

            if (engines.contains(engineThread)) {
                return;
            }
            engines.add(engineThread);

            SwingUtilities.invokeLater(new OpenLapDebugger(debugger, engineThread));
        }
    };
}

/**
 * Runnable that will open a new debugger window for passed engine thread. This
 * class is supposed to be executed by the EDT.
 *
 * @author Honza
 */
class OpenLapDebugger implements Runnable {

    private final JPDADebugger debugger;
    private final JPDAThread engineThread;

    /**
     * Prepare for opening of debugging window, we 
     * @param debugger Debugger access to the Yaposh project 
     * @param engineThread Thread Yaposh engine is running in the @debugger
     */
    OpenLapDebugger(JPDADebugger debugger, JPDAThread engineThread) {
        this.debugger = debugger;
        this.engineThread = engineThread;
    }

    @Override
    public void run() {
        assert SwingUtilities.isEventDispatchThread();

        YaposhEngine engine = new YaposhEngine(new EngineThread(debugger, engineThread));
        DashWindow view = new DashWindow(engine, "Lap debugger");
        engine.addListener(view);

        engine.initialize();
        view.open();
        view.requestActive();
    }
}
