package cz.cuni.amis.pogamut.multi.worldview.objects;

import java.util.Map;

import cz.cuni.amis.pogamut.base.communication.worldview.object.WorldObjectId;
import cz.cuni.amis.pogamut.multi.agent.ITeamId;
import cz.cuni.amis.pogamut.multi.communication.translator.event.ISharedWorldObjectUpdatedEvent;
import cz.cuni.amis.pogamut.multi.communication.worldview.object.ISharedProperty;
import cz.cuni.amis.pogamut.multi.communication.worldview.object.ISharedWorldObject;
import cz.cuni.amis.pogamut.multi.communication.worldview.property.PropertyId;
import cz.cuni.amis.pogamut.multi.worldview.objects.TestSharedObjectImpl.TestSharedObjectUpdatedEvent;

public abstract class TestSharedObject implements ISharedWorldObject {
	
	protected WorldObjectId objectId;
	protected long simTime;
	public abstract TestSharedObject clone();
	
	protected TestSharedObject()
	{
		objectId = null;
		simTime = 0;
	}
	
	protected TestSharedObject(WorldObjectId objectId, long simTime)
	{
		this.simTime = simTime;
		this.objectId = objectId;
	}
	
	public abstract String getSharedString();	
	public abstract long getSharedLong();

	@Override
	public WorldObjectId getId() {
		return this.objectId;
	}

	@Override
	public long getSimTime() {
		return this.simTime;
	}

	@Override
	public Class getCompositeClass() {
		return TestCompositeObject.class;
	}
	
	
	public ISharedWorldObjectUpdatedEvent createUpdateEvent(long time,
			ITeamId teamId) {
		return new TestSharedObjectUpdatedEvent(this, time, teamId);
	}
}
