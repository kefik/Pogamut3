/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cuni.amis.pogamut.unreal.t3dgenerator.elements;

import cz.cuni.amis.pogamut.unreal.t3dgenerator.datatypes.DynamicReference;

/**
 * Abstract predecessor for any object that can be referenced by {@link DynamicReference} and
 * whose name should be generated if not present.
 * @author Martin Cerny
 */
public interface IUnrealReferencableByName {
    public String getNameForReferences();
    public void setNameForReferences(String name);
    public String getClassName();
}
