package cz.cuni.amis.pogamut.base.component.bus.event;

import cz.cuni.amis.pogamut.base.component.IComponent;
import cz.cuni.amis.pogamut.base.component.bus.IComponentEvent;
import cz.cuni.amis.utils.token.Token;

/**
 * Class for accepting events - provides description of which event should be checked and method that performs the check.
 * <p><p>
 * Preferred way of usage are anonymous classes which override {@link EventFilter#accept(IComponentEvent)} that otherwise
 * always return null.
 * 
 * @author Jimmy
 *
 * @param <T>
 */
public class EventFilter<T> implements WaitForEvent.IEventFilter<T>  {

	private Class<T> eventClass;
	
	private Class<? extends IComponent> componentClass;

	private Token componentId;

	/**
	 * @param eventClass which events should be examined inside {@link EventFilter#accept(IComponentEvent)}
	 */
	public EventFilter(Class<T> eventClass) {
		this.eventClass = eventClass;
	}
	
	/**
	 * 
	 * @param eventClass which events should be examined inside {@link EventFilter#accept(IComponentEvent)}
	 * @param componentClass from which component class the events are examined inside {@link EventFilter#accept(IComponentEvent)}
	 */
	public EventFilter(Class<T> eventClass, Class<? extends IComponent> componentClass) {
		this.eventClass = eventClass;
		this.componentClass = componentClass;
	}
	
	/**
	 * @param eventClass which events should be examined inside {@link EventFilter#accept(IComponentEvent)}
	 * @param componentId from which component id the events are examined inside {@link EventFilter#accept(IComponentEvent)}
	 */
	public EventFilter(Class<T> eventClass, Token componentId) {
		this.eventClass = eventClass;
		this.componentId = componentId;
	}

	@Override
	public boolean accept(T event) {
		return true;
	}
		
	@Override
	public Class getComponentClass() {
		return componentClass;
	}

	@Override
	public Token getComponentId() {
		return componentId;
	}

	@Override
	public Class<T> getEventClass() {
		return eventClass;
	}

}