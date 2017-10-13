package cz.cuni.amis.pogamut.base.component.controller;

import cz.cuni.amis.pogamut.base.component.bus.event.IStartedEvent;
import cz.cuni.amis.pogamut.base.component.bus.event.IStartingEvent;

/**
 * Defines the way the {@link ComponentController} behaves - when it calls {@link IComponentControlHelper}.init()
 * and {@link IComponentControlHelper}.start() methods.
 * @author Jimmy
 */
public enum ComponentDependencyType {

	/**
	 * Starts component whenever all dependents broadcasted at least {@link IStartingEvent} (or {@link IStartedEvent}).
	 */
	STARTS_WITH,
	
	/**
	 * Starts component whenever all dependents broadcasted {@link IStartedEvent}
	 */
	STARTS_AFTER;
	
}
