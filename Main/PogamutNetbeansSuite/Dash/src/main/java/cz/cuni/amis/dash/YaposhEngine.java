package cz.cuni.amis.dash;

import com.sun.jdi.AbsentInformationException;
import cz.cuni.amis.pogamut.sposh.dbg.engine.*;
import cz.cuni.amis.pogamut.sposh.dbg.exceptions.UnexpectedMessageException;
import cz.cuni.amis.pogamut.sposh.dbg.lap.LapBreakpoint;
import cz.cuni.amis.pogamut.sposh.elements.*;
import cz.cuni.amis.pogamut.sposh.engine.EngineLog;
import cz.cuni.amis.pogamut.sposh.engine.PoshEngine;
import cz.cuni.amis.pogamut.sposh.exceptions.FubarException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.net.InetSocketAddress;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import org.netbeans.api.debugger.*;
import org.netbeans.api.debugger.jpda.*;
import org.netbeans.api.debugger.jpda.event.JPDABreakpointEvent;
import org.netbeans.api.debugger.jpda.event.JPDABreakpointListener;
import org.openide.util.Exceptions;

/**
 * Class attached to a Yaposh engine debugged in NetBeans. This class creates
 * various java breakpoints in the {@link PoshPlan Yaposh engine} and listens,
 * when engine does trigger them, e.g. it has breakpoint that listens for {@link EngineLog#pathReached(cz.cuni.amis.pogamut.sposh.elements.LapPath)
 * } and when some path is reached, it notifys listeners.
 *
 * @author Honza
 */
public final class YaposhEngine extends AbstractDebugEngine {

    private static final Class ENGINE_CLASS = PoshEngine.class;
    private static final String EVALUATION_METHOD_NAME = "evaluatePlan";
    private static final String EVALUATION_METHOD_SIGNATURE = "(Lcz/cuni/amis/pogamut/sposh/executor/IWorkExecutor;)Lcz/cuni/amis/pogamut/sposh/engine/PoshEngine$EvaluationResultInfo;";
    private static final String EVALUATION_EXIT_METHOD_NAME = "evaluatePlanExit";
    private static final String EVALUATION_EXIT_METHOD_SIGNATURE = "()V";
    private static final Class ENGINE_LOG_CLASS = EngineLog.class;
    private static final String PATH_REACHED_EXIT_METHOD_NAME = "pathReachedExit";
    private static final String PATH_REACHED_EXIT_METHOD_SIGNATURE = "()V";
    /**
     * Name of group into which the breakpoints will belong.
     */
    private static final String BREAKPOINT_GROUP_NAME = "dash";
    private final EngineThread engineThread;
    /**
     * Breakpoint at the entry of {@link PoshEngine#evaluatePlan(cz.cuni.amis.pogamut.sposh.executor.IWorkExecutor) evaluation method}
     * of Yaposh engine.
     */
    private MethodBreakpoint evaluationEntryBreakpoint;
    /**
     * Listener that is notified upon entry into evaluate method.
     */
    private JPDABreakpointListener getPlanListener;
    private JPDABreakpointListener notifyEvaluationReached;
    /**
     * Breakpoint for {@link EngineLog#firingPathExit() } method. This
     * breakpoints notifies that some path in the plan is about to be fired.
     */
    private MethodBreakpoint pathReachedBreakpoint;
    /**
     * Listener that will be notified when the engine is about to fire a path.
     */
    private JPDABreakpointListener pathReachedListener;
    /**
     * Breakpoint at the exit of {@link PoshEngine#evaluatePlan(cz.cuni.amis.pogamut.sposh.executor.IWorkExecutor) evaluation method}
     * of Yaposh engine.
     */
    private MethodBreakpoint evaluationExitBreakpoint;
    /**
     * Listener that is notified upon exit from evaluate method, at that moment
     * it resumes the server.
     */
    private JPDABreakpointListener resumeServerListener;
    /**
     * Notify all {@link IDebugEngineListener}s about {@link IDebugEngineListener#evaluationFinished()
     * }.
     */
    private JPDABreakpointListener notifyEvaluationFinished;
    /**
     * When the session ends, this listener is called. If necessary, it resumes
     * the server and then it calls {@link #disconnect(java.lang.String, boolean)
     * } . This takes care about proper cleanup after ending the Nb and after
     * JVM has been killed.
     */
    private DebuggerManagerListener disconnectListener;
    /**
     * Manager of server, it is responsible for pausing and resuming the server.
     */
    private ServerManager serverManager;
    private static final Logger log = Logger.getLogger("YaposhEngine");
    /**
     * Set of all breakpoints in the engine
     */
    private final Set<LapBreakpoint> breakpoints = new HashSet<LapBreakpoint>();
    /**
     * Recieved plan.
     */
    private PoshPlan plan;
    /**
     * Address of UT200r server.
     */
    private final InetSocketAddress serverAddress = new InetSocketAddress("localhost", 3001);

    /**
     * Create the engine.
     *
     * @param engineThread Thread of the debugged Yaposh engine.
     */
    public YaposhEngine(EngineThread engineThread) {
        this.engineThread = engineThread;
    }

    /**
     * Create {@link ServerManager} and add all needed breakpoints to the
     * engine. Notify listeners of engine that we are now connected to the
     * engine.
     */
    @Override
    public void initialize() {
        log.info("Connect to the server");
        serverManager = new ServerManager();
        serverManager.connect();

        log.info("Adding breakpoint at evaluation entry");
        evaluationEntryBreakpoint = createEntryBreakpoint(BREAKPOINT_GROUP_NAME, ENGINE_CLASS, EVALUATION_METHOD_NAME, EVALUATION_METHOD_SIGNATURE);
        getPlanListener = new GetPlanListener();
        evaluationEntryBreakpoint.addJPDABreakpointListener(getPlanListener);
        notifyEvaluationReached = new NotifyEvaluationReached();
        evaluationEntryBreakpoint.addJPDABreakpointListener(notifyEvaluationReached);
        DebuggerManager.getDebuggerManager().addBreakpoint(evaluationEntryBreakpoint);

        log.info("Adding breakpoint at path reached method");
        pathReachedListener = new PathReachedListener();
        pathReachedBreakpoint = createEntryBreakpoint(BREAKPOINT_GROUP_NAME, ENGINE_LOG_CLASS, PATH_REACHED_EXIT_METHOD_NAME, PATH_REACHED_EXIT_METHOD_SIGNATURE);

        pathReachedBreakpoint.addJPDABreakpointListener(pathReachedListener);
        DebuggerManager.getDebuggerManager().addBreakpoint(pathReachedBreakpoint);

        log.info("Adding breakpoint at evaluation exit");
        evaluationExitBreakpoint = createEntryBreakpoint(BREAKPOINT_GROUP_NAME, ENGINE_CLASS, EVALUATION_EXIT_METHOD_NAME, EVALUATION_EXIT_METHOD_SIGNATURE);
        resumeServerListener = new ResumeServerListener();
        evaluationExitBreakpoint.addJPDABreakpointListener(resumeServerListener);
        notifyEvaluationFinished = new NotifyEvaluationFinished();
        evaluationExitBreakpoint.addJPDABreakpointListener(notifyEvaluationFinished);
        DebuggerManager.getDebuggerManager().addBreakpoint(evaluationExitBreakpoint);

        log.info("Adding cleanup listener");
        disconnectListener = new DisconnectListener();
        DebuggerManager.getDebuggerManager().addDebuggerListener(disconnectListener);

        notifyConnected();
    }

    /**
     * Disconnect from the debugged engine. Disconnect the {@link ServerManager}
     * and remove breakpoints from the debugged Yaposh engine. Notify listeners
     * that we are now disconnected from the engine.
     *
     * @param reason Why are we disconnecting, passed to listeners
     * @param error Are we disconnecting because of error? Pass to listeners.
     */
    @Override
    public void disconnect(String reason, boolean error) {
        log.info("Disconnect from the server");
        serverManager.disconnect();

        log.info("Removing breakpoint at evaluation entry");
        evaluationEntryBreakpoint.removeJPDABreakpointListener(getPlanListener);
        evaluationEntryBreakpoint.removeJPDABreakpointListener(notifyEvaluationReached);
        DebuggerManager.getDebuggerManager().removeBreakpoint(evaluationEntryBreakpoint);

        log.info("Removing breakpoint at processing path method");
        pathReachedBreakpoint.removeJPDABreakpointListener(pathReachedListener);
        DebuggerManager.getDebuggerManager().removeBreakpoint(pathReachedBreakpoint);

        log.info("Removing breakpoint at evaluation exit");
        evaluationExitBreakpoint.removeJPDABreakpointListener(resumeServerListener);
        evaluationExitBreakpoint.removeJPDABreakpointListener(notifyEvaluationFinished);
        DebuggerManager.getDebuggerManager().removeBreakpoint(evaluationExitBreakpoint);

        log.info("Removing cleanup listener");
        DebuggerManager.getDebuggerManager().removeDebuggerListener(disconnectListener);

        notifyDisconnected(reason, error);
    }

    /**
     * Create breakpoint in the @methodName method of the @breakpointClas class.
     *
     * The exit method is used as workaround for slow {@link MethodBreakpoint#TYPE_METHOD_EXIT exit type}
     * breakpoint (~1.2 seconds penalty in the debuggee). Instead of using exit
     * type, I have added an extra method right before return from the method
     * and I am adding an {@link MethodBreakpoint#TYPE_METHOD_ENTRY entry type}
     * breakpoint to the exit method.
     *
     * @param groupName Name of the group, will be shown in the Breakpoints
     * window, useful for determining which breakpoint is added multiple times.
     * @return created breakpoint
     */
    private MethodBreakpoint createEntryBreakpoint(String groupName, Class breakpointClass, String methodName, String signature) {
        MethodBreakpoint bp = MethodBreakpoint.create();
        bp.setClassFilters(new String[]{breakpointClass.getName()});
        bp.setMethodName(methodName);
        bp.setMethodSignature(signature);
        bp.setGroupName(groupName);
        bp.setSuspend(MethodBreakpoint.SUSPEND_NONE);
        bp.setBreakpointType(MethodBreakpoint.TYPE_METHOD_ENTRY);
        return bp;
    }

    /**
     * Add breakpoint at the node at @path and notify listeners.
     *
     * @param path Path at which we want to add breakpoint.
     * @param single Is it a single breakpoint or permanent?
     * @return If added breakpoint was new one
     */
    @Override
    public boolean addBreakpoint(LapPath path, boolean single) {
        assert SwingUtilities.isEventDispatchThread();

        removeBreakpoint(path);

        LapBreakpoint breakpoint = new LapBreakpoint(path, single);
        boolean ret = breakpoints.add(breakpoint);
        for (IDebugEngineListener listener : getListeners()) {
            listener.breakpointAdded(breakpoint);
        }
        return ret;
    }

    private LapBreakpoint findBreakpoint(LapPath searchedPath) {
        for (LapBreakpoint bp : breakpoints) {
            if (bp.getPath().equals(searchedPath)) {
                return bp;
            }
        }
        return null;
    }

    /**
     * Remove breakpoint at node found at @path.
     *
     * @param path Path where is breakpoint we want to remove
     * @return Was there was a breakpoint at @path?
     */
    @Override
    public boolean removeBreakpoint(LapPath path) {
        assert SwingUtilities.isEventDispatchThread();
        LapBreakpoint foundBp = findBreakpoint(path);
        if (foundBp != null) {
            breakpoints.remove(foundBp);
            for (IDebugEngineListener listener : getListeners()) {
                listener.breakpointRemoved(foundBp);
            }
            return true;
        }
        return false;
    }

    private void removeBreakpointInEDT(final LapPath path) throws InterruptedException, InvocationTargetException {
        Runnable removeBreakpoint = new Runnable() {

            @Override
            public void run() {
                removeBreakpoint(path);
            }
        };
        if (SwingUtilities.isEventDispatchThread()) {
            removeBreakpoint.run();
        } else {
            SwingUtilities.invokeAndWait(removeBreakpoint);
        }
    }

    private void disconnectInEDT(final String disconnectMsg, final boolean error) {
        Runnable disconnectRunnable = new Runnable() {

            @Override
            public void run() {
                disconnect(disconnectMsg, error);
            }
        };
        if (SwingUtilities.isEventDispatchThread()) {
            disconnectRunnable.run();
        } else {
            try {
                SwingUtilities.invokeAndWait(disconnectRunnable);
            } catch (Exception ex) {
                throw new FubarException(ex);
            }
        }
    }

    private void notifyPathReachedInEDT(final LapPath path) throws InterruptedException, InvocationTargetException {
        log.log(Level.INFO, "Notify pathReached in EDT {0}", Thread.currentThread());
        Runnable notifyPathReachedRunnable = new Runnable() {

            @Override
            public void run() {
                notifyPathReached(path);
            }
        };
        if (SwingUtilities.isEventDispatchThread()) {
            notifyPathReachedRunnable.run();
        } else {
            SwingUtilities.invokeAndWait(notifyPathReachedRunnable);
        }
    }

    private class PathReachedListener extends EvaluationListener {

        private final String GET_REACHED_PATH_METHOD = "getLastReachedPath";

        @Override
        public void breakpointReached(JPDABreakpointEvent jpdabe) {
            JPDAThread breakpointThread = jpdabe.getThread();
            if (!breakpointThread.equals(engineThread.getThread())) {
                return;
            }

            String errorMsg = null;
            String pathString = null;
            LapPath path = null;
            try {
                pathString = getPath(jpdabe);
                path = LapPath.parse(pathString);
                notifyPathReachedInEDT(path);
            } catch (AbsentInformationException ex) {
                errorMsg = "Unable to get the path from the engine. Engine thread is running or has no callstack.";
            } catch (NoSuchMethodException ex) {
                errorMsg = MessageFormat.format("Unable to get the path from the engine. No member method String {0}() found.", GET_REACHED_PATH_METHOD);
            } catch (InvalidExpressionException ex) {
                errorMsg = MessageFormat.format("Unable to get the path from the engine. {0}", ex.getMessage());
            } catch (ParseException ex) {
                errorMsg = MessageFormat.format("Unable to parse recieved path {0}: {1}", pathString, ex.getMessage());
            } catch (InterruptedException ex) {
                errorMsg = MessageFormat.format("EDT thread was interrupted while notifying listeners about new path {0}.", pathString);
            } catch (InvocationTargetException ex) {
                errorMsg = MessageFormat.format("While notifying listeners about plan {0}, an exception was thrown: {1}", pathString, ex.toString());
            } finally {
                if (errorMsg != null) {
                    log.log(Level.SEVERE, errorMsg);
                    disconnectInEDT(errorMsg, true);
                    return;
                }
            }

            // if path is breakpoint, add breakpoint and stop server
            for (LapBreakpoint breakpoint : breakpoints) {
                if (!breakpoint.getPath().equals(path)) {
                    continue;
                }
                if (breakpoint.isSingle()) {
                    try {
                        removeBreakpointInEDT(path);
                    } catch (InterruptedException ex) {
                        disconnectInEDT("While removing breakpoint, the EDT thread was interrupted.", true);
                        return;
                    } catch (InvocationTargetException ex) {
                        disconnectInEDT("Exception during breakpoint removal " + ex.getCause(), true);
                        return;
                    }
                }

                String breakpointFQN = breakpoint.getPrimitiveName(plan);
                DebuggerManager dm = DebuggerManager.getDebuggerManager();

                MethodBreakpoint javaBreakpoint;
                LapType bpType = breakpoint.getType(plan);
                if (bpType == LapType.ACTION) {
                    javaBreakpoint = createSelfRemoveBreakpoint(breakpointFQN, "run", MethodBreakpoint.TYPE_METHOD_ENTRY);
                } else if (bpType == LapType.SENSE) {
                    javaBreakpoint = createSelfRemoveBreakpoint(breakpointFQN, "query", MethodBreakpoint.TYPE_METHOD_ENTRY);
                } else {
                    throw new IllegalStateException("Lap breakpoint at path " + breakpoint.getPath() + " is not ACTIOn nor SENSE.");
                }
                // Is there already a breakpoint at the specified place?
                boolean isDuplicate = false;
                for (Breakpoint testedBreakpoint : dm.getBreakpoints()) {
                    if (testedBreakpoint instanceof MethodBreakpoint) {
                        MethodBreakpoint methodBreakpoint = (MethodBreakpoint) testedBreakpoint;

                        if (Arrays.equals(javaBreakpoint.getClassFilters(), methodBreakpoint.getClassFilters())
                                && javaBreakpoint.getMethodName().equals(methodBreakpoint.getMethodName())
                                && javaBreakpoint.getMethodSignature().equals(methodBreakpoint.getMethodSignature())) {
                            isDuplicate = true;
                        }
                    }
                }
                try {
                    ServerManager.pause(YaposhEngine.this, serverAddress);
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                } catch (UnexpectedMessageException ex) {
                    Exceptions.printStackTrace(ex);
                }

                if (!isDuplicate) {
                    dm.addBreakpoint(javaBreakpoint);
                }
            }
        }

        private String getPath(JPDABreakpointEvent jpdabe) throws AbsentInformationException, NoSuchMethodException, InvalidExpressionException {
            JPDAThread thread = jpdabe.getThread();
            This thisVar = getThisVariable(thread);
            String stringPath = callStringMethod(jpdabe.getDebugger(), thisVar, thread, GET_REACHED_PATH_METHOD);
            return stringPath;
        }
    }

    private void notifyEvaluationReachedInEDT() throws InterruptedException, InvocationTargetException {
        log.log(Level.INFO, "Notify evaluationReached in EDT {0}", Thread.currentThread());
        Runnable notifyEvaluationReachedRunnable = new Runnable() {

            @Override
            public void run() {
                notifyEvaluationReached();
            }
        };
        if (SwingUtilities.isEventDispatchThread()) {
            notifyEvaluationReachedRunnable.run();
        } else {
            SwingUtilities.invokeAndWait(notifyEvaluationReachedRunnable);




        }
    }

    private class NotifyEvaluationReached extends EvaluationListener {

        @Override
        public void breakpointReached(JPDABreakpointEvent event) {
            JPDAThread breakpointThread = event.getThread();
            if (!breakpointThread.equals(engineThread.getThread())) {
                return;
            }
            try {
                notifyEvaluationReachedInEDT();
            } catch (InterruptedException ex) {
                Exceptions.printStackTrace(ex);
            } catch (InvocationTargetException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

    private void notifyEvaluationFinishedInEDT() throws InterruptedException, InvocationTargetException {
        log.log(Level.INFO, "Notify evaluationFinished in EDT {0}", Thread.currentThread());
        Runnable notifyEvaluationFinishedRunnable = new Runnable() {

            @Override
            public void run() {
                notifyEvaluationFinished();
            }
        };
        if (SwingUtilities.isEventDispatchThread()) {
            notifyEvaluationFinishedRunnable.run();
        } else {
            SwingUtilities.invokeAndWait(notifyEvaluationFinishedRunnable);
        }
    }

    private class NotifyEvaluationFinished extends EvaluationListener {

        @Override
        public void breakpointReached(JPDABreakpointEvent event) {
            JPDAThread breakpointThread = event.getThread();
            if (!breakpointThread.equals(engineThread.getThread())) {
                return;
            }
            try {
                notifyEvaluationFinishedInEDT();
            } catch (InterruptedException ex) {
                Exceptions.printStackTrace(ex);
            } catch (InvocationTargetException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

    private class GetPlanListener extends EvaluationListener {

        private final String GET_PLAN_METHOD = "getPoshPlan";
        private String planText;

        @Override
        public synchronized void breakpointReached(JPDABreakpointEvent event) {
            JPDAThread breakpointThread = event.getThread();
            if (!breakpointThread.equals(engineThread.getThread())) {
                return;
            }

            if (planText != null) {
                return;
            }
            String errorMsg = null;
            try {
                planText = getPlan(event);
                log.log(Level.INFO, "Plan recieved: {0}", planText);
                plan = new PoshParser(new StringReader(planText)).parsePlan();
                notifyEnginePlanRecieved();
            } catch (AbsentInformationException ex) {
                errorMsg = "Unable to get the plan from the engine. Engine thread is running or has no callstack.";
            } catch (NoSuchMethodException ex) {
                errorMsg = MessageFormat.format("Unable to get the plan from the engine. No member method String {0}() found.", GET_PLAN_METHOD);
            } catch (InvalidExpressionException ex) {
                errorMsg = MessageFormat.format("Unable to get the plan from the engine. {0}", ex.getMessage());
            } catch (InterruptedException ex) {
                errorMsg = "EDT thread was interrupted while notifying listeners about plan.";
            } catch (InvocationTargetException ex) {
                StringWriter stringWriter = new StringWriter();
                PrintWriter printWriter = new PrintWriter(stringWriter);
                ex.getCause().printStackTrace(printWriter);
                printWriter.flush();
                String stackTrace = stringWriter.toString();
                errorMsg = MessageFormat.format("While notifying listeners about plan, an exception was thrown: {0}: {1}<br/><pre>{2}</pre>", ex.getCause(), ex.getCause().toString(), stackTrace);
            } catch (ParseException ex) {
                errorMsg = MessageFormat.format("Unable to parse plan: {0}, {1}", planText, ex.getMessage());
            } finally {
                if (errorMsg != null) {
                    log.log(Level.SEVERE, errorMsg);
                    disconnectInEDT(errorMsg, true);
                    return;
                }
            }

        }

        private String getPlan(JPDABreakpointEvent jpdabe) throws AbsentInformationException, NoSuchMethodException, InvalidExpressionException {
            JPDAThread thread = jpdabe.getThread();
            This thisVar = getThisVariable(thread);
            return callStringMethod(jpdabe.getDebugger(), thisVar, thread, GET_PLAN_METHOD);
        }

        private void notifyEnginePlanRecieved() throws InterruptedException, InvocationTargetException {
            final String name = getDisplayName(engineThread.getDebugger(), engineThread);
            Runnable notifyRunnable = new Runnable() {

                @Override
                public void run() {
                    notifyPlanRecieved(name, plan);
                }
            };
            if (SwingUtilities.isEventDispatchThread()) {
                notifyRunnable.run();
            } else {
                SwingUtilities.invokeAndWait(notifyRunnable);
            }
        }
    }

    private class ResumeServerListener implements JPDABreakpointListener {

        @Override
        public void breakpointReached(JPDABreakpointEvent event) {
            JPDAThread breakpointThread = event.getThread();
            if (!breakpointThread.equals(engineThread.getThread())) {
                return;
            }
            try {
                ServerManager.resume(YaposhEngine.this, serverAddress);
            } catch (IOException ex) {
                String disconnectMessage = MessageFormat.format("Unable to resume the server {0}", serverAddress.toString());
                disconnectInEDT(disconnectMessage, true);
            }
        }
    }

    /**
     * Listener that makes sure we {@link IDebugEngine#disconnect(java.lang.String, boolean)
     * } when session of the engine is removed.
     */
    private class DisconnectListener extends DebuggerManagerAdapter {

        @Override
        public void sessionRemoved(Session session) {
            if (isDebuggerInSession(session, engineThread.getDebugger())) {
                boolean error = false;
                String disconnectMessage = "The session has been terminated.";
                try {
                    ServerManager.resume(YaposhEngine.this, serverAddress);
                    ServerManager.clear(YaposhEngine.this);
                } catch (IOException ex) {
                    error = true;
                    disconnectMessage = "Unable to resume the server " + serverAddress.toString();
                } finally {
                    disconnectInEDT(disconnectMessage, error);
                }
            }
        }

        private boolean isDebuggerInSession(Session session, JPDADebugger debugger) {
            if (session.getCurrentEngine().lookup(null, JPDADebugger.class).contains(debugger)) {
                return true;
            }

            for (String language : session.getSupportedLanguages()) {
                DebuggerEngine engine = session.getEngineForLanguage(language);
                List<? extends JPDADebugger> engineDebuggers = engine.lookup(null, JPDADebugger.class);
                if (engineDebuggers.contains(debugger)) {
                    return true;
                }
            }
            return false;
        }
    }
}
