/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cuni.amis.pogamut.udk.t3dgenerator.elements;

import cz.cuni.amis.pogamut.unreal.t3dgenerator.annotations.UnrealBean;
import cz.cuni.amis.pogamut.unreal.t3dgenerator.datatypes.Vector3D;
import cz.cuni.amis.pogamut.unreal.t3dgenerator.datatypes.Rotation3D;
import cz.cuni.amis.pogamut.udk.t3dgenerator.elements.map.AbstractPrimitiveComponent;

/**
 * A predecessor for all actor objects
 * @author Martin Cerny
 * @see <a href="http://wiki.beyondunreal.com/UE3:Actor_%28UDK%29">http://wiki.beyondunreal.com/UE3:Actor_%28UDK%29</a>
 */
@UnrealBean("Actor")
public class AbstractActor extends AbstractBean {

    private Vector3D location = null;
    private Rotation3D rotation = null;
    private Vector3D prePivot = null; 
            
    private AbstractPrimitiveComponent collisionComponent = null;
    private String tag = null;

    public AbstractActor(String className){
        this(className, getDefaultArchetype(className), null);
    }


    public AbstractActor(String className, String archetypeName){
        this(className,archetypeName, null);
    }

    public AbstractActor(String className, String archetypeName, String name) {
        super(className,archetypeName,name);
        tag = className;
    }

    public Vector3D getLocation() {
        return location;
    }

    public void setLocation(Vector3D location) {
        this.location = location;
    }

    public Rotation3D getRotation() {
        return rotation;
    }

    public void setRotation(Rotation3D rotation) {
        this.rotation = rotation;
    }

    public AbstractPrimitiveComponent getCollisionComponent() {
        return collisionComponent;
    }

    public void setCollisionComponent(AbstractPrimitiveComponent collisionComponent) {
        this.collisionComponent = collisionComponent;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Vector3D getPrePivot() {
        return prePivot;
    }

    public void setPrePivot(Vector3D prePivot) {
        this.prePivot = prePivot;
    }





}
