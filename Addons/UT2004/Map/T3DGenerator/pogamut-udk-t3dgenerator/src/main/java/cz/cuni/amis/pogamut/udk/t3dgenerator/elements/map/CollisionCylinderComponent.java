/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cuni.amis.pogamut.udk.t3dgenerator.elements.map;

import cz.cuni.amis.pogamut.unreal.t3dgenerator.annotations.FieldName;

/**
 *
 * @author Martin Cerny
 */
public class CollisionCylinderComponent extends AbstractPrimitiveComponent{

    private float collisionHeight;
    private float collisionRadius;

    private Boolean alwaysRenderIfSelected;

    @FieldName("CollideActors")
    private Boolean collideActors;

    public CollisionCylinderComponent(String archetypeName, float collisionHeight, float collisionRadius) {
        super("CollisionCylinder", archetypeName, "CylinderComponent");
        this.collisionHeight = collisionHeight;
        this.collisionRadius = collisionRadius;
    }

    public Boolean getAlwaysRenderIfSelected() {
        return alwaysRenderIfSelected;
    }

    public void setAlwaysRenderIfSelected(Boolean alwaysRenderIfSelected) {
        this.alwaysRenderIfSelected = alwaysRenderIfSelected;
    }

    public Boolean getCollideActors() {
        return collideActors;
    }

    public void setCollideActors(Boolean collideActors) {
        this.collideActors = collideActors;
    }

    public float getCollisionHeight() {
        return collisionHeight;
    }

    public void setCollisionHeight(float collisionHeight) {
        this.collisionHeight = collisionHeight;
    }

    public float getCollisionRadius() {
        return collisionRadius;
    }

    public void setCollisionRadius(float collisionRadius) {
        this.collisionRadius = collisionRadius;
    }

    

}
