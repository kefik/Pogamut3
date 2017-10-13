package cz.cuni.amis.introspection.python;

import cz.cuni.amis.introspection.Folder;
import cz.cuni.amis.introspection.IntrospectionException;
import cz.cuni.amis.introspection.Property;
import cz.cuni.amis.introspection.ScriptFolder;
import cz.cuni.amis.introspection.ScriptProxyManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import javax.script.*;

import org.python.core.Py;
import org.python.core.PyObject;
import org.python.core.PyString;


public class PythonEngineFolder extends ScriptFolder {  
    
    private ScriptEngine engine;
    
    public PythonEngineFolder(ScriptEngine engine) {
        super("PythonEngine", engine);
        this.engine = engine;
    }
        
    /**
     * Returns adapter for variables which have or haven't children (based on parameter hasChildren)
     * 
     * @param properties whether we want properties = basic types (false) or complex types = list, dicts, classes (true)
     * @return list of adapters
     */
    private ArrayList<PyObjectAdapter> filterEngineVariables(ScriptEngine engine, boolean hasChildren){
    	ScriptContext context = engine.getContext();
        List<Integer> scopes = context.getScopes();
        int scope = scopes.get(0);
        final Bindings bindings = context.getBindings(scope);
        Set<Entry<String,Object>> entries = bindings.entrySet();
        Object value;
        PyObjectWrapper wrapper = null;
        PyObjectAdapter adapter = null;
        ArrayList<PyObjectAdapter> list = new ArrayList<PyObjectAdapter>();
        for (Entry<String, Object> entry : entries) {
            final String key = entry.getKey();
            value = entry.getValue();
            wrapper = PyObjectWrappersManager.getWrapper(value.getClass());
            if (wrapper instanceof PyUnsupportedWrapper) continue;
            if (hasChildren) {
            	// we want only types with children
            	if (!wrapper.hasChildren(value)) continue;            	
            } else {
            	// we want only basic types            	
            	if (wrapper.hasChildren(value)) continue;
            }
            
            adapter = new PyObjectAdapter(
                    key,
                    new PyObjectPlace(){
                    	private Bindings b = bindings;
                    	private String k = key;
                
		                @Override
		                public Object get() {
		                    try{
		                        return b.get(k);
		                    } catch (Exception e) {
		                        return Py.None;
		                    }
		                }
		                
		                @Override
		                public void set(PyObject newValue) {
		                    bindings.put(key, newValue);
		                }
		            }
            );
            list.add(adapter);
        }
        return list;
    }
    
    @Override
    protected Folder[] computeFolders(ScriptEngine object) {
    	ArrayList<PyObjectAdapter> adapters = this.filterEngineVariables(object, true);        
        Folder[] proxies = new Folder[adapters.size()];
        for (int i = 0; i < adapters.size(); ++i){
            proxies[i] = new PythonInstrospectableProxy(adapters.get(i));
        }
        return proxies;
    }
    
    @Override
    protected Property[] computeProperties(ScriptEngine object) {
    	ArrayList<PyObjectAdapter> adapters = this.filterEngineVariables(object, false);        
        Property[] proxies = new Property[adapters.size()];
        for (int i = 0; i < adapters.size(); ++i){
            proxies[i] = new PythonProperty(adapters.get(i));
        }
        return proxies;
    }
    
    
// ==========================
// MAIN METHOD - testing only
// ==========================
    
    public static void main(String[] args) {
    	ScriptEngine engine = (new ScriptEngineManager()).getEngineByName("python");
    	
    	try {
			engine.eval(new InputStreamReader((new FileInputStream(System.getProperty("user.dir") + "\\src\\cz\\cuni\\pogamut\\introspection\\python\\PythonEngineScriptProxyTest.py"))));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (ScriptException e) {
			e.printStackTrace();
			System.exit(1);
		}
    	
    	PythonEngineFolder intro = new PythonEngineFolder(engine);
    	
    	Folder[] ips = intro.computeFolders(engine);
    	for(Folder ip : ips){
    		System.out.println("Children - " + ip.getName());
    	}
    	
    	Property[] ps = intro.computeProperties(engine);
    	for(Property p : ps){
    		try {
				System.out.println(p.getName() + " = " + p.getValue().toString());
			} catch (IntrospectionException e) {
				e.printStackTrace();
			}
    	}
    }

    
}
