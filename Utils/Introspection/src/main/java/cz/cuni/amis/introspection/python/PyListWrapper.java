package cz.cuni.amis.introspection.python;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.python.core.PyDictionary;
import org.python.core.PyList;
import org.python.core.PyObject;


public class PyListWrapper extends PyObjectWrapper {
	
	public PyListWrapper() {
		super(ArrayList.class);
	}
	
	@Override
	public ArrayList<PyObjectAdapter> getChildren(Object object) {
		if (!(object instanceof PyList))
			throw new IllegalArgumentException("object is not instance of PyList");
		final PyList pyList = (PyList) object;
		int count = (pyList.__len__());
		ArrayList<PyObjectAdapter> list = new ArrayList<PyObjectAdapter>(count);
		PyObject obj;
		for (int i = 0; i < count; ++i){
			final int place = i;
			obj = pyList.__finditem__(place);
			list.add(
				new PyObjectAdapter(
						String.valueOf(place),
						new PyObjectPlace(){
							private int myPlace = place;
							@Override
							public void set(PyObject newValue) {
								pyList.__setitem__(myPlace, newValue);
							}
							@Override
							public PyObject get(){
								try{
									return pyList.__finditem__(myPlace);
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
	public boolean hasChildren(Object pyObject) {
		return true;
	}

	@Override
	public Object getJavaObject(Object pyObject) {
		if (!(pyObject instanceof PyList)) 
			throw new IllegalArgumentException("pyObject is not instance of PyList");
		PyList pyList = (PyList) pyObject;
		int count = (pyList.__len__());
		ArrayList list = new ArrayList(count);
		PyObject obj = null;
		PyObjectWrapper wrapper = null;
		for (int i = 0; i < count; ++i){
			obj = pyList.__getitem__(i);
			wrapper = PyObjectWrappersManager.getWrapper(obj.getClass());
			list.add(wrapper.getJavaObject(obj));
		}
		return list;
	}

	/**
	 * Accepts Collection<PyObject> as parameter of newValue.
	 * Returns PyList
	 */
	@Override
	public PyObject getNewValue(Object newValue) {
		if (!(newValue instanceof Collection)){
			throw new IllegalArgumentException();
		} 
		Collection collection = (Collection)newValue;
		PyList list = new PyList();
		Iterator iter = collection.iterator();
		while (iter.hasNext()){
			Object next = iter.next();
			if (!(next instanceof PyObject))
				throw new IllegalArgumentException("value of the collection is not instance of PyObject");
			 list.__add__((PyObject) next);
		}		
		return list;
	}

}
