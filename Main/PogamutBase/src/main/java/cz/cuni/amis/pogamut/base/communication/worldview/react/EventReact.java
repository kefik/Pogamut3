package cz.cuni.amis.pogamut.base.communication.worldview.react;

import cz.cuni.amis.pogamut.base.communication.worldview.IWorldView;
import cz.cuni.amis.pogamut.base.communication.worldview.event.IWorldEvent;
import cz.cuni.amis.pogamut.base.communication.worldview.event.IWorldEventListener;

/**
 * This abstract class allows you to easily hook a specific event-handling behavior. It automatically
 * register a listener for a specified {@link IWorldEvent} for you and calls {@link EventReact#react(IWorldEvent)}
 * method automatically.
 * <p><p>
 * If you need to react only once to the event, use {@link EventReactOnce}.
 * <p><p>
 * Use {@link EventReact#enable()} and {@link EventReact#disable()} to enable react / disable react. The reaction is enabled
 * as default.
 * <p><p>
 * <b>WARNING:</b>Use as anonymous class, but <b>save it as a field</b> of your class! Note, that we're using weak-references to 
 * listeners and if you do not save pointer to the object, it will be gc()ed!
 * 
 * @author Jimmy
 *
 * @param <EVENT>
 */
public abstract class EventReact<EVENT extends IWorldEvent> {

	protected IWorldEventListener<EVENT> reactListener = new IWorldEventListener<EVENT>() {

		@Override
		public void notify(EVENT event) {
			preReact(event);
			react(event);
			postReact(event);
		}
	
	};
	
	protected IWorldView reactWorldView;

	protected Class<EVENT> reactEventClass;
	
	private boolean reactHooked = false;
	
	public EventReact(Class<EVENT> eventClass, IWorldView worldView) {
		this.reactWorldView = worldView;
		this.reactEventClass = eventClass;
		enable();
	}
	
	/**
	 * Disables the reaction.
	 */
	public synchronized void disable() {
		if (reactHooked) {
			reactHooked = false;
			reactWorldView.removeEventListener(reactEventClass, reactListener);
		}
	}
	
	/**
	 * Enables the reaction.
	 */
	public synchronized void enable() {
		if (!reactHooked) {
			reactHooked = true;
			if (!reactWorldView.isListening(reactListener)) reactWorldView.addEventListener(reactEventClass, reactListener);
		}
	}
	
	/**
	 * pre-{@link EventReact#react(IWorldEvent)} hook allowing you to do additional work before the react method.
	 * @param event
	 */
	protected void preReact(EVENT event) {
	}

	/**
	 * React upon event notification.
	 * @param event
	 */
	protected abstract void react(EVENT event);
	
	/**
	 * post-{@link EventReact#react(IWorldEvent)} hook allowing you to do additional work after the react method.
	 * @param event
	 */
	protected void postReact(EVENT event) {
	}

}
