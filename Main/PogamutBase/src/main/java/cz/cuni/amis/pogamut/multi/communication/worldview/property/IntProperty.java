package cz.cuni.amis.pogamut.multi.communication.worldview.property;

import cz.cuni.amis.pogamut.base.communication.worldview.object.WorldObjectId;
import cz.cuni.amis.pogamut.multi.communication.worldview.object.ISharedProperty;

/**
 * Implementation of the ISharedProperty interface for Integer value type.
 * <p><p>
 * For the sake of simplicity of GB2004 protocol message definition.
 * <p><p>
 * Effectively the same as {@link IntegerProperty}.
 * 
 * @author Jimmy
 *
 */
public class IntProperty extends AbstractSharedProperty<Integer> {
	
	@SuppressWarnings("rawtypes")
	public IntProperty(WorldObjectId objId, String identifier, Integer value, Class compositeClass)
	{
		super(objId, identifier, value, compositeClass);
	}
	
	public IntProperty(IntProperty other)
	{
		super(other);
	}

	@Override
	public ISharedProperty clone() {
		return new IntProperty(this);
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
