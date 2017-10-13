package cz.cuni.amis.concurrency;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import cz.cuni.amis.tests.ConcurrencyTest;
import cz.cuni.amis.tests.ConcurrentTask;

public class Test02_HashMap extends ConcurrencyTest {
	
	public static HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
	
	public static class IncValueAsync extends ConcurrentTask {

		private int from;
		private int to;
		
		public IncValueAsync(int from, int to) {
			this.from = from;
			this.to = to;
		}
		
		@Override
		protected void runImpl() {
			for (int i = from; i < to; ++i) {
				synchronized(map) {
					map.put(i, 1);
				}
			}
			for (int i = from; i < to; ++i) {
				synchronized(map) {
					map.get(i);
				}
			}
			for (int i = from; i < to; ++i) {
				synchronized(map) {
					map.remove(i);
				}
			}
		}
		
	}
	
	@Test
	public void test() {
		
		int threads = 40;
		
		int jobs = 1000;
		int add = 1000;
		
		List<ConcurrentTask> tasks = new ArrayList<ConcurrentTask>();
		
		for (int i = 0; i < jobs; ++i) {
			tasks.add(new IncValueAsync(0, add));
		}
		
		runConcurrent(tasks, threads);
		
		log.info("Checking map, expecting 0 items...");
		
		if (map.size() != 0) {
			testFailed("map.size() == " + map.size() + " != 0");
		}
		
		testOk();
	}
	
}
