/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cuni.amis.pogamut.unreal.t3dgenerator.datatypes;

/**
 * A reference that is initialized by string classname and name. Useful 
 * for referencing objects, that are not part of the generated map and thus
 * are not represented by any object within the running program.
 * @author Martin Cerny
 */
public class StaticReference extends UnrealReference{
    private String className;
    private String referenceTarget;

    public StaticReference(String className, String referenceTarget) {
        this.className = className;
        this.referenceTarget = referenceTarget;
    }



    @Override
    public String getClassName() {
        return className;
    }

    @Override
    public String getReferenceTarget() {
        return referenceTarget;
    }

}
