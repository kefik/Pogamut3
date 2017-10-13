/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cuni.amis.pogamut.udk.t3dgenerator.elements.map;

import cz.cuni.amis.pogamut.unreal.t3dgenerator.datatypes.LightmassPointLightSettings;

/**
 *
 * @author Martin Cerny
 * @see <a href="http://wiki.beyondunreal.com/UE3:PointLightComponent_(UDK)">http://wiki.beyondunreal.com/UE3:PointLightComponent_(UDK)</a>
 */
public class PointLightComponent extends AbstractPrimitiveComponent {

    public static final String CLASSNAME = "PointLightComponent";
    private LightmassPointLightSettings lightmassSettings;
    private Float falloffExponent;
    private Float minShadowFalloffRadius;
    private Float radius;
    private Float shadowFalloffExponent;
    private Float shadowRadiusMultiplier;

    public PointLightComponent(String componentName, float radius) {
        this(componentName,getDefaultArchetype(CLASSNAME), radius);
    }

    public PointLightComponent(String componentName, String archetypeName, float radius) {
        super(componentName, archetypeName, CLASSNAME);
        this.radius = radius;
    }

    public Float getFalloffExponent() {
        return falloffExponent;
    }

    public LightmassPointLightSettings getLightmassSettings() {
        return lightmassSettings;
    }

    public Float getMinShadowFalloffRadius() {
        return minShadowFalloffRadius;
    }

    public Float getRadius() {
        return radius;
    }

    public Float getShadowFalloffExponent() {
        return shadowFalloffExponent;
    }

    public Float getShadowRadiusMultiplier() {
        return shadowRadiusMultiplier;
    }
    
    
}
