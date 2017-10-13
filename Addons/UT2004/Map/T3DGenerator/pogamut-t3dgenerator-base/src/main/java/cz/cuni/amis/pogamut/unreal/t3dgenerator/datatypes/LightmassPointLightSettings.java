/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cuni.amis.pogamut.unreal.t3dgenerator.datatypes;

import cz.cuni.amis.pogamut.unreal.t3dgenerator.annotations.UnrealDataType;

/**
 *
 * @author Martin Cerny
 * @see <a href="http://wiki.beyondunreal.com/UE3:EngineTypes_structs_%28UDK%29#LightmassPointLightSettings">http://wiki.beyondunreal.com/UE3:EngineTypes_structs_%28UDK%29#LightmassPointLightSettings</a>
 */
@UnrealDataType
public class LightmassPointLightSettings extends LightmassLightSettings{
    private Float lightSourceRadius;

    public LightmassPointLightSettings(Float lightSourceRadius, Float indirectLightingScale, Float indirectLightingSaturation, Float shadowExponent) {
        super(indirectLightingScale, indirectLightingSaturation, shadowExponent);
        this.lightSourceRadius = lightSourceRadius;
    }

    public Float getLightSourceRadius() {
        return lightSourceRadius;
    }       
    
}
