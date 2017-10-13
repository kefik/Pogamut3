package cz.cuni.sposh.debugger;

import java.util.Arrays;
import org.netbeans.api.debugger.Breakpoint;
import org.netbeans.api.debugger.DebuggerManager;
import org.netbeans.api.debugger.jpda.MethodBreakpoint;
import org.netbeans.api.debugger.jpda.event.JPDABreakpointListener;

/**
 * Simple manager that is managing class of {@link MethodBreakpoint} that share
 * common properties(same method and breakpoint group). It can easily add it,
 * remove previously created instances and add or remove listeners.
 *
 * @author HonzaH
 */
public class BreakpointManager {

    private final String[] classFilter;
    private final String methodName;
    private final String methodSignature;
    private final String breakpointGroup;
    private MethodBreakpoint methodBreakpoint;

    /**
     * Create new manager for specified breakpoint type.
     *
     * @param classFQN fully qualified name of class with method
     * @param methodName name of the method
     * @param methodSignature signature of the method
     * @param breakpointGroup name of breakpoint group.
     */
    private BreakpointManager(String classFQN, String methodName, String methodSignature, String breakpointGroup) {
        this.classFilter = new String[]{classFQN};
        this.methodName = methodName;
        this.methodSignature = methodSignature;
        this.breakpointGroup = breakpointGroup;
    }
    /**
     * fqn of posh engine class which has the all important evaluation method
     */
    public static final String LAP_ENGINE_CLASS = "cz.cuni.amis.pogamut.sposh.engine.PoshEngine"; // NOI18N
    /**
     * name of evaluation method in the {@link WatchLapAction#ENGINE_CLASS}
     */
    public static final String LAP_METHOD_NAME = "evaluatePlan"; // NOI18N
    /**
     * signature of evaluation method
     */
    public static final String LAP_METHOD_SIGNATURE = "(Lcz/cuni/amis/pogamut/sposh/executor/IWorkExecutor;)Lcz/cuni/amis/pogamut/sposh/engine/PoshEngine$EvaluationResultInfo;"; // NOI18N

    /**
     * Create a manager at the entry into the {@link #LAP_ENGINE_CLASS} {@link #LAP_METHOD_NAME}
     * that has specified group.
     *
     * @param breakpointGroup Breakpoint group of the manager.
     * @return created manager
     */
    public static BreakpointManager createLapEvaluation(String breakpointGroup) {
        return new BreakpointManager(LAP_ENGINE_CLASS, LAP_METHOD_NAME, LAP_METHOD_SIGNATURE, breakpointGroup);
    }

    /**
     * Create a breakpoint that matches properties (class filter, name,
     * signature, group) of the manager.
     *
     * @return Created breakpoint
     */
    private MethodBreakpoint createBreakpoint() {
        MethodBreakpoint bp = MethodBreakpoint.create();
        bp.setClassFilters(classFilter);
        bp.setMethodName(methodName);
        bp.setMethodSignature(methodSignature);
        bp.setSuspend(MethodBreakpoint.SUSPEND_NONE);
        bp.setGroupName(breakpointGroup);

        return bp;
    }

    /**
     * Each manager must have some breakpoint into which it adds listeners ect.
     * This method returns the breakpoint that satisfies properties of the
     * manager.
     *
     * @return Get breakpoint of this manager.
     */
    public synchronized MethodBreakpoint getBreakpoint() {
        if (methodBreakpoint == null) {
            methodBreakpoint = createBreakpoint();
            DebuggerManager.getDebuggerManager().addBreakpoint(methodBreakpoint);
        }
        return methodBreakpoint;
    }

    /**
     * Addd listener to the breakpoint of the manager.
     *
     * @param listener
     */
    public void addListener(JPDABreakpointListener listener) {
        getBreakpoint().addJPDABreakpointListener(listener);
    }

    /**
     * Remove listener from the breakpoint of the manager
     *
     * @param listener
     */
    public void removeListener(JPDABreakpointListener listener) {
        getBreakpoint().removeJPDABreakpointListener(listener);
    }

    /**
     * Has passed breakpoint properties specified by the manager?
     *
     * @param mbp tested breakpoint
     * @return true if breakpoint is managed by the manager, false otherwise.
     */
    public boolean isQualified(MethodBreakpoint mbp) {
        boolean sameGroup = breakpointGroup.equals(mbp.getGroupName());
        if (sameGroup) {
            boolean sameClassFilter = Arrays.equals(classFilter, mbp.getClassFilters());
            boolean sameMethodName = methodName.equals(mbp.getMethodName());
            boolean sameMethodSignature = methodSignature.equals(mbp.getMethodSignature());
            if (sameClassFilter && sameMethodName && sameMethodSignature) {
                return true;
            }
        }
        return false;
    }

    /**
     * Remove all breakpoints that have properties specified by this manager.
     *
     * @return number of breakpoints that were removed
     */
    public synchronized int purge() {
        DebuggerManager debuggerManager = DebuggerManager.getDebuggerManager();

        int removedBreakpoints = 0;
        for (Breakpoint bp : debuggerManager.getBreakpoints()) {
            if (bp instanceof MethodBreakpoint) {
                MethodBreakpoint testedBreakpoint = (MethodBreakpoint) bp;

                if (isQualified(testedBreakpoint)) {
                    debuggerManager.removeBreakpoint(testedBreakpoint);
                    ++removedBreakpoints;
                }
            }
        }
        // the breakpoint has been deleted in the loop
        methodBreakpoint = null;
        return removedBreakpoints;
    }
}
