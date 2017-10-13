/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cuni.amis.pogamut.unreal.t3dgenerator.datatypes;

import cz.cuni.amis.pogamut.unreal.t3dgenerator.elements.IUnrealReferencableByName;

/**
 * A reference created by reading name and class of another object.
 * The name is read at the time of generation, after preprocessing the objects,
 * so automatic naming is already performed then. Any changes to the name or class of 
 * target object are thus taken into account.
 * @author Martin Cerny
 */
public class DynamicReference extends UnrealReference{

    IUnrealReferencableByName targetElement;

    public DynamicReference(IUnrealReferencableByName targetElement) {
        this.targetElement = targetElement;
    }


    @Override
    public String getClassName() {
        return targetElement.getClassName();
    }

    @Override
    public String getReferenceTarget() {
        return targetElement.getNameForReferences();
    }

}
