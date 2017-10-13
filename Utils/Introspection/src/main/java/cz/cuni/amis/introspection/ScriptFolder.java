/*
 * ScriptProxy.java
 *
 * Created on 27. duben 2007, 16:34
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package cz.cuni.amis.introspection;

import javax.script.ScriptEngine;

/**
 * Interface for all IntrospectableProxies of ScriptEngines
 * @author ik
 */
public abstract class ScriptFolder extends AbstractObjectFolder<ScriptEngine> {
    
    public ScriptFolder(String name, ScriptEngine engine) {
        super(name, engine);
    };   
}
