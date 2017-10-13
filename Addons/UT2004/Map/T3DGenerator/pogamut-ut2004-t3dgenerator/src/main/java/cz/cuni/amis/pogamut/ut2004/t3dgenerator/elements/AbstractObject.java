/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cuni.amis.pogamut.ut2004.t3dgenerator.elements;

import cz.cuni.amis.pogamut.unreal.t3dgenerator.annotations.UnrealBean;
import cz.cuni.amis.pogamut.unreal.t3dgenerator.elements.AbstractUnrealBean;

/**
 * Abstract predecessor for all elements of type "Object"
 * @author Martin Cerny
 */
@UnrealBean("Object")
public class AbstractObject extends AbstractUnrealBean {
    
    public AbstractObject(String className){
        super(className);        
    }
}
