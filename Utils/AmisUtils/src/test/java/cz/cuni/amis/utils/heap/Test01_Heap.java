package cz.cuni.amis.utils.heap;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import junit.framework.TestFailure;

import org.junit.Test;

import cz.cuni.amis.tests.BaseTest;

public class Test01_Heap extends BaseTest {

	public String mainToStr(Integer[] nums) {
		if (nums.length == 0)
			return "";
		String str = nums[0].toString();
		for (int i = 1; i < nums.length; ++i) {
			str += ", " + nums[i].toString();
		}
		return str;
	}

	public boolean mainCheck(Heap heap, Integer[] nums) {
		log.info("Removing and checking " + mainToStr(nums));
		List heapInts = new ArrayList();
		List desiredInts = new ArrayList();
		for (int i = 0; i < nums.length; ++i) {
			desiredInts.add(nums[i]);
			heapInts.add(heap.getMin());
			heap.deleteMin();
		}
		if (heapInts.containsAll(desiredInts)) {
			log.info("OK");
			return true;
		} else {
			testFailed("KO!");
			return false;
		}		
	}

	public void mainAdd(Heap heap, Integer[] nums) {
		log.info("Adding: " + mainToStr(nums));
		for (int i = 0; i < nums.length; ++i) {
			heap.add(nums[i]);
		}
	}

	@Test
	public void test() {
		Heap heap = new Heap(new Comparator() {
			public int compare(Object arg0, Object arg1) {
				return (Integer) arg0 - (Integer) arg1;
			}
		}, 20);

		mainAdd(heap, new Integer[]   { 10, 100, 1, 50, 5 });
		mainCheck(heap, new Integer[] { 1, 5 });
		mainCheck(heap, new Integer[] { 10, 50 });
		mainAdd(heap, new Integer[]   { 80, 60, 70 });
		mainCheck(heap, new Integer[] { 60, 70, 80, 100 });
		mainAdd(heap, new Integer[]   { 5, 8, 3, 7, 4, 1, 9 });
		mainCheck(heap, new Integer[] { 5, 8, 3, 7, 4, 1, 9 });
		mainAdd(heap, new Integer[]   { 2, 7, 3, 5, 6, 4, 9, 1 });
		mainCheck(heap, new Integer[] { 1, 2, 3, 4 });
		mainAdd(heap, new Integer[]   { 20, 70, 30, 50, 60, 2, 3, 1, 4 });
		mainCheck(heap, new Integer[] { 20, 70, 30, 50, 60, 2, 3, 1, 4, 5, 6, 7, 9 });
		
		testOk();
	}

}
