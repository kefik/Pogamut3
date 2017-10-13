package cz.cuni.amis.pogamut.sposh.dbg.engine;

import com.sun.jdi.AbsentInformationException;
import java.util.Comparator;
import java.util.List;
import org.netbeans.api.debugger.DebuggerEngine;
import org.netbeans.api.debugger.DebuggerManager;
import org.netbeans.api.debugger.Session;
import org.netbeans.api.debugger.jpda.CallStackFrame;
import org.netbeans.api.debugger.jpda.JPDADebugger;
import org.netbeans.api.debugger.jpda.JPDAThread;
import org.netbeans.api.debugger.jpda.This;
import org.netbeans.api.debugger.jpda.event.JPDABreakpointEvent;

/**
 * This is a useful wrapper class for {@link JPDAThread} that makes dealing with
 * a logic thread of a posh bot much easier.
 *
 * @author HonzaH
 */
public class EngineThread implements Comparable<EngineThread>, Comparator<EngineThread> {

    private static final String ENGINE_THREAD_NAME_SUFFIX = " logic";
    private final JPDADebugger debugger;
    private final JPDAThread engineThread;

    /**
     * Create new engine thread container.
     * @param debugger debugger the thread belongs to
     * @param engineThread thread the engine is using to evalute the plan.
     */
    public EngineThread(JPDADebugger debugger, JPDAThread engineThread) {
        assert engineThread.getName().endsWith(ENGINE_THREAD_NAME_SUFFIX);

        this.debugger = debugger;
        this.engineThread = engineThread;
    }

    /**
     * Get actuall debug {@link JPDAThread thread} the engine is using.
     * @return thread posh engine is using.
     */
    public final JPDAThread getThread() {
        return engineThread;
    }

    /**
     * Debugger from which we got the {@link #engineThread engine thread}.
     * @return debugger of the engine thread
     */
    public final JPDADebugger getDebugger() {
        return debugger;
    }

    /**
     * Get name of the engine running in this thread. Name should be unique to
     * one session. Name is derived from the name of the {@link JPDAThread}
     * without {@link #ENGINE_THREAD_NAME_SUFFIX suffix}.
     *
     * @return Name of the engine thread.
     */
    public final String getName() {
        String name = engineThread.getName();
        int suffixLength = ENGINE_THREAD_NAME_SUFFIX.length();
        int threadNameLength = name.length() - suffixLength;

        return name.substring(0, threadNameLength);
    }

    /**
     * @return this variable of the JPDA thread.
     */
    public This getThisVariable() throws AbsentInformationException {
        CallStackFrame frame = engineThread.getCallStack()[0];
        This thisVar = frame.getThisVariable();

        return thisVar;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final EngineThread other = (EngineThread) obj;
        if (this.engineThread != other.engineThread && (this.engineThread == null || !this.engineThread.equals(other.engineThread))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 41 * hash + (this.engineThread != null ? this.engineThread.hashCode() : 0);
        return hash;
    }

    /**
     * Compare threads using their names.
     *
     * @param other Thread to compare to
     * @return -1, 0, 1 if name of this engine thread is less than, equals or
     * greater than other.
     */
    @Override
    public int compareTo(EngineThread other) {
        if (this.equals(other.engineThread)) {
            return 0;
        }
        return getName().compareTo(other.getName());
    }

    @Override
    public int compare(EngineThread o1, EngineThread o2) {
        return o1.compareTo(o2);
    }
    

    static EngineThread createBreakpointThread(JPDABreakpointEvent jpdabe) {
        return new EngineThread(jpdabe.getDebugger(), jpdabe.getThread());
    }
}
