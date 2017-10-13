package cz.cuni.amis.pogamut.ut2004multi.communication.worldview.objects;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import cz.cuni.amis.pogamut.base.communication.worldview.object.WorldObjectId;
import cz.cuni.amis.pogamut.multi.agent.ITeamId;
import cz.cuni.amis.pogamut.multi.communication.translator.event.ISharedPropertyUpdatedEvent;
import cz.cuni.amis.pogamut.multi.communication.translator.event.ISharedWorldObjectUpdatedEvent;
import cz.cuni.amis.pogamut.multi.communication.worldview.object.ISharedProperty;
import cz.cuni.amis.pogamut.multi.communication.worldview.property.LongProperty;
import cz.cuni.amis.pogamut.multi.communication.worldview.property.PropertyId;
import cz.cuni.amis.pogamut.multi.communication.worldview.property.StringProperty;
import cz.cuni.amis.utils.exception.PogamutException;
import cz.cuni.amis.utils.flag.FlagInteger;
import cz.cuni.amis.utils.flag.ImmutableFlag;

public class TestSharedViewableObjectImpl extends TestSharedViewableObject{

	private static FlagInteger instances = new FlagInteger(0);
	
	public static ImmutableFlag<Integer> getInstances() {
		return instances.getImmutable();
	}
	
	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		instances.decrement(1);
	}
	
	protected StringProperty stringProperty;
	protected LongProperty longProperty;
	protected HashMap<PropertyId, ISharedProperty> propertyMap = new HashMap<PropertyId, ISharedProperty>(2);
	
	public TestSharedViewableObjectImpl( Collection<ISharedProperty> properties ) 
	{
		instances.increment(1);
		
		long simTime = 0;
		boolean longSet = false;
		boolean stringSet = false;
		
		for (ISharedProperty p : properties)
		{
			if ( objectId == null )
			{
				this.objectId = p.getObjectId();
			}
			else if ( objectId != p.getObjectId()) //due to the nature of IDs we can use ==
			{
				throw new PogamutException("Ids don't match in creating TestSharedObject", this);
			}
			if ( p.getPropertyValueClass() == Long.class)
			{
				if ( !longSet )
				{
					this.longProperty = (LongProperty)p;					
					longSet = true;
				}
				else
				{
					throw new PogamutException("Trying to create TestSharedObject with more than one longProperty", this);
				}
			}
			else if (p.getPropertyValueClass() == String.class)
			{
				if ( !stringSet )
				{
					this.stringProperty = (StringProperty)p;
					stringSet = true;
				}
				else
				{
					throw new PogamutException("Trying to create TestSharedObject with more than one stringProperty", this);
				}				
			}
			propertyMap.put(p.getPropertyId(), p); //everything ok, put the property into propertymap
		}
		
		if (!longSet || !stringSet)
		{
			throw new PogamutException("Not all properties provided while creating TestSharedObject", this);
		}		
	}

	@Override
	public ISharedProperty getProperty(PropertyId id) {
		return propertyMap.get(id);
	}

	@Override
	public Map<PropertyId, ISharedProperty> getProperties() {
		return propertyMap;
	}

	@Override
	public TestSharedViewableObject clone() {
		return new TestSharedViewableObjectImpl( propertyMap.values() );
	}

	@Override
	public String getSharedString() {
		return stringProperty.getValue();
	}

	@Override
	public long getSharedLong() {
		return longProperty.getValue();
	}
	
	public TestSharedViewableObjectUpdatedEvent createUpdateEvent( long simTime, ITeamId teamId )
	{
		return new TestSharedViewableObjectUpdatedEvent(this, simTime, teamId);
	}
	
	public static class TestSharedViewableObjectUpdatedEvent implements ISharedWorldObjectUpdatedEvent
	{

		private TestSharedViewableObject obj;
		private long time;
		private ITeamId teamId;
		
		public TestSharedViewableObjectUpdatedEvent(TestSharedViewableObject obj, long time, ITeamId teamId)
		{
			this.obj = obj;
			this.time = time;
			this.teamId = teamId;
		}
		
		@Override
		public long getSimTime() {
			return time;
		}

		@Override
		public WorldObjectId getId() {
			return obj.getId();
		}

		@Override
		public ITeamId getTeamId() {
			return teamId;
		}

		@Override
		public Collection<ISharedPropertyUpdatedEvent> getPropertyEvents() {
			Collection<ISharedPropertyUpdatedEvent> ret = new LinkedList<ISharedPropertyUpdatedEvent>();
			for ( ISharedProperty p : obj.getProperties().values() )
			{
				ret.add( p.createUpdateEvent(time, teamId) );
			}
			return ret;
		}

		@Override
		public Class getCompositeObjectClass() {
			return obj.getCompositeClass();
		}
		
	}

}
