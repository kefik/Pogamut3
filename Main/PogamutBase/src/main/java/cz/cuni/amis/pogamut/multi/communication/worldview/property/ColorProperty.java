package cz.cuni.amis.pogamut.multi.communication.worldview.property;

import java.awt.Color;

import cz.cuni.amis.pogamut.base.communication.worldview.object.WorldObjectId;
import cz.cuni.amis.pogamut.multi.communication.worldview.object.ISharedProperty;

/**
 * Implementation of the ISharedProperty interface for Color value type.
 * @author srlok
 *
 */
public class ColorProperty extends AbstractSharedProperty<Color> {
	
	@SuppressWarnings("rawtypes")
	public ColorProperty(WorldObjectId objId, String identifier, Color value, Class compositeClass)
	{
		super(objId, identifier, value, compositeClass);
	}
	
	public ColorProperty(ColorProperty other)
	{
		super(other);
	}

	@Override
	public ISharedProperty clone() {
		return new ColorProperty(this);
	}

	@Override
	public Class getPropertyValueClass() {
		return Color.class;
	}

	@Override
	protected Color cloneValue() {
		if (this.value == null) return null;
		return new Color(value.getRGB());
	}
		
}
