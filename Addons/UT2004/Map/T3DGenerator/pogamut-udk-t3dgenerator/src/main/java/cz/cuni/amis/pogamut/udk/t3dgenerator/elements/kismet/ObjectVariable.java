/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cuni.amis.pogamut.udk.t3dgenerator.elements.kismet;

import cz.cuni.amis.pogamut.unreal.t3dgenerator.annotations.FieldName;
import cz.cuni.amis.pogamut.unreal.t3dgenerator.datatypes.StaticReference;
import cz.cuni.amis.pogamut.unreal.t3dgenerator.datatypes.UnrealReference;

/**
 *
 * @author Martin Cerny
 */
public class ObjectVariable  extends KismetVariable{

    @FieldName("ObjValue")
    private UnrealReference value;
    
    public ObjectVariable(UnrealReference value){
        this("Engine.Default__SeqVar_Object", value);
    }

    public ObjectVariable(String archetypeName, UnrealReference value){
        super("SeqVar_Object",archetypeName);
        this.value = value;
    }

    public UnrealReference getValue() {
        return value;
    }

    public void setValue(UnrealReference value) {
        this.value = value;
    }
    
    

}
