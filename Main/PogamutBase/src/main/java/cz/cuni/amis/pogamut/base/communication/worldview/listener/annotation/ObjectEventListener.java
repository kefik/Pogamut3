package cz.cuni.amis.pogamut.base.communication.worldview.listener.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import cz.cuni.amis.pogamut.base.communication.worldview.IWorldView;
import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObjectEvent;
import cz.cuni.amis.pogamut.base.communication.worldview.object.WorldObjectId;

/**
 * Used by {@link AnnotationListenerRegistrator} to register level E listener 
 * ({@link IWorldView#addObjectListener(WorldObjectId, Class, cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObjectListener)} 
 * for the annotated method. The annotated method must have 1 parameter of {@link IWorldObjectEvent}.
 * <p><p>
 * The listeners are created by calling {@link AnnotationListenerRegistrator#addListeners()} and removed by 
 * calling {@link AnnotationListenerRegistrator#removeListeners()}.
 * 
 * @author Jimmy
 */
@Target(value = { ElementType.METHOD })
@Retention(value = RetentionPolicy.RUNTIME)
public @interface ObjectEventListener {

	/**
	 * Class implementing {@link WorldObjectId}, the class must have a constructor with 1 String parameter.
	 * @return
	 */
	Class<? extends WorldObjectId> idClass();
	
	/**
	 * Id of the object you want to listen to.
	 * @return
	 * @see IWorldView#addObjectListener(WorldObjectId, Class, cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObjectListener)
	 */
	String objectId();
	
	/**
	 * Events you want the method to receive.
	 * @return
	 * @see IWorldView#addObjectListener(WorldObjectId, Class, cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObjectListener)
	 */
	Class<?> eventClass();
	
}
