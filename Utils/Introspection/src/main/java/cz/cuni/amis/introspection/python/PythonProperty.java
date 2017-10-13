package cz.cuni.amis.introspection.python;

import cz.cuni.amis.introspection.IntrospectionException;
import org.python.core.PyObject;

import cz.cuni.amis.introspection.python.DummyClass;
import cz.cuni.amis.introspection.Property;

public class PythonProperty extends Property {
		
	private PyObjectAdapter adapter;
	
	private PyObjectWrapper lastWrapper = null;
	
	public PythonProperty(String name, PyObjectPlace place){
            super("empty");
            this.adapter = new PyObjectAdapter(name, place);
	}
	
	public PythonProperty(PyObjectAdapter adapter){
	super("empty");
            this.adapter = adapter;
	}

	public String getName() {
		return this.adapter.getName();
	}

	public Class getType() {
		Object object = adapter.getObject();
		this.lastWrapper = this.adapter.getWrapper(object);
		if (this.lastWrapper instanceof PyUnsupportedWrapper) return DummyClass.class;
		return this.lastWrapper.getJavaClass();
	}

	public Object getValue() throws IntrospectionException {
		Object object = adapter.getObject();
		this.lastWrapper = this.adapter.getWrapper(object);
		if (this.lastWrapper instanceof PyUnsupportedWrapper) return "unsupported class";
		return this.lastWrapper.getJavaObject(object);
	}

	public void setValue(Object newValue) throws IntrospectionException {
		if (this.lastWrapper == null){
			Object object = adapter.getObject();
			this.lastWrapper = this.adapter.getWrapper(object);
		}
		if (this.lastWrapper instanceof PyUnsupportedWrapper) return;
		this.adapter.set(this.lastWrapper, newValue);
	}

}
