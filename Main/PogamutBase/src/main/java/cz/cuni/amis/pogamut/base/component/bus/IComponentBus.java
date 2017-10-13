package cz.cuni.amis.pogamut.base.component.bus;

import java.util.Set;

import cz.cuni.amis.pogamut.base.component.IComponent;
import cz.cuni.amis.pogamut.base.component.bus.event.IFatalErrorEvent;
import cz.cuni.amis.pogamut.base.component.bus.event.IResetEvent;
import cz.cuni.amis.pogamut.base.component.bus.exception.ComponentBusErrorException;
import cz.cuni.amis.pogamut.base.component.bus.exception.ComponentBusNotRunningException;
import cz.cuni.amis.pogamut.base.component.bus.exception.ComponentIdClashException;
import cz.cuni.amis.pogamut.base.component.bus.exception.FatalErrorPropagatingEventException;
import cz.cuni.amis.pogamut.base.component.bus.exception.MoreComponentsForClassException;
import cz.cuni.amis.pogamut.base.component.bus.exception.ResetFailedException;
import cz.cuni.amis.utils.token.IToken;

/**
 * Component bus is meant as "event bus". Every agent will have exactly one component bus
 * that serves as general-purpose event propagation bus.
 * <p><p>
 * Component bus stops its work when {@link IFatalErrorEvent} is passed to the component bus
 * or an exception is thrown by any listener.
 * <p><p>
 * Every component that uses the bus should register at least one listener - listener for {@link IFatalErrorEvent} events
 * as it is something as "global exception". Whenever the component is notified about the fatal error, it should stop its work.
 * <p><p>
 * Also - whenever the component catches an exception that it can't handle, it is bound to raise {@link IFatalErrorEvent} to 
 * notify the system that it is failing. If the component is not a critical piece of the puzzle, it may raise different event.
 * 
 * @author Jimmy
 */
public interface IComponentBus extends IComponent {

	//
	//
	// UTILITY METHODS
	//
	//
	
	/**
	 * Whether the bus is propagating events.
	 * <p><p>
	 * Whenever {@link IFatalErrorEvent} is caught - the component bus will stop working immediately and propagate
	 * the fatal error as the last event (so if you will receive {@link IFatalErrorEvent} inside your object then it
	 * is pointless to send more events to the bus.
	 * <p><p>
	 * The only way to make the bus working is to reset() it.
	 * <p><p>
	 * Use this method to examine whether {@link IFatalErrorEvent} occured on the bus (and was broadcast) or not.
	 * 
	 * @return
	 */
	public boolean isRunning();
	
	/**
	 * Restarts the bus and the whole agent system.
	 * <p><p>
	 * Broadcasts {@link IResetEvent} event. All components should stop working upon reset event and reinitialize their inner data structures
	 * into the initial state. If reset is not possible, the component should throw an exception.
	 * <p><p>
	 * If exception is caught during the reset(), the {@link IFatalErrorEvent} is propagated and the component bus won't start.
	 * <p><p> 
	 * If reset() is called, when the component bus is still running, it first broadcasts {@link IFatalErrorEvent}.
	 * <p><p>
	 * Note that you can't propagate any events during the reset (they will be discarded).
	 * 
	 *  @throws ResetFailedException thrown when an exception happens during reset operation, nested exception is available through {@link Exception#getCause()}
	 */
	public void reset() throws ResetFailedException;
	
	
	//
	//
	// COMPONENTS
	//
	//
	
	/**
	 * Registers component into the bus.
	 * <p><p>
	 * If different component with the same {@link IComponent#getComponentId()} is already registered, 
	 * than it throws {@link ComponentIdClashException} and broadcast {@link IFatalErrorEvent}. 
	 * 
	 * @param component
	 * @throws ComponentIdClashException
	 */
	public void register(IComponent component) throws ComponentIdClashException;
	
	/**
	 * Removes component from the bus.
	 * 
	 * @param component
	 */
	public void remove(IComponent component);
	
	/**
	 * Returns registered component of 'componentId'.
	 * <p><p>
	 * Returns null if no such component exists.
	 * 
	 * @param componentId
	 * @return
	 */
	public IComponent getComponent(IToken componentId);
	
	/**
	 * Returns component of class 'cls', if there is more then one component for that
	 * class than an exception is thrown.
	 * <p><p>
	 * If no components exist for 'cls', returns empty set.
	 * 
	 * @param <T>
	 * @param cls
	 * @return
	 * @throws MoreComponentsForClassException
	 */
	public <T> T getComponent(Class<T> cls) throws MoreComponentsForClassException;
	
	/**
	 * Return all registered components that descend from / implement class 'cls'.
	 * 
	 * @param cls
	 * @return
	 */
	public <T> Set<T> getComponents(Class<T> cls);
	
	//
	//
	// EVENT LISTENERS
	// 
	//
	
	/**
	 * Attach listener to all events of class 'event'.
	 * 
	 * @param event
	 * @param listener
	 */
	public void addEventListener(Class<?> event, IComponentEventListener<?> listener);

	/**
	 * Attach listener to all events of class 'event' that is produced by any component of class 'component'.
	 * 
	 * @param event
	 * @param component
	 * @param listener
	 */
	public void addEventListener(Class<?> event, Class<?> component, IComponentEventListener<?> listener);

	/**
	 * Attach listener to all events of class 'event' that is produced by \component with name 'componentName'.
	 * <p><p>
	 * Note that every component should have unique ID in the context of component bus instance.
	 * <p><p>
	 * @param event
	 * @param componentName
	 * @param listener
	 */
	public void addEventListener(Class<?> event, IToken componentName, IComponentEventListener<?> listener);
	
	/**
	 * Attach listener to all events of class 'event' that is produced by the 'component'.
	 * <p><p>
	 * Note that every component should have unique ID in the context of component bus instance.
	 * <p><p>
	 * @param event
	 * @param component
	 * @param listener
	 */
	public void addEventListener(Class<?> event, IComponent component, IComponentEventListener<?> listener);
	
	/**
	 * Tests whether 'listener' is listening on events of class 'class'.
	 * @param event
	 * @param listener
	 * @return
	 */
	public boolean isListening(Class<?> event, IComponentEventListener<?> listener);
	
	/**
	 * Tests whether 'listener' is listening on events of class 'event' on components of class 'component'.
	 * 
	 * @param event
	 * @param component
	 * @param listener
	 */
	public boolean isListening(Class<?> event, Class<?> component, IComponentEventListener<?> listener);
	
	/**
	 * Tests whether 'listener' is listening on events of class 'event' on component of name 'componentName'.
	 * 
	 * @param event
	 * @param componentName
	 * @param listener
	 * @return
	 */
	public boolean isListening(Class<?> event, IToken componentName, IComponentEventListener<?> listener);
	
	/**
	 * Tests whether 'listener' is listening on events of class 'event' on the 'component'.
	 * 
	 * @param event
	 * @param component
	 * @param listener
	 * @return
	 */
	public boolean isListening(Class<?> event, IComponent component, IComponentEventListener<?> listener);
	
	/**
	 * Removes 'listener' from event 'event'.
	 * 
	 * @param event
	 * @param listener
	 */
	public void removeEventListener(Class<?> event, IComponentEventListener<?> listener);
	
	/**
	 * Removes 'listener' from event 'event' on component 'component'.
	 * @param event
	 * @param component
	 * @param listener
	 */
	public void removeEventListener(Class<?> event, Class<?> component, IComponentEventListener<?> listener);
	
	/**
	 * Removes 'listener' from event 'event' on component of name 'componentName'.
	 * @param event
	 * @param componentName
	 * @param listener
	 */
	public void removeEventListener(Class<?> event, IToken componentName, IComponentEventListener<?> listener);
	
	/**
	 * Removes 'listener' from event 'event' on the 'component'.
	 * @param event
	 * @param component
	 * @param listener
	 */
	public void removeEventListener(Class<?> event, IComponent component, IComponentEventListener<?> listener);
	
	//
	//
	// EVENTS PROPAGATION
	//
	//
	
	/**
	 * Propagates new event.
	 * <p><p>
	 * If this event is produced as an answer to other event (that is in the context of the listener call),
	 * than the event will be propagated after all listeners reacting to current event finishes.
	 * That is - the event will be postponed.
	 * <p><p>
	 * Can't be used to propagate {@link IResetEvent}. 
	 * 
	 * @param event
	 * @param whether the event has been processed or it has been added to the queue for the future invocation
	 * 
	 * @throws ComponentBusNotRunningException thrown when the bus is not running
	 * @throws ComponentBusErrorException bus exception (report to authors)
	 * @throws FatalErrorPropagatingEventException thrown when some listener throws an exception during the event propagation (use {@link Exception#getCause()} to get the original exception).'
	 * 
	 * @return whether the event has been propagated
	 */
	public boolean event(IComponentEvent<?> event) throws ComponentBusNotRunningException, ComponentBusErrorException, FatalErrorPropagatingEventException;
	
	/**
	 * Propagates new event in the context of current event (if called within the context of event).
	 * <p><p>
	 * If this method is called from in the context of some listener - then the event will be immediately propagate
	 * (unless some other eventTransactional() call is made). It allows you to create chain of events that create
	 * something like "transaction". If exception is thrown during this process, every listener will be notified about
	 * that and may act accordingly.
	 * <p><p>
	 * Note that if this method is not called in the context of listener then it will be postponed as if event() method 
	 * is called.
	 * <p><p>
	 * Can't be used to propagate {@link IResetEvent}.
	 * 
	 * @param event
	 * 
 	 * @throws ComponentBusNotRunningException thrown when the bus is not running
	 * @throws ComponentBusErrorException whenever an exception happened during the propagation of the event (wrapped exception is accessible under {@link Exception#getCause()})
 	 * @throws FatalErrorPropagatingEventException thrown when some listener throws an exception during the event propagation (use {@link Exception#getCause()} to get the original exception).
	 */
	public void eventTransactional(IComponentEvent<?> event) throws ComponentBusNotRunningException, ComponentBusErrorException, FatalErrorPropagatingEventException;
	
}