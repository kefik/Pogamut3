package cz.cuni.amis.introspection.python;

import java.util.ArrayList;

import org.python.core.PyObject;

import cz.cuni.amis.introspection.AbstractObjectFolder;
import cz.cuni.amis.introspection.Folder;
import cz.cuni.amis.introspection.Property;

public class PythonInstrospectableProxy extends AbstractObjectFolder<Object> {
	   
	private PyObjectAdapter adapter;
	
	public PythonInstrospectableProxy(String name, PyObjectPlace place){
		super(name, place.get());
		this.adapter = new PyObjectAdapter(name, place);
	}
	
	public PythonInstrospectableProxy(PyObjectAdapter adapter){
		super("none", adapter.getObject());
		this.adapter = adapter;		
	}
	
	public String getName() {
        return this.adapter.getName();
    }
	
	/**
	 * Filters children of the proxy according to 'hasChildren' parameter.
	 * 
	 * @param object
	 * @param hasChildren true = return only items with children, false = opposite
	 * @return return list of wrappers who can or can't have children
	 */
	protected ArrayList<PyObjectAdapter> filterObject(Object object, boolean hasChildren){
		ArrayList<PyObjectAdapter> children = this.adapter.getChildren();
		if (children == null) return new ArrayList<PyObjectAdapter>(0);
		ArrayList<PyObjectAdapter> list = new ArrayList<PyObjectAdapter>();
		Object obj = null;
		PyObjectWrapper wrapper = null;
		for(PyObjectAdapter adapter : children){
			obj = adapter.getObject();
			wrapper = adapter.getWrapper(obj);
			if (wrapper instanceof PyUnsupportedWrapper) continue;
			if (hasChildren){
				// we want only items whose have children
				if (wrapper.hasChildren(obj)) list.add(adapter);
			} else {
				// we want only items whose don't have children
				if (!wrapper.hasChildren(obj)) list.add(adapter);
			}
		}
		return list;
	}
	
	protected Folder[] computeFolders(Object object) {
		ArrayList<PyObjectAdapter> list = this.filterObject(object, true);		
		Folder[] proxies = new Folder[list.size()];
		for (int i = 0; i < list.size(); ++i){
			proxies[i] = new PythonInstrospectableProxy(list.get(i));
		}
		return proxies;
	}

	protected Property[] computeProperties(Object object) {
		ArrayList<PyObjectAdapter> list = this.filterObject(object, false);
		Property[] properties = new Property[list.size()];
		for (int i = 0; i < list.size(); ++i){
			properties[i] = new PythonProperty(list.get(i));
		}
		return properties;
	}

	public void closeIntrospection() {
	}

}
