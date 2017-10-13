package cz.cuni.amis.introspection.python;

import java.util.ArrayList;

import org.python.core.Py;
import org.python.core.PyDictionary;
import org.python.core.PyObject;

public class PyUnsupportedWrapper extends PyObjectWrapper {
	
	public PyUnsupportedWrapper() {		
		super(null);
	}
	
	public String getDescription(PyObject unsupportedObject){
		return "unsupported "+unsupportedObject.getClass().toString(); 
	}

	@Override
	public ArrayList<PyObjectAdapter> getChildren(Object object) {
		return null;
	}
	
	@Override
	public boolean hasChildren(Object pyObject) {
		return false;
	}

	@Override
	public Object getJavaObject(Object pyObject) {
		return null;
	}

	@Override
	public PyObject getNewValue(Object newValue) {
		return Py.None;
	}

}
