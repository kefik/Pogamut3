package cz.cuni.amis.utils.astar;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class Test01_AStarHeapComparator {
	
	Map<Integer, Integer> costs;
	
	AStarHeapComparator<Integer> comparator;
	
	private void lower(int a, int b) {
		System.out.println("Expecting: " + a + " < " + b);
		if (comparator.compare(a, b) >= 0) {
			System.out.println("[ERROR] Comparator returned " + comparator.compare(a,b));
			throw new RuntimeException();
		}
		System.out.println("OK");
	}
	
	private void same(int a, int b) {
		System.out.println("Expecting: " + a + " == " + b);
		if (comparator.compare(a, b) != 0) {
			System.out.println("[ERROR] Comparator returned " + comparator.compare(a,b));
			throw new RuntimeException();
		}
		System.out.println("OK");
	}
	
	private void greater(int a, int b) {
		System.out.println("Expecting: " + a + " > " + b);
		if (comparator.compare(a, b) <= 0) {
			System.out.println("[ERROR] Comparator returned " + comparator.compare(a,b));
			throw new RuntimeException();
		}
		System.out.println("OK");
	}
	
	@Test
	public void test() {
		
		costs = new HashMap<Integer, Integer>();
		
		comparator = new AStarHeapComparator<Integer>(costs);
		
		// let's initialize 'costs'
		
		System.out.println("Init: 1->1, 2->2, 3->3");
		
		costs.put(1,1);
		costs.put(2,2);
		costs.put(3,3);
		
		// now try to compare
		
		same(1,1);
		
		lower(1,2);
		greater(2,1);
		
		lower(2,3);
		greater(3,2);
				
		// alter cost
		System.out.println("Altering: 1->4");		
		costs.put(1,4);
		
		same(1,1);
		
		greater(1,2);
		lower(2,1);
		
		greater(1,3);
		lower(3,1);
		
		// alter cost		
		System.out.println("Altering: 2->5");
		costs.put(2, 5);		
		
		same(2,2);
		
		lower(1,2);
		greater(2,1);
		
		greater(2,3);
		lower(3,2);
		
		// alter cost		
		System.out.println("Altering: 3->5");
		costs.put(3,5);		
		
		lower(1,3);
		greater(3,1);
		
		same(2,3);
		
		System.out.println("---/// TEST OK ///---");
		
	}

}
