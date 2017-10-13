/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cuni.amis.pogamut.unreal.t3dgenerator.datatypes;

/**
 * An empty reference. Since null values of a property is treated
 * as "use default", this allows for setting an explicit null reference to an object.
 * @author Martin Cerny
 */
public class NullReference extends UnrealReference {

    @Override
    public String getClassName() {
        return null;
    }

    @Override
    public String getReferenceTarget() {
        return null;
    }

    @Override
    public String toReferenceString() {
        return "None";
    }



}
