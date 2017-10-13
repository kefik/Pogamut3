package cz.cuni.amis.events.listener.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import object.IObjectEvent;
import object.IObjectId;
import cz.cuni.amis.events.IObjectBoard;

/**
 * Used by {@link AnnotationListenerRegistrator} to register level D listener 
 * ({@link IObjectBoard#addObjectListener(object.IObjectId, object.IObjectEventListener)} 
 * for the annotated method. The annotated method must have 1 parameter of {@link IObjectEvent}.
 * <p><p>
 * The listeners are created by calling {@link AnnotationListenerRegistrator#addListeners()} and removed by 
 * calling {@link AnnotationListenerRegistrator#removeListeners()}.
 * 
 * @author Jimmy
 */
@Target(value = { ElementType.METHOD })
@Retention(value = RetentionPolicy.RUNTIME)
public @interface ObjectListener {

	/**
	 * Class implementing {@link WorldObjectId}, the class must have a constructor with 1 String parameter.
	 * @return
	 */
	Class<? extends IObjectId> idClass();
	
	/**
	 * Id of the object you want to listen to.
	 * @return
	 * @see IWorldView#addObjectListener(cz.cuni.amis.pogamut.base.communication.worldview.object.WorldObjectId, cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObjectListener)
	 */
	String objectId();
	
}
