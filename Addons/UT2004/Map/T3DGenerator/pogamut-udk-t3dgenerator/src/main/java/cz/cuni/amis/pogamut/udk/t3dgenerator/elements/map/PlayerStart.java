/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cuni.amis.pogamut.udk.t3dgenerator.elements.map;

import cz.cuni.amis.pogamut.unreal.t3dgenerator.annotations.UnrealComponent;
import cz.cuni.amis.pogamut.unreal.t3dgenerator.datatypes.Vector3D;

/**
 *
 * @author Martin Cerny
 * @see <a href="http://wiki.beyondunreal.com/UE3:PlayerStart_(UDK)">http://wiki.beyondunreal.com/UE3:PlayerStart_(UDK)</a>
 */
public class PlayerStart extends NavigationPoint {

    private Boolean enabled;
    private Boolean primaryStart;
    private Integer teamIndex;

    @UnrealComponent    
    private SpriteComponent spriteComponent;
    
    @UnrealComponent    
    private SpriteComponent spriteComponent2;

    public PlayerStart(Vector3D location) {
        super("PlayerStart", location, new CollisionCylinderComponent("CylinderComponent'Engine.Default__PlayerStart:CollisionCylinder'", 80, 40));
        spriteComponent2 = new SpriteComponent("SpriteComponent'Engine.Default__PlayerStart:Sprite'", "EditorResources.S_Player", "PlayerStart", "Sprite");
        spriteComponent = new SpriteComponent("SpriteComponent'Engine.Default__PlayerStart:Sprite'", "EditorResources.S_Player", "PlayerStart", "Sprite");
    }


    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Boolean getPrimaryStart() {
        return primaryStart;
    }

    public void setPrimaryStart(Boolean primaryStart) {
        this.primaryStart = primaryStart;
    }

    public Integer getTeamIndex() {
        return teamIndex;
    }

    public void setTeamIndex(Integer teamIndex) {
        this.teamIndex = teamIndex;
    }

    public SpriteComponent getSpriteComponent() {
        return spriteComponent;
    }

    public SpriteComponent getSpriteComponent2() {
        return spriteComponent2;
    }

    


}
