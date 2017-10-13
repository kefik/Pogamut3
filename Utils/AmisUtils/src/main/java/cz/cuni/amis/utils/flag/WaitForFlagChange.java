package cz.cuni.amis.utils.flag;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import cz.cuni.amis.utils.collections.MyCollections;
import cz.cuni.amis.utils.exception.PogamutInterruptedException;

/**
 * This class is implementing the waiting on some flag value.
 * <BR><BR>
 * Note that you may call only one AWAIT() at time.
 * <BR><BR>
 * Typical usage:
 * <BR>
 * boolean flagValue = new WaitForFlagChange&lt;Boolean&gt;(booleanFlag, Boolean.TRUE).await();
 * <BR>
 * int flagValue = new WaitForFlagChange&lt;Integer&gt;(integerFlag, new Integer[]{1,3}).await();
 * 
 * @author Jimmy
 *
 * @param <TYPE>
 */
public class WaitForFlagChange<TYPE> {
	
	public static interface IAccept<TYPE> {
		
		public boolean accept(TYPE flagValue);
		
	}
	
	private class ListAccept implements IAccept<TYPE> {
		
		private List<TYPE> waitingFor;
		
		public ListAccept(Collection<TYPE> list) {
			waitingFor = new ArrayList<TYPE>(list.size());
			waitingFor.addAll(list);
		}
		
		/**
		 * Accepts all.
		 */
		public ListAccept() {
			this.waitingFor = null;
		}
		
		public ListAccept(TYPE... accept) {
			waitingFor = new ArrayList(accept.length);
			waitingFor.addAll(MyCollections.toList(accept));
		}

		@Override
		public boolean accept(TYPE flagValue) {
			if (waitingFor == null) return true;
			return waitingFor.contains(flagValue);
		}
		
	}
	
	private IAccept<TYPE> accept;
	private IFlag<TYPE> flag;
	
	private Object latchAccessMutex = new Object();
	private Object mutex = new Object();
	
	private CountDownLatch latch = null;
	
	private boolean isResult = false;
	private TYPE result = null;
	
	private class Listener implements FlagListener<TYPE> {
		
		public Listener(IFlag<TYPE> flag) {
			WaitForFlagChange.this.result = flag.getFlag();			
			if (!(isResult = isDesiredValue(WaitForFlagChange.this.result))) {
				flag.addListener(this);
			}
			// must do second check because of the flag that has been just attached
			if (!isResult) {
				TYPE result = flag.getFlag();
				if (isDesiredValue(result)) { 
					isResult = true;
					WaitForFlagChange.this.result = result;
					flag.removeListener(this);
					synchronized(latchAccessMutex) {
						if (latch != null) {
							latch.countDown();
						}
					}	
				}
			}
		}

		@Override
		public void flagChanged(TYPE changedValue) {
			if (!isResult && isDesiredValue(changedValue)) {
				flag.removeListener(this);
				synchronized(latchAccessMutex) {
					isResult = true;
					result = changedValue;
					if (latch != null) {
						latch.countDown();
					}
				}				
			}
		}
		
	}
	
	/**
	 * Wait for the next flag change.
	 * @param flag
	 */
	public WaitForFlagChange(IFlag<TYPE> flag) {
		this.flag = flag;
		this.accept = new ListAccept();
	}
	
	public WaitForFlagChange(IFlag<TYPE> flag, IAccept<TYPE> waitingFor) {
		this.flag = flag;
		this.accept = waitingFor;
	}
	
	public WaitForFlagChange(IFlag<TYPE> flag, TYPE waitingFor) {
		this.flag = flag;
		this.accept = new ListAccept(waitingFor);
	}
	
	public WaitForFlagChange(IFlag<TYPE> flag, TYPE[] waitingFor) {
		this.flag = flag;
		this.accept = new ListAccept(waitingFor);		
	}
	
	public WaitForFlagChange(IFlag<TYPE> flag, Collection<TYPE> waitingFor) {
		this.flag = flag;
		this.accept = new ListAccept(waitingFor);		
	}
		
	private boolean isDesiredValue(TYPE value) {
		return accept.accept(value);
	}
	
	/**
	 * Note that you may call only await() from one thread! If the instance is already in used
	 * it may produce unwanted behavior (e.g. dead-lock).
	 * 
	 * @return value from the flag that raised the latch
	 * @throws InterruptedException
	 */
	public TYPE await() throws PogamutInterruptedException {
		synchronized(mutex) {
			synchronized(latchAccessMutex) {
				latch = new CountDownLatch(1);
			}
			// instantiation checks whether we doesn't have desired result already, 
			// if not adds itself as a listener to a flag
			Listener listener = new Listener(flag); 
			if (isResult) return result;			
			try {
				latch.await(); // the latch is raised whenever a listener receive a correct result
			} catch (InterruptedException e) {
				throw new PogamutInterruptedException(e, this);
			}
			synchronized(latchAccessMutex) {
				flag.removeListener(listener);
				latch = null;
			}
			return result;
		}
	}
	
	/**
	 * Note that you may call only await() from one thread! If the instance is already in used
	 * it may produce unwanted behavior.
	 * <p><p>
	 * Returns null if desired value hasn't been set at the flag before timeout.
	 * 
	 * @param timeout
	 * @param timeUnit
	 * @return value of the flag
	 * @throws PogamutInterruptedException
	 */
	public TYPE await(long timeout, TimeUnit timeUnit) throws PogamutInterruptedException {
		synchronized(mutex) {
			synchronized(latchAccessMutex) {
				latch = new CountDownLatch(1);
			}
			// instantiation checks whether we doesn't have desired result already, 
			// if not adds itself as a listener to a flag
			Listener listener = new Listener(flag); 
			if (isResult) return result;			
			try {
				latch.await(timeout, timeUnit); // the latch is raised whenever a listener receive a correct result
			} catch (InterruptedException e) {
				throw new PogamutInterruptedException(e, this);
			}
			synchronized(latchAccessMutex) {
				flag.removeListener(listener);
				latch = null;
			}
			return result;
		}
	}

}
