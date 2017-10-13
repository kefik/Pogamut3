package cz.cuni.amis.introspection.python;

import java.math.BigInteger;
import java.util.HashMap;

import org.python.core.PyDictionary;
import org.python.core.PyFloat;
import org.python.core.PyInstance;
import org.python.core.PyInteger;
import org.python.core.PyList;
import org.python.core.PyLong;
import org.python.core.PyString;
import org.python.core.PyTuple;

/**
 * Here is a class which manages wrappers for Jython classes.
 * If you wish to add a new wrapper, just create a wrapper and register
 * it here in protected constructor.
 * 
 * @author Jimmy
 */
public class PyObjectWrappersManager {

	private static HashMap<Class, PyObjectWrapper> wrappers = new HashMap<Class, PyObjectWrapper>();	
	
	private static PyObjectWrappersManager thisInstance = new PyObjectWrappersManager();
	
	private static PyUnsupportedWrapper unsupported = new PyUnsupportedWrapper();
	
	protected PyObjectWrappersManager(){
		PyObjectWrappersManager.wrappers.put(PyInteger.class,    new PyIntegerWrapper());
		PyObjectWrappersManager.wrappers.put(Integer.class,      new PyIntegerWrapper());
		PyObjectWrappersManager.wrappers.put(PyFloat.class,      new PyFloatWrapper());
		PyObjectWrappersManager.wrappers.put(Float.class,        new PyFloatWrapper());
		PyObjectWrappersManager.wrappers.put(Double.class,       new PyFloatWrapper());
		PyObjectWrappersManager.wrappers.put(PyLong.class,       new PyLongWrapper());
		PyObjectWrappersManager.wrappers.put(Long.class,         new PyLongWrapper());
		PyObjectWrappersManager.wrappers.put(BigInteger.class,   new PyLongWrapper());
		PyObjectWrappersManager.wrappers.put(PyString.class,     new PyStringWrapper());
		PyObjectWrappersManager.wrappers.put(String.class,       new PyStringWrapper());
		PyObjectWrappersManager.wrappers.put(PyList.class,       new PyListWrapper());
		PyObjectWrappersManager.wrappers.put(PyDictionary.class, new PyDictionaryWrapper());
		PyObjectWrappersManager.wrappers.put(PyTuple.class,      new PyTupleWrapper());
		PyObjectWrappersManager.wrappers.put(PyInstance.class,   new PyInstanceWrapper());
	}
	
	public static PyObjectWrapper getWrapper(Class c){
		PyObjectWrapper wrapper = PyObjectWrappersManager.wrappers.get(c);
		if (wrapper == null) return PyObjectWrappersManager.unsupported;
		return wrapper;
	}
	
	public static void registerWrapper(Class c, PyObjectWrapper wrapper){
		PyObjectWrappersManager.wrappers.put(c, wrapper);
	}
	
}
