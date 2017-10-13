package cz.cuni.amis.concurrency;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import cz.cuni.amis.tests.BaseTest;
import cz.cuni.amis.utils.maps.SyncHashMap;

public class Test04_SyncHashMap_Reads extends BaseTest {
	
	public static SyncHashMap<Integer, Integer> map = new SyncHashMap<Integer, Integer>();
	
	public static CountDownLatch latch;
	
	public static class GetValue implements Runnable {

		private int from;
		private int to;
		
		public GetValue(int from, int to) {
			this.from = from;
			this.to = to;
		}
		
		@Override
		public void run() {
			for (int i = from; i < to; ++i) {
				map.get(i);
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
			
			for (int i = 0; i < add; ++i) {
				map.put(i, i);
			}
			
			for (int i = 0; i < jobs; ++i) {
				executor.execute(new GetValue(0, add));
			}
			
			try {
				latch.await();
			} catch (InterruptedException e) {
				throw new RuntimeException("Interrupted.", e);
			}
			
			log.info("Checking map, expecting " + add + " items...");
			
			if (map.size() != add) {
				testFailed("map.size() == " + map.size() + " != " + add);
			}
						
			testOk();
		} finally {
			executor.shutdownNow();
		}
	}
	
}
