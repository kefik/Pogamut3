package cz.cuni.amis.pogamut.base.communication.worldview.stubs;

import java.lang.reflect.Field;

import cz.cuni.amis.utils.ClassUtils;

import junit.framework.Assert;

public abstract class AbstractEntityStub implements Cloneable {

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (!getClass().equals(obj.getClass())) {
			return false;
		}
		for (Field field : ClassUtils.getAllFields(this.getClass(), false)) {
			field.setAccessible(true);
			try {
				Object thisValue = field.get(this);
				Object objValue = field.get(obj);
				if (!(thisValue.equals(objValue))) {
					return false;
				}
			} catch (Exception e) {
				e.printStackTrace();
				Assert.fail("Failed to compare objects for equality...");
			}
		}
		return true;
	}
	
	@Override
	public AbstractEntityStub clone() {
		try {
			return (AbstractEntityStub) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private String getClassNamePart() {
		String name = getClass().getSimpleName();
		if (name.startsWith("Event")) name = name.substring(5);
		if (name.endsWith("Stub")) name = name.substring(0, name.length()-4);
		return name;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(getClassNamePart());
		sb.append("[");
		boolean first = true;
		for (Field field : ClassUtils.getAllFields(this.getClass(), false)) {
			field.setAccessible(true);
			if (first) first = false;
			else sb.append(", ");
			try {
				sb.append(field.getName());
				sb.append(" = ");
				sb.append(field.get(this));
			} catch (Exception e) {
				e.printStackTrace();
			}
	
		}
		sb.append("]");
		return sb.toString();
	}

}