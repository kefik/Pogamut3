package cz.cuni.amis.pogamut.base.communication.worldview.listener.annotation.exception;

import java.lang.reflect.Method;
import java.util.logging.Logger;

import cz.cuni.amis.pogamut.base.communication.worldview.listener.annotation.AnnotationListenerRegistrator;
import cz.cuni.amis.pogamut.base.communication.worldview.listener.annotation.EventListener;
import cz.cuni.amis.pogamut.base.communication.worldview.listener.annotation.ObjectClassListener;
import cz.cuni.amis.pogamut.base.communication.worldview.listener.annotation.ObjectClassEventListener;
import cz.cuni.amis.pogamut.base.communication.worldview.listener.annotation.ObjectListener;
import cz.cuni.amis.pogamut.base.communication.worldview.listener.annotation.ObjectEventListener;
import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObjectEvent;
import cz.cuni.amis.utils.ClassUtils;
import cz.cuni.amis.utils.exception.PogamutException;

public class ListenerMethodParametersException extends PogamutException {

	public ListenerMethodParametersException(Method method, String message, AnnotationListenerRegistrator origin) {
		super("Method signature " + ClassUtils.getMethodSignature(method) + " is not suitable! " + message, origin);
	}
	
	public ListenerMethodParametersException(Method method, EventListener annotation, AnnotationListenerRegistrator origin) {
		super("Method signature " + ClassUtils.getMethodSignature(method) + " does not match required signature " + getLevelSignature(annotation, method) + ".", origin);
	}
	
	public ListenerMethodParametersException(Method method, ObjectClassListener annotation, AnnotationListenerRegistrator origin) {
		super("Method signature " + ClassUtils.getMethodSignature(method) + " does not match required signature " + getLevelSignature(annotation, method) + ".", origin);
	}
	
	public ListenerMethodParametersException(Method method, ObjectClassEventListener annotation, AnnotationListenerRegistrator origin) {
		super("Method signature " + ClassUtils.getMethodSignature(method) + " does not match required signature " + getLevelSignature(annotation, method) + ".", origin);
	}
	
	public ListenerMethodParametersException(Method method, ObjectListener annotation, AnnotationListenerRegistrator origin) {
		super("Method signature " + ClassUtils.getMethodSignature(method) + " does not match required signature " + getLevelSignature(annotation, method) + ".", origin);
	}
	
	public ListenerMethodParametersException(Method method, ObjectEventListener annotation, AnnotationListenerRegistrator origin) {
		super("Method signature " + ClassUtils.getMethodSignature(method) + " does not match required signature " + getLevelSignature(annotation, method) + ".", origin);
	}
	
	public ListenerMethodParametersException(Method method, EventListener annotation, Logger log, AnnotationListenerRegistrator origin) {
		super("Method signature " + ClassUtils.getMethodSignature(method) + " does not match required signature " + getLevelSignature(annotation, method) + ".", log, origin);
	}

	public ListenerMethodParametersException(Method method, ObjectClassListener annotation, Logger log, AnnotationListenerRegistrator origin) {
		super("Method signature " + ClassUtils.getMethodSignature(method) + " does not match required signature " + getLevelSignature(annotation, method) + ".", log, origin);
	}
	
	public ListenerMethodParametersException(Method method, ObjectClassEventListener annotation, Logger log, AnnotationListenerRegistrator origin) {
		super("Method signature " + ClassUtils.getMethodSignature(method) + " does not match required signature " + getLevelSignature(annotation, method) + ".", log, origin);
	}
	
	public ListenerMethodParametersException(Method method, ObjectListener annotation, Logger log, AnnotationListenerRegistrator origin) {
		super("Method signature " + ClassUtils.getMethodSignature(method) + " does not match required signature " + getLevelSignature(annotation, method) + ".", log, origin);
	}
	
	public ListenerMethodParametersException(Method method, ObjectEventListener annotation, Logger log, AnnotationListenerRegistrator origin) {
		super("Method signature " + ClassUtils.getMethodSignature(method) + " does not match required signature " + getLevelSignature(annotation, method) + ".", log, origin);
	}
	
	private static String getLevelSignature(EventListener annotation, Method method) {
		return "void " + method.getName() + "(" + annotation.eventClass() + " event)";
	}
	
	private static String getLevelSignature(ObjectClassListener annotation, Method method) {
		return "void " + method.getName() + "(" + IWorldObjectEvent.class + " event)";
	}
	
	private static String getLevelSignature(ObjectClassEventListener annotation, Method method) {
		return "void " + method.getName() + "(" + annotation.eventClass() + " event)";
	}
	
	private static String getLevelSignature(ObjectListener annotation, Method method) {
		return "void " + method.getName() + "(" + IWorldObjectEvent.class + " event)";
	}
	
	private static String getLevelSignature(ObjectEventListener annotation, Method method) {
		return "void " + method.getName() + "(" + annotation.eventClass() + " event)";
	}
	
	
}
