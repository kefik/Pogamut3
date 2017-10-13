/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cuni.amis.pogamut.udk.t3dgenerator.elements.kismet;

/**
 *
 * @author Martin Cerny
 */
public class FinishSequenceAction extends AbstractKismetObject {
    public static final String IN_LINK = "In";

    private String outputLabel;
    
    public FinishSequenceAction(String archetypeName, String outputLabel){
        super("SeqAct_FinishSequence", archetypeName, new String[]{IN_LINK}, new String[]{}, new String[]{});
        this.outputLabel = outputLabel;
    }

    public FinishSequenceAction(String outputLabel){
        this("Engine.Default__SeqAct_FinishSequence",outputLabel);
    }

    public String getOutputLabel() {
        return outputLabel;
    }

    public void setOutputLabel(String outputLabel) {
        this.outputLabel = outputLabel;
    }
    
    
}
