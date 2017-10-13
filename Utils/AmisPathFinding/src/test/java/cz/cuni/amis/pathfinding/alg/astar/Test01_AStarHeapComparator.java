package cz.cuni.amis.pathfinding.alg.astar;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import cz.cuni.amis.tests.BaseTest;

public class Test01_AStarHeapComparator extends BaseTest {
	
	Map<Integer, Integer> costs;
	
	AStarHeapComparator<Integer> comparator;
	
	private void lower(int a, int b) {
		log.info("Expecting: " + a + " < " + b);
		if (comparator.compare(a, b) >= 0) {
			testFailed("Comparator returned " + comparator.compare(a,b));
		}
		testOk();
	}
	
	private void same(int a, int b) {
		log.info("Expecting: " + a + " == " + b);
		if (comparator.compare(a, b) != 0) {
			testFailed("Comparator returned " + comparator.compare(a,b));
			throw new RuntimeException();
		}
		testOk();
	}
	
	private void greater(int a, int b) {
		log.info("Expecting: " + a + " > " + b);
		if (comparator.compare(a, b) <= 0) {
			testFailed("Comparator returned " + comparator.compare(a,b));
			throw new RuntimeException();
		}
		testOk();
	}
	
	@Test
	public void test() {
		
		costs = new HashMap<Integer, Integer>();
		
		comparator = new AStarHeapComparator<Integer>(costs);
		
		// let's initialize 'costs'
		
		log.info("Init: 1->1, 2->2, 3->3");
		
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
		log.info("Altering: 1->4");		
		costs.put(1,4);
		
		same(1,1);
		
		greater(1,2);
		lower(2,1);
		
		greater(1,3);
		lower(3,1);
		
		// alter cost		
		log.info("Altering: 2->5");
		costs.put(2, 5);		
		
		same(2,2);
		
		lower(1,2);
		greater(2,1);
		
		greater(2,3);
		lower(3,2);
		
		// alter cost		
		log.info("Altering: 3->5");
		costs.put(3,5);		
		
		lower(1,3);
		greater(3,1);
		
		same(2,3);
		
		testOk();
		
	}

}
