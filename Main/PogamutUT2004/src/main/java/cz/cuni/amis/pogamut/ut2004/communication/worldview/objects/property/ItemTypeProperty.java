package cz.cuni.amis.pogamut.ut2004.communication.worldview.objects.property;

import cz.cuni.amis.pogamut.base.communication.worldview.object.WorldObjectId;
import cz.cuni.amis.pogamut.multi.communication.worldview.object.ISharedProperty;
import cz.cuni.amis.pogamut.multi.communication.worldview.property.AbstractSharedProperty;
import cz.cuni.amis.pogamut.ut2004.communication.messages.ItemType;

/**
 * Implementation of the ISharedProperty interface for ItemType value type.
 * @author srlok
 *
 */
public class ItemTypeProperty extends AbstractSharedProperty<ItemType> {
	
	@SuppressWarnings("rawtypes")
	public ItemTypeProperty(WorldObjectId objId, String identifier, ItemType value, Class compositeClass)
	{
		super(objId, identifier, value, compositeClass);
	}
	
	public ItemTypeProperty(ItemTypeProperty other)
	{
		super(other);
	}

	@Override
	public ISharedProperty clone() {
		return new ItemTypeProperty(this);
	}
	
	@Override
	public ItemType cloneValue() {
		return value;
	}

	@Override
	public Class getPropertyValueClass() {
		return ItemType.class;
	}
		
}
