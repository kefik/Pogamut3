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
public class ExternalVariable extends KismetVariable{
    public static final String CLASSNAME = "SeqVar_External";
    
    private UnrealReference expectedType;
    @FieldName("VariableLabel")
    private String label;
    
    public ExternalVariable(String archetypeName, String label, String expectedType){
        super(CLASSNAME, archetypeName);
        this.expectedType = new StaticReference("Class", expectedType);
        this.label = label;
    }

    public ExternalVariable(String label){
        this(getDefaultArchetype(CLASSNAME), label, "Engine.SeqVar_Object");
    }

    public UnrealReference getExpectedType() {
        return expectedType;
    }

    /**
     * Sets the reference to a static reference to class object of the specified name
     * @param expectedType 
     */
    public void setExpectedType(String expectedType) {
        this.expectedType = new StaticReference("Class", expectedType);
    }
    
    public void setExpectedType(UnrealReference expectedType) {
        this.expectedType = expectedType;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
    
    
}
