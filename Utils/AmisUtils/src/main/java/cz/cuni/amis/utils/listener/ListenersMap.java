package cz.cuni.amis.utils.listener;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * Map of the listeners according to some key.
 * <br><br>
 * Comes in handy when you need the map of Listeners&lt;? extends Listener&gt; and work
 * with them (otherwise you're doomed to write tedious and ugly for-cycles forever).
 * <br><br>
 * And believe me, you don't want to parametrize the class more further the 'Key'...
 * ... just remember it must be used with instances of 'Listener' and Java-generics are hell.
 *  
 * 
 * @author Jimmy
 */
@SuppressWarnings("unchecked")
public class ListenersMap<Key> {

	/**
	 * Map of the listeners, key is the event class where the listener is hooked to.
	 */
	private Map<Key, Listeners<IListener>> listenersMap = 
	 				new ConcurrentHashMap<Key, Listeners<IListener>>();
	
	private int listenersCount = 0;
	
	private Logger log;
	
	private String name;
	
	public Logger getLog() {
		return log;
	}
	
	private void setListenersLog(Key key, Listeners listeners) {
		listeners.setLog(log, name+key);
	}

	public void setLog(Logger log, String name) {
		synchronized(listenersMap) {
			this.log = log;
			this.name = name;
			if (this.name == null) {
				this.name = "ListenersMap-";
			} else {
				this.name += "-";
			}
			for (Entry<Key, Listeners<IListener>> entry : listenersMap.entrySet()) {
				setListenersLog(entry.getKey(), entry.getValue());
			}
		}
	}

	public void add(Key key, IListener listener) {
		synchronized(listenersMap) {
			Listeners<IListener> listeners = listenersMap.get(key);
			if (listeners == null) {
				listeners = new Listeners<IListener>();
				setListenersLog(key, listeners);
				listenersMap.put(key, listeners);
			}
			listeners.addWeakListener(listener);
			++listenersCount;
		}
	}
	
	public boolean isListening(IListener listener) {
		for (Key key : listenersMap.keySet()) {
			if (isListening(key, listener)) return true;
		}
		return false;
	}
	
	public boolean isListening(Key key, IListener listener) {
		Listeners<IListener> listeners = listenersMap.get(key);
		if (listeners == null) return false;
		return listeners.isEqualListening(listener);
	}
	
	public void remove(IListener listener) {
		for (Key key : listenersMap.keySet()) {
			remove(key, listener);
		}			
	}
	
	public void remove(Key key, IListener listener) {
		Listeners<IListener> listeners = listenersMap.get(key);
		if (listeners == null) return;
		listenersCount -= listeners.removeListener(listener);
		if (listeners.count() == 0) {
			synchronized(listenersMap) {
				if (listeners.count() == 0) {
					listenersMap.remove(key);
				}
			}
		}
	}
	
	public void notify(Listeners.ListenerNotifier<IListener> notifier) {
		for (Key key : listenersMap.keySet()) {
			notify(key, notifier);
		}			
	}
	
	public void notify(Key key, Listeners.ListenerNotifier<IListener> notifier) {
		Listeners<IListener> listeners = listenersMap.get(key);
		if (listeners == null) return;
		listeners.notify(notifier);
	}
	
	public boolean notifySafe(Listeners.ListenerNotifier<IListener> notifier, Logger exceptionLog) {
		boolean noException = true;
		for (Key key : listenersMap.keySet()) {
			noException = notifySafe(key, notifier, exceptionLog) && noException;
		}			
		return noException;
	}
	
	public boolean notifySafe(Key key, Listeners.ListenerNotifier<IListener> notifier, Logger exceptionLog) {
		Listeners<IListener> listeners = listenersMap.get(key);
		if (listeners == null) return true;
		return listeners.notifySafe(notifier, exceptionLog);
	}
	
	/**
	 * Notice that "hasListeners" may report true even if there are no listeners registered here (because 
	 * of weak references). But it will usually return correct value.
	 * @return
	 */
	public boolean hasListeners() {
		return listenersCount > 0;
	}
	
}
