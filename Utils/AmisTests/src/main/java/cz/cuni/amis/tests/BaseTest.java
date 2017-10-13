package cz.cuni.amis.tests;


import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;

import cz.cuni.amis.utils.simple_logging.SimpleLogging;

public class BaseTest {
	
	public static final String NEW_LINE = System.getProperty("line.separator");
	
	protected static Logger log; 
	
	@BeforeClass
	public static void baseTestBeforeClass() {
		SimpleLogging.initLogging();
		log = Logger.getLogger("Test");
		log.setLevel(Level.ALL);
		
		log.info("BaseTest.baseTestBeforeClass() BEGIN");
		
		Properties props = System.getProperties();
		log.info("  Logging initialized.");
		log.info("  System.getProperties():");
		List<Object> keys = new ArrayList<Object>(props.keySet());
		Collections.sort(keys, new Comparator() {
			@Override
			public int compare(Object o1, Object o2) {
				if (o1 == null && o2 == null) return 0;
				if (o1 == null) return -1;
				if (o2 == null) return -2;
				if (o1 instanceof String && o2 instanceof String) {
					return ((String)o1).compareTo((String)o2);
				}
				return o1.hashCode() - o2.hashCode();
			}			
		});
		for (Object key : keys) {
			if (key == null) continue;
			log.info("    " + key + " = " + props.getProperty(key.toString()));
		}
		log.info("  -------");
		log.info("  BaseTest.isWindows(): " + isWindows());
		log.info("  BaseTest.isLinux(): " + isLinux());
		log.info("  BaseTest.isMac(): " + isMac());
		log.info("  BaseTest.is32Bit(): " + is32Bit());
		log.info("  BaseTest.is64Bit(): " + is64Bit());
		if (isWindows() && isLinux() || isWindows() && isMac() || isLinux() && isMac()) {
			RuntimeException e = new RuntimeException("Environment not recognized... isWindows(), isLinux(), isMac() inconsistent!");
			log.severe(process(e));
			throw e;
		}
		if (is32Bit() && is64Bit()) {
			RuntimeException e = new RuntimeException("Environment not recognized, is32Bit() == true, is64Bit() == true!");
			log.severe(process(e));
			throw e;
		}
		if (!isWindows() && !isLinux() && !isMac()) {
			RuntimeException e = new RuntimeException("Environment not recognized, isWindows() == false, isLinux() == false, isMac() == false!");
			log.severe(process(e));
			throw e;
		}
		if (!is32Bit() && !is64Bit()) {
			RuntimeException e = new RuntimeException("Environment not recognized, is32Bit() == false, is64Bit() == false!");
			log.severe(process(e));
			throw e;
		}
		log.info("BaseTest.baseTestBeforeClass() END");
	}
	
	private long testStart;
	
	@Before
	public void beforeTest() {
		testStart = System.currentTimeMillis();
	}
	
	@After
	public void afterTest() {
		log.finest("Test time: " + (System.currentTimeMillis() - testStart) + " ms");
	}
	
	protected Level getLogLevel() {
		if (log.getLevel() == Level.ALL) return Level.FINEST;
		if (log.getLevel() == Level.OFF) return Level.WARNING;
		if (log.getLevel() == Level.CONFIG) return Level.WARNING;
		return log.getLevel();
	}
	
	protected void assertTrue(String msg, boolean cnd) {
		if (!cnd) testFailed(msg);
	}
	
	protected void assertFalse(String msg, boolean cnd) {
		assertTrue(msg, !cnd);
	}
	
	protected void assertFail(String msg) {
		RuntimeException e = new RuntimeException(msg); 
		log.severe(process(e));
		throw e;
	}
		
	protected void log(String msg) {
		log.info(msg);
	}
	
	protected void testOk() {		
		log.log(getLogLevel(), "---/// TEST OK ///---");
	}
	
	protected void testFailed() {
		testFailed("TEST FAILED!");
	}
	
	public static boolean isMac() {
		return System.getProperty("os.name").contains("Mac");
	}
	
	public static boolean isWindows() {
		return System.getProperty("os.name").contains("Windows");
	}
	
	public static boolean isLinux() {
		return System.getProperty("os.name").contains("Linux");
	}
	
	public static boolean is32Bit() {
		if (System.getProperty("sun.arch.data.model") != null) {
			return System.getProperty("sun.arch.data.model").contains("32");			
		}		
		return  !System.getProperty("java.vm.name").contains("64");
	}
	
	public static boolean is64Bit() {
		if (System.getProperty("sun.arch.data.model") != null) {
			return System.getProperty("sun.arch.data.model").contains("64");			
		}		
		return System.getProperty("java.vm.name").contains("64");
	}
	
	protected void testFailed(String msg) {
		RuntimeException e = new RuntimeException(msg);
		log.severe(process("TEST FAILED", e));
		throw e;	
	}
	
	public static String process(String message, Throwable e) {
		StringBuffer sb = new StringBuffer();
		if (message != null) {
			sb.append(message);
			sb.append(NEW_LINE);
		}
		Throwable cur = e;
		if (cur != null) {
			sb.append(cur.getClass().getName() + ": " + cur.getMessage() +
					cur.getStackTrace() == null || cur.getStackTrace().length == 0 ? 
							" (at UNAVAILABLE)"
						:	" (at " + cur.getStackTrace()[0].toString() + ")"
			);
			cur = cur.getCause();
			while (cur != null) {
				sb.append(NEW_LINE);
				sb.append("caused by: ");
				sb.append(cur.getClass().getName() + ": " + cur.getMessage() + 
						cur.getStackTrace() == null || cur.getStackTrace().length == 0 ? 
								" (at UNAVAILABLE)"
							:	" (at " + cur.getStackTrace()[0].toString() + ")"
				);
				cur = cur.getCause();
			}
			sb.append(NEW_LINE);
			sb.append("Stack trace:");
			sb.append(NEW_LINE);
			StringWriter stringError = new StringWriter();
			PrintWriter printError = new PrintWriter(stringError);
			e.printStackTrace(printError);
			sb.append(stringError.toString());
		}
		return sb.toString();
	}

	public static String process(Throwable e) {
		return process(null, e);
	}
	
}
