package cz.cuni.amis.utils.astar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.junit.Before;
import org.junit.Test;

public class Test03_AStarHeap_DecreaseKey {

	Map<Integer, Integer> costs;	
	AStarHeapComparator<Integer> comparator;
	AStarHeap<Integer> heap;
	
	@Before
	public void setup() {
		costs = new HashMap<Integer, Integer>();		
		comparator = new AStarHeapComparator<Integer>(costs);
		heap = new AStarHeap<Integer>(comparator);
	}
	
	private void initCosts(int num) {
		// init costs
		for (int i = 0; i < num; ++i) {
			costs.put(i,i);
		}
	}
	
	private void initRevertCosts(int num) {
		// init costs
		for (int i = 0; i < num; ++i) {
			costs.put(i,num-i);
		}
	}
	
	private void putInHeapRandom(int num) {
		List<Integer> array = new ArrayList<Integer>(num);
		for (int i = 0; i < num; ++i) {
			array.add(i);
		}
		
		Random random = new Random(System.currentTimeMillis());
		
		while(array.size() > 0) {
			heap.add(array.remove(random.nextInt(array.size())));
		}
	}
	
	@Test
	public void test1() {
		System.out.println("test1()");
		
		initCosts(100);
		putInHeapRandom(100);
		
		for (int i = 0; i < 50; ++i) {
			if (heap.getMin() != i) {
				System.out.println("[ERROR] Expecting " + i + ", got " + heap.getMin());
				throw new RuntimeException("[ERROR] Expecting " + i + ", got " + heap.getMin());
			}
			heap.deleteMin();
		}
		
		for (int i = 50; i < 100; ++i) {
			costs.put(i, i-25);
			heap.decreaseKey(i);
		}
		
		for (int i = 50; i < 100; ++i) {
			if (heap.getMin() != i) {
				System.out.println("[ERROR] Expecting " + i + ", got " + heap.getMin());
				throw new RuntimeException("[ERROR] Expecting " + i + ", got " + heap.getMin());
			}
			heap.deleteMin();
		}
		
		System.out.println("---/// TEST OK ///---");
	}
	
	@Test
	public void test2() {
		System.out.println("test2()");
		
		initCosts(101);
		putInHeapRandom(100);
		
		for (int i = 0; i < 50; ++i) {
			if (heap.getMin() != i) {
				System.out.println("[ERROR] Expecting " + i + ", got " + heap.getMin());
				throw new RuntimeException("[ERROR] Expecting " + i + ", got " + heap.getMin());
			}
			heap.deleteMin();
		}
		
		for (int i = 50; i < 100; ++i) {
			costs.put(i, (int)Math.abs(i-75));
			heap.decreaseKey(i);
		}
		
		if (heap.getMin() != 75) {
			System.out.println("[ERROR] Expecting 75, got " + heap.getMin());
			throw new RuntimeException("[ERROR] Expecting 75, got " + heap.getMin());
		}
		heap.deleteMin();
		
		for (int i = 50; i < 74; ++i) {
			int expecting1 = 74 - (i-50);
			int expecting2 = 76 + (i-50);
			
			System.out.println("Expecting: {" + expecting1 + ", " + expecting2 + "}...");
			
			int first = heap.getMin();
			heap.deleteMin();
			int second = heap.getMin();
			heap.deleteMin();
			
			if (! ((first == expecting1 && second == expecting2) || (first == expecting2 && second == expecting1) )) {
				System.out.println("[ERROR] Got: " + expecting1 + ", " + expecting2);
				throw new RuntimeException("[ERROR] Expecting {" + expecting1 + ", " + expecting2 + "}, got " + first + ", " + second);
			}
			System.out.println("OK");
		}
		
		System.out.println("---/// TEST OK ///---");
	}
	
}
