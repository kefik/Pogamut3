package cz.cuni.amis.pogamut.multi.worldview.objects;

import cz.cuni.amis.pogamut.base.communication.worldview.object.WorldObjectId;
import cz.cuni.amis.pogamut.multi.agent.ITeamId;
import cz.cuni.amis.pogamut.multi.communication.translator.event.ICompositeWorldObjectUpdatedEvent;
import cz.cuni.amis.pogamut.multi.communication.translator.event.ILocalWorldObjectUpdatedEvent;
import cz.cuni.amis.pogamut.multi.communication.translator.event.ISharedWorldObjectUpdatedEvent;
import cz.cuni.amis.pogamut.multi.communication.translator.event.IStaticWorldObjectUpdatedEvent;
import cz.cuni.amis.pogamut.multi.communication.worldview.object.ICompositeWorldObject;

public abstract class TestCompositeObject implements ICompositeWorldObject {

	protected WorldObjectId id;
	
	protected TestCompositeObject(WorldObjectId id)
	{
		this.id = id;
	}
	
	@Override
	public WorldObjectId getId() {
		return this.id;
	}
	
	public abstract String getLocalString();
	public abstract long getLocalLong();
	
	public abstract String getStaticString();
	public abstract long getStaticLong();
	
	public abstract String getSharedString();
	public abstract long getSharedLong();


	public ICompositeWorldObjectUpdatedEvent createUpdateEvent(long time, ITeamId teamId) {
		return new TestCompositeObject.TestCompositeObjectUpdatedEvent(this, time, teamId);
	}
	
	public static class TestCompositeObjectUpdatedEvent implements ICompositeWorldObjectUpdatedEvent
	{

		private TestCompositeObject data;
		private long time;
		private ITeamId teamId;
		
		public TestCompositeObjectUpdatedEvent( TestCompositeObject data, long time, ITeamId teamId)
		{
			this.data = data;
			this.time = time;
			this.teamId = teamId;
		}
		
		@Override
		public long getSimTime() {
			return time;
		}

		@Override
		public WorldObjectId getId() {
			return data.getId();
		}

		@Override
		public ILocalWorldObjectUpdatedEvent getLocalEvent() {
			return new TestLocalObject.TestLocalObjectUpdatedEvent( (TestLocalObject)data.getLocal() , time);
		}

		@Override
		public ISharedWorldObjectUpdatedEvent getSharedEvent() {
			return new TestSharedObjectImpl.TestSharedObjectUpdatedEvent((TestSharedObject)data.getShared(), time, teamId);
		}

		@Override
		public IStaticWorldObjectUpdatedEvent getStaticEvent() {
			return new TestStaticObject.TestStaticObjectUpdatedEvent((TestStaticObject)data.getStatic(), time);
		}
		
	}
	
}
