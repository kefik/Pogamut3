package cz.cuni.amis.concurrency;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.junit.Ignore;
import org.junit.Test;

import cz.cuni.amis.tests.BaseTest;

@Ignore
public class Test09_ReadWriteLock extends BaseTest {
	
	public static int bogus;
	
	public CountDownLatch latch;
	
	// IF YOU USE 'int' INSTEAD OF ATOMIC INTEGER ... THE TEST WON'T PASS!!!
	//public AtomicInteger value = new AtomicInteger(0);
	public volatile static int value = 0;
	
	public AtomicBoolean alone = new AtomicBoolean(true);
	
	public ReadWriteLock lock = new ReentrantReadWriteLock(false);
	
	public Lock readLock = lock.readLock();
	
	public Lock writeLock = lock.readLock();
	
	private StringBuffer errors = new StringBuffer();

	private boolean failure;
	
	private void error(String error) {
		failure = true;
		synchronized(errors) {
			errors.append(error);
			errors.append("\n");
		}
	}
	
	public class IncReadDecValueAsync implements Runnable {

		private int from;
		private int to;
		
		public IncReadDecValueAsync(int from, int to) {
			this.from = from;
			this.to = to;
		}
		
		@Override
		public void run() {
			boolean isAlone = false;
			for (int i = from; i < to; ++i) {
//				synchronized(lock) {
//					value += 1;
//				}
//				synchronized(lock) {
//					++bogus;
//				}
//				synchronized(lock) {
//					value -= 1;
//				}
				writeLock.lock();
				try {
					isAlone = alone.getAndSet(false);
					if (!isAlone) {
						errors.append("Not alone!");
					}					
					//value.getAndIncrement();
					isAlone = alone.getAndSet(true);
					value += 1;
					if (isAlone) {
						errors.append("Is alone and should not be!");
					}					
				} finally {
					writeLock.unlock();
				}
				readLock.lock();
				try {
					++bogus;
				} finally {
					readLock.unlock();
				}
				writeLock.lock();
				try {
					isAlone = alone.getAndSet(false);
					if (!isAlone) {
						errors.append("Not alone!");
					}	
					//value.getAndDecrement();
					value -= 1;
					isAlone = alone.getAndSet(true);
					if (isAlone) {
						errors.append("Is alone and should not be!");
					}
				} finally {
					writeLock.unlock();
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
				executor.execute(new IncReadDecValueAsync(0, add));
			}
			
			try {
				latch.await();
			} catch (InterruptedException e) {
				throw new RuntimeException("Interrupted.", e);
			}
			
			log.info("Checking value, expecting 0 ...");
			
			if (value != 0) {
				testFailed("value == " + value + " != 0");
				//log.warning("value == " + value + " != 0");
			}
			
//			if (value.get() != 0) {
//				testFailed("value == " + value.get() + " != 0");
//			}
			
			if (failure) {
				testFailed(errors.toString());
			}
			
			testOk();
		} finally {
			executor.shutdownNow();
		}
	}
	
	public static void main(String[] args) {
		Test09_ReadWriteLock test = new Test09_ReadWriteLock();
		BaseTest.baseTestBeforeClass();
		test.beforeTest();
		test.test();
		test.afterTest();
	}
}
