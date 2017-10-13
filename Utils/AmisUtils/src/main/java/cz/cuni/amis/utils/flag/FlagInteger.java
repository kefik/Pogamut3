package cz.cuni.amis.utils.flag;

import java.io.Serializable;

/**
 * This class is cruicial in order to have synchronized increments on the integer flag.
 * <p><p>
 * The .setFlag() mechanism is very complex and it is sometimes not viable to do .setFlag() call
 * inside synchronized statements (deadlocks!) therefore we have to solve it inside the class.
 * 
 * @author Jimmy
 *
 */
public class FlagInteger extends Flag<Integer> implements Serializable {
	
	/**
	 * This class extends the DoInSync of the reason that was passed along.
	 */
	public static abstract class DoInSyncWithReason<T, R> extends DoInSync<T> {
		
		/**
		 * Tells you whether you operate over immutable flag (can't call setFlag() then) or not.
		 * @return
		 */
		protected boolean isImmutable() {
			return flag instanceof ImmutableFlag;
		}
		
		/**
		 * Set value in sync.
		 */
		protected void setFlag(T value, R reason) {
			if (flag instanceof ImmutableFlag) throw new UnsupportedOperationException("trying to set flag of the immutable flag!");
			flag.value = value;
			flag.notifier.setValue(value);
			flag.listeners.notify(flag.notifier);			
		}
		
		protected T getFlag() {
			return flag.getFlag();
		}
		
		/**
		 * @param flag
		 */
		public abstract void execute(T flagValue);
		
		/**
		 * @param flag
		 */
		public abstract void execute(T flagValue, R reason);
		
	}
	
	public FlagInteger() {
		super(0);
	}
	
	public FlagInteger(Integer initial) {
		super(initial);
	}
	
	public void increment(final int number) {
		inSync(
			new DoInSync<Integer>() {
	
				@Override
				public void execute(Integer flagValue) {
					setFlag(flagValue+number);
				}
				
			}
		);
	}
	
	public void decrement(final int number) {
		inSync(
			new DoInSync<Integer>() {
	
				@Override
				public void execute(Integer flagValue) {
					setFlag(flagValue-number);
				}
				
			}
		);
	}

}