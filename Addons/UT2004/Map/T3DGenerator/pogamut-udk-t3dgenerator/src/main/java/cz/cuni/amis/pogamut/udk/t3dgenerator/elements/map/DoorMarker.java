/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cuni.amis.pogamut.udk.t3dgenerator.elements.map;

import cz.cuni.amis.pogamut.unreal.t3dgenerator.datatypes.Vector3D;
import cz.cuni.amis.pogamut.unreal.t3dgenerator.datatypes.UnrealReference;

/**
 * A marker for doors does not seem to work well with UDK.
 * @author Martin Cerny
 * @see <a href="http://wiki.beyondunreal.com/UE3:DoorMarker_(UDK)">http://wiki.beyondunreal.com/UE3:DoorMarker_(UDK)</a>
 */
public class DoorMarker extends NavigationPoint{

    private UnrealReference mover;
    private UnrealReference doorTrigger;
    private Boolean blockedWhenClosed;
    private Boolean initiallyClosed;
    
    public DoorMarker(Vector3D location, UnrealReference mover) {
        super("DoorMarker", location, new CollisionCylinderComponent("Engine.Default__DoorMarker:CollisionCylinder", 50,50));
        this.mover = mover;
    }

    public UnrealReference getMover() {
        return mover;
    }

    public void setMover(UnrealReference mover) {
        this.mover = mover;
    }

    public Boolean getBlockedWhenClosed() {
        return blockedWhenClosed;
    }

    public void setBlockedWhenClosed(Boolean blockedWhenClosed) {
        this.blockedWhenClosed = blockedWhenClosed;
    }

    public Boolean getInitiallyClosed() {
        return initiallyClosed;
    }

    public void setInitiallyClosed(Boolean initiallyClosed) {
        this.initiallyClosed = initiallyClosed;
    }

    public UnrealReference getDoorTrigger() {
        return doorTrigger;
    }

    public void setDoorTrigger(UnrealReference doorTrigger) {
        this.doorTrigger = doorTrigger;
    }
    
    
    
    
}
