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
 * A predecessor for all navigation point-based actors
 * @author Martin Cerny
 * @see <a href="http://wiki.beyondunreal.com/UE3:NavigationPoint_%28UDK%29">http://wiki.beyondunreal.com/UE3:NavigationPoint_%28UDK%29</a>
 */
public abstract class NavigationPoint extends AbstractActor{
    
    @UnrealProperty
    @UnrealComponent
    private CollisionCylinderComponent cylinderComponent;

    @UnrealProperty
    @UnrealComponent
    private PathRenderingComponent pathRenderer;

    public NavigationPoint(String className,  Vector3D location,  CollisionCylinderComponent cylinderComponent) {
        super(className);
        this.cylinderComponent = cylinderComponent;
        this.pathRenderer = new PathRenderingComponent("Engine.Default__" + className +":PathRenderer");
        setLocation(location);
    }

    public CollisionCylinderComponent getCylinderComponent() {
        return cylinderComponent;
    }

    public void setCylinderComponent(CollisionCylinderComponent cylinderComponent) {
        this.cylinderComponent = cylinderComponent;
    }

    public PathRenderingComponent getPathRenderer() {
        return pathRenderer;
    }


    
}
