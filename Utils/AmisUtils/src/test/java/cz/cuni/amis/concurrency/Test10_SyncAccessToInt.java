package cz.cuni.amis.concurrency;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.junit.Test;

import cz.cuni.amis.tests.BaseTest;

public class Test10_SyncAccessToInt extends BaseTest {
	
	private int bogus;
	
	private CountDownLatch latch;

	private Object valueMutex = new Object();
	
	private int value = 0;
		
	private class IncReadDecValueAsync implements Runnable {

		private int count;
		
		public IncReadDecValueAsync(int count) {
			this.count = count;
		}
		
		@Override
		public void run() {
			for (int i = 0; i < count; ++i) {
				synchronized(valueMutex) {
					++value;
				}
				synchronized(valueMutex) {
					++bogus;
				}
				synchronized(valueMutex) {
					--value;
				}				
			}
			synchronized(latch) {
				latch.countDown();
				log.info("Jobs remaining: " + latch.getCount());
			}
		}
	}
	
	@Test
	public void test() {
		
		int threads = 40;
		
		ThreadPoolExecutor executor = new ThreadPoolExecutor(threads, threads, 1000, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
		
		try {
			int jobs = 1000;
			int add = 1000;
			
			latch = new CountDownLatch(jobs);
			
			for (int i = 0; i < jobs; ++i) {
				executor.execute(new IncReadDecValueAsync(add));
			}
			
			try {
				latch.await();
			} catch (InterruptedException e) {
				throw new RuntimeException("Interrupted.", e);
			}
			
			log.info("Checking value, expecting 0 ...");
			
			if (value != 0) {
				testFailed("value == " + value + " != 0");
			}
			
			testOk();
		} finally {
			executor.shutdownNow();
		}
	}
	
}
