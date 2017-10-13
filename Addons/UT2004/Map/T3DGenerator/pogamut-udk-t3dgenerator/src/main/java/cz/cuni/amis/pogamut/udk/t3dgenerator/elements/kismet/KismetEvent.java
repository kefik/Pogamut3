/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cuni.amis.pogamut.udk.t3dgenerator.elements.kismet;

import cz.cuni.amis.pogamut.unreal.t3dgenerator.datatypes.UnrealReference;
import org.apache.commons.lang.ArrayUtils;


/**
 *
 * @author Martin Cerny
 */
public abstract class KismetEvent extends AbstractKismetObject {
    public static final String INSTIGATOR_LINK = "Instigator";

    private static String[] addInstigator(String[] variableLinkNames){
        return (String[]) ArrayUtils.addAll(new String[]{INSTIGATOR_LINK}, variableLinkNames);
    }

    /**
     * Whether the event can be caused only by player. It is false by default,
     * since a gamebots controller is not a player.
     */
    private Boolean playerOnly = false;

    private Integer maxTriggerCount;
    private Float reTriggerDelay;
    private Boolean enabled;
    private Boolean clientSideOnly;
    private Integer priority;

    /**
     * Creates new KismetEvent, "instigator" default variable link is added as first variable link.
     * @param className
     * @param archetype
     * @param outputLinkNames
     * @param variableLinkNames
     */
    public KismetEvent(String className, UnrealReference archetype, String[] outputLinkNames, String[] variableLinkNames){
        super(className, archetype, new String[]{}, outputLinkNames, addInstigator(variableLinkNames));
    }

    /**
     * Creates new KismetEvent, "instigator" default variable link is added as first variable link.
     * @param className
     * @param archetypeName
     * @param outputLinkNames
     * @param variableLinkNames
     */
    public KismetEvent(String className, String archetypeName, String[] outputLinkNames, String[] variableLinkNames){
        super(className, archetypeName, new String[]{}, outputLinkNames, addInstigator(variableLinkNames));
    }

    public Boolean getClientSideOnly() {
        return clientSideOnly;
    }

    public void setClientSideOnly(Boolean clientSideOnly) {
        this.clientSideOnly = clientSideOnly;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Integer getMaxTriggerCount() {
        return maxTriggerCount;
    }

    public void setMaxTriggerCount(Integer maxTriggerCount) {
        this.maxTriggerCount = maxTriggerCount;
    }

    public Boolean getPlayerOnly() {
        return playerOnly;
    }

    public void setPlayerOnly(Boolean playerOnly) {
        this.playerOnly = playerOnly;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Float getReTriggerDelay() {
        return reTriggerDelay;
    }

    public void setReTriggerDelay(Float reTriggerDelay) {
        this.reTriggerDelay = reTriggerDelay;
    }

}
