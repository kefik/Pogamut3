package cz.cuni.amis.pogamut.multi.communication.worldview.property;

import cz.cuni.amis.pogamut.base.communication.worldview.object.WorldObjectId;
import cz.cuni.amis.pogamut.base3d.worldview.object.Rotation;
import cz.cuni.amis.pogamut.multi.communication.worldview.object.ISharedProperty;

public class LongProperty extends AbstractSharedProperty<Long> {

	public LongProperty(WorldObjectId objId, String identifier, Long value, Class compositeClass)
	{
		super(objId, identifier, value, compositeClass);
	}
	
	public LongProperty( LongProperty other )
	{
		super( other );
		
	}

	@Override
	public Class<?> getPropertyValueClass() {
		return Long.class;
	}

	@Override
	protected Long cloneValue() {
		return value.longValue();
	}

	@Override
	public ISharedProperty<Long> clone() {
		if (this.value == null) return null;
		return new LongProperty(this);
	}
	
}
