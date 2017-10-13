package cz.cuni.amis.utils.listener;

import java.lang.ref.WeakReference;
import java.util.EventListener;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import cz.cuni.amis.utils.ExceptionToString;
import cz.cuni.amis.utils.NullCheck;

/**
 * This object is implementing listeners list, where you may store both type of references to 
 * the listeners (strong reference / weak reference).
 * <BR><BR>
 * It takes quite of effort to maintain the list with both strong / weak references,
 * therefore the class was created.
 * <BR><BR>
 * Because we don't know what method we will be calling on the listeners the public
 * interface ListenerNotifier exists. If you want to fire all the listeners, just
 * implement this interface and stuff it to the notify() method of the "Listeners".
 * (This somehow resembles the Stored Procedure pattern...)
 * <BR><BR>
 * Another interface is used for removing listeners. You may want to go through the listeners
 * list and mark some of them to be removed (according to some rule like 'instanceof'). See
 * static interface ListenerRemover.
 * <BR><BR>
 * The class is thread-safe.
 * 
 * @author Jimmy
 */
@SuppressWarnings("hiding")
public class Listeners<Listener extends EventListener> {	
	
	/**
	 * Used to raise the event in the listeners.
	 * 
	 * @author Jimmy
	 *
	 * @param <Listener>
	 */
	public static interface ListenerNotifier<Listener extends EventListener> {
		
		public Object getEvent();
		
		public void notify(Listener listener);
		
	}
	
	public static class AdaptableListenerNotifier<LISTENER extends IListener> implements ListenerNotifier<LISTENER> {

		private Object event;
		
		public AdaptableListenerNotifier<LISTENER> setEvent(Object event) {
			this.event = event;
			return this;
		}
		
		@Override
		public Object getEvent() {
			return event;
		}

		@Override
		public void notify(LISTENER listener) {
			listener.notify(event);
		}
		
	}
	
	/**
	 * Used as a visitor to the listeners that should tell which listeners to remove...
	 * 
	 * @author Jimmy
	 *
	 * @param <Listener>
	 */
	public static interface ListenerRemover {
		 
		/**
		 * If returns true, the 'listener' will be removed from the list (detached).
		 * @param listener
		 * @return
		 */
		public boolean remove(EventListener listener);
		
	}
	
	/**
	 * Abstract class that stores the Listeners and can fire the event when needed.
	 * @author Jimmy
	 */	
    private static abstract class ListenerStore<Listener extends EventListener> {
    	
        /**
         * May return null if listener was gc()ed.
         * <BR><BR>
         * If null is returned, we will remove the entry from the listeners list.
         * @return
         */      
        public abstract Listener getListener();

		public abstract void clearListener();
        
    }

    /**
     * Listener store class referencing the FlagListener with "strong" reference.
     * @author Jimmy
     */
    private static class StrongListenerStore<Listener extends EventListener> extends ListenerStore<Listener> {

        private Listener listener;

        public StrongListenerStore(Listener listener) {
            this.listener = listener;
        }

        @Override
		public Listener getListener() {
            return listener;
        }
        
        @Override
		public void clearListener() {
        	this.listener = null;
        }
		
    }
    
    /**
     * Listener store class referencing the FlagListener with weak reference.
     * @author Jimmy
     *
     * @param <T>
     */
    private static class WeakListenerStore<Listener extends EventListener> extends ListenerStore<Listener> {
        
        private WeakReference<Listener> listenerReference = null;
        
        public WeakListenerStore(Listener listener) {
            this.listenerReference = new WeakReference<Listener>(listener);
        }
        
        @Override
		public Listener getListener() {
            return listenerReference.get();
        }
        
        @Override
		public void clearListener() {
        	this.listenerReference.clear();
        }
        
    }
    
    /**
     * Logger used by this object.
     */
    private Logger log;

    /**
     * ID used to identify particular Listeners instance in the logs.
     */
    private String name;
    
    /**
     * Access to this field is synchronized through this field.
     */
    private ConcurrentLinkedQueue<ListenerStore<Listener>> listeners = new ConcurrentLinkedQueue<ListenerStore<Listener>>(); 
    
    /**
     * Set to true whenever the iteration over listeners occurs and to false in the end.
     * Only if you are the topmost iterator, you may remove listeners from the list!
     */
    private boolean listenersIteration = false;

    /**
     * Returns logger used by this object (null as default).
     * @return
     */
    public Logger getLog() {
		return log;
	}

    /**
     * Sets logger for this object (logger is null by default).
     * @param log
     */
	public void setLog(Logger log, String name) {
		this.log = log;
		this.name = name;
	}

	/**
     * Adds listener with strong reference to it.
     * @param listener
     */
    public void addStrongListener(Listener listener) {
    	NullCheck.check(listener, "listener");
    	synchronized(listeners) {
    		listeners.add(new StrongListenerStore<Listener>(listener));
    	}
    }
    
    /**
     * Adds listener with weak reference to it.
     * @param listener
     */
    public void addWeakListener(Listener listener) {
    	NullCheck.check(listener, "listener");
    	synchronized(listeners) {
    		listeners.add(new WeakListenerStore<Listener>(listener));
    	}
    }
    
    /**
     * Removes all listeners that are equal() to this one.
     * @param listener
     * @return how many listeners were removed
     */
    public int removeEqualListener(EventListener listener) {
    	if (listener == null) return 0;
    	int removed = 0;
    	synchronized(listeners) {
    		
    		boolean listenersIterationOriginal = listenersIteration;
    		listenersIteration = true;
    		
    		try {
	    		Iterator<ListenerStore<Listener>> iterator = listeners.iterator();
	    		while(iterator.hasNext()) {
	    			ListenerStore<Listener> store = iterator.next();
	    			Listener storedListener = store.getListener();
	    			if (storedListener == null) {
	    				if (!listenersIterationOriginal) {
	    					if ((store instanceof WeakListenerStore) && log != null && log.isLoggable(Level.FINE)) {
	    						log.fine((name == null ? "" : name + ": ") + "Weakly referenced listener was GC()ed.");
	    					}
	    					iterator.remove();
	    				}
	    				continue;
	    			}
	    			if (listener.equals(storedListener)) {
	    				store.clearListener();
	    				++removed;
	    			}
	    		}
    		} finally {
    			listenersIteration = listenersIterationOriginal;
    		}
    	}
    	return removed;
    }
    
    /**
     * Removes all listeners that are == to this one (not equal()! must be the same object).
     * @param listener
     * @return how many listeners were removed
     */
    public int removeListener(EventListener listener) {
    	if (listener == null) return 0;
    	int removed = 0;
    	synchronized(listeners) {
    		boolean listenersIterationOriginal = listenersIteration;
    		listenersIteration = true;
    		
    		try {
	    		Iterator<ListenerStore<Listener>> iterator = listeners.iterator();
	    		while(iterator.hasNext()) {
	    			ListenerStore<Listener> store = iterator.next(); 
	    			Listener storedListener = store.getListener();
	    			if (storedListener == null) {
	    				if (!listenersIterationOriginal) {
	    					if ((store instanceof WeakListenerStore) && log != null && log.isLoggable(Level.FINE)) {
	    						log.fine((name == null ? "" : name + ": ") + "Weakly referenced listener was GC()ed.");
	    					}
	    					iterator.remove();
	    				}
	    				continue;
	    			}
	    			if (listener == storedListener) {
	    				store.clearListener();
	    				++removed;
	    			}
	    		}
    		} finally {
    			listenersIteration = listenersIterationOriginal;
    		}
    	}
    	return removed;
    }
    
    /**
     * Calls notifier.notify() on each of the stored listeners, allowing you to execute stored
     * command.
     * 
     * @param notifier
     */
    public void notify(ListenerNotifier<Listener> notifier) {
    	    	
    	synchronized(listeners) {
    		boolean listenersIterationOriginal = listenersIteration;
    		listenersIteration = true;
    		
    		try {
    			Iterator<ListenerStore<Listener>> iterator = listeners.iterator();
	    		while(iterator.hasNext()) {
	    			ListenerStore<Listener> store = iterator.next();
	    			Listener storedListener = store.getListener();
	    			if (storedListener == null) {
	    				if (!listenersIterationOriginal) {
	    					if ((store instanceof WeakListenerStore) && log != null && log.isLoggable(Level.FINE)) {
	    						log.fine((name == null ? "" : name + ": ") + "Weakly referenced listener was GC()ed.");
	    					}
	    					iterator.remove();
	    				}
	    				continue;
	    			}
	    			notifier.notify(storedListener);
	    		}
    		} finally {
    			listenersIteration = listenersIterationOriginal;
    		}
    	}
    }
    
    /**
     * Calls notifier.notify() on each of the stored listeners, allowing you to execute stored
     * command.
     * <p><p>
     * Every notification is run inside try/catch block, exceptions are reported into the log
     * (if not null) and method returns false if some exception is thrown.
     * 
     * @param notifier
     * @param exceptionLog where to log exceptions, may be null
     * @return true, if no exception happened
     */
    public boolean notifySafe(ListenerNotifier<Listener> notifier, Logger exceptionLog) {
    	
    	boolean noException = true;
    	
    	synchronized(listeners) {
    		boolean listenersIterationOriginal = listenersIteration;
    		listenersIteration = true;
    		try {
	    		Iterator<ListenerStore<Listener>> iterator = listeners.iterator();
	    		while(iterator.hasNext()) {
	    			ListenerStore<Listener> store = iterator.next();
	    			Listener storedListener = store.getListener();
	    			if (storedListener == null) {
	    				if (!listenersIterationOriginal) {
	    					if ((store instanceof WeakListenerStore) && log != null && log.isLoggable(Level.FINE)) {
	    						log.fine((name == null ? "" : name + ": ") + "Weakly referenced listener was GC()ed.");
	    					}
	    					iterator.remove();
	    				}
	    				continue;
	    			}
	    			try {
	    				notifier.notify(storedListener);
	    			} catch (Exception e) {
	    				noException = false;
	    				if (exceptionLog != null) {
	    					if (exceptionLog.isLoggable(Level.SEVERE)) exceptionLog.severe(ExceptionToString.process("Exception during event processing (" + notifier.getEvent() + ").", e));
	    				}
	    			}
	    		}
    		} finally {
    			listenersIteration = listenersIterationOriginal;
    		}
    	}
    	
    	return noException;
    }
    
    
    /**
     * Returns true if at least one equals listener to the param 'listener' is found.	 
     * @param listener
     * @return
     */
    public boolean isEqualListening(EventListener listener) {
    	if (listener == null) return false;
    	synchronized(listeners) {
    		boolean listenersIterationOriginal = listenersIteration;
    		listenersIteration = true;
    		
    		try {
	    		Iterator<ListenerStore<Listener>> iterator = listeners.iterator();
	    		while(iterator.hasNext()) {
	    			ListenerStore<Listener> store = iterator.next();
	    			Listener storedListener = store.getListener();
	    			if (storedListener == null) {
	    				if (!listenersIterationOriginal) {
	    					if ((store instanceof WeakListenerStore) && log != null && log.isLoggable(Level.FINE)) {
	    						log.fine((name == null ? "" : name + ": ") + "Weakly referenced listener was GC()ed.");
	    					}
	    					iterator.remove();
	    				}
	    				continue;
	    			}
	    			if (listener.equals(storedListener)) {
	    				return true;
	    			}
	    		}
    		} finally {
    			listenersIteration = listenersIterationOriginal;
    		}
    	}
    	return false;
    }
    
    /**
     * Returns true if at least one == listener to the param 'listener' is found.
     * <BR><BR>
     * Not using equal() but pointer ==.
     * 	 
     * @param listener
     * @return
     */
    public boolean isListening(EventListener listener) {
    	if (listener == null) return false;
    	synchronized(listeners) {
    		boolean listenersIterationOriginal = listenersIteration;
    		listenersIteration = true;
    		
    		try {
	    		Iterator<ListenerStore<Listener>> iterator = listeners.iterator();
	    		while(iterator.hasNext()) {
	    			ListenerStore<Listener> store = iterator.next();
	    			Listener storedListener = store.getListener();
	    			if (storedListener == null) {
	    				if (!listenersIterationOriginal) {
	    					if ((store instanceof WeakListenerStore) && log != null && log.isLoggable(Level.FINE)) {
	    						log.fine((name == null ? "" : name + ": ") + "Weakly referenced listener was GC()ed.");
	    					}
	    					iterator.remove();
	    				}
	    				continue;
	    			}
	    			if (listener == storedListener) {
	    				return true;
	    			}
	    		}
    		} finally {
    			listenersIteration = listenersIterationOriginal;
    		}
    	}
    	return false;
    }
    
    public void clearListeners() {
    	synchronized(listeners) {
    		if (!listenersIteration) {
    			listeners.clear();
    		} else {
    			for (ListenerStore store : listeners) {
    				store.clearListener();
    			}
    		}
    	}
    }
    
    /**
     * Returns count of listners in the list, note that this may not be exact as we store also
     * listeners with weak listeners, but the list will be purged in next opportunity (like raising
     * event, removing listener).
     * <p><p>
     * Beware that, unlike in most collections, this method is
     * <em>NOT</em> a constant-time operation. Because of the
     * asynchronous nature of used queue, determining the current
     * number of elements requires an O(n) traversal.
     * 
     * @return
     */
    public int count() {
    	synchronized(listeners) {
    		int count = 0;
    		for (ListenerStore store : listeners) {
    			if (store.getListener() != null) ++count;
    		}
    		return count;
    	}
    }
    
    /**
     * This will iterate over all of the listeners and do the query "whether the listner should be
     * removed from the Listeners object". 
     * <BR><BR>
     * If 'remover' returns true to the listener, the listener
     * is removed.
     * 
     * @param remover
     */
    public void remove(ListenerRemover remover) {
    	synchronized(listeners) {
    		boolean listenersIterationOriginal = listenersIteration;
    		listenersIteration = true;
    		try {
	    		Iterator<ListenerStore<Listener>> iterator = listeners.iterator();
	    		while(iterator.hasNext()) {
	    			ListenerStore<Listener> store = iterator.next();
	    			Listener storedListener = store.getListener();
	    			if (storedListener == null) {
	    				if (!listenersIterationOriginal) {
	    					if ((store instanceof WeakListenerStore) && log != null && log.isLoggable(Level.FINE)) {
	    						log.fine((name == null ? "" : name + ": ") + "Weakly referenced listener was GC()ed.");
	    					}
	    					iterator.remove();
	    				}
	    				continue;
	    			}
	    			if (remover.remove(storedListener)) {
	    				if (!listenersIterationOriginal) iterator.remove();
	    				else store.clearListener();
	    	 		}
	    		}
    		} finally {
    			listenersIteration = listenersIterationOriginal;
    		}
    	}
    }

}
