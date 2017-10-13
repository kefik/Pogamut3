package cz.cuni.amis.utils.listener;

import java.util.EventListener;

import cz.cuni.amis.utils.listener.Listeners.ListenerNotifier;

/**
 * Basic listener interface allowing the object to receive 'Events'.
 * @author Jimmy
 *
 * @param <T>
 */
public interface IListener<T> extends EventListener {

	public void notify(T event);
	
	/**
	 * Convenient class for notifying about events using {@link Listeners} or {@link ListenersMap}.
	 * @author Jimmy
	 *
	 * @param <EVENT>
	 * @param <LISTENER>
	 */
	public static class Notifier<LISTENER extends IListener> implements ListenerNotifier<LISTENER> {

		private Object event;

		public Notifier(Object event) {
			this.event = event;
		}
		
		@Override
		public Object getEvent() {
			return event;
		}
		
		public void setEvent(Object event) {
			this.event = event;
		}

		@Override
		public void notify(LISTENER listener) {
			listener.notify(event);
		}
		
	}

}
