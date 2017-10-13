/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cuni.amis.pogamut.udk.t3dgenerator.elements.kismet;

/**
 *
 * @author Martin Cerny
 */
public class SequenceActivatedEvent extends KismetEvent {
    public static final String OUT_LINK = "Out";

    private String inputLabel;
    
    public SequenceActivatedEvent(String inputLabel){
        this("Engine.Default__SeqEvent_SequenceActivated", inputLabel);
    }

    public SequenceActivatedEvent(String archetypeName, String inputLabel){
        super("SeqEvent_SequenceActivated", archetypeName, new String[] {OUT_LINK}, new String[]{});
        this.inputLabel = inputLabel;
    }

    public String getInputLabel() {
        return inputLabel;
    }
    
    
}
