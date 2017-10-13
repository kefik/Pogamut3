package cz.cuni.amis.introspection.python;

import java.util.ArrayList;

import org.python.core.PyObject;

public abstract class PyObjectWrapper {
	
	private Class javaClass = null;
	
	public PyObjectWrapper(Class javaClass){
		this.javaClass = javaClass;
	}
	
	/**
	 * Returns class which PyObject represents and whose instance is
	 * expected in the set() method as parameter 
	 * @return JavaClass
	 */
	public Class getJavaClass() {
		return this.javaClass;
	}

	/**
	 * If the PyObject contains children (e.g. List, Instance, Dictionary),
	 * they are returned as list of PyObjectAdapter.
	 * @return list of PyObjectAdapter if the pyObject contains children (List, Instance, etc.)
	 */
	public abstract ArrayList<PyObjectAdapter> getChildren(Object pyObject);
	
	/**
	 * Whether the wrapper may have children (e.g. List, Instance, Dictionary).
	 * 
	 * Warning - it does not tells anything about the number of children.
	 * For instance - if it's list which doesn't have any items, it will
	 * still return true.
	 * 
	 * @return true if wrapper can have a child (or more children)
	 */
	public abstract boolean hasChildren(Object pyObject);
	
	/**
	 * Returns java representation of the object.
	 * 
	 * This works well for BASIC_TYPEs, others
	 * are returned as String representations.
	 * 
	 * Note that basic type wrappers (PyInteger, PyLong, PyFloat, PyString)
	 * handles also the java type of objects because the ScriptEngine
	 * sometimes return those types in their java representations.
	 * 
	 * @return Object Java representation of the object
	 */
	public abstract Object getJavaObject(Object pyObject);
	
	/**
	 * Creates instance of correct descendant of the PyObject according
	 * to the type of wrapper from newValue. New value must be instance
	 * of the class which returns getJavaClass().
	 * @param newValue
	 */
	public abstract PyObject getNewValue(Object newValue);
	
}
