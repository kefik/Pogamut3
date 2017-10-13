package cz.cuni.amis.events.event;

import cz.cuni.amis.utils.listener.IListener;

/**
 * Abstract listener that listens for world events.
 * <p><p>
 * Every listener instance is defining also an event which it wants to listen on
 * by it's parameter type "Event". That's why there is a private "dummy" method in the ancestor class
 * that returns it's type so we can (by Java Reflection API) get the class of the event
 * where to hook the listener to. 
 *  
 * @author Jimmy
 * @param <EVENT>
 */
public interface IEventListener<EVENT extends IEvent> extends IListener<EVENT> {
		
}
