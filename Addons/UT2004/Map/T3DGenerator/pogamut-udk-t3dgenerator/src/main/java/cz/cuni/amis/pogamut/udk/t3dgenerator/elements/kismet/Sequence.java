/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cuni.amis.pogamut.udk.t3dgenerator.elements.kismet;

import cz.cuni.amis.pogamut.unreal.t3dgenerator.annotations.UnrealChildCollection;
import cz.cuni.amis.pogamut.unreal.t3dgenerator.annotations.UnrealProperty;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author Martin Cerny
 */
public class Sequence extends AbstractKismetObject{

    @UnrealProperty
    @UnrealChildCollection() 
    private List<AbstractKismetObject> sequenceObjects;
    
    public Sequence(String[] inputLinkNames, String[] outputLinkNames, String[] variableLinkNames) {
        this("Engine.Default__Sequence",inputLinkNames, outputLinkNames, variableLinkNames);
        sequenceObjects = new ArrayList<AbstractKismetObject>();
    }

    public Sequence(String archetypeName, String[] inputLinkNames, String[] outputLinkNames, String[] variableLinkNames) {
        super("Sequence", archetypeName, inputLinkNames, outputLinkNames, variableLinkNames);

        for(int i = 0; i < inputLinkNames.length; i++){
            getInputLink(inputLinkNames[i]).setDescription(inputLinkNames[i]);
        }
        for(int i = 0; i < outputLinkNames.length; i++){
            getOutputLink(outputLinkNames[i]).setDescription(outputLinkNames[i]);
        }
        for(int i = 0; i < variableLinkNames.length; i++){
            getVariableLink(variableLinkNames[i]).setDescription(variableLinkNames[i]);
        }
    }

    public void addSequenceObject(AbstractKismetObject object){
        if(object.getParentSequence() != null){
            throw new IllegalArgumentException("Object can be inserted to a sequence only once.");
        }
        object.setParentSequence(this);
        sequenceObjects.add(object);
    }
    
    public void addSequenceObjects(Collection<? extends AbstractKismetObject> objects){
        for(AbstractKismetObject obj : objects){
            addSequenceObject(obj);
        }
    }

    public List<AbstractKismetObject> getSequenceObjects() {
        return sequenceObjects;
    }
    
    


}
