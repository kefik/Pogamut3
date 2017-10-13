package cz.cuni.amis.utils;

import junit.framework.Assert;

import org.junit.Test;

import cz.cuni.amis.tests.BaseTest;

public class Test02_NKeyCommutative extends BaseTest {

	/**
	 * Test method - checks whether key1.equals(key2) (or not equals according to 'equals' parameter).
	 * @param key1
	 * @param key2
	 * @param equals
	 */
	private static void testEquals(NKeyCommutative key1, NKeyCommutative key2, boolean equals) {
		if (equals) {
			System.out.println(key1 + " == " + key2 + " ?");
			Assert.assertTrue("NO! Failure...", key1.equals(key2));
		} else {
			System.out.println(key1 + " != " + key2 + " ?");
			Assert.assertTrue("NO! Failure...", !key1.equals(key2));
		}
		System.out.println("YES");
	}

	@Test
	public void test() {
		NKeyCommutative key1 = new NKeyCommutative(new Integer[]{1,2});
		NKeyCommutative key2 = new NKeyCommutative(new Integer[]{1,2,3});
		NKeyCommutative key3 = new NKeyCommutative(new Integer[]{2,1});
		NKeyCommutative key4 = new NKeyCommutative(new Integer[]{2,3,1});
		NKeyCommutative key5 = new NKeyCommutative(new Integer[]{1,2,3,4});
		NKeyCommutative key6 = new NKeyCommutative(new Integer[]{1,2}, new Integer[]{3,4});
		NKeyCommutative key7 = new NKeyCommutative(new Integer[]{2,1}, new Integer[]{4,3});
		NKeyCommutative key8 = new NKeyCommutative(new Integer[]{1,2}, new Integer[]{3,4}, new Integer[]{5});


		testEquals(key1, key1, true);
		testEquals(key2, key2, true);
		testEquals(key3, key3, true);
		testEquals(key4, key4, true);
		testEquals(key5, key5, true);
		testEquals(key6, key6, true);
		testEquals(key7, key7, true);
		testEquals(key8, key8, true);
		testEquals(key1, key2, false);
		testEquals(key1, key3, true);
		testEquals(key1, key4, false);
		testEquals(key1, key5, false);
		testEquals(key1, key6, false);
		testEquals(key1, key7, false);
		testEquals(key1, key8, false);
		testEquals(key2, key1, false);
		testEquals(key2, key3, false);
		testEquals(key2, key4, true);
		testEquals(key2, key5, false);
		testEquals(key2, key6, false);
		testEquals(key2, key7, false);
		testEquals(key2, key8, false);
		testEquals(key3, key1, true);
		testEquals(key3, key2, false);
		testEquals(key3, key4, false);
		testEquals(key3, key5, false);
		testEquals(key3, key6, false);
		testEquals(key3, key7, false);
		testEquals(key3, key8, false);
		testEquals(key4, key1, false);
		testEquals(key4, key2, true);
		testEquals(key4, key3, false);
		testEquals(key4, key5, false);
		testEquals(key4, key6, false);
		testEquals(key4, key7, false);
		testEquals(key4, key8, false);
		testEquals(key5, key1, false);
		testEquals(key5, key2, false);
		testEquals(key5, key3, false);
		testEquals(key5, key4, false);
		testEquals(key5, key6, true);
		testEquals(key5, key7, true);
		testEquals(key5, key8, false);
		testEquals(key6, key1, false);
		testEquals(key6, key2, false);
		testEquals(key6, key3, false);
		testEquals(key6, key4, false);
		testEquals(key6, key5, true);
		testEquals(key6, key7, true);
		testEquals(key6, key8, false);
		testEquals(key7, key1, false);
		testEquals(key7, key2, false);
		testEquals(key7, key3, false);
		testEquals(key7, key4, false);
		testEquals(key7, key5, true);
		testEquals(key7, key6, true);
		testEquals(key7, key8, false);
		testEquals(key8, key1, false);
		testEquals(key8, key2, false);
		testEquals(key8, key3, false);
		testEquals(key8, key4, false);
		testEquals(key8, key5, false);
		testEquals(key8, key6, false);
		testEquals(key8, key7, false);
		
		testOk();
	}


}
