/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cuni.amis.pogamut.udk.t3dgenerator.elements.map;

import cz.cuni.amis.pogamut.unreal.t3dgenerator.annotations.UnrealChild;
import cz.cuni.amis.pogamut.unreal.t3dgenerator.annotations.UnrealComponent;
import cz.cuni.amis.pogamut.unreal.t3dgenerator.datatypes.Vector3D;

/**
 *
 * @author Martin Cerny
 * @see <a href="http://wiki.beyondunreal.com/UE3:PathNode_%28UDK%29">http://wiki.beyondunreal.com/UE3:PathNode_%28UDK%29</a>
 */
public class PathNode extends NavigationPoint{
    
    @UnrealChild
    @UnrealComponent
    private SpriteComponent spriteComponent = new SpriteComponent("Engine.Default__PathNode:Sprite", "EditorResources.S_Pickup", "Navigation");
    
    @UnrealChild
    @UnrealComponent
    private ArrowComponent arrowComponent = new ArrowComponent("Engine.Default__PathNode:Arrow");

    public PathNode(Vector3D location){
        super("PathNode", location, new CollisionCylinderComponent("Engine.Default__PathNode:CollisionCylinder", 50,50));

    }

    public ArrowComponent getArrowComponent() {
        return arrowComponent;
    }

    public SpriteComponent getSpriteComponent() {
        return spriteComponent;
    }
    
    
}
