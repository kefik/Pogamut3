package cz.cuni.amis.pogamut.base.communication.worldview.stubs;

import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObject;
import cz.cuni.amis.pogamut.base.communication.worldview.object.WorldObjectId;

public class ObjectItemStub extends AbstractObjectStub implements IWorldObject {

	protected String name;
	protected int location;

	public ObjectItemStub(String id, String name, int location, double time) {
		super(WorldObjectId.get(id));
		this.name = name;
		this.location = location;
	}

	@Override
	public ObjectItemStub clone() {
		return (ObjectItemStub) super.clone();
	}

	public String getName() {
		return name;
	}

	public int getLocation() {
		return location;
	}

}
