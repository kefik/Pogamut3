package cz.cuni.amis.pogamut.multi.communication.worldview.property;

import cz.cuni.amis.pogamut.base.communication.worldview.object.WorldObjectId;
import cz.cuni.amis.pogamut.multi.communication.worldview.object.ISharedProperty;

/**
 * Implementation of the ISharedProperty interface for Double value type.
 * @author srlok
 *
 */
public class FloatProperty extends AbstractSharedProperty<Float> {
	
	@SuppressWarnings("rawtypes")
	public FloatProperty(WorldObjectId objId, String identifier, Float value, Class compositeClass)
	{
		super(objId, identifier, value, compositeClass);
	}
	
	public FloatProperty(FloatProperty other)
	{
		super(other);
	}

	@Override
	public ISharedProperty clone() {
		return new FloatProperty(this);
	}

	@Override
	public Class getPropertyValueClass() {
		return Float.class;
	}

	@Override
	protected Float cloneValue() {
		if (this.value == null) return null;
		return new Float(value);
	}
	
}
