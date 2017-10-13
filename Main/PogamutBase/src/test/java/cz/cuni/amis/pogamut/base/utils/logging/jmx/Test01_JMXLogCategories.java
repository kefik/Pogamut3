/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cuni.amis.pogamut.base.utils.logging.jmx;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.Notification;
import javax.management.NotificationListener;
import javax.management.ObjectName;
import javax.management.ReflectionException;

import junit.framework.Assert;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import cz.cuni.amis.pogamut.base.utils.Pogamut;
import cz.cuni.amis.pogamut.base.utils.jmx.PogamutJMX;
import cz.cuni.amis.pogamut.base.utils.logging.ILogCategories;
import cz.cuni.amis.pogamut.base.utils.logging.LogCategories;
import cz.cuni.amis.pogamut.base.utils.logging.LogCategory;
import cz.cuni.amis.utils.exception.PogamutException;
import java.util.logging.Level;

/**
 * Exports ILogCategories through JMX and then tests receiving messages over the
 * JMX.
 * 
 * @author Ik
 */
import cz.cuni.amis.tests.BaseTest;
				
public class Test01_JMXLogCategories extends BaseTest {

	public Test01_JMXLogCategories() {
	}

	@BeforeClass
	public static void setUpClass() throws Exception {
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
	}

	@Before
	public void setUp() {
	}

	@After
	public void tearDown() {
	}

	CountDownLatch messageReceivedLatch = new CountDownLatch(1);
	String receivedMessage = null;

	// TODO add test methods here.
	// The methods must be annotated with annotation @Test. For example:
	//
	@Test
	public void receiveLogEventThroughJMX()
			throws InstanceAlreadyExistsException, MBeanRegistrationException,
			NotCompliantMBeanException, MalformedObjectNameException,
			PogamutException, MalformedURLException, IOException,
			InterruptedException, MBeanException, InstanceNotFoundException,
			AttributeNotFoundException, ReflectionException {
		
		final String testMsg = "TEST LOG MESSAGE FROM receiveLogEventThroughJMX()";
		
		ObjectName parentName = PogamutJMX.getObjectName("testDomain", "root", "test");
		
		
		// export the log on the MBean server
		ILogCategories logCategories = new LogCategories();
		
		String testLogCategoryNameStr = "testLogCategory";
		LogCategory testLog = logCategories.getCategory(testLogCategoryNameStr);
				
		JMXLogCategories jmxLogCategories = new JMXLogCategories(
				logCategories,
				Pogamut.getPlatform().getMBeanServer(),
				parentName
		);

		// connect through RMI and get the proxy
		MBeanServerConnection mbsc = Pogamut.getPlatform().getMBeanServerConnection();
		ObjectName logCatsName = jmxLogCategories.getJMXLogCategoriesName();
		
		// get the name of all log category names
		String[] catNames = (String[]) mbsc.getAttribute(logCatsName, "CategoryNames");		
		boolean found = false;
		for (String catName : catNames) {
			if (catName.equals(testLogCategoryNameStr)) {
				found = true;
				break;
			}
		}
		Assert.assertTrue(testLogCategoryNameStr + " must be among exported log category names", found);
		
		// get the object name for the test log category
		ObjectName testCategoryName = 
			(ObjectName) 
			mbsc.invoke(
				logCatsName, 
				"getJMXLogCategoryName", 
				new Object[]{ testLogCategoryNameStr }, 
				new String[] { "java.lang.String" }
			);
		
		// add the listener
		mbsc.addNotificationListener(testCategoryName,
			new NotificationListener() {

				public void handleNotification(Notification notification,
						Object handback) {
					receivedMessage = notification.getMessage();
					messageReceivedLatch.countDown();
				}
			}, null, this
		);

		// send log event
		if (testLog.isLoggable(Level.INFO)) testLog.info(testMsg);
		// wait
		messageReceivedLatch.await(30000, TimeUnit.MILLISECONDS);
		// compare
		assertTrue("Received message must contain testMsg", receivedMessage.contains(testMsg));
		
		System.out.println("---/// TEST OK ///---");
		
		Pogamut.getPlatform().close();
	}
}
