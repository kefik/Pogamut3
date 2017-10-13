package cz.cuni.amis.utils;

import org.junit.Test;

import cz.cuni.amis.tests.BaseTest;

/**
 * This is actually not testing anything (apart whether {@link ExceptionToString} won't throw NPE).
 * @author Jimmy
 *
 */
public class Test03_ExceptionToString extends BaseTest {

	@Test
	public void test01() {
		try {
			throw new RuntimeException("We're doomed");
		} catch (Exception e) {
			System.out.println(ExceptionToString.process("Hups!", e));
		}
		testOk();
	}
	
	private void failAt1(int num) {
		if (num == 0) {
			throw new RuntimeException("Failing!");
		}
		failAt1(num-1);
	}
	
	private void failAt2(int num) {
		if (num == 0) {
			failAt1(5);
		}
		failAt2(num-1);
	}
	
	private void failSafe(int num) {
		try {
			if (num == 0) {
				failAt1(2);
			}
			failSafe(num-1);
		} catch (Exception e) {
			throw new RuntimeException("failSafe(" + num + ") caught", e);
		}
	}
	
	@Test
	public void test02() {
		try {
			failAt1(5);
		} catch (Exception e) {
			System.out.println(ExceptionToString.process("Hups!", e));
		}
		testOk();
	}
	
	@Test
	public void test03() {
		try {
			failAt2(5);
		} catch (Exception e) {
			System.out.println(ExceptionToString.process("Hups!", e));
		}
		testOk();
	}
	
	@Test
	public void test04() {
		try {
			failSafe(5);
		} catch (Exception e) {
			System.out.println(ExceptionToString.process("Hups!", e));
		}
		testOk();
	}
	
}
