/*
 * PythonProxyFactory.java
 *
 * Created on 30. duben 2007, 12:03
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package cz.cuni.amis.introspection.python;

import cz.cuni.amis.introspection.ScriptFolder;
import cz.cuni.amis.introspection.ScriptProxyFactory;
import javax.script.ScriptEngine;

/**
 * Factory object that is registered through SPI standard and creates 
 * PythonEngineScriptProxy if it is asked to create proxy for 
 * com.sun.script.jython.JythonScriptEngine engine. 
 * @author ik
 */
public class PythonProxyFactory extends ScriptProxyFactory {
    
    /** Creates a new instance of PythonProxyFactory */
    public PythonProxyFactory() {
    }
    
    public ScriptFolder createProxy(ScriptEngine engine) {
             if(engine.getClass() == com.sun.script.jython.JythonScriptEngine.class) return new PythonEngineFolder(engine);
       
        return null;       // engine isn't supported
    }
}
