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
 * A simple static mesh.
 * @author Martin Cerny
 * @see <a href="http://wiki.beyondunreal.com/UE3:StaticMeshActor_(UDK)">http://wiki.beyondunreal.com/UE3:StaticMeshActor_(UDK)</a>
 */
public class StaticMeshActor extends AbstractActor {
    /**
     * Class name in unreal
     */
    public static final String CLASSNAME = "StaticMeshActor";

    @UnrealProperty
    @UnrealComponent
    private StaticMeshComponent staticMeshComponent;

    public StaticMeshActor(String meshName, Vector3D location){
        this(new StaticMeshComponent(meshName),location);
    }
    
    public StaticMeshActor(StaticMeshComponent meshComponent, Vector3D location) {
        super(CLASSNAME);
        init(location, meshComponent);
    }

    
    public StaticMeshActor(String archetypeName, StaticMeshComponent meshComponent, Vector3D location) {
        super(CLASSNAME, archetypeName);
        init(location, meshComponent);
    }

    private void init(Vector3D location, StaticMeshComponent meshComponent) {
        setLocation(location);
        staticMeshComponent = meshComponent;
        setCollisionComponent(meshComponent);
    }

    public StaticMeshComponent getStaticMeshComponent() {
        return staticMeshComponent;
    }



}
