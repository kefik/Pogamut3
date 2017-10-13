package cz.cuni.amis.pogamut.multi.communication.worldview.property;

import javax.vecmath.Vector3d;

import cz.cuni.amis.pogamut.base.communication.worldview.object.WorldObjectId;
import cz.cuni.amis.pogamut.multi.communication.worldview.object.ISharedProperty;

/**
 * Implementation of the ISharedProperty interface for Vector3d value type.
 * @author srlok
 *
 */
public class Vector3dProperty extends AbstractSharedProperty<Vector3d> {
	
	@SuppressWarnings("rawtypes")
	public Vector3dProperty(WorldObjectId objId, String identifier, Vector3d value, Class compositeClass)
	{
		super(objId, identifier, value, compositeClass);
	}
	
	public Vector3dProperty(Vector3dProperty other)
	{
		super(other);
	}

	@Override
	public ISharedProperty clone() {
		return new Vector3dProperty(this);
	}

	@Override
	public Class getPropertyValueClass() {
		return Vector3d.class;
	}

	@Override
	protected Vector3d cloneValue() {
		if (this.value == null) return null;
		return new Vector3d(value);
	}
		
	
}
