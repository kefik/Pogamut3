package cz.cuni.amis.utils.flag;

import java.io.Serializable;

/**
 * Allows you to pass reasons of flag change along with new value of the flag.
 * <p><p><p>
 * This extension of Flag is MAGICAL! And I mean it ... it is correct from the point of view of OOP (asfaik),
 * but the Flag alone is complex class and we're trying to hack another parameter to the setFlag() method!
 * <p><p>
 * Anyway, it works as you would expect it to work ;)
 * 
 * 
 * @author Jimmy
 *
 * @param <TYPE>
 * @param <REASON>
 */
public class ReasonFlag<TYPE, REASON> extends Flag<TYPE> implements IReasonFlag<TYPE, REASON>, Serializable {
	
	class SetInSyncWithReason extends SetInSync {
		
		private REASON reason;
		
		public SetInSyncWithReason(TYPE newValue, REASON reason) {
			super(newValue);
			this.newValue = newValue;
			this.reason = reason;
		}

		@Override
		public void execute(TYPE flagValue) {
			if (
				(newValue == null && flagValue != null)
				||
				!newValue.equals(flagValue)
		       ) {
				if (flag instanceof ImmutableFlag) throw new UnsupportedOperationException("trying to set flag of the immutable flag!");			
				flag.value = newValue;
				flag.notifier.setValue(newValue);
				if (flag instanceof ReasonFlag) {
					((ReasonFlagListener.ReasonFlagListenerNotifier)flag.notifier).setReason(reason);
					flag.listeners.notify(flag.notifier);
					// to clear the reason for normal "DoInSync" to work correctly.
					((ReasonFlagListener.ReasonFlagListenerNotifier)flag.notifier).setReason(null);
				} else {
					flag.listeners.notify(flag.notifier);
				}
			}
		}
		
	}

	
	/**
     * Initialize the flag without 'initialValue', initial value will be null.
     */
    public ReasonFlag() {
    	this(null);    	
    }

    /**
     * Initialize the flag with 'initialValue'.
     * @param initialValue
     */
    public ReasonFlag(TYPE initialValue) {
    	super(initialValue);
    	this.notifier = new ReasonFlagListener.ReasonFlagListenerNotifier<TYPE, REASON>();
    }

	/**
     * Changes the flag and informs all listeners.
     * <p><p>
     * Should not produce any dead-locks even though it is synchronized method.
     * 
     * @param newValue
     * @throws InterruptedRuntimeException if interrupted during the await on the freeze latch
     */
    public void setFlag(TYPE newValue, REASON reasonForChange) {
    	inSyncInner(new SetInSyncWithReason(newValue, reasonForChange), false);
    }
    
    /**
     * Use {@link ReasonFlagListener} to get reasons or simple {@link FlagListener} to only receives
     * new values.
     * <p><p>
     * {@link FlagListener} will be wrapped using {@link ReasonFlagListener.FlagListenerAdapter}
     * <p><p>
     * For more documentation see {@link Flag}
     * 
     * @param listener
     */
    @Override
    public void addStrongListener(FlagListener<TYPE> listener) {
       	super.addStrongListener(listener);
    }
        
    /**
     * Use {@link ReasonFlagListener} to get reasons or simple {@link FlagListener} to only receives
     * new values.
     * <p><p>
     * {@link FlagListener} will be wrapped using {@link ReasonFlagListener.FlagListenerAdapter}
     * <p><p>
     * For more documentation see {@link Flag}. 
     * <p><p>
     * <b>WARNING:</b>The listener is stored via weak-reference!
     * 
     * @param listener
     */
    @Override
    public void addListener(FlagListener<TYPE> listener) {
       	super.addListener(listener);
    }

    //
    //
    // TESTING OF THE CLASS!!!
    //
    //
    
    private static enum DummyReason {
    	NEW,
    	UPDATE,
    	FAILURE;
    }
    
    /**
     * TEST METHOD! 
     * <p><p>
     * Test by eye :-(
     */
    public static void main(String[] args) {
    	
    	ReasonFlag<Boolean, DummyReason> flag = new ReasonFlag<Boolean, DummyReason>(false);
    	
    	ReasonFlagListener<Boolean, DummyReason> listener1 = new ReasonFlagListener<Boolean, DummyReason>() {

			@Override
			public void flagChanged(Boolean changedValue, DummyReason reason) {
				System.out.println("1 -> " + changedValue + " because " + reason);
			}

			@Override
			public void flagChanged(Boolean changedValue) {
				System.out.println("1 -> " + changedValue);				
			}
    		
    	};
    	
    	ReasonFlagListener<Boolean, DummyReason> listener2 = new ReasonFlagListener<Boolean, DummyReason>() {

			@Override
			public void flagChanged(Boolean changedValue, DummyReason reason) {
				System.out.println("2 -> " + changedValue + " because " + reason);
			}

			@Override
			public void flagChanged(Boolean changedValue) {
				System.out.println("2 -> " + changedValue);				
			}
    		
    	};
    	
    	FlagListener<Boolean> listener3 = new FlagListener<Boolean>() {

			@Override
			public void flagChanged(Boolean changedValue) {
				System.out.println("3 -> " + changedValue);				
			}
    		
    	};
    	
    	flag.addListener(listener1);
    	flag.addListener(listener2);
    	flag.addListener(listener3);
    	
    	flag.setFlag(true);
    	
    	flag.setFlag(false, DummyReason.UPDATE);
    	
    	flag.setFlag(true);
    	
    	flag.setFlag(false, DummyReason.FAILURE);	
    }

         /**
     * @return Immutable version of this flag, setFlag(T) method of such
     * a flag will raise an exception.
     */
    @Override
    public ImmutableReasonFlag<TYPE, REASON> getImmutable() {
        if (immutableWrapper == null) {
            immutableWrapper = new ImmutableReasonFlag<TYPE, REASON>(this);
        }
        return (ImmutableReasonFlag)immutableWrapper;
    }
	
}
