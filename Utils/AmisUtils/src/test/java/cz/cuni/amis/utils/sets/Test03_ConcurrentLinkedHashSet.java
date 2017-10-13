package cz.cuni.amis.utils.sets;

import java.util.Iterator;

import org.junit.Assert;
import org.junit.Test;

import cz.cuni.amis.tests.BaseTest;

public class Test03_ConcurrentLinkedHashSet extends BaseTest {
	
	@Test
	public void test() {
		ConcurrentLinkedHashSet<Integer> set = new ConcurrentLinkedHashSet<Integer>();
		for (int i = 0; i < 25; ++i) {
			set.add(i);
		}
		
		log.info("Inserted: ");
		Iterator<Integer> iter = set.iterator();
		boolean first = true;
		while (iter.hasNext()) {
			if (first) first = false;
			else System.out.print(", ");
			System.out.print(iter.next());
		}
		log.info("");
		log.info("");
		
		int i = 0;
		iter = set.iterator();
		while(iter.hasNext()) {
			int j = iter.next();
			if (i == j) {
				log.info("Set next is "+ j + " == " + i);
			} else {
				log.info("Set next is "+ j + " != " + i);
			}
			Assert.assertTrue("set should seustain the order of insertion", i == j);
			++i;
		}
		Assert.assertTrue("set should not lose items at will", i == 25);
		
		log.info("");
		log.info("Removing every second item, set:");
		
		for (i = 1; i < 25; i+=2) {
			set.remove(i);
		}
		iter = set.iterator();
		first = true;
		while (iter.hasNext()) {
			if (first) first = false;
			else System.out.print(", ");
			System.out.print(iter.next());
		}
		log.info("");
		log.info("");
		
		i = 0;
		iter = set.iterator();
		while(iter.hasNext()) {
			int j = iter.next();
			if (i == j) {
				log.info("Set next is "+ j + " == " + i);
			} else {
				log.info("Set next is "+ j + " != " + i);
			}
			Assert.assertTrue("set should seustain the order of insertion", i == j);
			i += 2;
		}
		Assert.assertTrue("set should not lose items at will", i == 26);
		
		testOk();
	}

	public static void main(String[] args) {
		Test03_ConcurrentLinkedHashSet test = new Test03_ConcurrentLinkedHashSet();
		
		test.test();
	}
	
}
