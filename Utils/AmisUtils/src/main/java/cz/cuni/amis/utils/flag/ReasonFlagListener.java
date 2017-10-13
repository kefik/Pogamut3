package cz.cuni.amis.utils.flag;

import cz.cuni.amis.utils.listener.Listeners;

/**
 * Listener for {@link ReasonFlag}. It allows to sense reasons of the changes. It provides
 * a simple message passing mechanism along with changes in the flag.
 * <p><p>
 * For instance, it allows us to send reason why the Mediator has been shut down. 
 * 
 * @author Jimmy
 *
 * @param <TYPE>
 * @param <REASON>
 */
public interface ReasonFlagListener<TYPE, REASON> extends FlagListener<TYPE> {
	
	/**
	 * Notifier for the ReasonFlagListener.
	 * 
	 * @author Jimmy
	 *
	 * @param <T>
	 */
	public static class ReasonFlagListenerNotifier<T, R> extends FlagListener.FlagListenerNotifier<T> {
		
		private R reason;
		
		public ReasonFlagListenerNotifier() {
			super();
		}
		
		public ReasonFlagListenerNotifier(T changedValue) {
			super(changedValue);
		}
		
		public ReasonFlagListenerNotifier(T changedValue, R reasonForChange) {
			super(changedValue);
			this.reason = reasonForChange;
		}
		
		public void setReason(R reason) {
			this.reason = reason;
		}
		
		/**
		 * If ReasonFlagListener is passed into the method, it is redirected into appropriate method.
		 */
		@Override
		public void notify(FlagListener<T> listener) {
			if (listener instanceof ReasonFlagListener) {
				notify((ReasonFlagListener)listener);
			} else {
				super.notify(listener);
			}
		}

		public void notify(ReasonFlagListener<T, R> listener) {
			if (reason == null) {
				// if there is no reason specified, call simple flagChanged
				listener.flagChanged(value);
			} else {
				// if the reason is specified, call appropriate method
				listener.flagChanged(value, reason);
			}
		}
		
	}

	/**
	 * Adapter for the simple FlagListener that does not care about reasons (the reason is masked
	 * during the call...)
	 * 
	 * @author Jimmy
	 *
	 * @param <T>
	 * @param <R>
	 */
	public static class FlagListenerAdapter<T, R> implements ReasonFlagListener<T, R> {
		
		private FlagListener<T> listener;

		public FlagListenerAdapter(FlagListener<T> listener) {
			this.listener = listener;
		}

		@Override
		public void flagChanged(T changedValue, R reason) {
			listener.flagChanged(changedValue);			
		}

		@Override
		public void flagChanged(T changedValue) {
			listener.flagChanged(changedValue);
		}
		
	}

	/**
	 * This method is called whenever the flag has changed its value and the changer
	 * also specified a reason for this change.
	 * @param changedValue
	 * @param reason
	 */
	public void flagChanged(TYPE changedValue, REASON reason);
	
}
