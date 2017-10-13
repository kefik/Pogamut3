package cz.cuni.amis.concurrency;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.junit.Ignore;
import org.junit.Test;

import cz.cuni.amis.tests.BaseTest;

public class Test08_HashSet extends BaseTest {
	
	public static Set<Integer> set = new HashSet<Integer>();
	
	public static CountDownLatch latch;
	
	public static class IncValueAsync implements Runnable {

		private int from;
		private int to;
		
		public IncValueAsync(int from, int to) {
			this.from = from;
			this.to = to;
		}
		
		@Override
		public void run() {
			for (int i = from; i < to; ++i) {
				// MUST BE synchronized(set) OTHERWISE HAS CONCURRENCY ISSUES
				synchronized(set) {
					set.add(i);
				}
			}
			for (int i = from; i < to; ++i) {
				// MUST BE synchronized(set) OTHERWISE HAS CONCURRENCY ISSUES
				synchronized(set) {
					set.remove(i);
				}
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
			int total = 0;
			int jobs = 1000;
			int add = 1000;
			
			latch = new CountDownLatch(jobs);
			
			for (int i = 0; i < jobs; ++i) {
				executor.execute(new IncValueAsync(total, total+add));
				total += add;
			}
			
			try {
				latch.await();
			} catch (InterruptedException e) {
				throw new RuntimeException("Interrupted.", e);
			}
			
			log.info("Checking set ... set.size() == " + set.size());
			
			if (set.size() != 0) {
				testFailed("HashSet.add() / HashSet.remove() has concurrency issues!");
			}
			
			testOk();
			
		} finally {
			executor.shutdownNow();
		}
	}
	
}
