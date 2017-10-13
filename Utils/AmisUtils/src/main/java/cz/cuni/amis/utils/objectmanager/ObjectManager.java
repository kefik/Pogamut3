package cz.cuni.amis.utils.objectmanager;

import java.lang.ref.SoftReference;
import java.util.LinkedList;
import java.util.Queue;


/**
 * ObjectManager is a class that helps you to keep unused objects 
 * in the memory. Rather then to create the objects again and again you can
 * retrieve their old instances. Of course those objects must be reusable and
 * have support for resetting it's crucial variables to your liking.
 * <p><p>
 * Implementation is using soft references therefore you don't have to worry
 * about memory leakage - if JVM will need more memory it will purge those
 * soft references.
 * <p><p>
 * Note that the manager is thread-safe + get() will always return you
 * an old (or possibly) new instance of the object.
 * <p><p>
 * We discourage you to use ObjectManager for objects that have multiple
 * references throughout your code as it is hard to know whether the object
 * is not referenced from other parts of your code so you can give it back
 * to the manager.
 * 
 * @author Jimmy
 *
 * @param <MANAGED_OBJECT>
 */
public class ObjectManager<MANAGED_OBJECT> {

	/**
	 * Queue with unused objects, using soft references.
	 */
	private Queue<SoftReference<MANAGED_OBJECT>> freeObjects;
	
	/**
	 * Factory that allows us to produce new objects as needed.
	 */
	private IObjectFactory<MANAGED_OBJECT> objectFactory;
	
	/**
	 * How many new objects should be created if there is a shortage of 
	 * free objects.
	 */
	private int moreNewObjectsCount;
	
	public ObjectManager(IObjectFactory<MANAGED_OBJECT> objectFactory) {
		this(objectFactory, 10, 5);
	}
	
	/**
	 * @param objectFactory
	 * @param initialObjectCount how many objects to create in advance
	 */
	public ObjectManager(IObjectFactory<MANAGED_OBJECT> objectFactory, int initialObjectCount) {
		this(objectFactory, initialObjectCount, 5);
	}
	
	/**
	 * @param objectFactory
	 * @param initialObjectCount how many objects to create in advance 
	 * @param moreNewObjectsCount how many new objects to create in advance in case of shortage of free objects
	 */
	public ObjectManager(IObjectFactory<MANAGED_OBJECT> objectFactory, int initialObjectCount, int moreNewObjectsCount) {
		
		if (moreNewObjectsCount <= 0) moreNewObjectsCount = 1;
		
		this.moreNewObjectsCount = moreNewObjectsCount;
		
		this.objectFactory = objectFactory;
		
		freeObjects = new LinkedList<SoftReference<MANAGED_OBJECT>>();
		
		generateNewObjects(initialObjectCount);
	}

	/**
	 * Generate 'count' new objects into the queue.
	 * @param count
	 */
	private void generateNewObjects(int count) {
		for (int i = 0; i < count; ++i) {
			freeObjects.add(new SoftReference<MANAGED_OBJECT>(objectFactory.newObject()));
		}
	}
	
	/**
	 * Returns you an instance of object. Note that it might be used one.  
	 * @return
	 */
	public MANAGED_OBJECT get() {
		synchronized(freeObjects) {
			if (freeObjects.size() == 0) generateNewObjects(moreNewObjectsCount);			
			SoftReference<MANAGED_OBJECT> reference = freeObjects.poll();
			MANAGED_OBJECT obj = reference.get();
			while (obj == null && freeObjects.size() > 0) {
				reference = freeObjects.poll();
				obj = reference.get();
			}
			if (obj != null) return obj;
			return objectFactory.newObject();
		}
	}
	
	/**
	 * Returns an instance of object to the manager, it will be stored in the manager
	 * via soft reference. Note that you have to ensure that returned objects
	 * are still usable! Also - you must be sure that this object is not used
	 * in any part of your code!
	 * 
	 * @param obj
	 */
	public void giveBack(MANAGED_OBJECT obj) {
		synchronized(freeObjects) {
			freeObjects.add(new SoftReference<MANAGED_OBJECT>(obj));
		}
	}

}
