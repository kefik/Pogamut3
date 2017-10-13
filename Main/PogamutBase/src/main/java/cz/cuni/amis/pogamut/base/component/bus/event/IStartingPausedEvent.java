package cz.cuni.amis.pogamut.base.component.bus.event;

import cz.cuni.amis.pogamut.base.component.IComponent;
import cz.cuni.amis.pogamut.base.component.bus.IComponentEvent;

/**
 * This event is similar to {@link IStartingEvent}, it marks that the component is going to start.
 * In contrast to starting event, it is additionally telling that after the start, the component is going to be paused,
 * i.e., it won't broadcast {@link IStartedEvent} but it will broadcast {@link IPausedEvent}.
 * <p><p>
 * Such event is needed if we need to synchronize the start of the multi-agent simulation where we have to
 * <ol>
 * <li>construct all agent instances</li>
 * <li>start all agent instances in paused state</li>
 * <li>resume all agent instances at once</li>
 * </ol>
 * Otherwise we're facing the trouble that some agent will become effective in the environment before others crippling the plausibility of
 * such simulation and violating agent's assumptions (that there are comrades / enemies present in the environment from the beginning of the simulation).
 * 
 * @author Jimmy
 *
 * @param <SOURCE>
 */
public interface IStartingPausedEvent<SOURCE extends IComponent> extends IComponentEvent<SOURCE> {

	/**
	 * Provides human readable information why the component is starting.
	 * @return
	 */
	public String getMessage();
	
}
