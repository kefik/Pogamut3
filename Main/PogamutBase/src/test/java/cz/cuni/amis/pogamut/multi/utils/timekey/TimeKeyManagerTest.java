package cz.cuni.amis.pogamut.multi.utils.timekey;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import org.junit.Ignore;
import org.junit.Test;

import cz.cuni.amis.pogamut.multi.utils.exception.TimeKeyNotLockedException;
import cz.cuni.amis.pogamut.multi.worldview.objects.CheckInstances;
import cz.cuni.amis.tests.BaseTest;
import cz.cuni.amis.utils.exception.PogamutInterruptedException;

@Ignore
public class TimeKeyManagerTest extends BaseTest {

	public static final int MAX_KEY_TO_LOCK = 100;
	
	protected boolean failure = false;
		
	protected class Locker implements Runnable {
		
		/**
		 * FIXED SEED! To have "the same" test with respect to the keys.
		 */
		private Random r = new Random(10);
		
		private HashSet<Long> locks = new HashSet<Long>();
		
		private int keysPerRun;
		private int runs;
		private int id;

		private ITimeKeyManager timeKeyManager;

	
		public Locker(ITimeKeyManager timeKeyManager, int runs, int keysPerRun, int id)
		{
			this.id = id;
			this.timeKeyManager = timeKeyManager;
			this.runs = runs;
			this.keysPerRun = keysPerRun;
		}
		
		protected void makeLock(long l)
		{
			if ( locks.contains(l))
			{
				return;
			}
			//log.info("[" + id + "] Adding lock " + l);
			timeKeyManager.lock( l );
			locks.add(l);
		}
		
		protected void removeLock(long l)
		{
			if (locks.remove(l)) {
				//log.info("[" + id + "] Removing lock " + l);
				try {
					timeKeyManager.unlock( l );
				} catch (TimeKeyNotLockedException e) {
					failure = true;
					log.severe("Trying to unlock not locked TimeKey : " + l );
					throw new RuntimeException("Trying to unlock not locked TimeKey : " + l);
				}
			}
		}
		
		protected void checkLocks()
		{
			for ( Long l : locks) {
				if (!timeKeyManager.isLocked(l)) {
					failure = true;
					log.severe("TimeKey " + l + " should be locked, but is not!!!");
					throw new RuntimeException("TimeKey " + l + " should be locked, but is not!!!");
				}
			}
		}
		
		protected void removeLocks()
		{
			while (locks.size() > 0) {
				removeLock(locks.iterator().next());				
				checkLocks();
			}
		}
		
		@Override
		public void run() {
			while ( runs > 0 )
			{
				if ( runs % 20 == 0)
				{
					log.info( "Thread " + id + ": " + runs + " runs remaining");
				}
				for (int i = 0; i < keysPerRun; ++i) {
					//makeLock(r.nextInt(MAX_KEY_TO_LOCK));					
					makeLock(i);
				}
				checkLocks();
				removeLocks();
				--runs;
			}			
			log.info("Thread " + id + " : Finished.");
		}
		
	}
	
	protected void testTimeKeyManager(ITimeKeyManager timeKeyManager, int threadCount, int runs, int keysPerRun) 
	{
		Thread[] threads = new Thread[threadCount];
		for ( int i = 0; i < threadCount; ++i)
		{
			threads[i] = new Thread( new Locker(timeKeyManager, runs, keysPerRun, i));
		}
		for ( Thread t : threads)
		{
			t.start();
		}
		for ( Thread t : threads)
		{
			try {
				t.join();
			} catch (InterruptedException e) {
				throw new PogamutInterruptedException(e, this);
			}
		}
		
		if (failure) {
			assertFail("There was a failure in the test!");
		}
		
		log.info("Locked: " + timeKeyManager.getHeldKeysStr());
		for ( long l = -1000; l < 1000; ++l ) {
			if (timeKeyManager.isLocked( l )) {
				testFailed("TimeKey  " + l + " is still locked");
			}			
		}
		
		threads = null;			
	}
	
}
