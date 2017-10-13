package cz.cuni.amis.pogamut.multi.communication.worldview.property;

import cz.cuni.amis.pogamut.base.communication.worldview.object.WorldObjectId;
import cz.cuni.amis.pogamut.base3d.worldview.object.Rotation;
import cz.cuni.amis.pogamut.multi.communication.worldview.object.ISharedProperty;

/**
 * Implementation of the ISharedProperty interface for Double value type.
 * @author srlok
 *
 */
public class RotationProperty extends AbstractSharedProperty<Rotation> {
	
	@SuppressWarnings("rawtypes")
	public RotationProperty(WorldObjectId objId, String identifier, Rotation value, Class compositeClass)
	{
		super(objId, identifier, value, compositeClass);
	}
	
	public RotationProperty(RotationProperty other)
	{
		super(other);
	}

	@Override
	public ISharedProperty clone() {
		return new RotationProperty(this);
	}

	@Override
	public Class getPropertyValueClass() {
		return Rotation.class;
	}

	@Override
	protected Rotation cloneValue() {
		if (this.value == null) return null;
		return new Rotation(value);
	}
	
		
}
