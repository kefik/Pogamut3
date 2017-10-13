package cz.cuni.amis.pogamut.base.utils.jmx.flag;

import javax.management.AttributeChangeNotification;
import javax.management.Notification;

/**
 *
 * @author Ik
 */
public class FlagChangeNotification<T> extends Notification {
    
	
    public FlagChangeNotification(JMXFlagDecorator d, T newValue) {
    	// TODO: what is exactly NOTIFICATION TYPE? It seems that the identifier or notification type is
    	// used to distinguish between multiple types of notifications sent by a single emitter
    	// The question is - does every flag have own emitter?
        super("F", d.source, d.eventCounter++, newValue.toString());
        setUserData(newValue);
    }
 
}
