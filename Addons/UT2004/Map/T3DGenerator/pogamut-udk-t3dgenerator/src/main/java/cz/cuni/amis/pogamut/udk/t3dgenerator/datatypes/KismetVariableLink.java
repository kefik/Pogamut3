/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cuni.amis.pogamut.udk.t3dgenerator.datatypes;

import cz.cuni.amis.pogamut.unreal.t3dgenerator.annotations.FieldName;
import cz.cuni.amis.pogamut.unreal.t3dgenerator.annotations.UnrealDataType;
import cz.cuni.amis.pogamut.unreal.t3dgenerator.datatypes.UnrealReference;
import cz.cuni.amis.pogamut.unreal.t3dgenerator.datatypes.UnrealReference;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A variable link to an external variable in Kismet.
 * @author Martin Cerny
 */
@UnrealDataType
public class KismetVariableLink extends KismetAbstractLink{
    List<UnrealReference> linkedVariables;
    UnrealReference expectedType;

    /**
     * The external variable bound to this variable link. Makes sense only with sequences.
     */
    @FieldName("LinkVar")
    UnrealReference externalVariable;
    Integer minVars;

    public KismetVariableLink(List<UnrealReference> linkedVariables) {
        this.linkedVariables = linkedVariables;
    }

    public KismetVariableLink(UnrealReference singleLinkedVariable) {
        this.linkedVariables = Collections.singletonList(singleLinkedVariable);
    }

    public KismetVariableLink(List<UnrealReference> linkedVariables, Integer drawY, Integer overrideDelta) {
        this.linkedVariables = linkedVariables;
    }

    public UnrealReference getExpectedType() {
        return expectedType;
    }

    public void setExpectedType(UnrealReference expectedType) {
        this.expectedType = expectedType;
    }

    public void addLinkedVariable(UnrealReference variable){
        this.linkedVariables.add(variable);
    }

    /**
     * Get external variable
     * @return
     */
    public UnrealReference getExternalVariable() {
        return externalVariable;
    }

    public void setExternalVariable(UnrealReference linkVar) {
        this.externalVariable = linkVar;
    }

    public Integer getMinVars() {
        return minVars;
    }

    public void setMinVars(Integer minVars) {
        this.minVars = minVars;
    }

    public List<UnrealReference> getLinkedVariables() {
        return linkedVariables;
    }

}
