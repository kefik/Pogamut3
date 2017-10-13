package cz.cuni.amis.introspection.python;

import java.util.ArrayList;

import org.python.core.PyFloat;
import org.python.core.PyInteger;
import org.python.core.PyObject;


public class PyIntegerWrapper extends PyObjectWrapper {
	
	public PyIntegerWrapper() {
		super(Integer.class);
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
	 * Returns Integer instance of the stored value.
	 */
	@Override
	public Object getJavaObject(Object pyObject) {
		if (pyObject instanceof Integer) return new Integer((Integer)pyObject);
		if (pyObject instanceof PyInteger) {
			PyInteger pyInteger = (PyInteger) pyObject;
			return pyInteger.__tojava__(Integer.class);
		}
		throw new IllegalArgumentException("pyObject is instance neither of Integer nor PyInteger");
	}

	/**
	 * Returns PyInteger instance of the value newValue.
	 * NewValue must be of the type Integer, otherwise
	 * the IllegalCastException will occure.
	 */
	@Override
	public PyObject getNewValue(Object newValue) {
		if (newValue instanceof Integer) return new PyInteger((Integer) newValue);
		throw new IllegalArgumentException("newValue is not instance of Integer");
	}
	
}
