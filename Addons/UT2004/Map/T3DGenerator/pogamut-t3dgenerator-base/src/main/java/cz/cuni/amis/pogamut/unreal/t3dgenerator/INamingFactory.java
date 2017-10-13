/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cuni.amis.pogamut.unreal.t3dgenerator;

/**
 * An interface for class that provides automatic naming strategy for objects based on their class.
 * @author Martin Cerny
 */
public interface INamingFactory {
    /**
     * Generate a name of a new object.
     * @param objectClass
     * @return 
     */
    public String getName(String objectClass);
}
