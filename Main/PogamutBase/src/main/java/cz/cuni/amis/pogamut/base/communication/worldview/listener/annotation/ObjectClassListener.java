package cz.cuni.amis.pogamut.base.communication.worldview.listener.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import cz.cuni.amis.pogamut.base.communication.worldview.IWorldView;
import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObjectEvent;

/**
 * Used by {@link AnnotationListenerRegistrator} to register level B listener 
 * ({@link IWorldView#addObjectListener(Class, cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObjectListener)} 
 * for the annotated method. The annotated method must have 1 parameter of {@link IWorldObjectEvent}.
 * <p><p>
 * The listeners are created by calling {@link AnnotationListenerRegistrator#addListeners()} and removed by 
 * calling {@link AnnotationListenerRegistrator#removeListeners()}.
 * 
 * @author Jimmy
 */
@Target(value = { ElementType.METHOD })
@Retention(value = RetentionPolicy.RUNTIME)
public @interface ObjectClassListener {
	
	/**
	 * Object class you want to listen to. 
	 * @return
	 * @see IWorldView#addObjectListener(Class, cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObjectListener)
	 */
	Class<?> objectClass();
}