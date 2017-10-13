package cz.cuni.amis.utils.astar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.junit.Before;
import org.junit.Test;

public class Test02_AStarHeap {

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
	
	private void putInHeap(int num) {
		for (int i = 0; i < num; ++i) {
			heap.add(i);
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
	public void test1_Put100Sequention() {
		System.out.println("test1_Put100Sequention()");
		
		initCosts(100);
		putInHeap(100);
		
		for (int i = 0; i < 100; ++i) {
			if (heap.getMin() != i) {
				System.out.println("[ERROR] Expecting " + i + ", got " + heap.getMin());
				throw new RuntimeException("[ERROR] Expecting " + i + ", got " + heap.getMin());
			}
			heap.deleteMin();
		}
		
		System.out.println("---/// TEST OK ///---");
	}
	
	@Test
	public void test2_Put100Random() {
		System.out.println("test2_Put100Random()");		
		
		initCosts(100);
		putInHeapRandom(100);
		
		for (int i = 0; i < 100; ++i) {
			if (heap.getMin() != i) {
				System.out.println("[ERROR] Expecting " + i + ", got " + heap.getMin());
				throw new RuntimeException("[ERROR] Expecting " + i + ", got " + heap.getMin());
			}
			heap.deleteMin();
		}
		
		System.out.println("---/// TEST OK ///---");
	}
	
	@Test
	public void test3_Put10000Random() {
		System.out.println("test3_Put10000Random()");
		
		initCosts(10000);
		putInHeapRandom(10000);
		
		for (int i = 0; i < 10000; ++i) {
			if (heap.getMin() != i) {
				System.out.println("[ERROR] Expecting " + i + ", got " + heap.getMin());
				throw new RuntimeException("[ERROR] Expecting " + i + ", got " + heap.getMin());
			}
			heap.deleteMin();
		}
		
		System.out.println("---/// TEST OK ///---");
	}
	
	@Test
	public void test4_100x_Put10000Random() {
		System.out.println("test4_100x_Put10000Random()");
		
		initCosts(10000);
		
		for (int j = 0; j < 100; ++j) {
			System.out.println("Test " + (j+1) + " / 100");
			putInHeapRandom(10000);
			for (int i = 0; i < 10000; ++i) {
				if (heap.getMin() != i) {
					System.out.println("[ERROR] Expecting " + i + ", got " + heap.getMin());
					throw new RuntimeException("[ERROR] Expecting " + i + ", got " + heap.getMin());
				}
				heap.deleteMin();
			}
		}
		
		System.out.println("---/// TEST OK ///---");
	}
	
	@Test
	public void test5_Put10000Random_RevertCost() {
		System.out.println("test5_Put10000Random_RevertCost()");
		
		initRevertCosts(10000);
		putInHeapRandom(10000);
		
		for (int i = 0; i < 10000; ++i) {
			if (heap.getMin() != 9999-i) {
				System.out.println("[ERROR] Expecting " + (9999-i) + ", got " + heap.getMin());
				throw new RuntimeException("[ERROR] Expecting " + (9999-i) + ", got " + heap.getMin());
			}
			heap.deleteMin();
		}
		
		System.out.println("---/// TEST OK ///---");
	}
	
	@Test
	public void test6_100x_Put10000Random_RevertCost() {
		System.out.println("test6_100x_Put10000Random_RevertCost()");
		
		initRevertCosts(10000);
		
		for (int j = 0; j < 100; ++j) {
			System.out.println("Test " + (j+1) + " / 100");
			putInHeapRandom(10000);			
			for (int i = 0; i < 10000; ++i) {
				if (heap.getMin() != 9999-i) {
					System.out.println("[ERROR] Expecting " + (9999-i) + ", got " + heap.getMin());
					throw new RuntimeException("[ERROR] Expecting " + (9999-i) + ", got " + heap.getMin());
				}
				heap.deleteMin();
			}
		}
		
		System.out.println("---/// TEST OK ///---");
	}
	
	
}
