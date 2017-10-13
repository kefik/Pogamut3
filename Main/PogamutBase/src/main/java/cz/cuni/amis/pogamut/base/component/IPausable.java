package cz.cuni.amis.pogamut.base.component;

import cz.cuni.amis.pogamut.base.component.bus.event.IPausingEvent;

/**
 * Interface marking the component as pausable - it responds with transactional events on events {@link IPausingEvent} and
 * {@link IAgentResumingEvent}
 *  
 * @author Jimmy
 */
public interface IPausable {
	
	/**
	 * Pauses the component.
	 */
	public void pause();
	
	/**
	 * Resumes the component.
	 */
	public void resume();
	
}