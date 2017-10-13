package cz.cuni.amis.events.listener.annotation.exception;

import java.lang.reflect.Method;

import cz.cuni.amis.utils.ClassUtils;

public class MoreThanOneListenerLevelAnnotationException extends RuntimeException {

	public MoreThanOneListenerLevelAnnotationException(Method method, Object origin) {
		super("Method can have only one ListenerLevel annotation: " + ClassUtils.getMethodSignature(method));
	}

}
