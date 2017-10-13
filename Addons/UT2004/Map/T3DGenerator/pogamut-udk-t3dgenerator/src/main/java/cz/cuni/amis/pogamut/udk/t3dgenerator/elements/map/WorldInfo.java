/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cuni.amis.pogamut.udk.t3dgenerator.elements.map;

import cz.cuni.amis.pogamut.unreal.t3dgenerator.annotations.UnrealBean;
import cz.cuni.amis.pogamut.udk.t3dgenerator.elements.AbstractActor;

/**
 * Currently does not work well with importing into UDK editor Beta 2011-05.
 * If you find out, what is the issue, you will be more than welcome.
 * @author Martin Cerny
 * @see <a href="http://wiki.beyondunreal.com/UE3:WorldInfo_(UDK)">http://wiki.beyondunreal.com/UE3:WorldInfo_(UDK)</a>
 */
@UnrealBean("WorldInfo")
public class WorldInfo extends AbstractActor {

    private Float killZ = null;

    private Boolean mapNeedsLightingFullyRebuilt = true; //by default generated map needs lighting rebuilt
    

    public WorldInfo() {
        super("WorldInfo", "Engine.Default__WorldInfo");
    }

    public Float getKillZ() {
        return killZ;
    }

    public void setKillZ(Float killZ) {
        this.killZ = killZ;
    }

    public Boolean getMapNeedsLightingFullyRebuilt() {
        return mapNeedsLightingFullyRebuilt;
    }



}
