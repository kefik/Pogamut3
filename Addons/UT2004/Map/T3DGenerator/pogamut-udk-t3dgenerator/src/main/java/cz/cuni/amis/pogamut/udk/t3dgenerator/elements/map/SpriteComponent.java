/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cuni.amis.pogamut.udk.t3dgenerator.elements.map;

import cz.cuni.amis.pogamut.unreal.t3dgenerator.datatypes.StaticReference;
import cz.cuni.amis.pogamut.unreal.t3dgenerator.datatypes.UnrealReference;

/**
 * A sprite component of an object. This object is seen only in the
 * editor, but it is important to generate it, otherwise, the generated map
 * would be hard to modify in the editor.
 * @author Martin Cerny
 */
public class SpriteComponent extends AbstractPrimitiveComponent {

    private UnrealReference sprite;

    private String spriteCategoryName;

    public SpriteComponent(String archetypeName, String spriteName, String spriteCategoryName) {
        this(archetypeName, spriteName, spriteCategoryName, "Sprite");
    }

    public SpriteComponent(String archetypeName, String spriteName, String spriteCategoryName, String componentName) {
        super(componentName, archetypeName, "SpriteComponent");
        setSpriteName(spriteName);
        this.spriteCategoryName = spriteCategoryName;
        setHiddenGame(true);
        setAlwaysLoadOnClient(Boolean.FALSE);
        setAlwaysLoadOnServer(Boolean.FALSE);
    }

    public UnrealReference getSprite() {
        return sprite;
    }

    public String getSpriteCategoryName() {
        return spriteCategoryName;
    }

    public void setSprite(UnrealReference sprite) {
        this.sprite = sprite;
    }

    public void setSpriteCategoryName(String spriteCategoryName) {
        this.spriteCategoryName = spriteCategoryName;
    }


    public final void setSpriteName(String spriteName){
        setSprite(new StaticReference("Texture2D", spriteName));
    }
    
}
