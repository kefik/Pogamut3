package cz.cuni.amis.pogamut.base.communication.worldview.stubs;

import cz.cuni.amis.pogamut.base.communication.worldview.object.WorldObjectId;

public class ObjectPlayerInfoStub extends AbstractObjectStub {

	public static final WorldObjectId ID = WorldObjectId.get("PIId");

	protected int location = 0;

	protected int health = 0;

	public ObjectPlayerInfoStub(int location, int health, double time) {
		super(ID);
		this.location = location;
		this.health = health;
	}

	@Override
	public ObjectPlayerInfoStub clone() {
		return (ObjectPlayerInfoStub) super.clone();
	}

	public int getLocation() {
		return location;
	}

	public int getHealth() {
		return health;
	}

}
