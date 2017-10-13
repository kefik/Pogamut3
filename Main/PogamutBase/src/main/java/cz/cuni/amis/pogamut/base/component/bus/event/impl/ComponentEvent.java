package cz.cuni.amis.pogamut.base.component.bus.event.impl;

import java.lang.ref.SoftReference;
import java.lang.reflect.Method;

import cz.cuni.amis.pogamut.base.component.IComponent;
import cz.cuni.amis.pogamut.base.component.bus.IComponentEvent;
import cz.cuni.amis.utils.NullCheck;

/**
 * Note that this is sort of utility class providing pretty-print (reflection based) for toString(), not every
 * event must be descendant of this class. For listener event class specification use always interfaces!
 * @author Jimmy
 *
 * @param <SOURCE>
 */
public class ComponentEvent<SOURCE extends IComponent> implements IComponentEvent<SOURCE> {

	private SOURCE source;

	private SoftReference<String> desc = null;
	
	public ComponentEvent(SOURCE source) {
		this.source = source;
		NullCheck.check(this.source, "source");
	}
	
	@Override
	public SOURCE getSource() {
		return source;
	}
	
//	@Override
//	public String toString() {
//		if (desc != null) {
//			String desc = this.desc.get();
//			if (desc != null) return desc;
//		}
//		StringBuffer sb = new StringBuffer();
//		sb.append(getClass().getSimpleName());
//		sb.append("[");
//		Method[] methods = getClass().getMethods();
//		boolean first = true;
//		for (Method method : methods) {
//			if (!method.getName().startsWith("get") || method.getParameterTypes().length != 0 || method.getReturnType().equals(Void.class)) {
//				continue;
//			}
//			if (first) first = false;
//			else sb.append(", ");
//			sb.append(method.getName().substring(3));
//			sb.append("=");
//			Object obj;
//			try {
//				obj = method.invoke(this);
//			} catch (Exception e) {
//				sb.append("can't be obtained (" + e.getMessage() + ")");
//				continue;
//			}
//			sb.append(obj == null ? "null" : obj.toString());
//		}
//		sb.append("]");
//		String desc = sb.toString();
//		this.desc = new SoftReference<String>(sb.toString());
//		return desc;
//	}
	
	public String toString() {
		return getClass().getSimpleName() + "[source=" + source + "]";
	}

}
