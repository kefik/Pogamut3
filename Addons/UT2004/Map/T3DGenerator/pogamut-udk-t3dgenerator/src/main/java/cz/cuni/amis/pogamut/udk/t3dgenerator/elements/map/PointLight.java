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
 *
 * @author Martin Cerny
 * @see <a href="http://wiki.beyondunreal.com/UE3:DrawLightRadiusComponent_%28UDK%29">http://wiki.beyondunreal.com/UE3:DrawLightRadiusComponent_%28UDK%29</a>
 */
public class PointLight extends AbstractActor{

    public static final String CLASSNAME = "PointLight";

    @UnrealComponent
    @UnrealProperty
    private PointLightComponent lightComponent;
     
    @UnrealComponent
    private SpriteComponent spriteComponent;
    
    @UnrealComponent
    private DrawLightRadiusComponent drawLightSourceRadiusComponent;
    
    @UnrealComponent
    private DrawLightRadiusComponent drawLightRadiusComponent;
    
    public PointLight(Vector3D location){
        this(location, 32,1024);
    }
    
    public PointLight(Vector3D location, float sourceRadius, float lightRadius) {
        super(CLASSNAME);
        this.lightComponent = new PointLightComponent("PointLightComponent0", sourceRadius);
        this.spriteComponent = new SpriteComponent("Engine.Default__PointLight:Sprite", "EditorResources.LightIcons.Light_Point_Stationary_Statics", "Lighting", "Sprite");
        this.drawLightRadiusComponent = new DrawLightRadiusComponent("DrawLightRadius0", "Engine.Default__PointLight:DrawLightRadius0", lightRadius);
        this.drawLightSourceRadiusComponent = new DrawLightRadiusComponent("DrawLightSourceRadius0", "Engine.Default__PointLight:DrawLightSourceRadius0", sourceRadius);
        setLocation(location);        
    }

    public PointLightComponent getLightComponent() {
        return lightComponent;
    }

    public void setLightComponent(PointLightComponent lightComponent) {
        this.lightComponent = lightComponent;
    }

    public SpriteComponent getSpriteComponent() {
        return spriteComponent;
    }

    public void setSpriteComponent(SpriteComponent spriteComponent) {
        this.spriteComponent = spriteComponent;
    }

    public DrawLightRadiusComponent getDrawLightRadiusComponent() {
        return drawLightRadiusComponent;
    }

    public void setDrawLightRadiusComponent(DrawLightRadiusComponent drawLightRadiusComponent) {
        this.drawLightRadiusComponent = drawLightRadiusComponent;
    }

    public DrawLightRadiusComponent getDrawLightSourceRadiusComponent() {
        return drawLightSourceRadiusComponent;
    }

    public void setDrawLightSourceRadiusComponent(DrawLightRadiusComponent drawLightSourceRadiusComponent) {
        this.drawLightSourceRadiusComponent = drawLightSourceRadiusComponent;
    }
    
    
    
    
}
