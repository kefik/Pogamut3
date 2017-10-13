package cz.cuni.amis.concurrency;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

import cz.cuni.amis.tests.BaseTest;

public class Test05_AtomicInc extends BaseTest {
	
	public static Object valueMutex = new Object();
	
	public static int value;
	
	public static AtomicInteger atomicValue;
	
	public static CountDownLatch latch;
	
	public static class IncValueAsync implements Runnable {

		private int add;

		public IncValueAsync(int add) {
			this.add = add;
		}
		
		@Override
		public void run() {
			while(add > 0) {
				++value;
				--value;
				--add;
				if (add % 100 == 0) Thread.yield();				
			}
			synchronized(latch) {
				latch.countDown();
			}
			System.out.println("Jobs remaining: " + latch.getCount());
		}
		
	}
	
	public static class IncValueSync implements Runnable {

		private int add;

		public IncValueSync(int add) {
			this.add = add;
		}
		
		@Override
		public void run() {
			while(add > 0) {
				synchronized(valueMutex) {
					++value;
				}
				synchronized(valueMutex) {
					--value;
				}
				--add;
				if (add % 100 == 0) Thread.yield();
			}
			synchronized(latch) {
				latch.countDown();
			}
			System.out.println("Jobs remaining: " + latch.getCount());
		}
		
	}
	
	public static class IncValueAtomicInt implements Runnable {

		private int add;

		public IncValueAtomicInt(int add) {
			this.add = add;
		}
		
		@Override
		public void run() {
			while(add > 0) {
				atomicValue.addAndGet(1);
				atomicValue.addAndGet(-1);
				--add;
				if (add % 100 == 0) Thread.yield();
			}
			synchronized(latch) {
				latch.countDown();
			}
			System.out.println("Jobs remaining: " + latch.getCount());
		}
		
	}
	
//	@Test
//	public void testAsync() {
//		
//		value = 0;
//		int threads = 40;
//		
//		ThreadPoolExecutor executor = new ThreadPoolExecutor(threads, threads, 1000, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
//		
//		try {
//			int total = 0;
//			int jobs = 1000;
//			int add = 1000;
//			
//			latch = new CountDownLatch(jobs);
//			
//			for (int i = 0; i < jobs; ++i) {
//				total += add;
//				executor.execute(new IncValueAsync(add));
//			}
//			
//			try {
//				latch.await();
//			} catch (InterruptedException e) {
//				throw new RuntimeException("Interrupted.", e);
//			}
//			
//			log.info("Expected 0, reached " + value);
//			
//			if (total != 0) {
//				log.warning("++X is not atomic operation ... expected");
//			} else {
//				log.warning("!!! ++X IS ATOMIC OPERATION?");
//			}
//			
//			testOk();
//		} finally {
//			executor.shutdownNow();
//		}
//	}
	
//	@Test
//	public void testSync() {
//		
//		value = 0;
//		int threads = 40;
//		
//		ThreadPoolExecutor executor = new ThreadPoolExecutor(threads, threads, 1000, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
//		
//		try {
//			int total = 0;
//			int jobs = 1000;
//			int add = 1000;
//			
//			latch = new CountDownLatch(jobs);
//			
//			for (int i = 0; i < jobs; ++i) {
//				total += add;
//				executor.execute(new IncValueSync(add));
//			}
//			
//			try {
//				latch.await();
//			} catch (InterruptedException e) {
//				throw new RuntimeException("Interrupted.", e);
//			}
//			
//			log.info("Expected 0, reached " + value);
//			
//			if (value != 0) {
//				testFailed("synchronized(mutex){ ++X } is not atomic operation !!!");
//			}
//			
//			testOk();
//		} finally {
//		
//			executor.shutdownNow();
//		}
//	}
	
	@Test
	public void testAtomicInt() {
		
		value = 0;
		atomicValue = new AtomicInteger(0);
		int threads = 40;
		
		ThreadPoolExecutor executor = new ThreadPoolExecutor(threads, threads, 1000, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
		
		try {
			int total = 0;
			int jobs = 1000;
			int add = 1000;
			
			latch = new CountDownLatch(jobs);
			
			for (int i = 0; i < jobs; ++i) {
				total += add;
				executor.execute(new IncValueAtomicInt(add));
			}
			
			try {
				latch.await();
			} catch (InterruptedException e) {
				throw new RuntimeException("Interrupted.", e);
			}
			
			log.info("Expected 0, reached " + atomicValue.get());
			
			if (0 != atomicValue.get()) {
				log.warning("atomicInteger.addAndGet(1) is not atomic operation !!!");
			}
			
			testOk();
		} finally {
			executor.shutdownNow();
		}
	}

}
