/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cuni.amis.pogamut.ut2004.t3dgenerator.elements;

import cz.cuni.amis.pogamut.unreal.t3dgenerator.annotations.UnrealBean;
import cz.cuni.amis.pogamut.unreal.t3dgenerator.datatypes.Vector3D;
import cz.cuni.amis.pogamut.unreal.t3dgenerator.datatypes.Rotation3D;
import cz.cuni.amis.pogamut.unreal.t3dgenerator.elements.AbstractUnrealBean;
import cz.cuni.amis.pogamut.ut2004.t3dgenerator.datatypes.ESurfaceType;

/**
 * A predecessor for all actor objects
 * @author Martin Cerny
 * @see <a href="http://wiki.beyondunreal.com/UE2:Actor_%28UT2003%29">http://wiki.beyondunreal.com/UE2:Actor_%28UT2003%29</a>
 */
@UnrealBean("Actor")
public class AbstractActor extends AbstractUnrealBean {

    private Vector3D location = null;
    private Rotation3D rotation = null;
    private String tag = null;
    
    private Boolean blockActors;
    private Boolean blockPlayers;
    private Boolean collideActors;
    private Boolean collideWorld;
    private Boolean pathColliding;
    private ESurfaceType surfaceType;
            

    public AbstractActor(String className){
        this(className, null);
    }


    public AbstractActor(String className,  String name) {
        super(className,name);
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

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Boolean getBlockActors() {
        return blockActors;
    }

    public void setBlockActors(Boolean blockActors) {
        this.blockActors = blockActors;
    }

    public Boolean getBlockPlayers() {
        return blockPlayers;
    }

    public void setBlockPlayers(Boolean blockPlayers) {
        this.blockPlayers = blockPlayers;
    }

    public Boolean getCollideActors() {
        return collideActors;
    }

    public void setCollideActors(Boolean collideActors) {
        this.collideActors = collideActors;
    }

    public Boolean getCollideWorld() {
        return collideWorld;
    }

    public void setCollideWorld(Boolean collideWorld) {
        this.collideWorld = collideWorld;
    }

    public Boolean getPathColliding() {
        return pathColliding;
    }

    public void setPathColliding(Boolean pathColliding) {
        this.pathColliding = pathColliding;
    }

    public ESurfaceType getSurfaceType() {
        return surfaceType;
    }

    public void setSurfaceType(ESurfaceType surfaceType) {
        this.surfaceType = surfaceType;
    }





}
