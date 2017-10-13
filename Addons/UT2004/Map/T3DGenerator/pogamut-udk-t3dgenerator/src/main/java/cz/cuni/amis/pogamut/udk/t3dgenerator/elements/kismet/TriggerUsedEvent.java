/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cuni.amis.pogamut.udk.t3dgenerator.elements.kismet;

import cz.cuni.amis.pogamut.unreal.t3dgenerator.datatypes.DynamicReference;
import cz.cuni.amis.pogamut.unreal.t3dgenerator.datatypes.UnrealReference;
import cz.cuni.amis.pogamut.udk.t3dgenerator.elements.AbstractBean;

/**
 *
 * @author Martin Cerny
 */
public class TriggerUsedEvent extends KismetEvent{
    public static final String DISTANCE_LINK = "Distance";
    public static final String UNUSED_LINK = "Unused";
    public static final String USED_LINK = "Used";

     private UnrealReference originator;

    private Boolean aimToInteract = false;

    private Float interactDistance;

    private String interactText;

    public TriggerUsedEvent(String archetypeName, UnrealReference originator) {
        super("SeqEvent_Used", archetypeName, new String[] { USED_LINK, UNUSED_LINK}, new String[] {DISTANCE_LINK});
        this.originator = originator;
    }

    public TriggerUsedEvent(UnrealReference originator) {
        this("SeqEvent_Used'Engine.Default__SeqEvent_Used'", originator);
    }

    public TriggerUsedEvent(AbstractBean originator){
        this(new DynamicReference(originator));
    }

    public Boolean getAimToInteract() {
        return aimToInteract;
    }

    public void setAimToInteract(Boolean aimToInteract) {
        this.aimToInteract = aimToInteract;
    }

    public Float getInteractDistance() {
        return interactDistance;
    }

    public void setInteractDistance(Float interactDistance) {
        this.interactDistance = interactDistance;
    }

    public String getInteractText() {
        return interactText;
    }

    public void setInteractText(String interactText) {
        this.interactText = interactText;
    }

    public UnrealReference getOriginator() {
        return originator;
    }

    public void setOriginator(UnrealReference originator) {
        this.originator = originator;
    }


}
