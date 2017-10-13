package cz.cuni.amis.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.junit.Test;

import cz.cuni.amis.tests.BaseTest;

public class Test04_Iterators extends BaseTest {

	@Test
	public void test01_Simple() {
		
		List<Integer> list1 = new ArrayList<Integer>();
		List<Integer> list2 = new ArrayList<Integer>();
		List<Integer> list3 = new ArrayList<Integer>();
		for (int i = 0; i < 5; ++i) {
			list1.add(i);
		}
		for (int i = 5; i < 10; ++i) {
			list2.add(i);
		}
		for (int i = 10; i < 15; ++i) {
			list3.add(i);
		}
		
		Iterator<Integer> iterator = new Iterators<Integer>(list1.iterator(), list2.iterator(), list3.iterator());
		
		for (int i = 0; i < 15; ++i) {
			if (!iterator.hasNext()) testFailed("iterator.hasNext() == false, can't be, there still need to be some elements");
			Integer value = iterator.next();
			if (value != i) {
				log.info("iterator.next() == " + value + " != " + i + " == expected");
				testFailed("iterator.next() has returned wrong element!");
			}
			log.info("iterator.next() == " + value + " == " + i + " == expected");
		}
		
		if (iterator.hasNext()) testFailed("iterator.hasNext() == true, but there should not be anymore elements");
		
		boolean exception = false;
		try {
			iterator.next();
		} catch (NoSuchElementException e) {
			log.info("iterator.next() exception caught, CORRECT!");
			exception = true;
		}
		if (!exception) testFailed("iterator.next() did not throw the exception even though it should have!");
		
		testOk();
	}
	
	@Test
	public void test02_Remove() {
		
		List<Integer> list1 = new ArrayList<Integer>();
		List<Integer> list2 = new ArrayList<Integer>();
		List<Integer> list3 = new ArrayList<Integer>();
		for (int i = 0; i < 5; ++i) {
			list1.add(i);
		}
		for (int i = 5; i < 10; ++i) {
			list2.add(i);
		}
		for (int i = 10; i < 15; ++i) {
			list3.add(i);
		}
		
		Iterator<Integer> iterator = new Iterators<Integer>(list1.iterator(), list2.iterator(), list3.iterator());
		
		for (int i = 0; i < 15; ++i) {
			if (!iterator.hasNext()) testFailed("iterator.hasNext() == false, can't be, there still need to be some elements");
			Integer value = iterator.next();
			if (value != i) {
				log.info("iterator.next() == " + value + " != " + i + " == expected");
				testFailed("iterator.next() has returned wrong element!");
			}
			log.info("iterator.next() == " + value + " == " + i + " == expected");
			iterator.remove();
			log.info("value removed");
		}
		
		if (iterator.hasNext()) testFailed("iterator.hasNext() == true, but there should not be anymore elements");
		
		if (list1.size() > 0) testFailed("All values should have been removed by iterator, but list1.size() == " + list1.size() + " > 0");
		if (list2.size() > 0) testFailed("All values should have been removed by iterator, but list2.size() == " + list1.size() + " > 0");
		if (list3.size() > 0) testFailed("All values should have been removed by iterator, but list3.size() == " + list1.size() + " > 0");
		log.info("All lists are empty.");
		
		boolean exception = false;
		try {
			iterator.next();
		} catch (NoSuchElementException e) {
			log.info("iterator.next() exception caught, CORRECT!");
			exception = true;
		}
		if (!exception) testFailed("iterator.next() did not throw the exception even though it should have!");
		
		testOk();
	}
	
	@Test
	public void test03_RemoveExceptions() {
		
		List<Integer> list1 = new ArrayList<Integer>();
		List<Integer> list2 = new ArrayList<Integer>();
		List<Integer> list3 = new ArrayList<Integer>();
		for (int i = 0; i < 5; ++i) {
			list1.add(i);
		}
		for (int i = 5; i < 10; ++i) {
			list3.add(i);
		}
		
		Iterator<Integer> iterator = new Iterators<Integer>(list1.iterator(), list2.iterator(), list3.iterator());
		
		boolean exception = false;
		try {
			iterator.remove();
		} catch (IllegalStateException e) {
			log.info("iterator.remove() exception caught, CORRECT!");
			exception = true;
		}
		if (!exception) testFailed("iterator.remove() did not throw the exception even though it should have!");
		exception = false;
	
		iterator.next();
		iterator.remove();
		try {
			iterator.remove();
		} catch (IllegalStateException e) {
			log.info("iterator.remove() exception caught, CORRECT!");
			exception = true;
		}
		if (!exception) testFailed("iterator.remove() did not throw the exception even though it should have!");
				
		testOk();
	}
	
	@Test
	public void test04_EmptyIterator_First() {
		
		List<Integer> list1 = new ArrayList<Integer>();
		List<Integer> list2 = new ArrayList<Integer>();
		List<Integer> list3 = new ArrayList<Integer>();
		for (int i = 0; i < 5; ++i) {
			list2.add(i);
		}
		for (int i = 5; i < 10; ++i) {
			list3.add(i);
		}
		
		Iterator<Integer> iterator = new Iterators<Integer>(list1.iterator(), list2.iterator(), list3.iterator());
		
		boolean exception = false;
		try {
			iterator.remove();
		} catch (IllegalStateException e) {
			log.info("iterator.remove() exception caught, CORRECT!");
			exception = true;
		}
		if (!exception) testFailed("iterator.remove() did not throw the exception even though it should have!");
		exception = false;
	
		iterator.next();
		iterator.remove();
		try {
			iterator.remove();
		} catch (IllegalStateException e) {
			log.info("iterator.remove() exception caught, CORRECT!");
			exception = true;
		}
		if (!exception) testFailed("iterator.remove() did not throw the exception even though it should have!");
		exception = false;
		
		for (int i = 1; i < 10; ++i) {
			if (!iterator.hasNext()) testFailed("iterator.hasNext() == false, can't be, there still need to be some elements");
			Integer value = iterator.next();
			if (value != i) {
				log.info("iterator.next() == " + value + " != " + i + " == expected");
				testFailed("iterator.next() has returned wrong element!");
			}
			log.info("iterator.next() == " + value + " == " + i + " == expected");
			iterator.remove();
			log.info("value removed");
		}
		
		if (iterator.hasNext()) testFailed("iterator.hasNext() == true, but there should not be anymore elements");
		
		if (list1.size() > 0) testFailed("All values should have been removed by iterator, but list1.size() == " + list1.size() + " > 0");
		if (list2.size() > 0) testFailed("All values should have been removed by iterator, but list2.size() == " + list1.size() + " > 0");
		if (list3.size() > 0) testFailed("All values should have been removed by iterator, but list3.size() == " + list1.size() + " > 0");
		log.info("All lists are empty.");
		
		try {
			iterator.next();
		} catch (NoSuchElementException e) {
			log.info("iterator.next() exception caught, CORRECT!");
			exception = true;
		}
		if (!exception) testFailed("iterator.next() did not throw the exception even though it should have!");
				
		testOk();
	}
	
	@Test
	public void test05_EmptyIterator_Middle() {
		
		List<Integer> list1 = new ArrayList<Integer>();
		List<Integer> list2 = new ArrayList<Integer>();
		List<Integer> list3 = new ArrayList<Integer>();
		for (int i = 0; i < 5; ++i) {
			list1.add(i);
		}
		for (int i = 5; i < 10; ++i) {
			list3.add(i);
		}
		
		Iterator<Integer> iterator = new Iterators<Integer>(list1.iterator(), list2.iterator(), list3.iterator());
		
		boolean exception = false;
		try {
			iterator.remove();
		} catch (IllegalStateException e) {
			log.info("iterator.remove() exception caught, CORRECT!");
			exception = true;
		}
		if (!exception) testFailed("iterator.remove() did not throw the exception even though it should have!");
		exception = false;
	
		iterator.next();
		iterator.remove();
		try {
			iterator.remove();
		} catch (IllegalStateException e) {
			log.info("iterator.remove() exception caught, CORRECT!");
			exception = true;
		}
		if (!exception) testFailed("iterator.remove() did not throw the exception even though it should have!");
		exception = false;
		
		for (int i = 1; i < 10; ++i) {
			if (!iterator.hasNext()) testFailed("iterator.hasNext() == false, can't be, there still need to be some elements");
			Integer value = iterator.next();
			if (value != i) {
				log.info("iterator.next() == " + value + " != " + i + " == expected");
				testFailed("iterator.next() has returned wrong element!");
			}
			log.info("iterator.next() == " + value + " == " + i + " == expected");
			iterator.remove();
			log.info("value removed");
		}
		
		if (iterator.hasNext()) testFailed("iterator.hasNext() == true, but there should not be anymore elements");
		
		if (list1.size() > 0) testFailed("All values should have been removed by iterator, but list1.size() == " + list1.size() + " > 0");
		if (list2.size() > 0) testFailed("All values should have been removed by iterator, but list2.size() == " + list1.size() + " > 0");
		if (list3.size() > 0) testFailed("All values should have been removed by iterator, but list3.size() == " + list1.size() + " > 0");
		log.info("All lists are empty.");
		
		try {
			iterator.next();
		} catch (NoSuchElementException e) {
			log.info("iterator.next() exception caught, CORRECT!");
			exception = true;
		}
		if (!exception) testFailed("iterator.next() did not throw the exception even though it should have!");
				
		testOk();
	}
	
	@Test
	public void test06_EmptyIterator_Last() {
		
		List<Integer> list1 = new ArrayList<Integer>();
		List<Integer> list2 = new ArrayList<Integer>();
		List<Integer> list3 = new ArrayList<Integer>();
		for (int i = 0; i < 5; ++i) {
			list1.add(i);
		}
		for (int i = 5; i < 10; ++i) {
			list2.add(i);
		}
		
		Iterator<Integer> iterator = new Iterators<Integer>(list1.iterator(), list2.iterator(), list3.iterator());
		
		boolean exception = false;
		try {
			iterator.remove();
		} catch (IllegalStateException e) {
			log.info("iterator.remove() exception caught, CORRECT!");
			exception = true;
		}
		if (!exception) testFailed("iterator.remove() did not throw the exception even though it should have!");
		exception = false;
	
		iterator.next();
		iterator.remove();
		try {
			iterator.remove();
		} catch (IllegalStateException e) {
			log.info("iterator.remove() exception caught, CORRECT!");
			exception = true;
		}
		if (!exception) testFailed("iterator.remove() did not throw the exception even though it should have!");
		exception = false;
		
		for (int i = 1; i < 10; ++i) {
			if (!iterator.hasNext()) testFailed("iterator.hasNext() == false, can't be, there still need to be some elements");
			Integer value = iterator.next();
			if (value != i) {
				log.info("iterator.next() == " + value + " != " + i + " == expected");
				testFailed("iterator.next() has returned wrong element!");
			}
			log.info("iterator.next() == " + value + " == " + i + " == expected");
			iterator.remove();
			log.info("value removed");
		}
		
		if (iterator.hasNext()) testFailed("iterator.hasNext() == true, but there should not be anymore elements");
		
		if (list1.size() > 0) testFailed("All values should have been removed by iterator, but list1.size() == " + list1.size() + " > 0");
		if (list2.size() > 0) testFailed("All values should have been removed by iterator, but list2.size() == " + list1.size() + " > 0");
		if (list3.size() > 0) testFailed("All values should have been removed by iterator, but list3.size() == " + list1.size() + " > 0");
		log.info("All lists are empty.");
		
		try {
			iterator.next();
		} catch (NoSuchElementException e) {
			log.info("iterator.next() exception caught, CORRECT!");
			exception = true;
		}
		if (!exception) testFailed("iterator.next() did not throw the exception even though it should have!");
				
		testOk();
	}
	
	@Test
	public void test07_HasNext_And_Remove() {
		
		List<Integer> list1 = new ArrayList<Integer>();
		List<Integer> list2 = new ArrayList<Integer>();
		List<Integer> list3 = new ArrayList<Integer>();
		for (int i = 0; i < 5; ++i) {
			list1.add(i);
		}
		for (int i = 5; i < 10; ++i) {
			list2.add(i);
		}
		for (int i = 10; i < 15; ++i) {
			list3.add(i);
		}
		
		Iterator<Integer> iterator = new Iterators<Integer>(list1.iterator(), list2.iterator(), list3.iterator());
		
		for (int i = 0; i < 15; ++i) {
			if (!iterator.hasNext()) testFailed("iterator.hasNext() == false, can't be, there still need to be some elements");
			Integer value = iterator.next();
			if (value != i) {
				log.info("iterator.next() == " + value + " != " + i + " == expected");
				testFailed("iterator.next() has returned wrong element!");
			}
			log.info("iterator.next() == " + value + " == " + i + " == expected");
			iterator.hasNext();
			iterator.remove();
			log.info("value removed");
		}
		
		if (iterator.hasNext()) testFailed("iterator.hasNext() == true, but there should not be anymore elements");
		
		if (list1.size() > 0) testFailed("All values should have been removed by iterator, but list1.size() == " + list1.size() + " > 0");
		if (list2.size() > 0) testFailed("All values should have been removed by iterator, but list2.size() == " + list1.size() + " > 0");
		if (list3.size() > 0) testFailed("All values should have been removed by iterator, but list3.size() == " + list1.size() + " > 0");
		log.info("All lists are empty.");
		
		boolean exception = false;
		try {
			iterator.next();
		} catch (NoSuchElementException e) {
			log.info("iterator.next() exception caught, CORRECT!");
			exception = true;
		}
		if (!exception) testFailed("iterator.next() did not throw the exception even though it should have!");
				
		testOk();
	}
	
	@Test
	public void test08_HasNext_And_Remove_EmptyIterator_First() {
		
		List<Integer> list1 = new ArrayList<Integer>();
		List<Integer> list2 = new ArrayList<Integer>();
		List<Integer> list3 = new ArrayList<Integer>();
		for (int i = 0; i < 5; ++i) {
			list2.add(i);
		}
		for (int i = 5; i < 10; ++i) {
			list3.add(i);
		}
		
		Iterator<Integer> iterator = new Iterators<Integer>(list1.iterator(), list2.iterator(), list3.iterator());
		
		boolean exception = false;
		iterator.hasNext();
		try {		
			iterator.remove();
		} catch (IllegalStateException e) {
			log.info("iterator.remove() exception caught, CORRECT!");
			exception = true;
		}
		if (!exception) testFailed("iterator.remove() did not throw the exception even though it should have!");
		exception = false;
	
		iterator.next();
		iterator.hasNext();
		iterator.remove();
		iterator.hasNext();
		try {			
			iterator.remove();
		} catch (IllegalStateException e) {
			log.info("iterator.remove() exception caught, CORRECT!");
			exception = true;
		}
		if (!exception) testFailed("iterator.remove() did not throw the exception even though it should have!");
		exception = false;
		
		for (int i = 1; i < 10; ++i) {
			if (!iterator.hasNext()) testFailed("iterator.hasNext() == false, can't be, there still need to be some elements");
			Integer value = iterator.next();
			if (value != i) {
				log.info("iterator.next() == " + value + " != " + i + " == expected");
				testFailed("iterator.next() has returned wrong element!");
			}
			log.info("iterator.next() == " + value + " == " + i + " == expected");
			iterator.hasNext();
			iterator.remove();
			log.info("value removed");
		}
		
		if (iterator.hasNext()) testFailed("iterator.hasNext() == true, but there should not be anymore elements");
		
		if (list1.size() > 0) testFailed("All values should have been removed by iterator, but list1.size() == " + list1.size() + " > 0");
		if (list2.size() > 0) testFailed("All values should have been removed by iterator, but list2.size() == " + list1.size() + " > 0");
		if (list3.size() > 0) testFailed("All values should have been removed by iterator, but list3.size() == " + list1.size() + " > 0");
		log.info("All lists are empty.");
		
		try {
			iterator.next();
		} catch (NoSuchElementException e) {
			log.info("iterator.next() exception caught, CORRECT!");
			exception = true;
		}
		if (!exception) testFailed("iterator.next() did not throw the exception even though it should have!");
				
		testOk();
	}
	
	@Test
	public void test09_HasNext_And_Remove_EmptyIterator_Middle() {
		
		List<Integer> list1 = new ArrayList<Integer>();
		List<Integer> list2 = new ArrayList<Integer>();
		List<Integer> list3 = new ArrayList<Integer>();
		for (int i = 0; i < 5; ++i) {
			list1.add(i);
		}
		for (int i = 5; i < 10; ++i) {
			list3.add(i);
		}
		
		Iterator<Integer> iterator = new Iterators<Integer>(list1.iterator(), list2.iterator(), list3.iterator());
		
		boolean exception = false;
		iterator.hasNext();
		try {			
			iterator.remove();
		} catch (IllegalStateException e) {
			log.info("iterator.remove() exception caught, CORRECT!");
			exception = true;
		}
		if (!exception) testFailed("iterator.remove() did not throw the exception even though it should have!");
		exception = false;
	
		iterator.next();
		iterator.hasNext();
		iterator.remove();
		iterator.hasNext();
		try {
			iterator.remove();
		} catch (IllegalStateException e) {
			log.info("iterator.remove() exception caught, CORRECT!");
			exception = true;
		}
		if (!exception) testFailed("iterator.remove() did not throw the exception even though it should have!");
		exception = false;
		
		for (int i = 1; i < 10; ++i) {
			if (!iterator.hasNext()) testFailed("iterator.hasNext() == false, can't be, there still need to be some elements");
			Integer value = iterator.next();
			if (value != i) {
				log.info("iterator.next() == " + value + " != " + i + " == expected");
				testFailed("iterator.next() has returned wrong element!");
			}
			log.info("iterator.next() == " + value + " == " + i + " == expected");
			iterator.hasNext();
			iterator.remove();
			log.info("value removed");
		}
		
		if (iterator.hasNext()) testFailed("iterator.hasNext() == true, but there should not be anymore elements");
		
		if (list1.size() > 0) testFailed("All values should have been removed by iterator, but list1.size() == " + list1.size() + " > 0");
		if (list2.size() > 0) testFailed("All values should have been removed by iterator, but list2.size() == " + list1.size() + " > 0");
		if (list3.size() > 0) testFailed("All values should have been removed by iterator, but list3.size() == " + list1.size() + " > 0");
		log.info("All lists are empty.");
		
		try {
			iterator.next();
		} catch (NoSuchElementException e) {
			log.info("iterator.next() exception caught, CORRECT!");
			exception = true;
		}
		if (!exception) testFailed("iterator.next() did not throw the exception even though it should have!");
				
		testOk();
	}
	
	@Test
	public void test10_HasNext_And_Remove_EmptyIterator_Last() {
		
		List<Integer> list1 = new ArrayList<Integer>();
		List<Integer> list2 = new ArrayList<Integer>();
		List<Integer> list3 = new ArrayList<Integer>();
		for (int i = 0; i < 5; ++i) {
			list1.add(i);
		}
		for (int i = 5; i < 10; ++i) {
			list2.add(i);
		}
		
		Iterator<Integer> iterator = new Iterators<Integer>(list1.iterator(), list2.iterator(), list3.iterator());
		
		boolean exception = false;
		iterator.hasNext();
		try {			
			iterator.remove();
		} catch (IllegalStateException e) {
			log.info("iterator.remove() exception caught, CORRECT!");
			exception = true;
		}
		if (!exception) testFailed("iterator.remove() did not throw the exception even though it should have!");
		exception = false;
	
		iterator.next();
		iterator.hasNext();
		iterator.remove();
		iterator.hasNext();
		try {
			iterator.remove();
		} catch (IllegalStateException e) {
			log.info("iterator.remove() exception caught, CORRECT!");
			exception = true;
		}
		if (!exception) testFailed("iterator.remove() did not throw the exception even though it should have!");
		exception = false;
		
		for (int i = 1; i < 10; ++i) {
			if (!iterator.hasNext()) testFailed("iterator.hasNext() == false, can't be, there still need to be some elements");
			Integer value = iterator.next();
			if (value != i) {
				log.info("iterator.next() == " + value + " != " + i + " == expected");
				testFailed("iterator.next() has returned wrong element!");
			}
			log.info("iterator.next() == " + value + " == " + i + " == expected");
			iterator.hasNext();
			iterator.remove();
			log.info("value removed");
		}
		
		if (iterator.hasNext()) testFailed("iterator.hasNext() == true, but there should not be anymore elements");
		
		if (list1.size() > 0) testFailed("All values should have been removed by iterator, but list1.size() == " + list1.size() + " > 0");
		if (list2.size() > 0) testFailed("All values should have been removed by iterator, but list2.size() == " + list1.size() + " > 0");
		if (list3.size() > 0) testFailed("All values should have been removed by iterator, but list3.size() == " + list1.size() + " > 0");
		log.info("All lists are empty.");
		
		try {
			iterator.next();
		} catch (NoSuchElementException e) {
			log.info("iterator.next() exception caught, CORRECT!");
			exception = true;
		}
		if (!exception) testFailed("iterator.next() did not throw the exception even though it should have!");
				
		testOk();
	}
	
	@Test
	public void test10_HasNext_And_Remove_EmptyIterator_First_NullIterator() {
		
		List<Integer> list1 = new ArrayList<Integer>();
		List<Integer> list2 = new ArrayList<Integer>();
		List<Integer> list3 = new ArrayList<Integer>();
		for (int i = 0; i < 5; ++i) {
			list2.add(i);
		}
		for (int i = 5; i < 10; ++i) {
			list3.add(i);
		}
		
		Iterator<Integer> iterator = new Iterators<Integer>(null, list1.iterator(), list2.iterator(), list3.iterator());
		
		boolean exception = false;
		iterator.hasNext();
		try {		
			iterator.remove();
		} catch (IllegalStateException e) {
			log.info("iterator.remove() exception caught, CORRECT!");
			exception = true;
		}
		if (!exception) testFailed("iterator.remove() did not throw the exception even though it should have!");
		exception = false;
	
		iterator.next();
		iterator.hasNext();
		iterator.remove();
		iterator.hasNext();
		try {			
			iterator.remove();
		} catch (IllegalStateException e) {
			log.info("iterator.remove() exception caught, CORRECT!");
			exception = true;
		}
		if (!exception) testFailed("iterator.remove() did not throw the exception even though it should have!");
		exception = false;
		
		for (int i = 1; i < 10; ++i) {
			if (!iterator.hasNext()) testFailed("iterator.hasNext() == false, can't be, there still need to be some elements");
			Integer value = iterator.next();
			if (value != i) {
				log.info("iterator.next() == " + value + " != " + i + " == expected");
				testFailed("iterator.next() has returned wrong element!");
			}
			log.info("iterator.next() == " + value + " == " + i + " == expected");
			iterator.hasNext();
			iterator.remove();
			log.info("value removed");
		}
		
		if (iterator.hasNext()) testFailed("iterator.hasNext() == true, but there should not be anymore elements");
		
		if (list1.size() > 0) testFailed("All values should have been removed by iterator, but list1.size() == " + list1.size() + " > 0");
		if (list2.size() > 0) testFailed("All values should have been removed by iterator, but list2.size() == " + list1.size() + " > 0");
		if (list3.size() > 0) testFailed("All values should have been removed by iterator, but list3.size() == " + list1.size() + " > 0");
		log.info("All lists are empty.");
		
		try {
			iterator.next();
		} catch (NoSuchElementException e) {
			log.info("iterator.next() exception caught, CORRECT!");
			exception = true;
		}
		if (!exception) testFailed("iterator.next() did not throw the exception even though it should have!");
				
		testOk();
	}
	
	@Test
	public void test12_HasNext_And_Remove_EmptyIterator_Middle_NullIterator() {
		
		List<Integer> list1 = new ArrayList<Integer>();
		List<Integer> list2 = new ArrayList<Integer>();
		List<Integer> list3 = new ArrayList<Integer>();
		for (int i = 0; i < 5; ++i) {
			list1.add(i);
		}
		for (int i = 5; i < 10; ++i) {
			list3.add(i);
		}
		
		Iterator<Integer> iterator = new Iterators<Integer>(list1.iterator(), null, list2.iterator(), list3.iterator());
		
		boolean exception = false;
		iterator.hasNext();
		try {			
			iterator.remove();
		} catch (IllegalStateException e) {
			log.info("iterator.remove() exception caught, CORRECT!");
			exception = true;
		}
		if (!exception) testFailed("iterator.remove() did not throw the exception even though it should have!");
		exception = false;
	
		iterator.next();
		iterator.hasNext();
		iterator.remove();
		iterator.hasNext();
		try {
			iterator.remove();
		} catch (IllegalStateException e) {
			log.info("iterator.remove() exception caught, CORRECT!");
			exception = true;
		}
		if (!exception) testFailed("iterator.remove() did not throw the exception even though it should have!");
		exception = false;
		
		for (int i = 1; i < 10; ++i) {
			if (!iterator.hasNext()) testFailed("iterator.hasNext() == false, can't be, there still need to be some elements");
			Integer value = iterator.next();
			if (value != i) {
				log.info("iterator.next() == " + value + " != " + i + " == expected");
				testFailed("iterator.next() has returned wrong element!");
			}
			log.info("iterator.next() == " + value + " == " + i + " == expected");
			iterator.hasNext();
			iterator.remove();
			log.info("value removed");
		}
		
		if (iterator.hasNext()) testFailed("iterator.hasNext() == true, but there should not be anymore elements");
		
		if (list1.size() > 0) testFailed("All values should have been removed by iterator, but list1.size() == " + list1.size() + " > 0");
		if (list2.size() > 0) testFailed("All values should have been removed by iterator, but list2.size() == " + list1.size() + " > 0");
		if (list3.size() > 0) testFailed("All values should have been removed by iterator, but list3.size() == " + list1.size() + " > 0");
		log.info("All lists are empty.");
		
		try {
			iterator.next();
		} catch (NoSuchElementException e) {
			log.info("iterator.next() exception caught, CORRECT!");
			exception = true;
		}
		if (!exception) testFailed("iterator.next() did not throw the exception even though it should have!");
				
		testOk();
	}
	
	@Test
	public void test13_HasNext_And_Remove_EmptyIterator_Last_NullIterator() {
		
		List<Integer> list1 = new ArrayList<Integer>();
		List<Integer> list2 = new ArrayList<Integer>();
		List<Integer> list3 = new ArrayList<Integer>();
		for (int i = 0; i < 5; ++i) {
			list1.add(i);
		}
		for (int i = 5; i < 10; ++i) {
			list2.add(i);
		}
		
		Iterator<Integer> iterator = new Iterators<Integer>(list1.iterator(), list2.iterator(), null, list3.iterator());
		
		boolean exception = false;
		iterator.hasNext();
		try {			
			iterator.remove();
		} catch (IllegalStateException e) {
			log.info("iterator.remove() exception caught, CORRECT!");
			exception = true;
		}
		if (!exception) testFailed("iterator.remove() did not throw the exception even though it should have!");
		exception = false;
	
		iterator.next();
		iterator.hasNext();
		iterator.remove();
		iterator.hasNext();
		try {
			iterator.remove();
		} catch (IllegalStateException e) {
			log.info("iterator.remove() exception caught, CORRECT!");
			exception = true;
		}
		if (!exception) testFailed("iterator.remove() did not throw the exception even though it should have!");
		exception = false;
		
		for (int i = 1; i < 10; ++i) {
			if (!iterator.hasNext()) testFailed("iterator.hasNext() == false, can't be, there still need to be some elements");
			Integer value = iterator.next();
			if (value != i) {
				log.info("iterator.next() == " + value + " != " + i + " == expected");
				testFailed("iterator.next() has returned wrong element!");
			}
			log.info("iterator.next() == " + value + " == " + i + " == expected");
			iterator.hasNext();
			iterator.remove();
			log.info("value removed");
		}
		
		if (iterator.hasNext()) testFailed("iterator.hasNext() == true, but there should not be anymore elements");
		
		if (list1.size() > 0) testFailed("All values should have been removed by iterator, but list1.size() == " + list1.size() + " > 0");
		if (list2.size() > 0) testFailed("All values should have been removed by iterator, but list2.size() == " + list1.size() + " > 0");
		if (list3.size() > 0) testFailed("All values should have been removed by iterator, but list3.size() == " + list1.size() + " > 0");
		log.info("All lists are empty.");
		
		try {
			iterator.next();
		} catch (NoSuchElementException e) {
			log.info("iterator.next() exception caught, CORRECT!");
			exception = true;
		}
		if (!exception) testFailed("iterator.next() did not throw the exception even though it should have!");
				
		testOk();
	}
	
	@Test
	public void test14_HasNext_And_Remove_EmptyIterator_Last_NullIterator() {
		
		List<Integer> list1 = new ArrayList<Integer>();
		List<Integer> list2 = new ArrayList<Integer>();
		List<Integer> list3 = new ArrayList<Integer>();
		for (int i = 0; i < 5; ++i) {
			list1.add(i);
		}
		for (int i = 5; i < 10; ++i) {
			list2.add(i);
		}
		
		Iterator<Integer> iterator = new Iterators<Integer>(list1.iterator(), list2.iterator(), list3.iterator(), null);
		
		boolean exception = false;
		iterator.hasNext();
		try {			
			iterator.remove();
		} catch (IllegalStateException e) {
			log.info("iterator.remove() exception caught, CORRECT!");
			exception = true;
		}
		if (!exception) testFailed("iterator.remove() did not throw the exception even though it should have!");
		exception = false;
	
		iterator.next();
		iterator.hasNext();
		iterator.remove();
		iterator.hasNext();
		try {
			iterator.remove();
		} catch (IllegalStateException e) {
			log.info("iterator.remove() exception caught, CORRECT!");
			exception = true;
		}
		if (!exception) testFailed("iterator.remove() did not throw the exception even though it should have!");
		exception = false;
		
		for (int i = 1; i < 10; ++i) {
			if (!iterator.hasNext()) testFailed("iterator.hasNext() == false, can't be, there still need to be some elements");
			Integer value = iterator.next();
			if (value != i) {
				log.info("iterator.next() == " + value + " != " + i + " == expected");
				testFailed("iterator.next() has returned wrong element!");
			}
			log.info("iterator.next() == " + value + " == " + i + " == expected");
			iterator.hasNext();
			iterator.remove();
			log.info("value removed");
		}
		
		if (iterator.hasNext()) testFailed("iterator.hasNext() == true, but there should not be anymore elements");
		
		if (list1.size() > 0) testFailed("All values should have been removed by iterator, but list1.size() == " + list1.size() + " > 0");
		if (list2.size() > 0) testFailed("All values should have been removed by iterator, but list2.size() == " + list1.size() + " > 0");
		if (list3.size() > 0) testFailed("All values should have been removed by iterator, but list3.size() == " + list1.size() + " > 0");
		log.info("All lists are empty.");
		
		try {
			iterator.next();
		} catch (NoSuchElementException e) {
			log.info("iterator.next() exception caught, CORRECT!");
			exception = true;
		}
		if (!exception) testFailed("iterator.next() did not throw the exception even though it should have!");
				
		testOk();
	}

	@Test
	public void test15_OnlyNull_1() {
				
		Iterator<Integer> iterator = new Iterators<Integer>((Iterator)null);
		
		boolean exception = false;
		if (iterator.hasNext()) testFailed("iterator.hasNext() == true even though there are no iterators!");
		try {			
			iterator.remove();
		} catch (IllegalStateException e) {
			log.info("iterator.remove() exception caught, CORRECT!");
			exception = true;
		}
		if (!exception) testFailed("iterator.remove() did not throw the exception even though it should have!");
		exception = false;
			
		try {
			iterator.next();
		} catch (NoSuchElementException e) {
			log.info("iterator.next() exception caught, CORRECT!");
			exception = true;
		}
		if (!exception) testFailed("iterator.next() did not throw the exception even though it should have!");
				
		testOk();
	}
	
	@Test
	public void test15_OnlyNull_2() {
				
		Iterator<Integer> iterator = new Iterators<Integer>((Iterator)null, (Iterator)null);
		
		boolean exception = false;
		if (iterator.hasNext()) testFailed("iterator.hasNext() == true even though there are no iterators!");
		try {			
			iterator.remove();
		} catch (IllegalStateException e) {
			log.info("iterator.remove() exception caught, CORRECT!");
			exception = true;
		}
		if (!exception) testFailed("iterator.remove() did not throw the exception even though it should have!");
		exception = false;
			
		try {
			iterator.next();
		} catch (NoSuchElementException e) {
			log.info("iterator.next() exception caught, CORRECT!");
			exception = true;
		}
		if (!exception) testFailed("iterator.next() did not throw the exception even though it should have!");
				
		testOk();
	}
	
	@Test
	public void test15_OnlyNull_3() {
				
		Iterator<Integer> iterator = new Iterators<Integer>((Iterable)null);
		
		boolean exception = false;
		if (iterator.hasNext()) testFailed("iterator.hasNext() == true even though there are no iterators!");
		try {			
			iterator.remove();
		} catch (IllegalStateException e) {
			log.info("iterator.remove() exception caught, CORRECT!");
			exception = true;
		}
		if (!exception) testFailed("iterator.remove() did not throw the exception even though it should have!");
		exception = false;
			
		try {
			iterator.next();
		} catch (NoSuchElementException e) {
			log.info("iterator.next() exception caught, CORRECT!");
			exception = true;
		}
		if (!exception) testFailed("iterator.next() did not throw the exception even though it should have!");
				
		testOk();
	}
	
	@Test
	public void test15_OnlyNull_4() {
				
		Iterator<Integer> iterator = new Iterators<Integer>((Iterable)null, (Iterable)null);
		
		boolean exception = false;
		if (iterator.hasNext()) testFailed("iterator.hasNext() == true even though there are no iterators!");
		try {			
			iterator.remove();
		} catch (IllegalStateException e) {
			log.info("iterator.remove() exception caught, CORRECT!");
			exception = true;
		}
		if (!exception) testFailed("iterator.remove() did not throw the exception even though it should have!");
		exception = false;
			
		try {
			iterator.next();
		} catch (NoSuchElementException e) {
			log.info("iterator.next() exception caught, CORRECT!");
			exception = true;
		}
		if (!exception) testFailed("iterator.next() did not throw the exception even though it should have!");
				
		testOk();
	}
	
}
