package cz.cuni.amis.pogamut.multi.worldview.objects;

import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldObjectUpdateResult;
import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldObjectUpdateResult.Result;
import cz.cuni.amis.pogamut.base.communication.worldview.object.WorldObjectId;
import cz.cuni.amis.pogamut.multi.communication.translator.event.IStaticWorldObjectUpdatedEvent;
import cz.cuni.amis.pogamut.multi.communication.worldview.object.IStaticWorldObject;
import cz.cuni.amis.utils.exception.PogamutException;

public abstract class TestStaticObject implements IStaticWorldObject{

	protected WorldObjectId id;
	protected long simTime;
	
	public abstract String getStaticString();
	public abstract long getStaticLong();
	
	protected TestStaticObject(WorldObjectId id, long simTime)
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
		return TestCompositeObject.class;
	}
	
	
	@Override
	public String toString() {
		return "TestStaticObject[id=" + getId() + ", time=" + getSimTime() + ", staticString=" + getStaticString() + ", staticLong=" + getStaticLong() + "]";
	}
	
	public IStaticWorldObjectUpdatedEvent createUpdateEvent( long time )
	{
		return new TestStaticObjectUpdatedEvent(this, time);
	}
	
	public static class TestStaticObjectUpdatedEvent implements IStaticWorldObjectUpdatedEvent
	{
		private long time;
		private TestStaticObject obj;
		
		public TestStaticObjectUpdatedEvent( TestStaticObject object, long time)
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
				TestStaticObjectImpl impl = new TestStaticObjectImpl(obj);
				this.obj = impl;
				return new IWorldObjectUpdateResult.WorldObjectUpdateResult<IStaticWorldObject>(Result.CREATED, impl);
			}
			else if ( object instanceof TestStaticObjectImpl )
			{
				TestStaticObjectImpl updatee = (TestStaticObjectImpl)object;
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
		
		@Override
		public String toString() {
			return "TestStaticObjectUpdatedEvent[id=" + getId() + ", time=" + getSimTime() + "]";
		}
		
	}

}
