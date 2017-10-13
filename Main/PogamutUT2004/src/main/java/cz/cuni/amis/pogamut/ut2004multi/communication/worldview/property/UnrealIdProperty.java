package cz.cuni.amis.pogamut.ut2004multi.communication.worldview.property;

import cz.cuni.amis.pogamut.base.communication.worldview.object.WorldObjectId;
import cz.cuni.amis.pogamut.multi.communication.worldview.object.ISharedProperty;
import cz.cuni.amis.pogamut.multi.communication.worldview.property.AbstractSharedProperty;
import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;

/**
 * SharedProperty with the UnrealId value.
 * @author srlok
 *
 */
public class UnrealIdProperty extends AbstractSharedProperty<UnrealId>
{
	@SuppressWarnings("rawtypes")
	public UnrealIdProperty(WorldObjectId objId, String identifier, UnrealId value, Class compositeClass)
	{
		super(objId, identifier, value, compositeClass);
	}
	
	@Override
	public boolean nullOverrides()
	{
		return true;
	}
	
	public UnrealIdProperty(UnrealIdProperty other)
	{
		super(other);
	}

	@Override
	public ISharedProperty clone() {
		return new UnrealIdProperty(this);
	}

	@Override
	public Class getPropertyValueClass() {
		return UnrealId.class;
	}

	@Override
	protected UnrealId cloneValue() {
		return this.value; //UnrealId's are comparable using == 
	}
	
	

}
