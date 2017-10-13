package cz.cuni.amis.pogamut.base.communication.worldview.listener.annotation.exception;

import java.lang.reflect.Method;

import cz.cuni.amis.utils.ClassUtils;
import cz.cuni.amis.utils.exception.PogamutException;

public class MissingConstructorException extends PogamutException {

	public MissingConstructorException(Method method, Class idClass, Object origin) {
		super("Method " + ClassUtils.getMethodSignature(method) + " referes to object id class of " + idClass + ", but this class does not declare constructor with a String parameter.", origin);
	}

}
