package cz.cuni.amis.pogamut.base.communication.worldview.stubs;

import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObject;
import cz.cuni.amis.pogamut.base.communication.worldview.object.WorldObjectId;

public abstract class AbstractObjectStub extends AbstractEntityStub implements IWorldObject {

	protected WorldObjectId id;
	
	protected long time;

	public AbstractObjectStub(WorldObjectId id) {
		this.id = id;
		this.time = 0;
	}
	
	public AbstractObjectStub(WorldObjectId id, long time) {
		this.id = id;
		this.time = time;
	}
	
	@Override
	public AbstractObjectStub clone() {
		return (AbstractObjectStub) super.clone();
	}
	
	@Override
	public WorldObjectId getId() {
		return id;
	}

	@Override
	public long getSimTime() {
		return time;
	}

}