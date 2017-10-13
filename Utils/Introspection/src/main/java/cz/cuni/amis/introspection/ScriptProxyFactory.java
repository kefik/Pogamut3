/*
 * ScriptProxyFactory.java
 *
 * Created on 30. duben 2007, 11:57
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package cz.cuni.amis.introspection;

import javax.script.ScriptEngine;

/**
 * Factory class for ScriptProxy objects. This class is registered through Java SPI standard.
 * @author ik
 */
public abstract class ScriptProxyFactory {
    
    /** Creates a new instance of ScriptProxyFactory */
    public ScriptProxyFactory() {
    }
    
    /**
     * Create instance of Proxy for given engine, return null if this engine isn't supported by this factory.
     */
    public abstract ScriptFolder createProxy(ScriptEngine engine);
}
