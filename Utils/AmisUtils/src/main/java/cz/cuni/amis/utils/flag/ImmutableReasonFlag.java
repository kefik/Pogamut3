package cz.cuni.amis.utils.flag;

import java.io.Serializable;

/**
 * Reason flag whose value cannot be set.
 * @author ik
 */
public class ImmutableReasonFlag<T, R> extends ImmutableFlag<T> implements IReasonFlag<T, R>, Serializable {

    public ImmutableReasonFlag(IReasonFlag<T, R> flag) {
        super(flag);
    }

    @Override
    public void setFlag(T newValue, R reasonForChange) {
    	// TODO: immutable???
        setFlag(newValue);
    }
}
