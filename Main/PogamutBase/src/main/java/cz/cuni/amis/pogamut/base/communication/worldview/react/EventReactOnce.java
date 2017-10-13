package cz.cuni.amis.pogamut.base.communication.worldview.react;

import cz.cuni.amis.pogamut.base.communication.worldview.IWorldView;
import cz.cuni.amis.pogamut.base.communication.worldview.event.IWorldEvent;

/**
 * This abstract class allows you to easily hook a specific event-handling behavior. It automatically
 * register a listener for a specified {@link IWorldEvent} for you and calls {@link EventReactOnce#react(IWorldEvent)}
 * method automatically. The {@link EventReactOnce#react(IWorldEvent)} will be called only once (upon first event received).
 * <p><p>
 * If you need to react every time, use {@link EventReact}.
 * <p><p>
 * <p><p>
 * Use {@link EventReactOnce#enable()} and {@link EventReactOnce#disable()} to enable react / disable react. The reaction is enabled
 * as default.
 * <b>WARNING:</b>Use as anonymous class, but <b>save it as a field</b> of your class! Note, that we're using weak-references to 
 * listeners and if you do not save pointer to the object, it will be gc()ed!
 * 
 * @author Jimmy
 *
 * @param <EVENT>
 */
public abstract class EventReactOnce<EVENT extends IWorldEvent> extends EventReact<EVENT> {

	public EventReactOnce(Class<EVENT> eventClass, IWorldView worldView) {
		super(eventClass, worldView);
	}
	
	/**
	 * Disables the reaction.
	 */
	@Override
	protected void postReact(EVENT event) {
		super.postReact(event);
		disable();
	}

}
