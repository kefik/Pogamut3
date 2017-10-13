package cz.cuni.amis.pogamut.base.communication.worldview.react;

import cz.cuni.amis.pogamut.base.communication.worldview.IWorldView;
import cz.cuni.amis.pogamut.base.communication.worldview.event.IWorldEvent;
import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObject;
import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObjectEvent;
import cz.cuni.amis.pogamut.base.communication.worldview.object.WorldObjectId;
import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObjectListener;

/**
 * This abstract class allows you to easily hook a specific event-handling behavior. It automatically
 * register a listener for a specified {@link IWorldObjectEvent} for you and calls {@link ObjectEventReact#react(IWorldObjectEvent)}
 * method automatically. The {@link ObjectEventReactOnce#react(IWorldObjectEvent)} will be called only once (upon first event received).
 * <p><p>
 * If you need to react on every event, use {@link ObjectEventReact}.
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
public abstract class ObjectEventReactOnce<OBJECT extends IWorldObject, EVENT extends IWorldObjectEvent<OBJECT>> extends ObjectEventReact<OBJECT, EVENT> {

	public ObjectEventReactOnce(Class<?> objectClass, IWorldView worldView) {
		super(objectClass, worldView);
	}	

	public ObjectEventReactOnce(Class<?> objectClass, Class<?> eventClass, IWorldView worldView) {
		super(objectClass, eventClass, worldView);
	}

	public ObjectEventReactOnce(WorldObjectId objectId, IWorldView worldView) {
		super(objectId, worldView);
	}

	public ObjectEventReactOnce(WorldObjectId objectId, Class<?> eventClass, IWorldView worldView) {
		super(objectId, eventClass, worldView);
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
