package cz.cuni.amis.pogamut.base.communication.worldview.stubs;

import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldObjectUpdateResult;
import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldObjectUpdatedEvent;
import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldObjectUpdateResult.Result;
import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObject;
import cz.cuni.amis.pogamut.base.communication.worldview.object.WorldObjectId;

public class ObjectDestroyedEventStub extends AbstractEntityStub implements IWorldObjectUpdatedEvent {
	
	private Class cls;
	private WorldObjectId id;

	public ObjectDestroyedEventStub(Class cls, WorldObjectId id) {
		this.cls = cls;
		this.id = id;
	}
	
	public ObjectDestroyedEventStub(Class cls, String id) {
		this.cls = cls;
		this.id = WorldObjectId.get(id);
	}
	
	public Class getObjectClass() {
		return cls;
	}

	@Override
	public WorldObjectId getId() {
		return id;
	}

	@Override
	public IWorldObjectUpdateResult update(IWorldObject obj) {
		return new IWorldObjectUpdateResult.WorldObjectUpdateResult(Result.DESTROYED, null);
	}
	
	@Override
	public long getSimTime() {
		return 0;
	}

}
