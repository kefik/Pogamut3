/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cuni.amis.pogamut.unreal.t3dgenerator.datatypes;

import cz.cuni.amis.pogamut.unreal.t3dgenerator.annotations.UnrealDataType;

/**
 *
 * @author Martin Cerny
 * @see <a href="http://wiki.beyondunreal.com/UE3:EngineTypes_structs_%28UDK%29#LightmassLightSettings">http://wiki.beyondunreal.com/UE3:EngineTypes_structs_%28UDK%29#LightmassLightSettings</a>
 */
@UnrealDataType
public class LightmassLightSettings {
        Float indirectLightingScale;
        Float indirectLightingSaturation; 
        Float shadowExponent;

    public LightmassLightSettings(Float indirectLightingScale, Float indirectLightingSaturation, Float shadowExponent) {
        this.indirectLightingScale = indirectLightingScale;
        this.indirectLightingSaturation = indirectLightingSaturation;
        this.shadowExponent = shadowExponent;
    }

    public Float getIndirectLightingSaturation() {
        return indirectLightingSaturation;
    }

    public Float getIndirectLightingScale() {
        return indirectLightingScale;
    }

    public Float getShadowExponent() {
        return shadowExponent;
    }
        
        
}
