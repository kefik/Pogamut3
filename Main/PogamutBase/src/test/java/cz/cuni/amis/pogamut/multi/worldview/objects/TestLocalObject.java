package cz.cuni.amis.pogamut.multi.worldview.objects;

import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldObjectUpdateResult;
import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldObjectUpdateResult.Result;
import cz.cuni.amis.pogamut.base.communication.worldview.object.WorldObjectId;
import cz.cuni.amis.pogamut.multi.communication.translator.event.ILocalWorldObjectUpdatedEvent;
import cz.cuni.amis.pogamut.multi.communication.worldview.object.ILocalWorldObject;
import cz.cuni.amis.utils.exception.PogamutException;

public abstract class TestLocalObject implements ILocalWorldObject{

	protected WorldObjectId id;
	protected long simTime;
	
	public abstract String getLocalString();
	public abstract long getLocalLong();
	
	protected TestLocalObject( WorldObjectId id, long simTime)
	{
		this.id = id;
		this.simTime = simTime;
	}
	
	public abstract TestLocalObject clone();
	
	@Override
	public WorldObjectId getId() {
		return this.id;
	}

	@Override
	public long getSimTime() {
		return this.simTime;
	}

	@Override
	public Class getCompositeClass() {
		return TestCompositeObject.class;
	}
	
	public TestLocalObjectUpdatedEvent createUpdateEvent( long simTime )
	{
		return new TestLocalObjectUpdatedEvent(this, simTime);
	}
	
	public String toString() {
		return "TestLocalObject[id=" + getId() + ", time=" + getSimTime() + ", localString=" + getLocalString() + ", localLong=" + getLocalLong() + "]";
	}
	
	public static class TestLocalObjectUpdatedEvent implements ILocalWorldObjectUpdatedEvent
	{
		private TestLocalObject data;
		private long simTime;
		
		public TestLocalObjectUpdatedEvent(TestLocalObject data, long simTime)
		{
			this.data = data;
			this.simTime = simTime;
		}
		
		@Override
		public long getSimTime() {
			return simTime;
		}

		@Override
		public WorldObjectId getId() {
			return data.getId();
		}
		
		

		@Override
		public IWorldObjectUpdateResult<ILocalWorldObject> update(
				ILocalWorldObject object) 
		{
			if ( object == null)
			{
				TestLocalObject ret = new TestLocalObjectImpl(data);
				data = ret;
				return new IWorldObjectUpdateResult.WorldObjectUpdateResult<ILocalWorldObject>( Result.CREATED, data);
			}
			if ( ! ( object instanceof TestLocalObjectImpl) )
			{
				throw new PogamutException("Wrong object class provided for update, expected TestLocalObjectImpl", this);
			}
			else
			{
				TestLocalObjectImpl toUpdate = (TestLocalObjectImpl)object;
				boolean updated = false;
				if ( toUpdate.longVal != data.getLocalLong() )
				{
					toUpdate.longVal = data.getLocalLong();
					updated = true;
				}
				if ( !toUpdate.stringVal.equals( data.getLocalString() ) )
				{
					toUpdate.stringVal = data.getLocalString();
					updated = true;
				}
				toUpdate.simTime = this.simTime;
				if ( updated )
				{
					return new IWorldObjectUpdateResult.WorldObjectUpdateResult<ILocalWorldObject>(Result.UPDATED, toUpdate);
				}
				else
				{
					return new IWorldObjectUpdateResult.WorldObjectUpdateResult<ILocalWorldObject>(Result.SAME, toUpdate);
				}
			}
			
		}
		
		@Override
		public String toString() {
			return "TestLocalObjectUpdatedEvent[id=" + getId() + ", time=" + getSimTime() + "]";
		}
		
	}

	
}
