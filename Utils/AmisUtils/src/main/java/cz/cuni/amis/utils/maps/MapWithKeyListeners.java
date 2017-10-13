package cz.cuni.amis.utils.maps;

import java.util.HashMap;
import java.util.Map;

import cz.cuni.amis.utils.listener.Event;
import cz.cuni.amis.utils.listener.IListener;
import cz.cuni.amis.utils.listener.Listeners;
import cz.cuni.amis.utils.listener.ListenersMap;

public class MapWithKeyListeners<KEY, VALUE> {
	
	public static class KeyCreatedEvent<KEY, VALUE> implements Event {
		
		private KEY key;
		private VALUE value;
		
		public KeyCreatedEvent(KEY key, VALUE value) {
			this.key = key;
			this.value = value;
		}

		public KEY getKey() {
			return key;
		}

		public void setKey(KEY key) {
			this.key = key;
		}

		public VALUE getValue() {
			return value;
		}

		public void setValue(VALUE value) {
			this.value = value;
		}
		
	}
	
	public static interface IKeyCreatedListener<KEY, VALUE> extends IListener<KeyCreatedEvent<KEY, VALUE>> {
	};
	
	public static class KeyCreatedEventListenerNotifier<KEY, VALUE> implements Listeners.ListenerNotifier<IListener> {

		public KeyCreatedEvent<KEY, VALUE> event;
		
		@Override
		public Object getEvent() {
			return event;
		}

		@Override
		public void notify(IListener listener) {
			listener.notify(event);
		}
				
	}

	private Map<KEY, VALUE> map = new HashMap<KEY, VALUE>();
	
	private ListenersMap<KEY> listeners = new ListenersMap<KEY>();
	
	private KeyCreatedEventListenerNotifier<KEY, VALUE> notifier = new KeyCreatedEventListenerNotifier<KEY, VALUE>();
	
	public void put(KEY key, VALUE value) {
		VALUE oldValue = null;
		synchronized(map) {
			oldValue = map.put(key, value);
		}
		if (oldValue == null) {
			notifier.event = new KeyCreatedEvent<KEY, VALUE>(key, value);
			listeners.notify(key, notifier);
		}
	}
	
	public void remove(KEY key) {
		synchronized(map) {
			map.remove(key);
		}
	}
	
	public void addWeakListener(KEY key, IKeyCreatedListener<KEY, VALUE> listener) {
		listeners.add(key, listener);
	}
	
	public boolean isListening(KEY key, IKeyCreatedListener<KEY, VALUE> listener) {
		return listeners.isListening(key, listener);
	}
	
	public void removeListener(KEY key, IKeyCreatedListener<KEY, VALUE> listener) {
		listeners.remove(key, listener);
	}
	
}
