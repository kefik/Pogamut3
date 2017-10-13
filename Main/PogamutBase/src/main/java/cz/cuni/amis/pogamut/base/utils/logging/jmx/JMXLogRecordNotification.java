package cz.cuni.amis.pogamut.base.utils.logging.jmx;

import java.io.Serializable;
import java.util.logging.LogRecord;

import javax.management.Notification;

public class JMXLogRecordNotification extends Notification implements Serializable {
	
	private static final long serialVersionUID = 7167453889653639394L;

	public static final String NOTIFICATION_TYPE = "notifications.pogamut.jmx.logrecord";
	
	private JMXLogRecordContainer logRecordContainer = null;
	
	public JMXLogRecordNotification(Object source, long sequenceNumber, long timeStamp, String msg, LogRecord logRecord) {
		super(NOTIFICATION_TYPE, source, sequenceNumber, timeStamp, msg);


        this.logRecordContainer = new JMXLogRecordContainer(logRecord);

        this.setUserData(logRecordContainer);

  /*      System.out.println("JMXLogRecordInfo, number of parameters " + logRecord.getParameters().length);
        for (Object o : logRecord.getParameters()) {
            System.out.println(" * " + o + " class " + (o == null ? "null" : o.getClass()));
        }

        logRecordContainer.printInfo();
*/
	}
	
}
