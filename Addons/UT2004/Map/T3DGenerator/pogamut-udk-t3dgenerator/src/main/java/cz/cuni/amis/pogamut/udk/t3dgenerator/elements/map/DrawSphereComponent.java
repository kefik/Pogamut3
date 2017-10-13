/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cuni.amis.pogamut.udk.t3dgenerator.elements.map;

import cz.cuni.amis.pogamut.unreal.t3dgenerator.datatypes.Color;

/**
 *
 * @author Martin Cerny
 */
public class DrawSphereComponent extends AbstractPrimitiveComponent {
    private Boolean drawLitSphere;
    private Boolean drawWireSphere;
    private Color sphereColor;
    private Float sphereRadius;
    private Integer sphereSides;
    

    public DrawSphereComponent(String componentName, String archetypeName, float sphereRadius) {
        super(componentName, archetypeName);
        this.sphereRadius = sphereRadius;
        setHiddenGame(true);
    }

    public Float getSphereRadius() {
        return sphereRadius;
    }

    public void setSphereRadius(Float sphereRadius) {
        this.sphereRadius = sphereRadius;
    }

    public Boolean getDrawLitSphere() {
        return drawLitSphere;
    }

    public void setDrawLitSphere(Boolean drawLitSphere) {
        this.drawLitSphere = drawLitSphere;
    }

    public Boolean getDrawWireSphere() {
        return drawWireSphere;
    }

    public void setDrawWireSphere(Boolean drawWireSphere) {
        this.drawWireSphere = drawWireSphere;
    }

    public Color getSphereColor() {
        return sphereColor;
    }

    public void setSphereColor(Color sphereColor) {
        this.sphereColor = sphereColor;
    }

    public Integer getSphereSides() {
        return sphereSides;
    }

    public void setSphereSides(Integer sphereSides) {
        this.sphereSides = sphereSides;
    }

    
 
    
}
