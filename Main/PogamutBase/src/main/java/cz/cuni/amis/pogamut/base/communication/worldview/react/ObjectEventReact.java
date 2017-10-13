package cz.cuni.amis.pogamut.base.communication.worldview.react;

import cz.cuni.amis.pogamut.base.communication.worldview.IWorldView;
import cz.cuni.amis.pogamut.base.communication.worldview.event.IWorldEvent;
import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObject;
import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObjectEvent;
import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObjectEventListener;
import cz.cuni.amis.pogamut.base.communication.worldview.object.WorldObjectId;

/**
 * This abstract class allows you to easily hook a specific event-handling behavior. It automatically
 * register a listener for a specified {@link IWorldObjectEvent} for you and calls {@link ObjectEventReact#react(IWorldObjectEvent)}
 * method automatically.
 * <p><p>
 * If you need to react only once to the event, use {@link ObjectEventReactOnce}.
 * <p><p>
 * Use {@link ObjectEventReact#enable()} and {@link ObjectEventReact#disable()} to enable react / disable react. The reaction is enabled
 * as default.
 * <p><p>
 * <b>WARNING:</b>Use as anonymous class, but <b>save it as a field</b> of your class! Note, that we're using weak-references to 
 * listeners and if you do not save pointer to the object, it will be gc()ed!
 * 
 * @author Jimmy
 *
 * @param <EVENT>
 */
public abstract class ObjectEventReact<OBJECT extends IWorldObject, EVENT extends IWorldObjectEvent<OBJECT>> {

	protected static final int LEVEL_B_EVENT = 1;
	protected static final int LEVEL_C_EVENT = 2;
	protected static final int LEVEL_D_EVENT = 3;
	protected static final int LEVEL_E_EVENT = 4;
	
	protected IWorldObjectEventListener<OBJECT, EVENT> reactListener = new IWorldObjectEventListener<OBJECT, EVENT>() {

		@Override
		public void notify(EVENT event) {
			preReact(event);
			react(event);
			postReact(event);
		}
	
	};
	
	protected IWorldView reactWorldView;

	protected Class reactEventClass;
	
	protected Class reactObjectClass;
	
	protected WorldObjectId reactObjectId;
	
	protected final int reactObjectEventType;
	
	private boolean reactHooked = false;
	
	public ObjectEventReact(Class<?> objectClass, IWorldView worldView) {
		this.reactWorldView = worldView;
		this.reactObjectClass = objectClass;
		reactObjectEventType = LEVEL_B_EVENT;
		enable();
	}
	

	public ObjectEventReact(Class<?> objectClass, Class<?> eventClass, IWorldView worldView) {
		this.reactWorldView = worldView;
		this.reactObjectClass = objectClass;
		this.reactEventClass = eventClass;
		reactObjectEventType = LEVEL_C_EVENT;
		enable();
	}

	public ObjectEventReact(WorldObjectId objectId, IWorldView worldView) {
		this.reactWorldView = worldView;
		this.reactObjectId = objectId;
		reactObjectEventType = LEVEL_D_EVENT;
		enable();
	}

	public ObjectEventReact(WorldObjectId objectId, Class<?> eventClass, IWorldView worldView) {
		this.reactWorldView = worldView;
		this.reactObjectId = objectId;
		this.reactEventClass = eventClass;
		reactObjectEventType = LEVEL_E_EVENT;
		enable();
	}
	
	/**
	 * Disables the reaction.
	 */
	public synchronized void disable() {
		if (reactHooked) {
			reactHooked = false;
			switch(reactObjectEventType) {
			case LEVEL_B_EVENT:
				reactWorldView.removeObjectListener(reactObjectClass, reactListener);
				break;
			case LEVEL_C_EVENT:
				reactWorldView.removeObjectListener(reactObjectClass, reactEventClass, reactListener);
				break;
			case LEVEL_D_EVENT:
				reactWorldView.removeObjectListener(reactObjectId, reactListener);
				break;
			case LEVEL_E_EVENT:
				reactWorldView.removeObjectListener(reactObjectId, reactEventClass, reactListener);
				break;
			default:
				throw new IllegalStateException("Unhandled objectEventType = " + reactObjectEventType + ".");
			}
		}
	}
	
	/**
	 * Enables the reaction.
	 */
	public synchronized void enable() {
		if (!reactHooked) {
			reactHooked = true;
			switch(reactObjectEventType) {
			case LEVEL_B_EVENT:
				if (!reactWorldView.isListening(reactEventClass, reactListener)) {
					reactWorldView.addObjectListener(reactObjectClass, reactListener);
				}
				break;
			case LEVEL_C_EVENT:
				if (!reactWorldView.isListening(reactObjectClass, reactListener)) {
					reactWorldView.addObjectListener(reactObjectClass, reactEventClass, reactListener);
				}
				break;
			case LEVEL_D_EVENT:
				if (!reactWorldView.isListening(reactObjectId, reactListener)) {
					reactWorldView.addObjectListener(reactObjectId, reactListener);
				}
				break;
			case LEVEL_E_EVENT:
				if (!reactWorldView.isListening(reactObjectId, reactEventClass, reactListener)) {
					reactWorldView.addObjectListener(reactObjectId, reactEventClass, reactListener);
				}
				break;
			default:
				throw new IllegalStateException("Unhandled objectEventType = " + reactObjectEventType + ".");
				
			}
		}
	}
	
	/**
	 * pre-{@link ObjectEventReact#react(IWorldEvent)} hook allowing you to do additional work before the react method.
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
	 * post-{@link ObjectEventReact#react(IWorldEvent)} hook allowing you to do additional work after the react method.
	 * @param event
	 */
	protected void postReact(EVENT event) {
	}

}
