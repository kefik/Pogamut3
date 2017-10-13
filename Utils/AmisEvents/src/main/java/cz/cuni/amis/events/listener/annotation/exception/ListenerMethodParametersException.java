package cz.cuni.amis.events.listener.annotation.exception;

import java.lang.reflect.Method;

import object.IObjectEvent;
import cz.cuni.amis.events.listener.annotation.AnnotationListenerRegistrator;
import cz.cuni.amis.events.listener.annotation.EventListener;
import cz.cuni.amis.events.listener.annotation.ObjectClassEventListener;
import cz.cuni.amis.events.listener.annotation.ObjectClassListener;
import cz.cuni.amis.events.listener.annotation.ObjectEventListener;
import cz.cuni.amis.events.listener.annotation.ObjectListener;
import cz.cuni.amis.utils.ClassUtils;

public class ListenerMethodParametersException extends RuntimeException {

	public ListenerMethodParametersException(Method method, String message, AnnotationListenerRegistrator origin) {
		super("Method signature " + ClassUtils.getMethodSignature(method) + " is not suitable! " + message);
	}
	
	public ListenerMethodParametersException(Method method, EventListener annotation, AnnotationListenerRegistrator origin) {
		super("Method signature " + ClassUtils.getMethodSignature(method) + " does not match required signature " + getLevelSignature(annotation, method) + ".");
	}
	
	public ListenerMethodParametersException(Method method, ObjectClassListener annotation, AnnotationListenerRegistrator origin) {
		super("Method signature " + ClassUtils.getMethodSignature(method) + " does not match required signature " + getLevelSignature(annotation, method) + ".");
	}
	
	public ListenerMethodParametersException(Method method, ObjectClassEventListener annotation, AnnotationListenerRegistrator origin) {
		super("Method signature " + ClassUtils.getMethodSignature(method) + " does not match required signature " + getLevelSignature(annotation, method) + ".");
	}
	
	public ListenerMethodParametersException(Method method, ObjectListener annotation, AnnotationListenerRegistrator origin) {
		super("Method signature " + ClassUtils.getMethodSignature(method) + " does not match required signature " + getLevelSignature(annotation, method) + ".");
	}
	
	public ListenerMethodParametersException(Method method, ObjectEventListener annotation, AnnotationListenerRegistrator origin) {
		super("Method signature " + ClassUtils.getMethodSignature(method) + " does not match required signature " + getLevelSignature(annotation, method) + ".");
	}

	private static String getLevelSignature(EventListener annotation, Method method) {
		return "void " + method.getName() + "(" + annotation.eventClass() + " event)";
	}
	
	private static String getLevelSignature(ObjectClassListener annotation, Method method) {
		return "void " + method.getName() + "(" + IObjectEvent.class + " event)";
	}
	
	private static String getLevelSignature(ObjectClassEventListener annotation, Method method) {
		return "void " + method.getName() + "(" + annotation.eventClass() + " event)";
	}
	
	private static String getLevelSignature(ObjectListener annotation, Method method) {
		return "void " + method.getName() + "(" + IObjectEvent.class + " event)";
	}
	
	private static String getLevelSignature(ObjectEventListener annotation, Method method) {
		return "void " + method.getName() + "(" + annotation.eventClass() + " event)";
	}

}
