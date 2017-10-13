package cz.cuni.amis.pogamut.base.utils.logging.jmx;

import cz.cuni.amis.pogamut.base.agent.jmx.AgentJMXComponents;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

import javax.management.ListenerNotFoundException;
import javax.management.MBeanNotificationInfo;
import javax.management.MalformedObjectNameException;
import javax.management.NotificationBroadcasterSupport;
import javax.management.NotificationEmitter;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;

import cz.cuni.amis.pogamut.base.utils.logging.LogPublisher;
import javax.management.ObjectName;


public class JMXLogPublisher extends LogPublisher implements JMXLogPublisherMBean, NotificationEmitter {	
	/**
         * MBean's id.
         */
        ObjectName objectName = null;

	/**
	 * Initialize publisher with the simplest formatter - just publishing the message.
	 */
	public JMXLogPublisher() {
		super(new Formatter(){
			@Override
			public String format(LogRecord record) {				
				return record.getMessage();
			}			
		});
	}
	
	/**
	 * Initialize the publisher with prespecified formatter.
	 * <p><p>
	 * WARNING: if formatter is null, nothing will be published via JMX!
	 * @param formatter
	 */
	public JMXLogPublisher(Formatter formatter) {
		super(formatter);
	}
	
	/**
	 * Support for the JMX notification broadcasting. Used to send notifications.
	 */
	protected NotificationBroadcasterSupport notification = 
		new NotificationBroadcasterSupport(
				new MBeanNotificationInfo(
					new String[]{ JMXLogRecordNotification.NOTIFICATION_TYPE },
					JMXLogRecordNotification.class.getName(), //Must be fully qualified name of the class "Log record notification",
					"Allows you to get messages from the logger"
				)
		);
	
	/**
	 * Category name of the publisher.
	 */
	protected String categoryName;
	
	public JMXLogPublisher(ObjectName parent, String categoryName) throws MalformedObjectNameException {
		this.categoryName = categoryName;
        this.objectName = JMXLogCategories.getJMXLogCategoryName(parent, categoryName);
	}

        @Override
	public String getCategoryName() {
		return categoryName;
	}
	
	/**
	 * Sequence number for the published logs.
	 */
	protected long sequenceNumber = 1;

	@Override
	public void close() throws SecurityException {
	}

	@Override
	public void flush() {
	}
	
	@Override
	public void publish(LogRecord record) {
		Formatter actualFormatter = formatter;
		if (actualFormatter != null) {
			String message = actualFormatter.format(record); 
			notification.sendNotification(
				new JMXLogRecordNotification(
					objectName,
					sequenceNumber++, 
					record.getMillis(),
					message,
					record
				)
			);
		}
	}

	/**
	 * Not used, things are published directly via publish(LogRecord)
	 */
	@Override
	public void publish(LogRecord record, String formattedMsg) {
		// not used, things published in publish(LogRecord) method
	}
	
	//
	// JMX Notification Interface follows
	//
	
	@Override
	public void removeNotificationListener(NotificationListener listener,
			NotificationFilter filter, Object handback)
			throws ListenerNotFoundException {
		notification.removeNotificationListener(listener, filter, handback);		
	}

	@Override
	public void addNotificationListener(NotificationListener listener,
			NotificationFilter filter, Object handback)
			throws IllegalArgumentException {
		notification.addNotificationListener(listener, filter, handback);		
	}

	@Override
	public MBeanNotificationInfo[] getNotificationInfo() {		
		return notification.getNotificationInfo();
	}

	@Override
	public void removeNotificationListener(NotificationListener listener)
			throws ListenerNotFoundException {
		notification.removeNotificationListener(listener);		
	}

}
