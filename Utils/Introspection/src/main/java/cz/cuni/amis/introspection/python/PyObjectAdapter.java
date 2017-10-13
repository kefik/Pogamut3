package cz.cuni.amis.introspection.python;

import java.util.ArrayList;
import java.util.HashMap;

import org.python.core.PyDictionary;
import org.python.core.PyFloat;
import org.python.core.PyInstance;
import org.python.core.PyInteger;
import org.python.core.PyList;
import org.python.core.PyLong;
import org.python.core.PyObject;
import org.python.core.PyString;
import org.python.core.PyTuple;

/**
 * This class is wrapper for PyObject which probes the PyObject
 * for it's type and accessibility and provides methods neccessary
 * for introspection of the PyObject
 * 
 * If you want to extend the number of classes the Python introspection
 * accepts, than simply create class PyNewClassWrapper extends PyObjectWrapper
 * and add it to a PyObjectAdapater.probeObject() body.
 * 
 * The problem with Python introspection is, that we can't rely on the
 * information about the wrapper of the object in stored place
 * as it can change between two calls of PyObjectAdapter methods.
 * 
 * That's why the method getWrapper() has as an parametr PyObject.
 *
 * It's advised to use the adapter like this:
 * 
 * 1) retrieve PyObject object = adapter.getPyObject()
 * 2) retrieve PyObjectWrapper wrapper = adapter.getWrapper(object)
 * 3) get java representation of the object from the wrapper or
 *    set the value to the engine using adapter.set(wrapper, newValue)
 * 
 * @author Jimmy
 */
public class PyObjectAdapter {	
	
	/**
	 * Visual name of the property.
	 */
	private String name = null;
		
	/**
	 * Place of the wrapped object ... if the set() is called this
	 * object determines where to write the new value.
	 */
	private PyObjectPlace place = null;
	
	/**
	 * Basic initialization of the class.
	 * 
	 * @param object
	 * @param place
	 */
	private PyObjectAdapter(String name){
		this.name = name;
		this.place = null;
	}
	
	/**
	 * Initialize the read-write adapter for wrapped PyObject.
	 * 
	 * Note that set() method will work iff it is supported by the
	 * wrapper.getNewValue() method.
	 * 
	 * @param name
	 * @param place
	 */
	public PyObjectAdapter(String name, PyObjectPlace place) {
		this(name);
		this.place = place;
	}
	
	public String toString(){
		return "PyObjectAdapter("+name+")";
	}
	
	/**
	 * Returns array list of childrens.
	 * @return ArrayList childrens
	 */
	public ArrayList<PyObjectAdapter> getChildren(){
		Object object = this.place.get();
		PyObjectWrapper wrapper = this.getWrapper(object);
		if (wrapper instanceof PyUnsupportedWrapper)
			return new ArrayList<PyObjectAdapter>(0);		
		ArrayList<PyObjectAdapter> list = wrapper.getChildren(object);
		if (list == null)
			return new ArrayList<PyObjectAdapter>(0);
		return list;
	}
	
	public String getName() {
		return this.name;
	}
	
	/**
	 * The problem with Python introspection is, that we can't rely on the
	 * information about the wrapper of the object in stored place
	 * as it can change between two calls of PyObjectAdapter methods.
	 * 
	 * That's why the method getWrapper() has as an parametr PyObject.
	 * 
	 * Call getWrapper(getPyObject()) if you want the wrapper but bear in 
	 * mind that the wrapper can change -> there for store it if you
	 * will want to call set().
	 * 
	 * @param object
	 * @return wrapper for the object
	 */
	public PyObjectWrapper getWrapper(Object object){
		return PyObjectWrappersManager.getWrapper(object.getClass());
	}
	
	/**
	 * Returns wrapped object.
	 * @return PyObject
	 */
	public Object getObject(){
		return this.place.get();
	}
	
	/**
	 * Returns java representation of the object.
	 *
	 * The java representation is returned according to the wrapper
	 * of the object stored in the adapter (precisly according
	 * to the object stored in defined place).
	 * 
	 * @return Object Java representation of the object
	 */
	public Object getJavaObject(){
		Object obj = this.place.get();
		if (!(obj instanceof PyObject)) return obj;
		PyObject pyObj = (PyObject) obj;
		PyObjectWrapper wrapper = this.getWrapper(pyObj);
		if (wrapper instanceof PyUnsupportedWrapper) return null;
		return wrapper.getJavaObject(pyObj);		
	}
	
	/**
	 * This accepts java class instance which is proper for the
	 * object it wrappes - it uses wrapper.getNewValue() for 
	 * obtaining the PyObject representation of the java object
	 * and then it uses place.set() to insert the new value to
	 * it's correct place in another python object.
	 * 
	 * @param newValue java object
	 */
	public void set(PyObjectWrapper wrapper, Object newValue){
		if (this.place != null){
			this.place.set(wrapper.getNewValue(newValue));
		}
	}
	
	/**
	 * Whether the object has children ...
	 * ... is list / dictionary / tuple / instance
	 * @return boolean
	 */
	public boolean hasChildren(){		
		Object object = this.place.get();
		return this.getWrapper(object).hasChildren(object);
	}
	
}
