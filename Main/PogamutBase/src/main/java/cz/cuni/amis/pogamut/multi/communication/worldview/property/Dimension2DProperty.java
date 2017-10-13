package cz.cuni.amis.pogamut.multi.communication.worldview.property;

import java.awt.geom.Dimension2D;

import cz.cuni.amis.pogamut.base.communication.worldview.object.WorldObjectId;
import cz.cuni.amis.pogamut.multi.communication.worldview.object.ISharedProperty;

/**
 * Implementation of the ISharedProperty interface for Dimension2D value type.
 * @author srlok
 *
 */
public class Dimension2DProperty extends AbstractSharedProperty<Dimension2D> {
	
	@SuppressWarnings("rawtypes")
	public Dimension2DProperty(WorldObjectId objId, String identifier, Dimension2D value, Class compositeClass)
	{
		super(objId, identifier, value, compositeClass);
	}
	
	public Dimension2DProperty(Dimension2DProperty other)
	{
		super(other);
	}

	@Override
	public ISharedProperty clone() {
		return new Dimension2DProperty(this);
	}

	@Override
	public Class getPropertyValueClass() {
		return Dimension2D.class;
	}

	@Override
	protected Dimension2D cloneValue() {
		if (this.value == null) return null;
		return (Dimension2D) value.clone();
	}
		
}
