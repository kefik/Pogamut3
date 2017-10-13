/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cuni.amis.pogamut.unreal.t3dgenerator.elements;

import cz.cuni.amis.pogamut.unreal.t3dgenerator.datatypes.UnrealReference;

/**
 * Interface for any object that can be referenced in T3D.
 * @author Martin Cerny
 */
public interface IUnrealReferencable {
    public UnrealReference getReference();
}
