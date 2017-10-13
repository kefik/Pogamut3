package cz.cuni.amis.utils.flag;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import cz.cuni.amis.utils.exception.PogamutException;
import cz.cuni.amis.utils.exception.PogamutIOException;
import cz.cuni.amis.utils.exception.PogamutInterruptedException;
import cz.cuni.amis.utils.listener.Listeners;

/**
 * This class may be used to create an observable value (you may attach change-listeners to it).
 * <p><p> 
 * This is flag class which is designed for Boolean or Integer types (but 
 * it should work with other types as well as long as they have equals() implemented
 * correctly).
 * <p><p>
 * It allows you to store the state of flag and register listeners on the flag.
 * <p><p>
 * Note that the implementation is:
 * <ol>
 * <li>thread-safe (truly),</li>
 * <li>recursion-safe (meaning that the flag may be changed from within the listener it notifies of the flag changes - such events are put into the queue and processed in correct sequence),</li>
 * <li>setters/getters are non-blocking (or they blocks for finite small amount of time ~ few synchronized statements used, which can't block each other for greater period of time).</li>
 * </ol>
 * <p><p>
 * Also note that can't be a really correct implementation of the flag that always returns
 * the right value - if you heavily use flag from let's say a tens of threads then there may
 * be glitches in the getFlag() returned value (but for the most implementation this value
 * will be correct in 99.99999%!).
 * <p><p>
 * Note that the implementation of notifying about flag-change is
 * strictly time-ordered. Every flag-change event is fully processed before another is raised/received.
 * There is a possibility that a listener on the flag change will attempt to change the flag again (mentioned recursion-safe).
 * In that case processing of this flag change is postponed until the previous event has been fully processed
 * (e.g. all listeners has been notified about it).
 * <p><p>
 * Last piece of magic - if you want to change the flag value in-sync (meaning that you need 100% safe reading of the flag value), instantiate Flag.DoInSync<T> class
 * and submit it via inSync() method - {@link DoInSync#execute(Object)} method will be executed in synchronized state so no one can change the flag value
 * while you're inside this method. 
 * 
 * @author Jimmy
 *
 * @param <T> type of the flag
 */
public class Flag<T> implements IFlag<T>, Serializable {

	/**
	 * Usage of this abstract class is as simple as it could be ... all you have to do is to instantiate
	 * it (using anonymous objects).<p>
	 * <p>
	 * Example:<p><p>
	 * <code>
	 * Flag&lt;Integer> flag = new Flag&lt;Integer>(10);<p>
	 * flag.inSync(
	 *     new Flag.DoInSync&lt;Integer>(flag) {<p>
	 *         public abstract void execute(Integer flagValue) {<p>
	 *             setFlag(flagValue+1);<p>
	 *         }<p>
	 *     }
	 * );<p>
	 * </code>
	 * <p><p>
	 * No need to do anything else! The class will submit itself to the flag upon construction.
	 * <p><p>
	 * Use it to create correct counters (or use directly FlagInteger class).
	 * 
	 * @author Jimmy
	 *
	 * @param <T>
	 */
	public static abstract class DoInSync<T> {
		
		Flag<T> flag;
		
		public DoInSync() {
		}
		
		void setFlagInstance(Flag<T> flag) {
			this.flag = flag;
		}
		
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
		protected void setFlag(T value) {
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
		
	}
	
	class SetInSync extends DoInSync<T> {
		
		T newValue;
		
		public SetInSync(T newValue) {
			this.newValue = newValue;
		}

		@Override
		public void execute(T flagValue) {
			if (
				(newValue == null && flagValue != null)
				||				
				(newValue != null && !newValue.equals(flagValue))
		       ) {
				setFlag(newValue);				
			}
		}
		
	}
		
	transient Listeners<FlagListener<T>> listeners = new Listeners<FlagListener<T>>();
	
	/**
	 * Do not read directly - always use getFlag() method.
	 */
    T value;
    
    transient FlagListener.FlagListenerNotifier<T> notifier = new FlagListener.FlagListenerNotifier<T>();
    
    /**
     * Mutex that we synchronized on when the result of getValue() should be changed.
     */
    transient Object setMutex = new Object();
    
    /**
     * Whether the set method is freezed.
     */
    transient boolean setFreezed = false;
    
    transient Object setFreezedMutex = new Object();
    
    transient Semaphore setFreezedSemaphore = new Semaphore(1);
    
    /**
     * If availablePermits() == 1, the queue is not being processed by the method processSetFlagQueue()
     */
    transient Semaphore commandQueueProcessing = new Semaphore(1);
    
    transient Object commandQueueProcessingMutex = new Object();
    
    transient List<DoInSync<T>> commandQueue = new LinkedList<DoInSync<T>>();    
    
    /** Immutable version of this flag. */
    transient ImmutableFlag<T> immutableWrapper = null;

    /**
     * Initialize the flag with 'null' as an initial value.
     */
    public Flag() {
        value = null;
    }

    /**
     * Initialize the flag with 'initialValue'.
     * @param initialValue
     */
    public Flag(T initialValue) {
        value = initialValue;
    }
    
    /**
     * Method honoring the de-serialization process, it correctly initializes all
     * that needs to be.
     * 
     * @return
     */
    private void readObject(ObjectInputStream ois) {
    	try {
			ois.defaultReadObject();
		} catch (IOException e) {
			throw new PogamutIOException("Could not read Flag", e);
		} catch (ClassNotFoundException e) {
			throw new PogamutException("Could not deserialize Flag", e);
		}
    	this.listeners = new Listeners<FlagListener<T>>();
    	this.notifier = new FlagListener.FlagListenerNotifier<T>();
        this.setMutex = new Object();
        this.setFreezed = false;
        this.setFreezedMutex = new Object();
        this.setFreezedSemaphore = new Semaphore(1);
        this.commandQueueProcessing = new Semaphore(1);
        this.commandQueueProcessingMutex = new Object();
        this.commandQueue = new LinkedList<DoInSync<T>>();    
        this.immutableWrapper = null;        
    }
    
    /**
     * Unsychronized!
     * <p><p>
     * setFlagProcessing must be acquired before calling! It will be released by this method.
     */
    private void processCommandQueue() {
    	// check the events list size, do we have more events to process?
	    // note that the implementation is tricky ... try to think it over before modifying
        while(true) {
        	DoInSync<T> command = null;        	
        	synchronized(commandQueue) {        		
	        	if (commandQueue.size() != 0) {
	        		command = commandQueue.get(0);
	        		commandQueue.remove(0);
	        	}
        	}
        	if (command != null) {
        		command.setFlagInstance(this);
        		command.execute(value);
        	}
        	synchronized(commandQueueProcessingMutex) {
        		if (commandQueue.size() == 0) {
        			commandQueueProcessing.release();
        			return;
        		}
        	}
        }    	
    }
    
    /**
     * Add a command that will be executed in-sync with other changes (you may be sure that no other changes
     * are taking place right now).
     * <p><p>
     * This is also used by the setFlag() method.
     * @param command
     * @param addAsFirst if true the command will be added as a first to execute
     */
    protected void inSyncInner(DoInSync<T> command, boolean addAsFirst) {
    	// we're going to change the result of getValue() method, synchronized on it
    	synchronized(setMutex) {
    		// we will modify the setFlagValues
    		synchronized(commandQueue) {
    			// we're going to query setFlagProcessing
    			synchronized(commandQueueProcessingMutex) {
    				// we're going to query the setFreezed flag
		    		synchronized(setFreezedMutex) {
		    			// insert command into the queue
		    			if (addAsFirst) commandQueue.add(0, command);
			        	else commandQueue.add(command);
		    			
						// is the set method freezed?		
				        if (setFreezed) return;
				        
				        // is the processSetFlagQueue running?
			    		if (commandQueueProcessing.availablePermits() <= 0) return;
			    		
			    		// we're not freezed nor the setFlag is running
			    		try {
			    			// acquire processing semaphore
                                                commandQueueProcessing.acquire();
                                        } catch (InterruptedException e) {
                                                //throw new RuntimeException("could not happen...");
                                                return;
                                        }
		    		}    			
    			}
	    	}
    	}
    	// there can never ever be two threads in this place - we've ruled them out in the 
    	// previous synchronized statement
		processCommandQueue();
    }
    
    /**
     * Add a command (to the end of the queue) that will be executed in-sync with other changes (you may be sure that no other changes
     * are taking place right now).
     * <p><p>
     * This is also used by the setFlag() method.
     * @param command
     */
    public void inSync(DoInSync<T> command) {
    	inSyncInner(command, false);
    }
        
    /**
     * Changes the flag and informs all listeners.
     * 
     * @param newValue
     * @throws InterruptedRuntimeException if interrupted during the await on the freeze latch
     */
    public void setFlag(T newValue) {
    	inSyncInner(new SetInSync(newValue), false);
    }
    
    /**
     * Whether the flag-change has been frozen, i.e., setFlag() won't change the flag value
     * immediately by will wait till {@link Flag#defreeze()} is called.
     */
    public boolean isFrozen() {
    	return setFreezed;
    }
    
    /**
     * This method will freeze the processing of the setFlag() method. Method is synchronized.
     * <p><p>
     * It waits until all setFlag() pending requests are resolved and then returns.
     * <p><p>
     * It may be used to for the synchronized registration of the listeners (if you really care
     * for the value of the flag before creating the listener). Or it may be used to obtain
     * a true value of the flag in the moment of the method call (as it waits for all the listeners
     * to execute).
     * <p>
     * In one of these cases, do this:<p>
     * <ol>
     * <li>flag.freeze()<li>
     * <li>examine the flag value and/or register new listeners</li>
     * <li>flag.defreeze() // DO NOT FORGET THIS!</li>
     * </ol>
     * <p>
     * Example: you have a flag that is counting how many alive threads you have, those threads may
     * be created concurrently ... without this synchronizing method you wouldn't be able to correctly
     * read the number of threads before incrementing the flag.
     * <p><p>
     * Beware of deadlocks when using this method, watch out:<p>
     * a) infinite recursion during the setFlag() (listeners are changing the flag value repeatedly)<p>
     * b) incorrect sequences of the freeze() / defreeze() calls
     * <p><p>
     * Of course you may simulate this behavior with simple synchronized() statement, but
     * this isn't always feasible as it blocks all other threads while accessing this flag,
     * note that this flag implementation promotes non-blocking methods.
     */
    public void freeze() {
    	try {
			setFreezedSemaphore.acquire();
		} catch (InterruptedException e) {
			throw new PogamutInterruptedException("wait on the freeze semapthore has been interrupter", e, this);
		}
    	synchronized(setFreezedMutex) {
    		setFreezed = true;
    	}
    	try {
			commandQueueProcessing.acquire(); // make sure the queue has been fully processed
			commandQueueProcessing.release();
		} catch (InterruptedException e) {
			throw new PogamutInterruptedException("interrupted during the wait for the setFlag() to finish it's work", e, this);
		}
    }
    
    /**
     * Method is synchronized. See {@link Flag#freeze()} for info.
     */
    public void defreeze() {    	    
    	synchronized(commandQueueProcessingMutex) {
    		try {
				commandQueueProcessing.acquire();
			} catch (InterruptedException e) {
				throw new PogamutInterruptedException("interrupted during acquiring setFlagProcessing", e, this);
			}    		
    	}
    	synchronized(setFreezedMutex) {
    		if (!setFreezed) throw new PogamutException("flag has been defreezed twice", this);
     		setFreezed = false;    		
    	}
    	processCommandQueue();
    	setFreezedSemaphore.release();
    }
    
    /**
     * Pauses the thread till the flag change to another value.
     * @return flag value that woke up the thread
     * @throws PogamutInterrputedException
     */
    public T waitForChange() throws PogamutInterruptedException {
    	return new WaitForFlagChange<T>(this).await();
    }
    
    /** 
     * Pauses the thread till the flag change to another value or timeout.
     * <p><p>
     * Returns null if times out, otherwise returns value that woke up the thread.
     * @param timeoutMillis
     * @param oneOfTheValue
     * @return null (timeout) or value that woke up the thread
     * @throws PogamutInterruptedException
     */
    public T waitForChange(long timeoutMillis) throws PogamutInterruptedException {
    	return new WaitForFlagChange<T>(this).await(timeoutMillis, TimeUnit.MILLISECONDS);
    }
    
    /**
     * Pauses the thread till the flag is set from the outside to one of specified values.
     * @param oneOfTheValue
     * @return flag value that woke up the thread
     * @throws PogamutInterruptedException
     */
    public T waitFor(T... oneOfTheValue) throws PogamutInterruptedException {
    	return new WaitForFlagChange<T>(this, oneOfTheValue).await();		
    }
    
    /** 
     * Pauses the thread till the flag is set from the outside to one of specified values or times out.
     * <p><p>
     * Returns null if times out, otherwise returns value that woke up the thread.
     * @param timeoutMillis
     * @param oneOfTheValue
     * @return null (timeout) or value that woke up the thread
     * @throws PogamutInterruptedException
     */
    public T waitFor(long timeoutMillis, T... oneOfTheValue) throws PogamutInterruptedException {
    	return new WaitForFlagChange<T>(this, oneOfTheValue).await(timeoutMillis, TimeUnit.MILLISECONDS);
    }
    
    /**
     * Returns the value of the flag.
     * <p><p>
     * Note that if the flag contains any set-flag pending requests queue it will return the last
     * value from this queue.
     * <p><p>
     * This has a big advantage for the multi-thread heavy-listener oriented designs.
     * <p>
     * Every time a listener is informed about the flag change it receives a new value of the flag
     * but additionally it may query the flag for the last value there will be set into it.
     * <p>
     * Note that if you use the Flag sparingly this mechanism won't affect you in 99.99999% of time.
     * <p><p>
     * Warning - this method won't return truly a correct value if you will use inSync() method because
     * this time we won't be able to obtain the concrete value of the flag after the DoInSync command
     * will be carried out - instead we return the first value we are aware of. Again this won't
     * affect you in any way (... but you should know such behavior exist ;-)) 
     * 
     * @return value of the flag
     */
    public T getFlag() {
    	synchronized(setMutex) {
	    	synchronized(commandQueue) {
	    		if (commandQueue.size() != 0) {
	    			for (int i = commandQueue.size()-1; i >= 0; --i) {
	    				DoInSync<T> command = commandQueue.get(i);
	    				if (command instanceof Flag.SetInSync) return ((SetInSync) command).newValue;
	    			}	    			
	    		}
	    		return value;
	    	}    	
    	}
    }
    
    /**
     * Tells whether the flag is set to 'one of the values' passed.
     * @param oneOfTheValue
     * @return
     */
    public boolean isOne(T... oneOfTheValue) {
    	T value = getFlag();
    	for (T one : oneOfTheValue) {
    		if (value.equals(one)) return true;
    	}
    	return false;
    }
    
    /**
     * Tells whether the flag is not set to anz of 'one of the values' passed.
     * @param oneOfTheValue
     * @return
     */
    public boolean isNone(T... oneOfTheValue) {
    	T value = getFlag();
    	for (T one : oneOfTheValue) {
    		if (value.equals(one)) return false;
    	}
    	return true;
    }
    
    /**
     * @return Immutable version of this flag, setFlag(T) method of such 
     * a flag will raise an exception. 
     */
    public ImmutableFlag<T> getImmutable() {
        if (immutableWrapper == null) {
            immutableWrapper = new ImmutableFlag<T>(this);
        }
        return immutableWrapper;
    }

    /**
     * Adds new listener to the flag (strong reference).
     * <BR><BR>
     * Using this method is memory-leak prone.
     * 
     * @param listener
     */
    public void addStrongListener(FlagListener<T> listener) {
        if (listener == null) {
            return;
        }
        listeners.addStrongListener(listener);
    }
        
    /**
     * Adds new listener to the flag with specified param. It will weak-reference the listener so when
     * you drop the references to it, it will be automatically garbage-collected.
     * <p><p>
     * Note that all anonymous
     * listeners are not subject to gc() because they are reachable from within the object where they were
     * created.
     * 
     * @param listener
     */
    @Override
    public void addListener(FlagListener<T> listener) {
    	if (listener == null) {
            return;
        }
    	listeners.addStrongListener(listener);
    }
    
    /**
     * Removes all registered 'listener' from the flag.
     * @param listener
     */
    @Override
    public void removeListener(FlagListener<T> listener) {
        if (listener == null) {
            return;
        }
        listeners.removeEqualListener(listener);        
    }

    /**
     * Removes all listeners.
     */
    @Override
    public void removeAllListeners() {
        listeners.clearListeners();
    }



    /**
     * Checks whether listener is already registered (using equals()).
     * <BR><BR>
     * @param listener
     * @return true if listener is already registered
     */
    public boolean isListenning(FlagListener<T> listener) {
        if (listener == null) {
            return false;
        }
        return listeners.isEqualListening(listener);
    }

    /** 
     * Call to clear (remove) all the listeners on the flag.
     * <BR><BR>
     * Should be used when the flag isn't going to be used again
     * to allow GC to collect the listeners (for instance anonymous objects).
     */
    public void clearListeners() {    	
    	listeners.clearListeners();
    }

}

