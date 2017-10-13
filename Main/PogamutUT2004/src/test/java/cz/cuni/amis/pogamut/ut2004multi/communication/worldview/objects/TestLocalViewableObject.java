package cz.cuni.amis.pogamut.ut2004multi.communication.worldview.objects;

import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldObjectUpdateResult;
import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldObjectUpdateResult.Result;
import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObject;
import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObjectEvent;
import cz.cuni.amis.pogamut.base.communication.worldview.object.WorldObjectId;
import cz.cuni.amis.pogamut.multi.communication.translator.event.ILocalWorldObjectUpdatedEvent;
import cz.cuni.amis.pogamut.multi.communication.worldview.object.ILocalWorldObject;
import cz.cuni.amis.utils.exception.PogamutException;

public abstract class TestLocalViewableObject implements ILocalWorldObject, ILocalGBViewable{

	protected WorldObjectId id;
	protected long simTime;
	
	public abstract String getLocalString();
	public abstract long getLocalLong();
	
	protected TestLocalViewableObject( WorldObjectId id, long simTime)
	{
		this.id = id;
		this.simTime = simTime;
	}
	
	public abstract TestLocalViewableObject clone();
	
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
		return TestCompositeViewableObject.class;
	}
	
	@Override
	public String toString()
	{
		return ("TestLocalObject["+id+"] : long="+getLocalLong()+" ; string=" + getLocalString() + " ;");
	}
	
	public TestLocalViewableObjectUpdatedEvent createUpdateEvent( long simTime )
	{
		return new TestLocalViewableObjectUpdatedEvent(this, simTime);
	}
	
	public static class TestLocalViewableObjectUpdatedEvent implements ILocalWorldObjectUpdatedEvent, IWorldObjectEvent<IWorldObject>
	{
		private TestLocalViewableObject data;
		private long simTime;
		
		public TestLocalViewableObjectUpdatedEvent(TestLocalViewableObject data, long simTime)
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
				TestLocalViewableObject ret = new TestLocalViewableObjectImpl(data);
				data = ret;
				return new IWorldObjectUpdateResult.WorldObjectUpdateResult<ILocalWorldObject>( Result.CREATED, data);
			}
			if ( ! ( object instanceof TestLocalViewableObjectImpl) )
			{
				throw new PogamutException("Wrong object class provided for update, expected TestLocalObjectImpl", this);
			}
			else
			{
				TestLocalViewableObjectImpl toUpdate = (TestLocalViewableObjectImpl)object;
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
				if ( toUpdate.visible != data.isVisible())
				{
					toUpdate.visible = data.isVisible();
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
		public IWorldObject getObject() {
			return data;
		}
		
	}

	
}
