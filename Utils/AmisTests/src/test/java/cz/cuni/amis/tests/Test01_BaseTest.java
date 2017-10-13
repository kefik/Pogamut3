package cz.cuni.amis.tests;

import java.util.logging.Level;

import org.junit.Test;

public class Test01_BaseTest extends BaseTest {

	@Test
	public void test01() {
		boolean exception;
		
		try {
			log.info("Checking log initialization...");
			exception = false;
		} catch (Exception e) {
			e.printStackTrace();
			exception = true;
		}
		if (exception) throw new RuntimeException("SHOULD NOT THROW EXCEPTION!");
		
		try {
			assertTrue("Should be OK", true);
			exception = false;
		} catch (Exception e) {
			e.printStackTrace();
			exception = true;
		}
		if (exception) throw new RuntimeException("SHOULD NOT THROW EXCEPTION!");
		
		try {
			assertTrue("Should NOT be OK", false);
			exception = false;
		} catch (Exception e) {
			exception = true;
		}
		if (!exception) throw new RuntimeException("SHOULD HAVE THROWN EXCEPTION!");
		
		try {
			assertFalse("Should be OK", false);
			exception = false;
		} catch (Exception e) {
			e.printStackTrace();
			exception = true;
		}
		if (exception) throw new RuntimeException("SHOULD NOT THROW EXCEPTION!");
		
		try {
			assertFalse("Should NOT be OK", true);
			exception = false;
		} catch (Exception e) {
			exception = true;
		}
		if (!exception) throw new RuntimeException("SHOULD HAVE THROWN EXCEPTION!");
		
		try {
			testOk();
			exception = false;
		} catch (Exception e) {
			e.printStackTrace();
			exception = true;
		}
		if (exception) throw new RuntimeException("SHOULD NOT THROW EXCEPTION!");
		
		try {
			testFailed("Checking testFailed()");
			exception = false;
		} catch (Exception e) {
			exception = true;
		}
		if (!exception) throw new RuntimeException("SHOULD HAVE THROWN EXCEPTION!");
		
		try {
			log.setLevel(Level.INFO);
			exception = false;
		} catch (Exception e) {
			e.printStackTrace();
			exception = true;
		}
		if (exception) throw new RuntimeException("SHOULD NOT THROW EXCEPTION!");
		
		try {
			assertTrue("Log level should be INFO.", getLogLevel() == Level.INFO);
		} catch (Exception e) {
			e.printStackTrace();
			exception = true;
		}
		if (exception) throw new RuntimeException("SHOULD NOT THROW EXCEPTION!");
		
		testOk();
	}
	
}
