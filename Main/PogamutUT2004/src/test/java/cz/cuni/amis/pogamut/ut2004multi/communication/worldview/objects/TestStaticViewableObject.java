package cz.cuni.amis.pogamut.ut2004multi.communication.worldview.objects;

import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldObjectUpdateResult;
import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldObjectUpdateResult.Result;
import cz.cuni.amis.pogamut.base.communication.worldview.object.WorldObjectId;
import cz.cuni.amis.pogamut.multi.communication.translator.event.IStaticWorldObjectUpdatedEvent;
import cz.cuni.amis.pogamut.multi.communication.worldview.object.IStaticWorldObject;
import cz.cuni.amis.utils.exception.PogamutException;

public abstract class TestStaticViewableObject implements IStaticWorldObject{

	protected WorldObjectId id;
	protected long simTime;
	
	public abstract String getStaticString();
	public abstract long getStaticLong();
	
	protected TestStaticViewableObject(WorldObjectId id, long simTime)
	{
		this.id = id;
		this.simTime = simTime;
	}
	
	@Override
	public WorldObjectId getId() {
		return id;
	}

	@Override
	public long getSimTime() {
		return simTime;
	}

	@Override
	public Class getCompositeClass() {
		return TestCompositeViewableObject.class;
	}
	
	
	public IStaticWorldObjectUpdatedEvent createUpdateEvent( long time )
	{
		return new TestStaticViewableObjectUpdatedEvent(this, time);
	}
	
	public static class TestStaticViewableObjectUpdatedEvent implements IStaticWorldObjectUpdatedEvent
	{
		private long time;
		private TestStaticViewableObject obj;
		
		public TestStaticViewableObjectUpdatedEvent( TestStaticViewableObject object, long time)
		{
			this.time = time;
			this.obj = object;
		}

		@Override
		public long getSimTime() {
			return time;
		}

		@Override
		public IWorldObjectUpdateResult<IStaticWorldObject> update(
				IStaticWorldObject object) {
			if ( object == null )
			{
				TestStaticViewableObjectImpl impl = new TestStaticViewableObjectImpl(obj);
				this.obj = impl;
				return new IWorldObjectUpdateResult.WorldObjectUpdateResult<IStaticWorldObject>(Result.CREATED, impl);
			}
			else if ( object instanceof TestStaticViewableObjectImpl )
			{
				TestStaticViewableObjectImpl updatee = (TestStaticViewableObjectImpl)object;
				boolean updated = false;
				if ( updatee.getStaticLong() != obj.getStaticLong() )
				{
					updated = true;
				}
				if ( !updatee.getStaticString().equals(obj.getStaticString() ))
				{
					updated = true;
				}
				if ( !updated )
				{
					//the object was not changed, we only change lastSeenTime
					updatee.simTime = this.time;
					this.obj = updatee;
					return new IWorldObjectUpdateResult.WorldObjectUpdateResult<IStaticWorldObject>(Result.SAME, obj);
				}
				else
				{
					throw new PogamutException( "Trying to change a staticWorldObjec " + obj.getId()  + " .", this);
				}
			}
			else
			{
				throw new IllegalArgumentException("Wrong object class provided. Expected TestStaticObjectImpl, instead got " + object.getClass() );
			}
		}

		@Override
		public WorldObjectId getId() {
			return obj.getId();
		}
		
	}

}
