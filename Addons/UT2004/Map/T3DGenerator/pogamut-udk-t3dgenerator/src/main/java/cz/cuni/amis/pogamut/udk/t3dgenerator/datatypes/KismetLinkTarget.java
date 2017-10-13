/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cuni.amis.pogamut.udk.t3dgenerator.datatypes;

import cz.cuni.amis.pogamut.unreal.t3dgenerator.annotations.FieldName;
import cz.cuni.amis.pogamut.unreal.t3dgenerator.annotations.UnrealDataType;
import cz.cuni.amis.pogamut.unreal.t3dgenerator.datatypes.DynamicReference;
import cz.cuni.amis.pogamut.unreal.t3dgenerator.datatypes.DynamicReference;
import cz.cuni.amis.pogamut.unreal.t3dgenerator.datatypes.UnrealReference;
import cz.cuni.amis.pogamut.unreal.t3dgenerator.datatypes.UnrealReference;
import cz.cuni.amis.pogamut.udk.t3dgenerator.elements.kismet.AbstractKismetObject;

/**
 * A target of an {@link KismetOutputLink}
 * @author Martin Cerny
 */
@UnrealDataType
public class KismetLinkTarget {

    @FieldName("LinkedOp")
    UnrealReference targetObject;

    @FieldName("InputLinkIdx")
    int inputLinkIndex;


    /**
     * Creates a new target with a {@link DynamicReference} to specified object
     * @param targetObject
     * @param inputLinkName 
     */
    public KismetLinkTarget(AbstractKismetObject targetObject, String inputLinkName){
        this(new DynamicReference(targetObject), targetObject.getInputLinkIndex(inputLinkName));
    }

    public KismetLinkTarget(UnrealReference targetObject, int inputLinkIndex) {
        this.targetObject = targetObject;
        this.inputLinkIndex = inputLinkIndex;
    }

    public int getInputLinkIndex() {
        return inputLinkIndex;
    }

    public UnrealReference getTargetObject() {
        return targetObject;
    }

    

}
