package cz.cuni.amis.pogamut.base.utils.jmx.flag;

import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.Notification;
import javax.management.NotificationListener;
import javax.management.ObjectName;

import cz.cuni.amis.pogamut.base.utils.jmx.PogamutJMX;
import cz.cuni.amis.utils.flag.Flag;

/**
 * Proxy exposing remote JMX MBean Flag as local flag.
 * @author ik
 */
public class FlagJMXProxy<T> extends Flag<T> {

    NotificationListener listener = null;

    public FlagJMXProxy(final ObjectName source, final MBeanServerConnection mbsc, final String flagName) throws MalformedObjectNameException {
        ObjectName name = PogamutJMX.getObjectName(source, flagName, PogamutJMX.FLAGS_SUBTYPE);
        try {
            listener = new NotificationListener() {

                @Override
                public void handleNotification(Notification notification, Object handback) {
                    if (notification.getSource().equals(source) && notification.getType().equals(flagName)) {
                        setFlag((T) notification.getUserData());
                    }
                }
            };
            // get current value of the flag
            T val = (T) mbsc.getAttribute(name, "Flag");
            setFlag(val);

            /* NOTE filters are send over RMI to the server !!! it is better to
             handle filtering in the listener itself.
             
            NotificationFilter nf = new NotificationFilter() {

            @Override
            public boolean isNotificationEnabled(Notification notification) {
            return notification.getSource().equals(source) && notification.getType().equals(flagName);
            }
            };
             */
            mbsc.addNotificationListener(name, listener, null, mbsc);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }
}
