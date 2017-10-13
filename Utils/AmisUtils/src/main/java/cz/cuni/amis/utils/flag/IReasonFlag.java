package cz.cuni.amis.utils.flag;

import java.io.Serializable;

/**
 * Allows you to pass reasons of flag change along with new value of the flag.
 * <p><p>
 * Note that implementation must be {@link Serializable}
 * 
 * @author ik
 */
public interface IReasonFlag<TYPE, REASON> extends IFlag<TYPE>, Serializable {

    /**
     * Changes the flag and informs all listeners.
     * <p><p>
     * Should not produce any dead-locks even though it is synchronized method.
     *
     * @param newValue
     * @throws InterruptedRuntimeException if interrupted during the await on the freeze latch
     */
    public void setFlag(TYPE newValue, REASON reasonForChange);
}
