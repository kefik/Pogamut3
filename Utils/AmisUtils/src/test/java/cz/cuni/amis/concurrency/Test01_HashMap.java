package cz.cuni.amis.concurrency;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Test;

import cz.cuni.amis.tests.ConcurrencyTest;
import cz.cuni.amis.tests.ConcurrentTask;

public class Test01_HashMap extends ConcurrencyTest {

	private Map<Integer, Integer> ints = new HashMap<Integer, Integer>();
	
	private int get(int i) {
		
		Integer result = ints.get(i);
		
		if (result != null) {
			return result;
		}
		
		synchronized(ints) {
			log.info(Thread.currentThread().getName() + ": get(" + i + ") regetting...");
			result = ints.get(i);
			if (result != null) {
				log.info(Thread.currentThread().getName() + ": get(" + i + ") sync get success!");
				return result;
			}
			log.info(Thread.currentThread().getName() + ": get(" + i + ") inserting...");
			ints.put(i, i);
			return i;
		}
		
	}
	
	private static final int COUNT = 10000;
	
	private class InsertJob extends ConcurrentTask {

		@Override
		protected void runImpl() {
			for (int i = 0; i < COUNT; ++i) {
				get(i);
			}
		}
		
	}
	
	@Test
	public void test() {	
		int threadCount = 30;
		
		List<ConcurrentTask> tasks = new ArrayList<ConcurrentTask>();
		
		for (int i = 0; i < threadCount; ++i) {
			tasks.add(new InsertJob());
		}
		
		runConcurrent(tasks, threadCount);
		
		log.info("Checking results...");
		
		// CHECK RESULTS
		
		Integer[] keys = ints.keySet().toArray(new Integer[COUNT]);
		Integer[] values = ints.values().toArray(new Integer[COUNT]);
		
		if (keys.length != COUNT) {
			testFailed("keys.length != " + COUNT + " == number of inserted pairs");
		}
		if (values.length != COUNT) {
			testFailed("values.length != " + COUNT + " == number of inserted pairs");			
		}
		
		// NULL CHECKS
		
		for (int i = 0; i < keys.length; ++i) {
			if (keys[i] == null) {
				testFailed("keys[" + i + "] == null");
			}
		}
		
		for (int i = 0; i < values.length; ++i) {
			if (values[i] == null) {
				testFailed("values[" + i + "] == null");
			}
		}
		
		Arrays.sort(keys);
		Arrays.sort(values);
		
		for (int i = 0; i < COUNT; ++i) {
			if (keys[i] != i) {
				testFailed("keys[i] == " + keys[i] + " != " + i + " which is expected value");
			}
			if (values[i] != i) {
				testFailed("values[i] == " + values[i] + " != " + i + " which is expected value");
			}
		}
		
		Integer[] keyRegs = new Integer[COUNT];
		Integer[] valueRegs = new Integer[COUNT];
		for (int i = 0; i < COUNT; ++i) {
			keyRegs[i] = i;
			valueRegs[i] = i;
		}
		
		for (Entry<Integer, Integer> entry : ints.entrySet()) {
			if (entry.getKey() == null) {
				testFailed("map contains entry with key==null");
			}
			if (entry.getValue() == null) {
				testFailed("map contains entry with value==null");
			}
			int key = entry.getKey();
			int value = entry.getValue();
			
			if (keyRegs[key] == null) {
				testFailed("key " + key + " appears in the map twice");
			}
			keyRegs[key] = null;
			
			if (valueRegs[value] == null) {
				testFailed("value " + value + " appears in the map twice");
			}
			valueRegs[value] = null;			
		}
		
		for (int i = 0; i < COUNT; ++i) {
			if (keyRegs[i] != null) {
				testFailed("key " + i + " is not in the map");
			}
			if (valueRegs[i] != null) {
				testFailed("value " + i + " is not in the map");
			}
		}
		
		for (int i = 0; i < COUNT; ++i) {
			Integer result = ints.get(i);
			if (result == null) {
				testFailed("key " + i + " is not in the map");
			}
			if (result != i) {
				testFailed("value under the key " + i + " is not " + i + " but " + result);
			}
		}
		
		testOk();
	}
	
}
