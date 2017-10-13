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
public class ArrowComponent extends AbstractPrimitiveComponent{

    private Color arrowColor = new Color(150, 200, 255, 255);
    
    private float arrowSize = 0.5f;
    
    private boolean treatAsSprite = true;
    
    private String spriteCategoryName = "Navigation";
        
    public ArrowComponent(String archeTypeName) {
        super("Arrow", archeTypeName);
    }


    public Color getArrowColor() {
        return arrowColor;
    }

    public float getArrowSize() {
        return arrowSize;
    }

    public String getSpriteCategoryName() {
        return spriteCategoryName;
    }

    public boolean isTreatAsSprite() {
        return treatAsSprite;
    }

    
}
