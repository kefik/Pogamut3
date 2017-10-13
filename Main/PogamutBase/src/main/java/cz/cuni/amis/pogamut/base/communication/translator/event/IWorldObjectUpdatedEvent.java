package cz.cuni.amis.pogamut.base.communication.translator.event;

import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldObjectUpdateResult.Result;
import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldObjectUpdateResult.WorldObjectUpdateResult;
import cz.cuni.amis.pogamut.base.communication.worldview.IWorldView;
import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObject;
import cz.cuni.amis.pogamut.base.communication.worldview.object.WorldObjectId;
import cz.cuni.amis.pogamut.base.communication.worldview.object.event.WorldObjectDestroyedEvent;
import cz.cuni.amis.pogamut.base.communication.worldview.object.event.WorldObjectFirstEncounteredEvent;
import cz.cuni.amis.pogamut.base.communication.worldview.object.event.WorldObjectUpdatedEvent;
import cz.cuni.amis.utils.NullCheck;
import cz.cuni.amis.utils.exception.PogamutException;

/**
 * Interface for the event that suppose to update the informations about the
 * object in the world.
 * <p><p>
 * Each event should return an id of the object it is meant to update, this
 * event is processed by the {@link IWorldViewEventInput} implementor that should
 * look up the object in it's view and update it with the method defined
 * by the implementor of this interface.
 * <p><p>
 * Update event may have four different outcomes according to {@link IWorldObjectUpdateResult#getResult()}.
 * <ol>
 * <li>{@link IWorldObjectUpdateResult.Result}.CREATED = <b>new object appeared in the world</b> - this is the case when the object's id is
 * unknown to the world view thus 'null' is passed to the update() method AND update() 
 * returns new world object</li>
 * <li>{@link IWorldObjectUpdateResult.Result}.UPDATED = <b>update state of the existing object in the world</b> - this is the case
 * when the object's id is known to the world view so the according world object is passed
 * to the update() method AND update() returns the same instance (but updated, i.e., some of its fields changes)
 * of the world object</li>
 * <li>{@link IWorldObjectUpdateResult.Result}.SAME = <b>object was not updated</b> (no new information has been set to it). 
 * </li>
 * <li>{@link IWorldObjectUpdateResult.Result}.DESTROYED = <b>object disappeared from the world</b> - this is the case when the object's id
 * is known to the world and should be destroyed. 
 * </ol>
 * <p>
 * <b>It's forbidden for the instance of update event to create a new world object instance in the
 * 2) case, it must always work over the instance passed to the update() method</b>
 * <p><p>
 * For the case 1), {@link IWorldView} will generate {@link WorldObjectFirstEncounteredEvent} followed
 * by {@link WorldObjectUpdatedEvent},
 * for the case 2) the world view will generate just {@link WorldObjectUpdatedEvent} and for the case 4) {@link WorldObjectDestroyedEvent}.
 * No event is generated for the case 3 as it does not bring new information. 
 * 
 * @author Jimmy
 */
public interface IWorldObjectUpdatedEvent extends IWorldChangeEvent {
	
	public WorldObjectId getId();
	
	public IWorldObjectUpdateResult<IWorldObject> update(IWorldObject obj);
	
	/**
	 * Shortcut implementation of {@link IWorldObjectUpdatedEvent} that informs that some object has been destroyed.
	 * 
	 * @author Jimmy
	 */
	public static class DestroyWorldObject implements IWorldObjectUpdatedEvent {

		private IWorldObject object;
		private long simTime;

		public DestroyWorldObject(IWorldObject object, long simTime) {
			this.object = object;
			NullCheck.check(this.object, "object");
			NullCheck.check(this.object.getId(), "object.getId()");
			this.simTime = simTime;
		}
		
		@Override
		public WorldObjectId getId() {
			return object.getId();
		}

		@Override
		public IWorldObjectUpdateResult<IWorldObject> update(IWorldObject obj) {
			if (obj == null) throw new PogamutException("Could not destroy 'null' object.", this);
			if (obj.getId() == null || !obj.getId().equals(object.getId())) throw new PogamutException("Could not destroy object " + object + " as provided object for update is different: " + obj, this);
			return new WorldObjectUpdateResult(Result.DESTROYED, null);
		}

		@Override
		public long getSimTime() {
			return simTime;
		}
		
		
	}

}
