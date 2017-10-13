/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cuni.amis.pogamut.udk.t3dgenerator.elements.map;

import cz.cuni.amis.pogamut.unreal.t3dgenerator.annotations.UnrealComponent;
import cz.cuni.amis.pogamut.unreal.t3dgenerator.annotations.UnrealProperty;
import cz.cuni.amis.pogamut.unreal.t3dgenerator.datatypes.Vector3D;
import cz.cuni.amis.pogamut.udk.t3dgenerator.elements.AbstractActor;

/**
 * A trigger object.
 * @author Martin Cerny
 * @see <a href="http://wiki.beyondunreal.com/UE3:Trigger_%28UDK%29">http://wiki.beyondunreal.com/UE3:Trigger_%28UDK%29</a>
 */
public class Trigger extends AbstractActor{

    
    @UnrealProperty
    @UnrealComponent
    private CollisionCylinderComponent cylinderComponent;
    
    @UnrealComponent
    private SpriteComponent spriteComponent;
    
    
    public Trigger(Vector3D location, float collisionHeight, float collisionRadius) {
        this(location, new CollisionCylinderComponent("Engine.Default__Trigger:CollisionCylinder", collisionHeight, collisionRadius));
    }
        
    public Trigger(Vector3D location, CollisionCylinderComponent cylinderComponent) {
        super("Trigger");
        setLocation(location);
        this.cylinderComponent = cylinderComponent;
        this.spriteComponent = new SpriteComponent("Engine.Default__Trigger:Sprite", "EditorResources.S_Trigger", "Triggers");
        setCollisionComponent(cylinderComponent);        
    }

    public CollisionCylinderComponent getCylinderComponent() {
        return cylinderComponent;
    }

    public SpriteComponent getSpriteComponent() {
        return spriteComponent;
    }
    
    
    
    
}
