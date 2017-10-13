/*
 * Copyright (C) 2013 AMIS research group, Faculty of Mathematics and Physics, Charles University in Prague, Czech Republic
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package cz.cuni.amis.pogamut.ut2004.t3dgenerator.elements.map;

import cz.cuni.amis.pogamut.ut2004.t3dgenerator.datatypes.ELightEffect;
import cz.cuni.amis.pogamut.ut2004.t3dgenerator.datatypes.ELightType;
import cz.cuni.amis.pogamut.ut2004.t3dgenerator.elements.AbstractActor;

/**
 *
 * @author Martin Cerny
 * @see http://wiki.beyondunreal.com/Legacy:Actor/Lighting
 */
public class Light extends AbstractActor {
    Float lightBrightness;
    Integer lightHue;
    Integer lightSaturation;
    
    Boolean actorShadows;
    Boolean attenByLife;
    Boolean corona;
    Boolean directionalCorona;
    Boolean dynamicLight ;
    Boolean lightingVisibility;
    Boolean specialLit;
    
    Integer lightCone ;
    ELightEffect lightEffect ;
    Integer lightPeriod ;
    Integer lightPhase ;
    Float lightRadius ;
    ELightType lightType;  
    
    public Light() {
        super("Light");
    }

    public Float getLightBrightness() {
        return lightBrightness;
    }

    public void setLightBrightness(Float lightBrightness) {
        this.lightBrightness = lightBrightness;
    }

    public Integer getLightHue() {
        return lightHue;
    }

    public void setLightHue(Integer lightHue) {
        this.lightHue = lightHue;
    }

    public Integer getLightSaturation() {
        return lightSaturation;
    }

    public void setLightSaturation(Integer lightSaturation) {
        this.lightSaturation = lightSaturation;
    }

    public Boolean getActorShadows() {
        return actorShadows;
    }

    public void setActorShadows(Boolean actorShadows) {
        this.actorShadows = actorShadows;
    }

    public Boolean getAttenByLife() {
        return attenByLife;
    }

    public void setAttenByLife(Boolean attenByLife) {
        this.attenByLife = attenByLife;
    }

    public Boolean getCorona() {
        return corona;
    }

    public void setCorona(Boolean corona) {
        this.corona = corona;
    }

    public Boolean getDirectionalCorona() {
        return directionalCorona;
    }

    public void setDirectionalCorona(Boolean directionalCorona) {
        this.directionalCorona = directionalCorona;
    }

    public Boolean getDynamicLight() {
        return dynamicLight;
    }

    public void setDynamicLight(Boolean dynamicLight) {
        this.dynamicLight = dynamicLight;
    }

    public Boolean getLightingVisibility() {
        return lightingVisibility;
    }

    public void setLightingVisibility(Boolean lightingVisibility) {
        this.lightingVisibility = lightingVisibility;
    }

    public Boolean getSpecialLit() {
        return specialLit;
    }

    public void setSpecialLit(Boolean specialLit) {
        this.specialLit = specialLit;
    }

    public Integer getLightCone() {
        return lightCone;
    }

    public void setLightCone(Integer lightCone) {
        this.lightCone = lightCone;
    }

    public ELightEffect getLightEffect() {
        return lightEffect;
    }

    public void setLightEffect(ELightEffect lightEffect) {
        this.lightEffect = lightEffect;
    }

    public Integer getLightPeriod() {
        return lightPeriod;
    }

    public void setLightPeriod(Integer lightPeriod) {
        this.lightPeriod = lightPeriod;
    }

    public Integer getLightPhase() {
        return lightPhase;
    }

    public void setLightPhase(Integer lightPhase) {
        this.lightPhase = lightPhase;
    }

    public Float getLightRadius() {
        return lightRadius;
    }

    public void setLightRadius(Float lightRadius) {
        this.lightRadius = lightRadius;
    }

    public ELightType getLightType() {
        return lightType;
    }

    public void setLightType(ELightType lightType) {
        this.lightType = lightType;
    }
    
    
    
}
