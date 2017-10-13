/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cuni.amis.pogamut.udk.t3dgenerator.datatypes;

import cz.cuni.amis.pogamut.unreal.t3dgenerator.annotations.FieldName;
import cz.cuni.amis.pogamut.unreal.t3dgenerator.annotations.UnrealDataType;

/**
 * A predecessor for all datatypes that represent kismet links.
 * @author Martin Cerny
 */
@UnrealDataType
public abstract class KismetAbstractLink  {
    Integer drawY;
    Integer overrideDelta;

    @FieldName("LinkDesc")
    String description;

    public KismetAbstractLink() {
        drawY = null;
        overrideDelta = null;
    }

    public KismetAbstractLink(Integer drawY, Integer overrideDelta) {
        this.drawY = drawY;
        this.overrideDelta = overrideDelta;
    }

    
    public String getDescription() {
        return description;
    }

    public Integer getDrawY() {
        return drawY;
    }

    public Integer getOverrideDelta() {
        return overrideDelta;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDrawY(Integer drawY) {
        this.drawY = drawY;
    }

    public void setOverrideDelta(Integer overrideDelta) {
        this.overrideDelta = overrideDelta;
    }

    

}
