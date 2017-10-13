package cz.cuni.amis.concurrency;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import cz.cuni.amis.tests.BaseTest;
import cz.cuni.amis.utils.maps.SyncHashMap;

public class Test03_SyncHashMap extends BaseTest {
	
	public static SyncHashMap<Integer, Integer> map = new SyncHashMap<Integer, Integer>();
	
	public static CountDownLatch latch;
	
	public static boolean failure = false;
	
	public static class PutGetRemoveAsync implements Runnable {

		private int from;
		private int to;
		
		public PutGetRemoveAsync(int from, int to) {
			this.from = from;
			this.to = to;
		}
		
		@Override
		public void run() {
			for (int i = from; i < to; ++i) {
				map.put(i, i);
				int value = map.get(i);
				if (value != i) {
					failure = true;
					throw new RuntimeException("Failed... get(" + i + ") == " + value + " != " + i + ".");
				}
				map.remove(i);
			}
			synchronized(latch) {
				latch.countDown();
				System.out.println("Jobs remaining: " + latch.getCount());
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
				executor.execute(new PutGetRemoveAsync(i*add, i*add + add));
			}
			
			try {
				latch.await();
			} catch (InterruptedException e) {
				throw new RuntimeException("Interrupted.", e);
			}
			
			log.info("Checking map, expecting 0 items...");
			
			if (map.size() != 0) {
				testFailed("map.size() == " + map.size() + " != 0");
			}
			
			if (failure) {
				testFailed("Failure is true :(");
			}
			
			testOk();
		} finally {
			executor.shutdownNow();
		}
	}
	
}
