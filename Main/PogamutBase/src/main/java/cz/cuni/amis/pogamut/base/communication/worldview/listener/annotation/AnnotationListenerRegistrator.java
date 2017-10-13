package cz.cuni.amis.pogamut.base.communication.worldview.listener.annotation;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import cz.cuni.amis.pogamut.base.communication.worldview.IWorldView;
import cz.cuni.amis.pogamut.base.communication.worldview.event.IWorldEvent;
import cz.cuni.amis.pogamut.base.communication.worldview.event.IWorldEventListener;
import cz.cuni.amis.pogamut.base.communication.worldview.listener.IListenerRegistrator;
import cz.cuni.amis.pogamut.base.communication.worldview.listener.ListenerLevel;
import cz.cuni.amis.pogamut.base.communication.worldview.listener.annotation.exception.ListenerMethodParametersException;
import cz.cuni.amis.pogamut.base.communication.worldview.listener.annotation.exception.MissingConstructorException;
import cz.cuni.amis.pogamut.base.communication.worldview.listener.annotation.exception.MoreThanOneListenerLevelAnnotationException;
import cz.cuni.amis.pogamut.base.communication.worldview.listener.exception.ListenersAlreadyRegisteredException;
import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObject;
import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObjectEvent;
import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObjectEventListener;
import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObjectListener;
import cz.cuni.amis.pogamut.base.communication.worldview.object.WorldObjectId;
import cz.cuni.amis.utils.ClassUtils;
import cz.cuni.amis.utils.Lazy;
import cz.cuni.amis.utils.NullCheck;
import cz.cuni.amis.utils.exception.PogamutException;
import cz.cuni.amis.utils.maps.LazyMap;

/**
 * The registrator that is driven by annotations on the class it introspects.
 * <p><p>
 * WARNING: the inheritance does not work here! Only the top class object is introspected.
 * 
 * 
 * @author Jimmy
 */
/**
 * @author Jimmy
 *
 */
public class AnnotationListenerRegistrator implements IListenerRegistrator{

	/**
	 * Tries to instantiate ID class of 'idClass' with 'id' as an identifier. It first looks for constructor having only one string parameter
	 * and then for static method "idClass get(String)".
	 * @param method
	 * @param idClass
	 * @param id
	 * @return
	 */
	public static WorldObjectId getId(Method method, Class idClass, String id) {
		try {
			Object newId = null;
			
			// try constructor
			Constructor constructor = null;
			
			try {
				constructor = idClass.getConstructor(String.class);
			} catch (Exception e) {
			}
			if (constructor != null) {
				newId = idClass.getConstructor(String.class).newInstance(id);
			} else {
				Method getMethod = null;
				
				try {
					getMethod = idClass.getMethod("get", String.class);
				} catch (Exception e) {					
				}
				
				if (getMethod != null && isStaticMethod(getMethod) && getMethod.getReturnType().isAssignableFrom(idClass)) {
					newId = getMethod.invoke(idClass, id);
				} else {
					throw new IllegalArgumentException("Creation of ID for the annotation on the method " + ClassUtils.getMethodSignature(method) + " has failed. Can't create object ID, id class " + idClass + " for id '" + id + "', as id class has neither constructor with String as parameter, nor static get(String) method for obtaining ids.");
				}
			}
			
			if (newId == null) {
				throw new IllegalArgumentException("Creation of ID for the annotation on the method " + ClassUtils.getMethodSignature(method) + " has failed. Failed to instantiate ID '" + id + "' using id class " + idClass +", result id is NULL! Have you specified correct idClass?");
			}
			
			if (!newId.getClass().isAssignableFrom(idClass)) {
				throw new IllegalArgumentException("Creation of ID for the annotation on the method " + ClassUtils.getMethodSignature(method) + " has failed. Failed to instantiate CORRECT ID '" + id + "' using id class " + idClass +". Result id is of incompatible class " + newId.getClass() + ". Have you specified correct idClass?");
			}
			
			if (!WorldObjectId.class.isAssignableFrom(newId.getClass())) {
				throw new IllegalArgumentException("Creation of ID for the annotation on the method " + ClassUtils.getMethodSignature(method) + " has failed. Failed to instantiate CORRECT ID '" + id + "' using id class " + idClass +". Result id is of incompatible class " + newId.getClass() + " as it does not extends WorldObjectId class. Have you specified correct idClass?");
			}
			
			return (WorldObjectId) newId;
			
		} catch (Exception e) {
			throw new IllegalArgumentException("Creation of ID for the annotation on the method " + ClassUtils.getMethodSignature(method) + " has failed. Failed to instantiate CORRECT ID '" + id + "' using id class " + idClass +". Exception occured during reflection.", e);
		}
	}
	
	/**
	 * Returns a new {@link WorldObjectId} for the given 'annotation'.
	 * @param method
	 * @param annotation
	 * @return id instance
	 */
	public static WorldObjectId getId(Method method, ObjectListener annotation) {
		NullCheck.check(method, "method");
		NullCheck.check(annotation, "annotation");
		if (annotation.idClass() == null) {				
			throw new IllegalArgumentException(ClassUtils.getMethodSignature(method) + "-@ObjectListener.idClass == null, specify class of the id!");
		}
		if (annotation.objectId() == null) {
			throw new IllegalArgumentException(ClassUtils.getMethodSignature(method) + "-@ObjectListener.idClass == null, specify class of the id!");
		}
		return getId(method, annotation.idClass(), annotation.objectId());
	}
	
	/**
	 * Tells whether 'method' is static.
	 * @param method
	 * @return
	 */
	public static boolean isStaticMethod(Method method) {
		return Modifier.isStatic(method.getModifiers());
	}
	
	/**
	 * Returns a new {@link WorldObjectId} for the given 'annotation'.
	 * @param method
	 * @param annotation
	 * @return id instance
	 */
	public static WorldObjectId getId(Method method, ObjectEventListener annotation) {
		NullCheck.check(method, "method");
		NullCheck.check(annotation, "annotation");
		if (annotation.idClass() == null) {				
			throw new IllegalArgumentException(ClassUtils.getMethodSignature(method) + "-@ObjectEventListener.idClass == null, specify class of the id!");
		}
		if (annotation.objectId() == null) {
			throw new IllegalArgumentException(ClassUtils.getMethodSignature(method) + "-@ObjectEventListener.idClass == null, specify class of the id!");
		}
		return getId(method, annotation.idClass(), annotation.objectId());
	}
	
	/**
	 * Returns listener level that is gained from the method's annotation.
	 * @param method
	 * @return listener level of the method
	 */
	public static ListenerLevel getListenerLevel(Method method) {
		ListenerLevel level = null;
		if (method.isAnnotationPresent(EventListener.class)) {
			level = ListenerLevel.A;
		}
		if (method.isAnnotationPresent(ObjectClassListener.class)) {
			if (level != null) throw new MoreThanOneListenerLevelAnnotationException(method, AnnotationListenerRegistrator.class);
			level = ListenerLevel.B;
		}
		if (method.isAnnotationPresent(ObjectClassEventListener.class)) {
			if (level != null) throw new MoreThanOneListenerLevelAnnotationException(method, AnnotationListenerRegistrator.class);
			level = ListenerLevel.C;
		}
		if (method.isAnnotationPresent(ObjectListener.class)) {
			if (level != null) throw new MoreThanOneListenerLevelAnnotationException(method, AnnotationListenerRegistrator.class);
			level = ListenerLevel.D;
		}
		if (method.isAnnotationPresent(ObjectEventListener.class)) {
			if (level != null) throw new MoreThanOneListenerLevelAnnotationException(method, AnnotationListenerRegistrator.class);
			level = ListenerLevel.E;
		}
		return level;
	}

	/**
	 * Level A listener that can be hooked to the world view (for more info 
	 * about listeners see {@link IWorldView}).
	 * 
	 * @author Jimmy
	 */
	private class LevelAListener implements IWorldEventListener {

		Method method;
		
		public LevelAListener(Method method) {
			NullCheck.check(method, "method");
			this.method = method;
			
			// check annotation
			if (getAnnotation() == null) {
				throw new ListenerMethodParametersException(method, "There is no EventListener annotation on the method!", AnnotationListenerRegistrator.this);				
			}
			if (getEventClass() == null) {
				throw new ListenerMethodParametersException(method, "EventListener.eventClass can't be null!", AnnotationListenerRegistrator.this);
			}
			if (!(IWorldEvent.class.isAssignableFrom(getEventClass()))) {
				throw new ListenerMethodParametersException(method, "EventListener.eventClass == " + getEventClass() + " is not instance of IWorldEvent! Are you trying to use the listener for IWorldObjects? If so, see usage of ObjectClassEventListener or ObjectClassListener or ObjectEventListener or ObjectListener as used inside ResponsiveBot PogamutUT2004 example!", AnnotationListenerRegistrator.this);
			}
			
			// check the method signature
			if (method.getParameterTypes().length != 1 ||
			    !method.getParameterTypes()[0].isAssignableFrom(method.getAnnotation(EventListener.class).eventClass())) { 
				throw new ListenerMethodParametersException(method, method.getAnnotation(EventListener.class), AnnotationListenerRegistrator.this);
			}
			if (method.getParameterTypes()[0].isAssignableFrom(IWorldObject.class)) {
				throw new ListenerMethodParametersException(method, "EventListener can't be used to listen for OBJECTS! You must use ObjectClassEventListener or ObjectClassListener or ObjectEventListener or ObjectListener annotation for that! See ResponsiveBot PogamutUT2004 example!", AnnotationListenerRegistrator.this);
			}			 
		}
		
		public EventListener getAnnotation() {
			return method.getAnnotation(EventListener.class);
		}
		
		public Class getEventClass() {
			return getAnnotation().eventClass();
		}
		
		@Override
		public void notify(Object event) {
			try {
				method.setAccessible(true);
				method.invoke(obj, event);
				method.setAccessible(false);
			} catch (Exception e) {
				throw new PogamutException("Could not invoke LevelA listener " + ClassUtils.getMethodSignature(method) + " with parameter of class " + event.getClass() + ".", e, log, this);
			}
		}
		
	}
	
	private class LevelBListener implements IWorldObjectListener {

		Method method;
		
		public LevelBListener(Method method) {
			NullCheck.check(method, "method");
			this.method = method; 
			
			// check the annotation
			if (getAnnotation() == null) {
				throw new ListenerMethodParametersException(method, "There is no ObjectClassListener annotation on the method!", AnnotationListenerRegistrator.this);				
			}
			if (getObjectClass() == null) {
				throw new ListenerMethodParametersException(method, "ObjectClassListener.objectClass can't be null!", AnnotationListenerRegistrator.this);
			}
			if (!IWorldObject.class.isAssignableFrom(getObjectClass())) {
				throw new ListenerMethodParametersException(method, "ObjectClassListener.objectClass == " + getObjectClass() + " is not instance of IWorldObject! Are you trying to use the listener for IWorldEvent? If so, use EventListener, see the example inside ResponsiveBot PogamutUT2004!", AnnotationListenerRegistrator.this);
			}
			
			// check the method signature
			if (method.getParameterTypes().length != 1 ||
			    !method.getParameterTypes()[0].isAssignableFrom(IWorldObjectEvent.class)) { 
				throw new ListenerMethodParametersException(method, method.getAnnotation(ObjectClassListener.class), AnnotationListenerRegistrator.this);
			}
			
		}
		
		public ObjectClassListener getAnnotation() {
			return method.getAnnotation(ObjectClassListener.class);
		}
		
		public Class getObjectClass() {
			return getAnnotation().objectClass();
		}
		
		@Override
		public void notify(Object event) {
			try {
				method.setAccessible(true);
				method.invoke(obj, event);
				method.setAccessible(false);
			} catch (Exception e) {
				throw new PogamutException("Could not invoke LevelB listener " + ClassUtils.getMethodSignature(method) + " with parameter of class " + event.getClass() + ".", e, log, this);
			}
		}
		
	}
	
	private class LevelCListener implements IWorldObjectEventListener {

		Method method;
		
		public LevelCListener(Method method) {
			NullCheck.check(method, "method");
			this.method = method; 
			
			// check the annotation
			if (getAnnotation() == null) {
				throw new ListenerMethodParametersException(method, "There is no ObjectClassEventListener annotation on the method!", AnnotationListenerRegistrator.this);				
			}
			if (getEventClass() == null) {
				throw new ListenerMethodParametersException(method, "ObjectClassEventListener.eventClass can't be null!", AnnotationListenerRegistrator.this);
			}
			if (!IWorldObjectEvent.class.isAssignableFrom(getEventClass())) {
				throw new ListenerMethodParametersException(method, "ObjectClassEventListener.eventClass == " + getEventClass() + " is not instance of IWorldObjectEvent! Are you trying to use the listener for IWorldEvent? If so, use EventListener, see the example inside ResponsiveBot PogamutUT2004!", AnnotationListenerRegistrator.this);	
			}
			if (getObjectClass() == null) {
				throw new ListenerMethodParametersException(method, "ObjectClassEventListener.objectClass can't be null!", AnnotationListenerRegistrator.this);
			}
			if (!IWorldObject.class.isAssignableFrom(getObjectClass())) {
				throw new ListenerMethodParametersException(method, "ObjectClassEventListener.objectClass == " + getObjectClass() + " is not instance of IWorldObject! Are you trying to use the listener for IWorldEvent? If so, use EventListener, see the example inside ResponsiveBot PogamutUT2004!", AnnotationListenerRegistrator.this);
			}
			
			// check the method signature
			if (method.getParameterTypes().length != 1 ||
			    !method.getParameterTypes()[0].isAssignableFrom(method.getAnnotation(ObjectClassEventListener.class).eventClass())) { 
				throw new ListenerMethodParametersException(method, method.getAnnotation(ObjectClassEventListener.class), AnnotationListenerRegistrator.this);
			}		
		}
		
		public ObjectClassEventListener getAnnotation() {
			return method.getAnnotation(ObjectClassEventListener.class);
		}
		
		public Class getEventClass() {
			return getAnnotation().eventClass();
		}
		
		public Class getObjectClass() {
			return getAnnotation().objectClass();
		}
		
		@Override
		public void notify(Object event) {
			try {
				method.setAccessible(true);
				method.invoke(obj, event);
				method.setAccessible(false);
			} catch (Exception e) {
				throw new PogamutException("Could not invoke LevelC listener " + ClassUtils.getMethodSignature(method) + " with parameter of class " + event.getClass() + ".", e, log, this);
			}
		}
		
	}
	
	private class LevelDListener implements IWorldObjectListener {

		Method method;
		
		WorldObjectId objectId;
		
		public LevelDListener(Method method) {
			NullCheck.check(method, "method");
			this.method = method; 
			
			// check the annotation
			if (getAnnotation() == null) {
				throw new ListenerMethodParametersException(method, "There is no ObjectListener annotation on the method!", AnnotationListenerRegistrator.this);				
			}
			
			// check the method signature
			if (method.getParameterTypes().length != 1 ||
			    !method.getParameterTypes()[0].isAssignableFrom(IWorldObjectEvent.class)) { 
				throw new ListenerMethodParametersException(method, method.getAnnotation(ObjectListener.class), AnnotationListenerRegistrator.this);
			}
			
			objectId = getId(method, getAnnotation());
		}
		
		public ObjectListener getAnnotation() {
			return method.getAnnotation(ObjectListener.class);
		}
		
		public WorldObjectId getObjectId() {
			return objectId;
		}
		
		@Override
		public void notify(Object event) {
			try {
				method.setAccessible(true);
				method.invoke(obj, event);
				method.setAccessible(false);
			} catch (Exception e) {
				throw new PogamutException("Could not invoke LevelD listener " + ClassUtils.getMethodSignature(method) + " with parameter of class " + event.getClass() + ".", e, log, this);
			}
		}
		
	}
	
	private class LevelEListener implements IWorldObjectEventListener {

		Method method;
		
		WorldObjectId objectId;
		
		public LevelEListener(Method method) {
			NullCheck.check(method, "method");
			this.method = method; 
			
			// check the annotation
			if (getAnnotation() == null) {
				throw new ListenerMethodParametersException(method, "There is no ObjectListener annotation on the method!", AnnotationListenerRegistrator.this);				
			}
			if (getEventClass() == null) {
				throw new ListenerMethodParametersException(method, "ObjectEventListener.eventClass can't be null!", AnnotationListenerRegistrator.this);
			}
			if (!IWorldObjectEvent.class.isAssignableFrom(getEventClass())) {
				throw new ListenerMethodParametersException(method, "ObjectEventListener.eventClass == " + getEventClass() + " is not instance of IWorldObjectEvent! Are you trying to use the listener for IWorldEvent? If so, use EventListener, see the example inside ResponsiveBot PogamutUT2004!", AnnotationListenerRegistrator.this);	
			}
			
			// check the method signature
			if (method.getParameterTypes().length != 1 ||
			    !method.getParameterTypes()[0].isAssignableFrom(method.getAnnotation(ObjectEventListener.class).eventClass())) { 
				throw new ListenerMethodParametersException(method, method.getAnnotation(ObjectEventListener.class), AnnotationListenerRegistrator.this);
			}
			
			objectId = getId(method, getAnnotation());
		}
		
		public ObjectEventListener getAnnotation() {
			return method.getAnnotation(ObjectEventListener.class);
		}
		
		public WorldObjectId getObjectId() {
			return objectId;
		}
		
		public Class getEventClass() {
			return getAnnotation().eventClass();
		}
		
		@Override
		public void notify(Object event) {
			try {
				method.setAccessible(true);
				method.invoke(obj, event);
				method.setAccessible(false);
			} catch (Exception e) {
				throw new PogamutException("Could not invoke LevelE listener " + ClassUtils.getMethodSignature(method) + " with parameter of class " + event.getClass() + ".", e, log, this);
			}
		}
		
	}
	
	private IWorldView worldView;
	private Object obj;
	private boolean listenersRegistered = false;
	
	private Lazy<List<Method>> methods = new Lazy<List<Method>>() {

		@Override
		protected List<Method> create() {
			return probeMethods();
		}
		
	};
	
	private Map<ListenerLevel, List<IWorldEventListener>> listeners = new LazyMap<ListenerLevel, List<IWorldEventListener>>() {

		@Override
		protected List<IWorldEventListener> create(ListenerLevel key) {
			return new ArrayList<IWorldEventListener>();
		}
		
	};
	
	private Logger log;
	
	public AnnotationListenerRegistrator(Object obj, IWorldView worldView, Logger log) {
		this.worldView = worldView;
		NullCheck.check(this.worldView, "worldView");
		this.obj = obj;
		NullCheck.check(this.obj, "obj");
		this.log = log;
		NullCheck.check(this.log, "log");
	}
	
	/**
	 * Check the {@link AnnotationListenerRegistrator#obj} methods for the listener annotations. 
	 * @return
	 */
	private List<Method> probeMethods() {
		List<Method> methods = new ArrayList<Method>();
		for (Method method : obj.getClass().getDeclaredMethods()) {
			if (getListenerLevel(method) != null) methods.add(method);
		}
		return methods;
	}
	
	/**
	 * Introspect all object's methods and register various listeners based on
	 * {@link EventListener}, etc... annotations.
	 * 
	 * @param obj
	 */
	@Override
	public synchronized void addListeners() throws ListenersAlreadyRegisteredException {
		if (listenersRegistered) throw new ListenersAlreadyRegisteredException(this);
		if (log.isLoggable(Level.FINER)) log.finer(obj + " -> " + worldView + ": Registering listeners.");
		for (Method method : this.methods.getVal()) {
			switch(getListenerLevel(method)){
			case A:
				if (log.isLoggable(Level.FINE)) log.fine(obj + " -> " + worldView + ": Registering level A listener for " + ClassUtils.getMethodSignature(method));
				LevelAListener listenerA = new LevelAListener(method);
				worldView.addEventListener(listenerA.getEventClass(), listenerA);
				listeners.get(ListenerLevel.A).add(listenerA);
				break;
			case B:
				if (log.isLoggable(Level.FINE)) log.fine(obj + " -> " + worldView + ": Registering level B listener for " + ClassUtils.getMethodSignature(method));
				LevelBListener listenerB = new LevelBListener(method);
				worldView.addObjectListener(listenerB.getObjectClass(), listenerB);
				listeners.get(ListenerLevel.B).add(listenerB);
				break;
			case C:
				if (log.isLoggable(Level.FINE)) log.fine(obj + " -> " + worldView + ": Registering level C listener for " + ClassUtils.getMethodSignature(method));
				LevelCListener listenerC = new LevelCListener(method);
				worldView.addObjectListener(listenerC.getObjectClass(), listenerC.getEventClass(), listenerC);
				listeners.get(ListenerLevel.C).add(listenerC);
				break;
			case D:
				if (log.isLoggable(Level.FINE)) log.fine(obj + " -> " + worldView + ": Registering level D listener for " + ClassUtils.getMethodSignature(method));
				LevelDListener listenerD = new LevelDListener(method);
				worldView.addObjectListener(listenerD.getObjectId(), listenerD);
				listeners.get(ListenerLevel.D).add(listenerD);
				break;
			case E:
				if (log.isLoggable(Level.FINE)) log.fine(obj + " -> " + worldView + ": Registering level E listener for " + ClassUtils.getMethodSignature(method));
				LevelEListener listenerE = new LevelEListener(method);
				worldView.addObjectListener(listenerE.getObjectId(), listenerE.getEventClass(), listenerE);
				listeners.get(ListenerLevel.E).add(listenerE);
				break;
			}	
		}
		if (log.isLoggable(Level.INFO)) log.info(obj + " -> " + worldView + ": Registered " + listeners.size() + " listeners.");
	}
	
	public int getListenersCount() {
		return listeners.get(ListenerLevel.A).size() + listeners.get(ListenerLevel.B).size() + listeners.get(ListenerLevel.C).size() + listeners.get(ListenerLevel.D).size() + listeners.get(ListenerLevel.E).size();
	}

	@Override
	public synchronized void removeListeners() {
		if (!listenersRegistered) return;
		if (log.isLoggable(Level.FINER)) log.finer(obj + " -> " + worldView + ": Removing " + getListenersCount()  + " listeners.");
		for (IWorldEventListener l : listeners.get(ListenerLevel.A)) {
			LevelAListener listenerA = (LevelAListener) l;
			worldView.removeEventListener(listenerA.getEventClass(), listenerA);
		}
		for (IWorldEventListener l : listeners.get(ListenerLevel.B)) {
			LevelBListener listenerB = (LevelBListener) l;
			worldView.removeObjectListener(listenerB.getObjectClass(), listenerB);
		}
		for (IWorldEventListener l : listeners.get(ListenerLevel.C)) {
			LevelCListener listenerC = (LevelCListener) l;
			worldView.removeObjectListener(listenerC.getObjectClass(), listenerC.getEventClass(), listenerC);
		}
		for (IWorldEventListener l : listeners.get(ListenerLevel.D)) {
			LevelDListener listenerD = (LevelDListener) l;
			worldView.removeObjectListener(listenerD.getObjectId(), listenerD);
		}
		for (IWorldEventListener l : listeners.get(ListenerLevel.E)) {
			LevelEListener listenerE = (LevelEListener) l;
			worldView.removeObjectListener(listenerE.getObjectId(), listenerE.getEventClass(), listenerE);
		}
		if (log.isLoggable(Level.INFO)) log.info(obj + " -> " + worldView + ": Listeners removed.");
	}

	public Logger getLog() {
		return log;
	}
	
}
