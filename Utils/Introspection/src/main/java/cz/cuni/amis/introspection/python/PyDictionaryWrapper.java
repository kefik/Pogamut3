package cz.cuni.amis.introspection.python;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.python.core.Py;
import org.python.core.PyDictionary;
import org.python.core.PyList;
import org.python.core.PyObject;
import org.python.core.PyString;

public class PyDictionaryWrapper extends PyObjectWrapper {
	
	private PyDictionary pyDict = null;

	public PyDictionaryWrapper() {
		super(HashMap.class);
	}

	@Override
	public ArrayList<PyObjectAdapter> getChildren(Object object) {
		if (!(object instanceof PyDictionary))
			throw new IllegalArgumentException("object is not instance of PyDictionary");
		final PyDictionary pyDict = (PyDictionary)object;
		PyList pyList = pyDict.keys();
		int count = pyList.__len__();
		ArrayList<PyObjectAdapter> list = new ArrayList<PyObjectAdapter>(count);
		PyObject obj;		
		for (int i = 0; i < count; ++i){
			final PyObject pyKey = pyList.__getitem__(i);
			Object name = pyKey.__tojava__(String.class);
			if ((name == Py.NoConversion) || (!(name instanceof String))) continue;			
			obj = pyDict.__finditem__(pyKey);
			list.add(
				new PyObjectAdapter(
						(String)name,
						new PyObjectPlace(){
							private PyObject myPlace = pyKey;
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

	@Override
	public Object getJavaObject(Object pyObject) {
		if (!(pyObject instanceof PyDictionary)) 
			throw new IllegalArgumentException("pyObject is not instance of PyDictionary");
		final PyDictionary pyDict = (PyDictionary)pyObject;
		Map map = new HashMap();
		PyList keys = pyDict.keys();
		int count = keys.__len__();		
		PyObject pyKey, pyValue;
		Object javaKey, javaValue;
		for (int i = 0; i < count; ++i){
			pyKey = keys.__getitem__(i);
			javaKey = PyObjectWrappersManager.getWrapper(pyKey.getClass()).getJavaObject(pyKey);
			pyValue = pyDict.__getitem__(pyKey);
			javaValue = PyObjectWrappersManager.getWrapper(pyValue.getClass()).getJavaObject(pyValue);
			map.put(javaKey, javaValue);
		}
		return map;
	}

	/**
	 * Accepts Map<String, PyObject> as newValue.
	 */
	@Override
	public PyObject getNewValue(Object newValue) {
		if (!(newValue instanceof Map)){
			throw new IllegalArgumentException("newValue not of type Map");			
		}
		Map map = (Map)newValue;
		Set set = map.keySet();
		Iterator iter = set.iterator();
		PyDictionary dict = new PyDictionary();
		while (iter.hasNext()) {
			Object next = iter.next();
			if (!(next instanceof String)) 
				throw new IllegalArgumentException("Key of the map is not string.");
			String key = (String)next;
			if (!(map.get(key) instanceof PyObject))
				throw new IllegalArgumentException("Value of the item of the map is not PyObject.");
			dict.__setitem__(new PyString(key), (PyObject) map.get(key));
		}
		
		return dict;
	}

	@Override
	public boolean hasChildren(Object pyObject) {
		return true;
	}
	
}
