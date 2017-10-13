package cz.cuni.amis.pogamut.multi.communication.worldview.property;

import cz.cuni.amis.pogamut.base.communication.worldview.object.WorldObjectId;
import cz.cuni.amis.pogamut.multi.communication.worldview.object.ISharedProperty;

/**
 * Implementation of the ISharedProperty interface for String value type.
 * @author srlok
 *
 */
public class StringProperty extends AbstractSharedProperty<String> {
	
	@SuppressWarnings("rawtypes")
	public StringProperty(WorldObjectId objId, String identifier, String value, Class compositeClass)
	{
		super(objId, identifier, value, compositeClass);
	}
	
	public StringProperty(StringProperty other)
	{
		super(other);
	}

	@Override
	public ISharedProperty clone() {
		return new StringProperty(this);
	}

	@Override
	public Class getPropertyValueClass() {
		return String.class;
	}

	@Override
	protected String cloneValue() {
		if (this.value == null) return null;
		return new String(value);
	}
		
}
