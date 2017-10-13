/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cuni.amis.pogamut.udk.t3dgenerator.elements;

import cz.cuni.amis.pogamut.unreal.t3dgenerator.annotations.UnrealBean;
import cz.cuni.amis.pogamut.unreal.t3dgenerator.datatypes.UnrealReference;

/**
 * Abstract predecessor for all elements of type "Object"
 * @author Martin Cerny
 */
@UnrealBean("Object")
public class AbstractObject extends AbstractBean {

    public AbstractObject(String className, UnrealReference archetype) {
        super(className,archetype);
    }

    public AbstractObject(String className, String archetypeName) {
        super(className,archetypeName);
    }
    
    public AbstractObject(String className){
        super(className);        
    }
}
