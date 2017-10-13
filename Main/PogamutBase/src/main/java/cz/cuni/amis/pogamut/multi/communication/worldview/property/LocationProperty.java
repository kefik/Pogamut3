package cz.cuni.amis.pogamut.multi.communication.worldview.property;

import cz.cuni.amis.pogamut.base.communication.worldview.object.WorldObjectId;
import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.multi.communication.worldview.object.ISharedProperty;

/**
 * Implementation of the ISharedProperty interface for Location value type.
 * @author srlok
 *
 */
public class LocationProperty extends AbstractSharedProperty<Location> {
	
	@SuppressWarnings("rawtypes")
	public LocationProperty(WorldObjectId objId, String identifier, Location value, Class compositeClass)
	{
		super(objId, identifier, value, compositeClass);
	}
	
	public LocationProperty(LocationProperty other)
	{
		super(other);
	}

	@Override
	public ISharedProperty clone() {
		return new LocationProperty(this);
	}

	@Override
	public Class getPropertyValueClass() {
		return Location.class;
	}

	@Override
	protected Location cloneValue() {
		if (this.value == null) return null;
		return new Location(value);
	}
	
	
}
