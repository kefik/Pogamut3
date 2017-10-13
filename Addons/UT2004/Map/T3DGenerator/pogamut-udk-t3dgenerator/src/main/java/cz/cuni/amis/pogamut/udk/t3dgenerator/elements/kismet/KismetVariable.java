/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cuni.amis.pogamut.udk.t3dgenerator.elements.kismet;

import cz.cuni.amis.pogamut.unreal.t3dgenerator.datatypes.UnrealReference;


/**
 *
 * @author Martin Cerny
 */
public abstract class KismetVariable extends AbstractKismetObject {

    public KismetVariable(String className, UnrealReference archetype){
        super(className, archetype, new String[]{}, new String[]{}, new String[]{});
    }

    public KismetVariable(String className, String archetypeName){
        super(className, archetypeName, new String[]{}, new String[]{}, new String[]{});
    }
}
