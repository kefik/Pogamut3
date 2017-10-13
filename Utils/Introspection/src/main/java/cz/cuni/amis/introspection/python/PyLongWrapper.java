package cz.cuni.amis.introspection.python;

import java.math.BigInteger;
import java.util.ArrayList;

import org.python.core.PyFloat;
import org.python.core.PyInteger;
import org.python.core.PyLong;
import org.python.core.PyObject;


public class PyLongWrapper extends PyObjectWrapper {

	public PyLongWrapper() {
		super(Long.class);
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
	 * Returns Long instance of the stored value.
	 */
	@Override
	public Object getJavaObject(Object pyObject) {
		if (pyObject instanceof Long) return new Long((Long) pyObject);
		if (pyObject instanceof BigInteger) return ((BigInteger) pyObject).longValue();
		if (pyObject instanceof PyLong){
			PyLong pyLong = (PyLong) pyObject;
			return pyLong.__tojava__(Long.class);
		}
		throw new IllegalArgumentException("pyObject is instance neither of Long nor BigInteger nor PyLong");
	}

	/**
	 * Returns PyLong instance of the value newValue.
	 * NewValue must be of the type Long, otherwise
	 * the IllegalCastException will occure.
	 */
	@Override
	public PyObject getNewValue(Object newValue) {
		if (newValue instanceof Long) return new PyLong((Long) newValue);
		if (newValue instanceof Integer) return new PyLong((Integer) newValue);
		if (newValue instanceof BigInteger) return new PyLong(((BigInteger) newValue).longValue());
		throw new IllegalArgumentException("newValue is neither instance of Long nor BigInteger nor Integer");
	}
	
}
