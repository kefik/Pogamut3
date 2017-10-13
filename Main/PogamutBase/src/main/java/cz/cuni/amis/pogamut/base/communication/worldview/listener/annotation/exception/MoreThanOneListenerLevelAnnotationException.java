package cz.cuni.amis.pogamut.base.communication.worldview.listener.annotation.exception;

import java.lang.reflect.Method;

import cz.cuni.amis.utils.ClassUtils;
import cz.cuni.amis.utils.exception.PogamutException;

public class MoreThanOneListenerLevelAnnotationException extends PogamutException {

	public MoreThanOneListenerLevelAnnotationException(Method method, Object origin) {
		super("Method can have only one ListenerLevel annotation: " + ClassUtils.getMethodSignature(method), origin);
	}

	
}
