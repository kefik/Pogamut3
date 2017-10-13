package cz.cuni.amis.introspection.python;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.python.core.PyDictionary;
import org.python.core.PyList;
import org.python.core.PyObject;
import org.python.core.PyTuple;


public class PyTupleWrapper extends PyObjectWrapper {
	
	public PyTupleWrapper() {
		super(ArrayList.class);
	}

	@Override
	public ArrayList<PyObjectAdapter> getChildren(Object object) {
		if (!(object instanceof PyTuple))
			throw new IllegalArgumentException("object is not instance of PyTuple");
		final PyTuple pyTuple = (PyTuple) object;
		int count = (pyTuple.__len__());
		ArrayList<PyObjectAdapter> list = new ArrayList<PyObjectAdapter>(count);
		for (int i = 0; i < count; ++i){
			final int place = i;
			list.add(
				new PyObjectAdapter(
						String.valueOf(i),
						new PyObjectPlace(){
							private int myPlace = place;
							@Override
							public void set(PyObject newValue) {								
							}
							@Override
							public PyObject get(){
								try{
									return pyTuple.__finditem__(myPlace);
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
		if (!(pyObject instanceof PyTuple)) 
			throw new IllegalArgumentException("pyObject is not instance of PyTuple");
		PyTuple pyTuple = (PyTuple) pyObject;
		int count = (pyTuple.__len__());
		ArrayList list = new ArrayList(count);
		PyObject obj = null;
		PyObjectWrapper wrapper = null;
		for (int i = 0; i < count; ++i){
			obj = pyTuple.__getitem__(i);
			wrapper = PyObjectWrappersManager.getWrapper(obj.getClass());
			list.add(wrapper.getJavaObject(obj));
		}
		return list;
	}

	/**
	 * Accepts List<PyObject> as parameter of newValue.
	 * Returns PyList
	 */
	@Override
	public PyObject getNewValue(Object newValue) {
		if (!(newValue instanceof List)){
			throw new IllegalArgumentException("newValue is not instance of List");
		} 
		List list = (List)newValue;
		PyObject[] elements = new PyObject[list.size()];
		for (int i = 0; i < list.size(); ++i){
			if (!(list.get(i) instanceof PyObject))
				throw new IllegalArgumentException("value of the list is not instance of PyObject");
			elements[i] = (PyObject)list.get(i);
		}
		return new PyTuple(elements);
	}

}
