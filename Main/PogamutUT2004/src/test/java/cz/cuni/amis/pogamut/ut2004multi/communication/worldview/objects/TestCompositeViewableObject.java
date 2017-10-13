package cz.cuni.amis.pogamut.ut2004multi.communication.worldview.objects;

import cz.cuni.amis.pogamut.base.communication.worldview.object.WorldObjectId;
import cz.cuni.amis.pogamut.multi.agent.ITeamId;
import cz.cuni.amis.pogamut.multi.communication.translator.event.ICompositeWorldObjectUpdatedEvent;
import cz.cuni.amis.pogamut.multi.communication.translator.event.ILocalWorldObjectUpdatedEvent;
import cz.cuni.amis.pogamut.multi.communication.translator.event.ISharedWorldObjectUpdatedEvent;
import cz.cuni.amis.pogamut.multi.communication.translator.event.IStaticWorldObjectUpdatedEvent;
import cz.cuni.amis.pogamut.multi.communication.worldview.object.ICompositeWorldObject;
import cz.cuni.amis.pogamut.ut2004.communication.worldview.objects.IGBViewable;

public abstract class TestCompositeViewableObject implements ICompositeWorldObject, IGBViewable {

	protected WorldObjectId id;
	
	protected TestCompositeViewableObject(WorldObjectId id)
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
		return new TestCompositeViewableObject.TestCompositeViewableObjectUpdatedEvent(this, time, teamId);
	}
	
	public static class TestCompositeViewableObjectUpdatedEvent implements ICompositeWorldObjectUpdatedEvent
	{

		private TestCompositeViewableObject data;
		private long time;
		private ITeamId teamId;
		
		public TestCompositeViewableObjectUpdatedEvent( TestCompositeViewableObject data, long time, ITeamId teamId)
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
			return new TestLocalViewableObject.TestLocalViewableObjectUpdatedEvent( (TestLocalViewableObject)data.getLocal() , time);
		}

		@Override
		public ISharedWorldObjectUpdatedEvent getSharedEvent() {
			return new TestSharedViewableObjectImpl.TestSharedViewableObjectUpdatedEvent((TestSharedViewableObject)data.getShared(), time, teamId);
		}

		@Override
		public IStaticWorldObjectUpdatedEvent getStaticEvent() {
			return new TestStaticViewableObject.TestStaticViewableObjectUpdatedEvent((TestStaticViewableObject)data.getStatic(), time);
		}
		
	}
	
}
