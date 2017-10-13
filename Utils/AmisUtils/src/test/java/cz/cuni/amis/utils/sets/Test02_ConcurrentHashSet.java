package cz.cuni.amis.utils.sets;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

import cz.cuni.amis.tests.BaseTest;
import cz.cuni.amis.utils.StopWatch;

public class Test02_ConcurrentHashSet extends BaseTest {
	
	private static class SetManager implements Runnable {
		
		private Random random = new Random(System.currentTimeMillis());
		private ConcurrentHashSet<Integer> set;
		private int num;
		
		public SetManager(ConcurrentHashSet<Integer> set, int num) {
			this.set = set;
			this.num = num;
		}

		@Override
		public void run() {
			List<Integer> ints = new ArrayList<Integer>();
			int total = 0;
			StopWatch watch = new StopWatch();
			for (int i = 0; i < 600; ++i) {
				switch(random.nextInt(4)) {
				case 0:
					try {
						Thread.sleep(5);
					} catch (InterruptedException e) {
					}
					break;
				case 1:
					int next = random.nextInt(10000);
					set.add(next);
					break;
				case 2:
					if (ints.size() == 0) continue;
					int index = random.nextInt(ints.size());
					set.remove(ints.remove(index));
					break;
				case 3: 					
					for (Integer stored : set) {
						total += stored;
					}
				}
			}
			log.info("Thread "+ num + ": total = " + total + ".");
			log.info("Thread "+ num + ": 1000 operations took "+ watch.stopStr());
		}
		
	}
	
	@Test
	public void test() {
		ConcurrentHashSet set = new ConcurrentHashSet<Integer>();
		Thread t1 = new Thread(new SetManager(set,1));
		Thread t2 = new Thread(new SetManager(set,2));
		Thread t3 = new Thread(new SetManager(set,3));
		Thread t4 = new Thread(new SetManager(set,4));
		
		StopWatch watch = new StopWatch();
		
		t1.start();
		t2.start();
		t3.start();
		t4.start();
		
		try {
			t1.join();
			t2.join();
			t3.join();
			t4.join();
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("Exception waiting for threads...");
		}		
		log.info("Total time: " + watch.stopStr());
		
		testOk();
	}

	public static void main(String[] args) {
		Test02_ConcurrentHashSet test = new Test02_ConcurrentHashSet();
		
		test.test();
	}
	
}
