package cz.cuni.amis.introspection.python;

import java.util.ArrayList;

import org.python.core.Py;
import org.python.core.PyDictionary;
import org.python.core.PyFloat;
import org.python.core.PyInstance;
import org.python.core.PyList;
import org.python.core.PyObject;
import org.python.core.PyStringMap;


public class PyInstanceWrapper extends PyObjectWrapper {
	
	public PyInstanceWrapper() {
		super(Object.class);
	}

	@Override
	public ArrayList<PyObjectAdapter> getChildren(Object object) {
		if (!(object instanceof PyInstance))
			throw new IllegalArgumentException("object is not instance of PyInstance");
		PyInstance pyInstance = (PyInstance)object;
		PyList keys = null;
		if (pyInstance.__dict__ instanceof PyDictionary)
			keys = ((PyDictionary)pyInstance.__dict__).keys();
		if (pyInstance.__dict__ instanceof PyStringMap)
			keys = ((PyStringMap)pyInstance.__dict__).keys();
		if (keys == null)
			return null;		
		final PyObject pyDict = pyInstance.__dict__;
		int count = keys.__len__();
		ArrayList<PyObjectAdapter> list = new ArrayList<PyObjectAdapter>(count);
		PyObject obj;		
		for (int i = 0; i < count; ++i){
			final PyObject key = keys.__getitem__(i);
			Object name = (String)key.__tojava__(String.class);
			if ((name == Py.NoConversion) || (!(name instanceof String))) continue;
			obj = pyDict.__finditem__(key);
			list.add(
				new PyObjectAdapter(
						(String)name,
						new PyObjectPlace(){
							private PyObject myPlace = key;
							@Override
							public void set(PyObject newValue) {
								pyDict.__setitem__(myPlace, newValue);
							}
							@Override
							public PyObject get(){
								try{
									return pyDict.__finditem__(myPlace);
								} catch (Exception e){
									return null;
								}
							}
						}
				)
			);
		}
		return list;
	}

	/**
	 * Unsupported! Returns null.
	 */
	@Override
	public Object getJavaObject(Object pyObject) {
		return null;
	}

	/**
	 * Unsupported! Returns null.
	 */
	@Override
	public PyObject getNewValue(Object newValue) {
		return null;
	}

	@Override
	public boolean hasChildren(Object pyObject) {
		PyInstance pyInstance = (PyInstance)pyObject;
		return pyInstance.__dict__ instanceof PyDictionary ||
		       pyInstance.__dict__ instanceof PyStringMap;
	}	

}
