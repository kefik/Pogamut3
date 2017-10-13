package cz.cuni.amis.introspection.python;

import java.util.ArrayList;

import org.python.core.PyFloat;
import org.python.core.PyInteger;
import org.python.core.PyList;
import org.python.core.PyObject;

public class PyFloatWrapper extends PyObjectWrapper {

	public PyFloatWrapper() {
		super(Double.class);		
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
	 * Returns Double instance of the stored value.
	 */
	@Override
	public Object getJavaObject(Object pyObject) {		
		if (pyObject instanceof Float)  return new Double((Float)pyObject);
		if (pyObject instanceof Double) return new Double((Double)pyObject);
		if (pyObject instanceof PyFloat){
			PyFloat pyFloat = (PyFloat)pyObject;
			return pyFloat.__tojava__(Double.class);
		} 
		throw new IllegalArgumentException("pyObject is instance neither of Float, nor Double, not PyFloat");
	}

	/**
	 * Returns PyFloat instance of the value newValue.
	 * NewValue must be of the type Double, otherwise
	 * the IllegalCastException will occure.
	 */
	@Override
	public PyObject getNewValue(Object newValue) {
		if (newValue instanceof Double) return new PyFloat((Double) newValue);
		if (newValue instanceof Float)  return new PyFloat((Float) newValue);
		if (newValue instanceof Integer) return new PyFloat(new Float((Integer) newValue));
		throw new IllegalArgumentException("newValue is instance neither of Double nor Float");
	}
	
}
