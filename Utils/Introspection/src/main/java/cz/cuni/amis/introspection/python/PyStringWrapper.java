package cz.cuni.amis.introspection.python;

import java.util.ArrayList;

import org.python.core.PyFloat;
import org.python.core.PyObject;
import org.python.core.PyString;


public class PyStringWrapper extends PyObjectWrapper {
	
	public PyStringWrapper() {
		super(String.class);
	}

	@Override
	public ArrayList<PyObjectAdapter> getChildren(Object object) {
		return null;
	}
	
	@Override
	public boolean hasChildren(Object pyObject) {
		return false;
	}

	/**
	 * Returns String instance of the stored value.
	 */
	@Override
	public Object getJavaObject(Object pyObject) {
		if (pyObject instanceof String) return new String((String)pyObject);
		if (pyObject instanceof PyString) {
			PyString pyString = (PyString) pyObject;
			return pyString.__tojava__(String.class);
		}
		throw new IllegalArgumentException("pyObject is instance neither of String nor PyString");
	}

	/**
	 * Returns PyString instance of the value newValue.
	 * NewValue must be of the type String, otherwise
	 * the IllegalCastException will occure.
	 */
	@Override
	public PyObject getNewValue(Object newValue) {
		if (newValue instanceof String) return new PyString((String) newValue);
		throw new IllegalArgumentException("newValue is not instace of String");		
	}

}
