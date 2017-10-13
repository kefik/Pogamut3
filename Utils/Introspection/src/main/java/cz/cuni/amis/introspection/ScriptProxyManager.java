/*
 * ScriptProxyManager.java
 *
 * Created on 27. duben 2007, 16:18
 *
 */

package cz.cuni.amis.introspection;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ServiceLoader;
import javax.script.ScriptEngine;

/**
 * SPI manager for getting ScriptProxies for ScriptEngines.
 * @author ik
 */
public class ScriptProxyManager  {
    static {
        // register introspectable proxy for jython script engine
        //ScriptProxyManager.getInstance().registerProxyForEngine(PythonEngineScriptProxy.class, com.sun.script.jython.JythonScriptEngine.class);
    }
    
    /**
     * This class cannot be instanciated.
     */
    protected ScriptProxyManager() {
    }
    
    static ScriptProxyManager instance = null;
    
    public static ScriptProxyManager getInstance() {
        if(instance == null)
            instance = new ScriptProxyManager();
        return instance;
    }

    /** Get proxy for given engine. Returns EmptyProxy if no proxy for this scripting engine is available. */
    public Folder getProxyForScriptEngine(ScriptEngine scriptEngine) {
        ServiceLoader loader = ServiceLoader.load(ScriptProxyFactory.class);
        Iterator it = loader.iterator();
        while (it.hasNext()) {
            ScriptProxyFactory factory = (ScriptProxyFactory)it.next();
            Folder proxy = factory.createProxy(scriptEngine);
            if(proxy != null) return proxy;
        }
        // no SPI provides proxy for this engine type, return EmptyProxy.
        return new EmptyFolder();
    }
    
    /**
     * Introspection proxy doing nothing.
     */
    public static class EmptyFolder extends Folder {
        public Folder[] getFolders() {
            return new Folder[]{};
        }
        
        public Property[] getProperties() {
            return new Property[]{};
        }
        
        public EmptyFolder() {
            super("No introspection available");
        }

	   
        
    }
}
