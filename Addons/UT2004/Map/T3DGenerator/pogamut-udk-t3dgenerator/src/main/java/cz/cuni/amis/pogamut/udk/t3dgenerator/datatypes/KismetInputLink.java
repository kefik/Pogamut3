/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cuni.amis.pogamut.udk.t3dgenerator.datatypes;

import cz.cuni.amis.pogamut.unreal.t3dgenerator.annotations.FieldName;
import cz.cuni.amis.pogamut.unreal.t3dgenerator.annotations.UnrealDataType;
import cz.cuni.amis.pogamut.unreal.t3dgenerator.datatypes.UnrealReference;
import cz.cuni.amis.pogamut.unreal.t3dgenerator.datatypes.UnrealReference;

/**
 * An input link in kismet.
 * @author Martin Cerny
 */
@UnrealDataType
public class KismetInputLink extends KismetAbstractLink {
    @FieldName("LinkedOp")
    private UnrealReference target;

    public KismetInputLink(UnrealReference target) {
        this.target = target;
    }

    public KismetInputLink(UnrealReference target,Integer drawY, Integer overrideDelta) {
        super(drawY, overrideDelta);
        this.target = target;
    }


    public UnrealReference getTarget() {
        return target;
    }

    public void setTarget(UnrealReference target) {
        this.target = target;
    }

    
}
