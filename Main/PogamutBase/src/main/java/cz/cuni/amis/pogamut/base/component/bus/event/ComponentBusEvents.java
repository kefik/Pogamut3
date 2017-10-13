package cz.cuni.amis.pogamut.base.component.bus.event;

import java.util.logging.Level;
import java.util.logging.Logger;

import cz.cuni.amis.pogamut.base.component.IComponent;
import cz.cuni.amis.pogamut.base.component.bus.IComponentBus;
import cz.cuni.amis.pogamut.base.component.bus.IComponentEvent;
import cz.cuni.amis.pogamut.base.component.bus.event.impl.FatalErrorEvent;
import cz.cuni.amis.pogamut.base.component.bus.event.impl.PausedEvent;
import cz.cuni.amis.pogamut.base.component.bus.event.impl.PausingEvent;
import cz.cuni.amis.pogamut.base.component.bus.event.impl.ResumedEvent;
import cz.cuni.amis.pogamut.base.component.bus.event.impl.ResumingEvent;
import cz.cuni.amis.pogamut.base.component.bus.event.impl.StartedEvent;
import cz.cuni.amis.pogamut.base.component.bus.event.impl.StartingEvent;
import cz.cuni.amis.pogamut.base.component.bus.event.impl.StartingPausedEvent;
import cz.cuni.amis.pogamut.base.component.bus.event.impl.StoppedEvent;
import cz.cuni.amis.pogamut.base.component.bus.event.impl.StoppingEvent;

/**
 * This class provides simple methods for propagation of events that happened in some component.
 * <p><p>
 * Every component should instantiate this class for itself as it provides a convenient way to send new events into 
 * the bus (but it is not mandatory).
 * <p><p>
 * There is a possibility to disable sending of all events by setting false via {@link ComponentBusEvents#setBroadcasting(boolean)}.
 * Think of it as applying aspect to all event-broadcasting used by your class which allows you to disable the cross-cutting concern
 * of sending events.
 * 
 * @author Jimmy
 */
public class ComponentBusEvents  {

	protected IComponentBus bus;
	protected IComponent component;
	protected Logger log;
	
	/**
	 * Whether the event broadcasting is enabled.
	 */
	protected boolean broadcasting = true;

	public ComponentBusEvents(IComponentBus bus, IComponent component, Logger log) {
		this.bus = bus;
		this.component = component;
		this.log = log;
	}

	/**
	 * When some method is called to broadcast some event then it will go through ONLY IFF isBroadcasting().
	 * @return
	 */
	public boolean isBroadcasting() {
		return broadcasting;
	}

	/**
	 * This may enable (== true) / disable (== false) whether this object will actually be sending any events or not.
	 * @param broadcasting
	 */
	public void setBroadcasting(boolean broadcasting) {
		this.broadcasting = broadcasting;
	}
	
	private boolean event(IComponentEvent event) {
		if (!isBroadcasting()) {
			if (log != null && log.isLoggable(Level.FINEST)) log.finest(component.getComponentId().getToken() + " WON'T SEND " + event + " to " + bus + " as broadcasting is DISABLED");
			return false;
		}
		if (log != null && log.isLoggable(Level.FINER)) log.finer(component.getComponentId().getToken() + " is sending " + event + " to " + bus); 
		if (bus.event(event)) {
			if (log != null && log.isLoggable(Level.FINEST)) log.finest(component.getComponentId().getToken() + " sent " + event + " to " + bus + " and was processed");
			return true;
		} else {
			if (log != null && log.isLoggable(Level.FINER)) log.warning(component.getComponentId().getToken() + " sent StartingEvent to " + bus + " and its processing was postponed.");
			return false;
		}
	}
	
	private boolean eventTransactional(IComponentEvent event) {
		if (!isBroadcasting()) {
			if (log != null && log.isLoggable(Level.FINEST)) log.finest(component.getComponentId().getToken() + " WON'T SEND TRANSACTIONAL " + event + " to " + bus + " as broadcasting is DISABLED");
			return false;
		}
		if (log != null && log.isLoggable(Level.FINER)) log.finer(component.getComponentId().getToken() + " is sending transactional " + event + " to " + bus); 
		bus.eventTransactional(event);
		if (log != null && log.isLoggable(Level.FINEST)) log.finest(component.getComponentId().getToken() + " sent transactional " + event + " to " + bus + " and was processed");
		return true;
	}

	public boolean starting() {
		return event(new StartingEvent(component));
	}
	
	public boolean starting(String message) {
		return event(new StartingEvent(component, message));
	}
	
	public boolean startingPaused() {
		return event(new StartingPausedEvent(component));
	}
	
	public boolean startingPaused(String message) {
		return event(new StartingPausedEvent(component, message));
	}
	
	public boolean started() {
		return event(new StartedEvent(component));
	}
	
	public boolean started(String message) {
		return event(new StartedEvent(component, message));
	}
	
	public boolean pausing() {
		return event(new PausingEvent(component));
	}
	
	public boolean pausing(String message) {
		return event(new PausingEvent(component, message));
	}
	
	public boolean paused() {
		return event(new PausedEvent(component));
	}
	
	public boolean paused(String message) {
		return event(new PausedEvent(component, message));
	}
	
	public boolean resuming() {
		return event(new ResumingEvent(component));
	}
	
	public boolean resuming(String message) {
		return event(new ResumingEvent(component, message));
	}
	
	public boolean resumed() {
		return event(new ResumedEvent(component));
	}
	
	public boolean resumed(String message) {
		return event(new ResumedEvent(component, message));
	}
	
	public boolean stopping() {
		return event(new StoppingEvent(component));
	}
	
	public boolean stopping(String message) {
		return event(new StoppingEvent(component, message));
	}
	
	public boolean stopped() {
		return event(new StoppedEvent(component));
	}
	
	public boolean stopped(String message) {
		return event(new StoppedEvent(component, message));
	}
	
	public boolean fatalError(String message) {
		return event(new FatalErrorEvent(component, message));
	}
	
	public boolean fatalError(String message, Throwable cause) {
		return event(new FatalErrorEvent(component, message, cause));
	}
	
	public boolean fatalError(Throwable cause) {
		return event(new FatalErrorEvent(component, cause));
	}
	
	public boolean startingTransactional() {
		return eventTransactional(new StartingEvent(component));
	}
	
	public boolean startingTransactional(String message) {
		return eventTransactional(new StartingEvent(component, message));
	}
	
	public boolean startingPausedTransactional() {
		return eventTransactional(new StartingPausedEvent(component));
	}
	
	public boolean startingPausedTransactional(String message) {
		return eventTransactional(new StartingPausedEvent(component, message));		
	}
	
	public boolean startedTransactional() {
		return eventTransactional(new StartedEvent(component));
	}
	
	public boolean startedTransactional(String message) {
		return eventTransactional(new StartedEvent(component, message));
	}
	
	public boolean pausingTransactional() {
		return eventTransactional(new PausingEvent(component));
	}
	
	public boolean pausingTransactional(String message) {
		return eventTransactional(new PausingEvent(component, message));
	}
	
	public boolean pausedTransactional() {
		return eventTransactional(new PausedEvent(component));
	}
	
	public boolean pausedTransactional(String message) {
		return eventTransactional(new PausedEvent(component, message));
	}
	
	public boolean resumingTransactional() {
		return eventTransactional(new ResumingEvent(component));
	}
	
	public boolean resumingTransactional(String message) {
		return eventTransactional(new ResumingEvent(component, message));
	}
	
	public boolean resumedTransactional() {
		return eventTransactional(new ResumedEvent(component));
	}
	
	public boolean resumedTransactional(String message) {
		return eventTransactional(new ResumedEvent(component, message));
	}
	
	public boolean stoppingTransactional() {
		return eventTransactional(new StoppingEvent(component));
	}
	
	public boolean stoppingTransactional(String message) {
		return eventTransactional(new StoppingEvent(component, message));
	}
	
	public boolean stoppedTransactional() {
		return eventTransactional(new StoppedEvent(component));
	}
	
	public boolean stoppedTransactional(String message) {
		return eventTransactional(new StoppedEvent(component, message));
	}
	
}
