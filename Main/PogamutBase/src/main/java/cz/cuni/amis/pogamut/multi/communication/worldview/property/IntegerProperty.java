package cz.cuni.amis.pogamut.multi.communication.worldview.property;

import cz.cuni.amis.pogamut.base.communication.worldview.object.WorldObjectId;
import cz.cuni.amis.pogamut.multi.communication.worldview.object.ISharedProperty;

/**
 * Implementation of the ISharedProperty interface for Integer value type.
 * @author srlok
 *
 */
public class IntegerProperty extends AbstractSharedProperty<Integer> {
	
	@SuppressWarnings("rawtypes")
	public IntegerProperty(WorldObjectId objId, String identifier, Integer value, Class compositeClass)
	{
		super(objId, identifier, value, compositeClass);
	}
	
	public IntegerProperty(IntegerProperty other)
	{
		super(other);
	}

	@Override
	public ISharedProperty clone() {
		return new IntegerProperty(this);
	}

	@Override
	public Class getPropertyValueClass() {
		return Integer.class;
	}

	@Override
	protected Integer cloneValue() {
		if (this.value == null) return null;
		return new Integer(value);
	}
		
}
