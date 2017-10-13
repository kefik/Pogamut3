package cz.cuni.amis.pogamut.multi.communication.worldview.property;

import cz.cuni.amis.pogamut.base.communication.worldview.object.WorldObjectId;
import cz.cuni.amis.pogamut.base3d.worldview.object.Velocity;
import cz.cuni.amis.pogamut.multi.communication.worldview.object.ISharedProperty;

/**
 * Implementation of the ISharedProperty interface for Velocity value type.
 * @author srlok
 *
 */
public class VelocityProperty extends AbstractSharedProperty<Velocity> {
	
	@SuppressWarnings("rawtypes")
	public VelocityProperty(WorldObjectId objId, String identifier, Velocity value, Class compositeClass)
	{
		super(objId, identifier, value, compositeClass);
	}
	
	public VelocityProperty(VelocityProperty other)
	{
		super(other);
	}
	

	@Override
	public ISharedProperty clone() {
		return new VelocityProperty(this);
	}

	@Override
	public Class getPropertyValueClass() {
		return Velocity.class;
	}

	@Override
	protected Velocity cloneValue() {
		if (this.value == null) return null;
		return new Velocity(this.value);
	}
		
}
