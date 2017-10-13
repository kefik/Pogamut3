/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cuni.amis.pogamut.udk.t3dgenerator.elements.map;

import cz.cuni.amis.pogamut.unreal.t3dgenerator.annotations.UnrealChild;
import cz.cuni.amis.pogamut.unreal.t3dgenerator.annotations.UnrealComponent;
import cz.cuni.amis.pogamut.unreal.t3dgenerator.datatypes.Vector3D;
import cz.cuni.amis.pogamut.unreal.t3dgenerator.datatypes.Rotation3D;
import cz.cuni.amis.pogamut.unreal.t3dgenerator.datatypes.StaticReference;
import cz.cuni.amis.pogamut.unreal.t3dgenerator.datatypes.UnrealReference;
import cz.cuni.amis.pogamut.udk.t3dgenerator.elements.AbstractActor;

/**
 * An instance of a Prefab.
 * The T3D generator is unable to create prefabs with it's subojbects (this seems to be a limitation in T3D) 
 * so this objects has no children.
 * @author Martin Cerny
 * @see <a href="http://wiki.beyondunreal.com/UE3:PrefabInstance_%28UDK%29">http://wiki.beyondunreal.com/UE3:PrefabInstance_%28UDK%29</a>
 * @see <a href="http://forums.epicgames.com/threads/809530-T3D-Import-Export-working-strangely-with-Prefabs-Kismet">http://forums.epicgames.com/threads/809530-T3D-Import-Export-working-strangely-with-Prefabs-Kismet</a>
 */
public class PrefabInstance extends AbstractActor {
    private UnrealReference templatePrefab;

    @UnrealComponent
    private PrefabSpriteComponent spriteComponent = new PrefabSpriteComponent();
    
    public PrefabInstance(String prefabName, Vector3D location) {
        this(prefabName,location,new Rotation3D(0, 0, 0));
    }

    public PrefabInstance(String prefabName, Vector3D location, Rotation3D rotation) {
        super("PrefabInstance", "Engine.Default__PrefabInstance");
        templatePrefab = new StaticReference("Prefab", prefabName);
        setLocation(location);
        setRotation(rotation);
    }

    public UnrealReference getTemplatePrefab() {
        return templatePrefab;
    }

    public PrefabSpriteComponent getSpriteComponent() {
        return spriteComponent;
    }



}
