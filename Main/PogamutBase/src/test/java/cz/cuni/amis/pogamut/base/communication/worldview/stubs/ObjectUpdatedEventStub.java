package cz.cuni.amis.pogamut.base.communication.worldview.stubs;

import java.lang.reflect.Field;

import junit.framework.Assert;
import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldObjectUpdateResult;
import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldObjectUpdatedEvent;
import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldObjectUpdateResult.Result;
import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObject;
import cz.cuni.amis.pogamut.base.communication.worldview.object.WorldObjectId;
import cz.cuni.amis.utils.ClassUtils;

public class ObjectUpdatedEventStub implements IWorldObjectUpdatedEvent {

	private AbstractObjectStub object;

	public ObjectUpdatedEventStub(AbstractObjectStub obj) {
		this.object = obj;
	}
	
	@Override
	public WorldObjectId getId() {
		return object.getId();
	}

	@Override
	public IWorldObjectUpdateResult update(IWorldObject obj) {
		if (obj == null) return new IWorldObjectUpdateResult.WorldObjectUpdateResult(Result.CREATED, object);
		if (!obj.getClass().equals(object.getClass())) {
			throw new RuntimeException("Can not update object " + obj + " with infos from " + object + " as they are of different class.");
		}
		if (!getId().equals(obj.getId())) {
			throw new RuntimeException("Can not update object " + obj + " with infos from " + object + " as they are of different IDs.");	
		}
		for (Field field : ClassUtils.getAllFields(obj.getClass(), false)) {
			field.setAccessible(true);
			if (field.getName().equalsIgnoreCase("id")) continue;
			try {
				field.set(obj, field.get(object));
			} catch (Exception e) {
				e.printStackTrace();
				Assert.fail("Failed to compare objects for equality...");
			}
		}
		return new IWorldObjectUpdateResult.WorldObjectUpdateResult(Result.UPDATED, obj);
	}
	
	public String toString() {
		return "ObjectUpdatedEvent[" + object + "]";
	}

	@Override
	public long getSimTime() {
		return 0;
	}

}
