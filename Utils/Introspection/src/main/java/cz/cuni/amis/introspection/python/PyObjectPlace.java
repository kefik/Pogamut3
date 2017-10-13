package cz.cuni.amis.introspection.python;

import org.python.core.PyObject;

/**
 * This absract class is used for anonymous classes which decribes
 * where the PyObject lies (it's owner), when we do set() on the
 * property we have to know where to write the new value. 
 * 
 * It's used to create anonymous classes for various typed of
 * places (inside an PyInstance, PyList, etc.) 
 * 
 * @author Jimmy
 */
public abstract class PyObjectPlace {
	
	/**
	 * This should set the PyObject to the right place.
	 * @param newValue
	 */
	public abstract void set(PyObject newValue);
	
	/**
	 * This should get the Object from the place.
	 * 
	 * May return null if Object doesn't exist at the location.
	 * @return Object from the place - null if there is not any
	 */
	public abstract Object get();

}
