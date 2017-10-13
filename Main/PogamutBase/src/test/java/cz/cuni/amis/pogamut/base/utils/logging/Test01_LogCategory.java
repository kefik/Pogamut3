package cz.cuni.amis.pogamut.base.utils.logging;

import java.util.logging.Level;

import org.junit.Test;

import cz.cuni.amis.pogamut.base.utils.logging.stub.CheckPublisher;

import cz.cuni.amis.tests.BaseTest;
				
public class Test01_LogCategory extends BaseTest {
	
	String[] lines = new String[] {
			"Log level set to ALL.", "Hello", "Hi!", "I'm", "Jimmy.", "I am Ruda", "Bye. Bye."
	};
	
	CheckPublisher publisher = null;
	LogCategory log = null;
	
	@Test
	public void test() {
		publisher = new CheckPublisher(lines);
		log = new LogCategory("MyCategory");
		log.addHandler(publisher);
		
		log.setLevel(Level.ALL);
		if (log.isLoggable(Level.SEVERE)) log.severe(lines[1]);
		if (log.isLoggable(Level.WARNING)) log.warning(lines[2]);
		if (log.isLoggable(Level.INFO)) log.info(lines[3]);
		if (log.isLoggable(Level.FINE)) log.fine(lines[4]);
		if (log.isLoggable(Level.FINER)) log.finer(lines[5]);
		if (log.isLoggable(Level.FINEST)) log.finest(lines[6]);
		
		publisher.checkExpectEmpty();
		
		System.out.println("---/// TEST OK ///---");
	}
	
	public static void main(String[] args) {
		Test01_LogCategory test = new Test01_LogCategory();
		test.test();
	}
}
