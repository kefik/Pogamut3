/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cuni.amis.pogamut.udk.t3dgenerator.elements.kismet;

import cz.cuni.amis.pogamut.unreal.t3dgenerator.datatypes.DynamicReference;
import cz.cuni.amis.pogamut.udk.t3dgenerator.datatypes.KismetVariableLink;
import cz.cuni.amis.pogamut.unreal.t3dgenerator.datatypes.StaticReference;

/**
 *
 * @author Martin Cerny
 */
public class MatineeMover extends Matinee{
    public static final String MOVER_LINK = "Mover";

    public MatineeMover(MatineeData data, ObjectVariable mover) {
        super(data);
        init(mover);
    }

    public MatineeMover(String archetypeName, MatineeData data, ObjectVariable mover) {
        super(archetypeName, data);
        init(mover);
    }

    private void init(ObjectVariable mover){
        addVariableLink(MOVER_LINK);
        KismetVariableLink moverLink = getVariableLink(MOVER_LINK);
        moverLink.setExpectedType(new StaticReference("Class", "Engine.SeqVar_Object"));
        moverLink.setDescription(MOVER_LINK);
        moverLink.addLinkedVariable(new DynamicReference(mover));
    }

}
