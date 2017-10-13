package cz.cuni.amis.pogamut.sposh.dbg.engine;

import com.sun.jdi.AbsentInformationException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.NoSuchElementException;
import org.netbeans.api.debugger.DebuggerEngine;
import org.netbeans.api.debugger.DebuggerManager;
import org.netbeans.api.debugger.Session;
import org.netbeans.api.debugger.jpda.CallStackFrame;
import org.netbeans.api.debugger.jpda.InvalidExpressionException;
import org.netbeans.api.debugger.jpda.JPDADebugger;
import org.netbeans.api.debugger.jpda.JPDAThread;
import org.netbeans.api.debugger.jpda.MethodBreakpoint;
import org.netbeans.api.debugger.jpda.This;
import org.netbeans.api.debugger.jpda.Variable;
import org.netbeans.api.debugger.jpda.event.JPDABreakpointEvent;
import org.netbeans.api.debugger.jpda.event.JPDABreakpointListener;

/**
 * Useful abstarct class extending {@link JPDABreakpointListener} that includes various useful methods used by the Dash.
 *
 * @author HonzaH
 */
public abstract class EvaluationListener implements JPDABreakpointListener {

    /**
     * Get name for this lap engine, create it from the session and thread
     * names. It is in form thread_session_nam '/' (thread_name % " logic")
     *
     * @param debugger Debugger where the engine is being debugged
     * @param thread Thread where the engine is running.
     * @return assembled name
     */
    public final String getDisplayName(JPDADebugger debugger, EngineThread thread) {
        String sessionName = getSession(debugger).getName();
        String engineName = thread.getName();

        return sessionName + '/' + engineName;
    }

    /**
     * @return this variable of the JPDA thread.
     */
    public final This getThisVariable(JPDAThread thread) throws AbsentInformationException {
        CallStackFrame frame = thread.getCallStack()[0];
        This thisVar = frame.getThisVariable();

        return thisVar;
    }

    /**
     * Get session that is using passed debugger.
     *
     * @return found session
     * @throws NoSuchElementException if unable to find session for the debugger
     */
    public static Session getSession(JPDADebugger debugger) {
        assert debugger != null;
        Session[] sessions = DebuggerManager.getDebuggerManager().getSessions();

        for (Session session : sessions) {
            // It seems that there are sessions with no supported languages, 
            // but valid current engine
            DebuggerEngine currentEngine = session.getCurrentEngine();
            if (currentEngine.lookup(null, JPDADebugger.class).contains(debugger)) {
                return session;
            }

            for (String lang : session.getSupportedLanguages()) {
                List<? extends JPDADebugger> sessionDebuggers =
                        session.getEngineForLanguage(lang).lookup(null, JPDADebugger.class);

                if (sessionDebuggers.contains(debugger)) {
                    return session;
                }
            }
        }
        throw new NoSuchElementException();
    }

    /**
     * Set some thread to be current, but w/o firing property. If the property
     * would be fired, the editor would focus on the frame of the current
     * thread.
     */
    protected final void setCurrentThread(JPDADebugger dbg, JPDAThread thread) {
        try {
            Method m = dbg.getClass().getDeclaredMethod("setCurrentThreadNoFire", JPDAThread.class);
            boolean accessible = m.isAccessible();
            m.setAccessible(true);
            m.invoke(dbg, thread);
            m.setAccessible(accessible);
        } catch (Exception ex) {
            throw new RuntimeException("Fail to invoke method", ex);
        }
    }

    /**
     * Call some method in the debuggee that is without parameters and returns a
     * string.
     */
    protected final String callStringMethod(JPDADebugger debugger, This thisVar, JPDAThread thread, String methodName) throws NoSuchMethodException, InvalidExpressionException {
        // Signature for parameterless method returning string
        String signature = "()Ljava/lang/String;";
        // REMEMBER TO SET CURRENT BACK!!
        JPDAThread oldThread = debugger.getCurrentThread();
        setCurrentThread(debugger, thread);

        Variable res = thisVar.invokeMethod(methodName, signature, new Variable[0]);

        // set current thread back
        setCurrentThread(debugger, oldThread);

        String resultString = res.getValue();
        return resultString.substring(1, resultString.length() - 1);
    }

    /**
     * Create new java breakpoint in the class of action primitive
     *
     * @param stateClass FQN of primitive state class
     * @param breakpointType Type of breakpoint, either {@link MethodBreakpoint#TYPE_METHOD_ENTRY}
     * or {@link MethodBreakpoint#TYPE_METHOD_EXIT}.
     */
    protected final MethodBreakpoint createSelfRemoveBreakpoint(String stateClass, String methodName, int breakpointType) {
        final MethodBreakpoint bp = MethodBreakpoint.create();
        bp.setClassFilters(new String[]{stateClass});
        bp.setMethodName(methodName);
        //bp.setMethodSignature("(Lcz/cuni/amis/pogamut/sposh/engine/VariableContext;)Ljava/lang/Boolean;");
        bp.setHidden(false);
        bp.setBreakpointType(breakpointType);

        bp.addJPDABreakpointListener(new JPDABreakpointListener() {

            @Override
            public void breakpointReached(JPDABreakpointEvent jpdabe) {
                bp.removeJPDABreakpointListener(this);
                DebuggerManager.getDebuggerManager().removeBreakpoint(bp);
            }
        });
        return bp;
    }
}
