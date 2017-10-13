package cz.cuni.amis.pogamut.base.utils.jmx.flag;

import java.io.Serializable;

import javax.management.InstanceAlreadyExistsException;
import javax.management.ListenerNotFoundException;
import javax.management.MBeanNotificationInfo;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.Notification;
import javax.management.NotificationBroadcasterSupport;
import javax.management.NotificationEmitter;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import javax.management.ObjectName;

import cz.cuni.amis.pogamut.base.utils.jmx.PogamutJMX;
import cz.cuni.amis.utils.flag.Flag;
import cz.cuni.amis.utils.flag.FlagListener;

/**
 * Translates Flag events to JMX events. Adds a listener on the flag and resends 
 * the events to supplied broadcaster support.
 * @author Ik
 */
public class JMXFlagDecorator<T extends Serializable> implements JMXFlagDecoratorMBean, NotificationEmitter {

    protected Flag<T> flag = null;
    protected ObjectName source;
    protected int eventCounter = 0;
    protected String flagName = null;

    protected FlagListener<T> listener = new FlagListener<T>() {

        @Override
        public void flagChanged(T changedValue) {
            Notification notification = new Notification(
                    flagName,
                    source,
                    eventCounter++,
                    changedValue.toString());
            notification.setUserData(flag.getFlag());
            nbs.sendNotification(notification);
        }
    };

    /**
     * 
     * @param flag Flag to be exposed through JMX.
     * @param source MBean or ObjectName of the object where the flag resides.
     * @param nbs NotificationBroadcasterSupport through which the events will be send.
     */
    public JMXFlagDecorator(Flag<T> flag, ObjectName source, MBeanServer mbs, String flagName) throws MalformedObjectNameException, InstanceAlreadyExistsException, MBeanRegistrationException, NotCompliantMBeanException {
        this.flag = flag;
        this.source = source;
        this.flagName = flagName;
        ObjectName name = PogamutJMX.getObjectName(source, flagName, PogamutJMX.FLAGS_SUBTYPE);
        mbs.registerMBean(this, name);
        flag.addListener(listener);
    }

    /**
     * Stops listening for the flag events.
     */
    public void stop() {
        flag.removeListener(listener);
    }

    /**
     * 
     * @return Notification info about this events possibly raised by this flag.
     */
    public MBeanNotificationInfo getMBeanNotificationInfo() {
        return new MBeanNotificationInfo(new String[]{flagName},
                Notification.class.getName(), "The flag has changed it's value.");
    }

    @Override
    public Serializable getFlag() {
        return flag.getFlag();
    }

    
        /**
     * Support object for sending notifications.
     */
    protected NotificationBroadcasterSupport nbs = new NotificationBroadcasterSupport();
    @Override
    public void removeNotificationListener(NotificationListener listener, NotificationFilter filter, Object handback) throws ListenerNotFoundException {
        nbs.removeNotificationListener(listener, filter, handback);
    }

    @Override
    public void addNotificationListener(NotificationListener listener, NotificationFilter filter, Object handback) throws IllegalArgumentException {
        nbs.addNotificationListener(listener, filter, handback);
    }

    @Override
    public void removeNotificationListener(NotificationListener listener) throws ListenerNotFoundException {
        nbs.removeNotificationListener(listener);
    }

    @Override
    public MBeanNotificationInfo[] getNotificationInfo() {
        return new MBeanNotificationInfo[]{
                    getMBeanNotificationInfo()
                };
    }
}
