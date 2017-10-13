package cz.cuni.amis.pogamut.ut2004multi.communication.worldview.objects;

import cz.cuni.amis.pogamut.base.communication.worldview.object.WorldObjectId;
import cz.cuni.amis.pogamut.multi.agent.ITeamId;
import cz.cuni.amis.pogamut.multi.communication.translator.event.ISharedWorldObjectUpdatedEvent;
import cz.cuni.amis.pogamut.multi.communication.worldview.object.ISharedWorldObject;

public abstract class TestSharedViewableObject implements ISharedWorldObject {
	
	protected WorldObjectId objectId;
	protected long simTime;
	@Override
	public abstract TestSharedViewableObject clone();
	
	protected TestSharedViewableObject()
	{
		objectId = null;
		simTime = 0;
	}
	
	protected TestSharedViewableObject(WorldObjectId objectId, long simTime)
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
		return TestCompositeViewableObject.class;
	}
	
	
	public ISharedWorldObjectUpdatedEvent createUpdateEvent(long time,
			ITeamId teamId) {
		return new TestSharedViewableObjectImpl.TestSharedViewableObjectUpdatedEvent(this, time, teamId);
	}
}
