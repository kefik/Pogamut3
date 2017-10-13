package cz.cuni.amis.utils.flag;

import java.util.EventListener;

import cz.cuni.amis.utils.listener.Listeners;

public interface FlagListener<T> extends EventListener {
	
	public static class FlagListenerNotifier<T> implements Listeners.ListenerNotifier<FlagListener<T>> {
		
		T value;
		
		public FlagListenerNotifier() {			
		}
		
		public FlagListenerNotifier(T changedValue) {
			this.value = changedValue;
		}
		
		public void setValue(T value) {
			this.value = value;
		}

		@SuppressWarnings("unchecked")
		@Override
		public void notify(FlagListener<T> listener) {
			listener.flagChanged(value);
		}

		@Override
		public Object getEvent() {
			return value;
		}
		
	}
	
	public void flagChanged(T changedValue);
}
