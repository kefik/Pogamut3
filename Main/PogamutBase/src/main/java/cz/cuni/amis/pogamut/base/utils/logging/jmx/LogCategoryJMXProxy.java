package cz.cuni.amis.pogamut.base.utils.logging.jmx;

import cz.cuni.amis.pogamut.base.utils.logging.LogCategory;
import java.io.IOException;
import java.util.logging.LogRecord;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanServerConnection;
import javax.management.Notification;
import javax.management.NotificationListener;
import javax.management.ObjectName;

/**
 * Proxies a single log category, intended to be used only for registering handlers.
 * TODO what to do when user logs a message on this instance? throw exception?
 * @author ik
 */
public class LogCategoryJMXProxy extends LogCategory {

    ObjectName categoryObjectName = null;
	private MBeanServerConnection mbsc;

    public LogCategoryJMXProxy(MBeanServerConnection mbsc, ObjectName parent, String categoryName) throws InstanceNotFoundException, IOException {
        super(categoryName);
        categoryObjectName = JMXLogCategories.getJMXLogCategoryName(parent, categoryName);
        this.mbsc = mbsc;
    }
    
    public void enableLogReading() throws InstanceNotFoundException, IOException {
    	// add notification listener
        mbsc.addNotificationListener(categoryObjectName, new NotificationListener() {

            @Override
            public void handleNotification(Notification notification, Object handback) {
                // user data is by convention in JMXLogRecordContainer 
                // because of trouble with serialializing parameters of a LogRecord
                JMXLogRecordContainer container = (JMXLogRecordContainer)notification.getUserData();
                //container.printInfo();

                log(container.getRecordWithParameters());
            }
        }, null, null);
    }
    
}
