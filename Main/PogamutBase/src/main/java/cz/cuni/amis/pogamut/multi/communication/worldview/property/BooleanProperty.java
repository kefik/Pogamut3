package cz.cuni.amis.pogamut.multi.communication.worldview.property;

import cz.cuni.amis.pogamut.base.communication.worldview.object.WorldObjectId;
import cz.cuni.amis.pogamut.multi.agent.ITeamId;
import cz.cuni.amis.pogamut.multi.communication.translator.event.ISharedPropertyUpdateResult;
import cz.cuni.amis.pogamut.multi.communication.translator.event.ISharedPropertyUpdatedEvent;
import cz.cuni.amis.pogamut.multi.communication.translator.event.ISharedPropertyUpdateResult.Result;
import cz.cuni.amis.pogamut.multi.communication.worldview.object.ISharedProperty;
import cz.cuni.amis.utils.exception.PogamutException;

/**
 * Implementation of the ISharedProperty interface for boolean value type.
 * @author srlok
 *
 */
public class BooleanProperty extends AbstractSharedProperty<Boolean> {
	
	@SuppressWarnings("rawtypes")
	public BooleanProperty(WorldObjectId objId, String identifier, Boolean value, Class compositeClass)
	{
		super(objId, identifier, value, compositeClass);
	}
	
	public BooleanProperty(BooleanProperty other)
	{
		super(other);
	}

	
	
	@Override
	public ISharedProperty clone() {
		return new BooleanProperty(this);
	}

	@Override
	public Class getPropertyValueClass() {
		return Boolean.class;
	}

	@Override
	protected Boolean cloneValue() {
		if (this.value == null) return null;
		return new Boolean(value);
	}
		
}
