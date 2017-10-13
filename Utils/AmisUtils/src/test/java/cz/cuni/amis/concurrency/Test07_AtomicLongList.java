package cz.cuni.amis.concurrency;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import cz.cuni.amis.tests.BaseTest;
import cz.cuni.amis.utils.concurrency.AtomicLongList;

public class Test07_AtomicLongList extends BaseTest {
	
	public static AtomicLongList list = new AtomicLongList(0, 50);
	
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
				list.addAndGet(i, 1);
			}
			for (int i = from; i < to; ++i) {
				list.addAndGet(i, -1);
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
				executor.execute(new IncValueAsync(0, add));
			}
			
			try {
				latch.await();
			} catch (InterruptedException e) {
				throw new RuntimeException("Interrupted.", e);
			}
			
			log.info("Checking list, expecing all 0...");
			
			for (int i = 0; i < add; ++i) {
				if (list.get(i) != 0) {
					testFailed("List[" + i + "] = " + list.get(i) + " != 0 !!! AtomicLongList has concurrency issues.");
				}
			}
						
			testOk();
			
		} finally {
			executor.shutdownNow();
		}
	}
	
}
