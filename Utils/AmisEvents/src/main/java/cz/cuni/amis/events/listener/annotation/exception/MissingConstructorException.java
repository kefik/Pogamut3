package cz.cuni.amis.events.listener.annotation.exception;

import java.lang.reflect.Method;

import cz.cuni.amis.events.listener.annotation.AnnotationListenerRegistrator;
import cz.cuni.amis.utils.ClassUtils;

public class MissingConstructorException extends RuntimeException {

	public MissingConstructorException(Method method, Class<?> idClass, AnnotationListenerRegistrator origin) {
		super("Method " + ClassUtils.getMethodSignature(method) + " referes to object id class of " + idClass + ", but this class does not declare constructor with a String parameter.");
	}

}
