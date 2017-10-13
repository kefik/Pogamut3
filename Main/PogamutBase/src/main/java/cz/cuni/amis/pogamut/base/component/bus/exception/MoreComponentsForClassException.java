package cz.cuni.amis.pogamut.base.component.bus.exception;

import java.util.Set;
import java.util.logging.Logger;

import cz.cuni.amis.utils.Const;
import cz.cuni.amis.utils.exception.PogamutException;

public class MoreComponentsForClassException extends PogamutException {

	public MoreComponentsForClassException(Class cls, Set components, Object origin) {
		super("More than one component registered to handle the request." + Const.NEW_LINE + "Request: " + classToString(cls) + Const.NEW_LINE + "Components: " + componentsToString(components), origin);
	}
	
	public MoreComponentsForClassException(Class cls, Set components, Logger log, Object origin) {
		super("More than one component registered to handle the request." + Const.NEW_LINE + "Request: " + classToString(cls) + Const.NEW_LINE + "Components: " + componentsToString(components), log, origin);
	}
	
	private static String classToString(Class request) {
		return request.getClass().toString();
	}
	
	private static String componentsToString(Set components) {
		StringBuffer sb = new StringBuffer();
		boolean first = true;
		for (Object component : components) {
			if (first) first = false;
			else sb.append(", ");
			sb.append(component);
		}
		return sb.toString();
	}

}
