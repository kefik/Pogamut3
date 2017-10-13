/*
 * Introspectable.java
 *
 * Created on April 24, 2007, 2:11 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package cz.cuni.amis.introspection.java;

import cz.cuni.amis.introspection.*;

/**
 * All objects that want to provide hierarchical view of their structure to the IDE must implement this interface.
 * @author student
 */

public interface Introspectable {
    /**
     * Returns Folder representing this object.
     * @return Folder representation of this object
     */
    Folder getFolder(String name);
}
