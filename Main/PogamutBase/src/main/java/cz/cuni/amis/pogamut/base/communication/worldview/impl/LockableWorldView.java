/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cuni.amis.pogamut.base.communication.worldview.impl;

import java.util.ArrayList;
import java.util.List;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldChangeEvent;
import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldObjectUpdatedEvent;
import cz.cuni.amis.pogamut.base.communication.worldview.ILockableWorldView;
import cz.cuni.amis.pogamut.base.component.bus.IComponentBus;
import cz.cuni.amis.pogamut.base.component.bus.exception.ComponentNotRunningException;
import cz.cuni.amis.pogamut.base.component.controller.ComponentDependencies;
import cz.cuni.amis.pogamut.base.utils.guice.AgentScoped;
import cz.cuni.amis.pogamut.base.utils.logging.IAgentLogger;

/**
 * WorldView that can be locked. If the world is locked all chages on objects are postponed
 * and issued when the world is unlocked. The events aren't locked by default, but 
 * there is a switch that enables locking of events.
 * @author Ik
 */
@AgentScoped
public class LockableWorldView extends EventDrivenWorldView implements ILockableWorldView {

	public static final String WORLDVIEW_DEPENDENCY = "LockableWorldViewDependency";
	
    private Boolean lock = false;
    /** List of events received when the worldview was locked. */
    protected List<IWorldChangeEvent> eventsToProcess = new ArrayList<IWorldChangeEvent>();
    /** Lock non object update events? */
    protected boolean lockEvents = false;

    @Inject
    public LockableWorldView(@Named(WORLDVIEW_DEPENDENCY) ComponentDependencies dependencies, IComponentBus bus, IAgentLogger log) {
        super(dependencies, bus, log);
    }

    /**
     * Prevent the WorldView from being changed.
     */
    public void lock() throws ComponentNotRunningException {
    	if (!isRunning()) throw new ComponentNotRunningException(controller.getState().getFlag(), this);
        synchronized (lock) {
            lock = true;
        }
    }

    /**
     * Unlock the WorldView and process all changes that happened when the world
     * was locked.
     */
    public void unlock() {
    	synchronized (lock) {
            lock = false;

            // process postponed events
            for (IWorldChangeEvent event : eventsToProcess) {
                super.notify(event);
            }
            eventsToProcess.clear();
        }
    }
    
    public boolean isLocked() {
    	return lock;
    }
    
    /**
     * Should the event processing also be locked? The object updating isn't 
     * affected by this switch.
     * @param lockEvents
     */
    public void setLockEvents(boolean lockEvents) {
        this.lockEvents = lockEvents;
    }
    
    public boolean isLockEvents() {
    	return this.lockEvents;
    }

    /**
     * Store all changes for later processing when the event is received in the
     * while the world is locked. Otherwise immedeately process the change.
     * @param event
     */
    public void notifyEvent(IWorldChangeEvent event) {
    	if (!isRunning()) throw new ComponentNotRunningException(controller.getState().getFlag(), this);
        synchronized (lock) {
            if (lock) {
                if (!lockEvents && !(event instanceof IWorldObjectUpdatedEvent)) {
                    eventsToProcess.add(event);
                } else {
                    super.notify(event);
                }
            }
        }
    }
}
