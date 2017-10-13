package cz.cuni.amis.pogamut.base.communication.worldview.listener.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import cz.cuni.amis.pogamut.base.communication.worldview.IWorldView;

/**
 * Used by {@link AnnotationListenerRegistrator} to register level A listener 
 * ({@link IWorldView#addEventListener(Class, cz.cuni.amis.pogamut.base.communication.worldview.event.IWorldEventListener)} 
 * for the annotated method. The annotated method must have 1 parameter of {@link EventListener#eventClass()}.
 * <p><p>
 * The listeners are created by calling {@link AnnotationListenerRegistrator#addListeners()} and removed by 
 * calling {@link AnnotationListenerRegistrator#removeListeners()}.
 * 
 * @author Jimmy
 */
@Target(value = { ElementType.METHOD })
@Retention(value = RetentionPolicy.RUNTIME)
public @interface EventListener {
	
	/**
	 * Event you want the method to receive.
	 * @return
	 * @see IWorldView#addEventListener(Class, cz.cuni.amis.pogamut.base.communication.worldview.event.IWorldEventListener)
	 */
	Class<?> eventClass();
}
