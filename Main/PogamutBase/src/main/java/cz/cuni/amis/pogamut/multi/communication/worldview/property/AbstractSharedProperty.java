package cz.cuni.amis.pogamut.multi.communication.worldview.property;

import cz.cuni.amis.pogamut.base.communication.worldview.object.WorldObjectId;
import cz.cuni.amis.pogamut.multi.agent.ITeamId;
import cz.cuni.amis.pogamut.multi.communication.translator.event.ISharedPropertyUpdateResult;
import cz.cuni.amis.pogamut.multi.communication.translator.event.ISharedPropertyUpdatedEvent;
import cz.cuni.amis.pogamut.multi.communication.translator.event.ISharedPropertyUpdateResult.Result;
import cz.cuni.amis.pogamut.multi.communication.worldview.object.ISharedProperty;
import cz.cuni.amis.utils.HashCode;
import cz.cuni.amis.utils.NullCheck;
import cz.cuni.amis.utils.SafeEquals;
import cz.cuni.amis.utils.exception.PogamutException;

/**
 * Abstract implementation of the ISharedProperty object,
 * this class manages the methods every ISharedProperty needs to have, regardless of its type.
 * <p>SharedProperties are generally derived from bot's subjective observation of the world (local properties). If the local properties, which are used to derive the
 * value of the sharedProperty are dated, the sharedProperty is considered "dirty", because the value <i>might</i> be wrong, the team that owns this property only 
 * <i>assumes</i> the value.</p> 
 * @author srlok
 *
 */
public abstract class AbstractSharedProperty<TYPE> implements ISharedProperty<TYPE> {
	
	protected PropertyId propertyId = null;
	protected boolean dirty = false;
	protected Class<?> compositeClass = null;
	protected TYPE value;
	private int hashCode;
		
	@Override
	public boolean nullOverrides()
	{
		return false;
	}
	
	/**
	 * Every descendant must implement this by calling value.clone() or otherwise returning a new copy of value.
	 * @return
	 */
	protected abstract TYPE cloneValue();
	
	public AbstractSharedProperty(WorldObjectId objId, String identifier, TYPE value, Class<?> compositeClass)
	{
		this.propertyId = PropertyId.get(objId, identifier);
		this.compositeClass = compositeClass;
		this.value = value;
		HashCode hc = new HashCode();
		hc.add(propertyId);
		this.hashCode = hc.getHash();
	}
	
	/**
	 * Used in copy-constructors,
	 * when writing a copy constructor for a descendant class, you must add a clone for value.
	 * @param source
	 */
	public AbstractSharedProperty(AbstractSharedProperty source)
	{
		this.hashCode = source.hashCode;
		this.propertyId = source.propertyId;
		this.dirty = source.dirty;
		this.compositeClass = source.compositeClass;
		this.value = (TYPE) source.cloneValue();
	}
	
	/**
	 * Used in propertyConstructors from data.
	 * @param objectId
	 * @param identifier
	 * @param compositeClass
	 */
	public AbstractSharedProperty( WorldObjectId objectId, String identifier, Class compositeClass)
	{
		this(objectId, identifier, (TYPE)null, compositeClass);
	}
		
	@Override
	public int hashCode() {
		return hashCode;
	}
	
	@Override
	public boolean equals(Object another) {
		if (another == null) return false;		
		if (!(another instanceof ISharedProperty)) { return false; }
		ISharedProperty other = (ISharedProperty)another;
		if (!getPropertyValueClass().isAssignableFrom(other.getPropertyValueClass())) return false;
		return ((this.isDirty() == other.isDirty()) && (SafeEquals.equals(this.getValue(), other.getValue()))) && this.getPropertyId().equals(other.getPropertyId());
	}
	
	@Override
	public abstract ISharedProperty<TYPE> clone();
	
	@Override
	public TYPE getValue() {
		return value;
	}
	
	@Override
	public void setValue(TYPE value) {
		this.value = value;
	}

	@Override
	public Class getCompositeClass()
	{
		return compositeClass;
	}
	
	@Override
	public boolean isDirty()
	{
		return dirty;
	}
	
	public void setDirty(boolean value)
	{
		this.dirty = value;
	}
	
	@Override
	public WorldObjectId getObjectId()
	{
		return this.propertyId.getWorldObjectId();
	}
	
	@Override
	public PropertyId getPropertyId()
	{
		return this.propertyId;
	}
	
	@Override
	public ISharedPropertyUpdatedEvent createUpdateEvent(long time, ITeamId teamId)
	{
		return new GenericPropertyUpdate(this, time, teamId);
	}
	
	public static class GenericPropertyUpdate implements ISharedPropertyUpdatedEvent
	{

		private ISharedProperty data;
		private long time;
		private ITeamId teamId;
		
		public GenericPropertyUpdate(ISharedProperty data, long time, ITeamId teamId)
		{
			this.data = data;
			NullCheck.check(this.data, "data");
			this.time = time;
			this.teamId = teamId;
		}
		
		@Override
		public long getSimTime() {
			return time;
		}

		@Override
		public WorldObjectId getObjectId() {
			return data.getObjectId();
		}

		@Override
		public PropertyId getPropertyId() {
			return data.getPropertyId();
		}

		@Override
		public ITeamId getTeamId() {
			return this.teamId;
		}

		@Override
		public ISharedPropertyUpdateResult update(ISharedProperty property) {
			if ( property == null )
			{
				//created event;
				data = data.clone(); //make sure we are returning new instance
				return new ISharedPropertyUpdateResult.SharedPropertyUpdateResult(Result.CREATED, data);
			}

			if (!data.getPropertyValueClass().isAssignableFrom(property.getPropertyValueClass())) {
				// BAD UPDATE! CLASSES DOES NOT MATCH
				throw new PogamutException("Unexpected object type provided for update, expected value class " + data.getPropertyValueClass() + " got data with value class " + property.getPropertyValueClass(), this);
			}
			
			ISharedProperty original = property;
			boolean updated = false;
			if ( !SafeEquals.equals(original.getValue(), data.getValue()) )
			{	
				if (data.getValue() != null)
				{
					//only update the value when we have something to update with (null means -> no info)
					original.setValue(data.getValue());
				}
			}
			this.data = original;
			
			if (updated)
			{
				return new ISharedPropertyUpdateResult.SharedPropertyUpdateResult(Result.UPDATED, original);
			}
			return new ISharedPropertyUpdateResult.SharedPropertyUpdateResult(Result.SAME, original);
			
			
		}
	}	
}
