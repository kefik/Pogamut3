/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cuni.amis.pogamut.unreal.t3dgenerator.datatypes;

/**
 * An abstract class representing an unreal reference.
 * @author Martin Cerny
 */
public abstract class UnrealReference  {

    public abstract String getClassName();

    public abstract String getReferenceTarget();


    public String toReferenceString() {
        return getClassName() + "'" + getReferenceTarget() + "'";
    }

    @Override
    public String toString() {
        return toReferenceString();
    }    
}
