package cz.cuni.amis.pogamut.multi.communication.worldview.property;

import cz.cuni.amis.pogamut.base.communication.worldview.object.WorldObjectId;
import cz.cuni.amis.pogamut.multi.communication.worldview.object.ISharedProperty;

/**
 * Implementation of the ISharedProperty interface for Double value type.
 * @author srlok
 *
 */
public class DoubleProperty extends AbstractSharedProperty<Double> {
	
	@SuppressWarnings("rawtypes")
	public DoubleProperty(WorldObjectId objId, String identifier, Double value, Class compositeClass)
	{
		super(objId, identifier, value, compositeClass);
	}
	
	public DoubleProperty(DoubleProperty other)
	{
		super(other);
	}

	@Override
	public ISharedProperty clone() {
		return new DoubleProperty(this);
	}

	@Override
	public Class getPropertyValueClass() {
		return Double.class;
	}

	@Override
	protected Double cloneValue() {
		if (this.value == null) return null;
		return new Double(value);
	}
	
	
		
}
