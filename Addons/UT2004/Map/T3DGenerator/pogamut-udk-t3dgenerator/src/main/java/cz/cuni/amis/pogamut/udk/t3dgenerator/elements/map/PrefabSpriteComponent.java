/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cuni.amis.pogamut.udk.t3dgenerator.elements.map;

import cz.cuni.amis.pogamut.unreal.t3dgenerator.annotations.StaticText;
import cz.cuni.amis.pogamut.udk.t3dgenerator.elements.map.SpriteComponent;

/**
 *
 * @author Martin Cerny
 */
public class PrefabSpriteComponent extends SpriteComponent {

    @StaticText
    private final String text = "\tbIsScreenSizeScaled=True\n" +
            "\tScreenSize=0.002500\n";
    
    public PrefabSpriteComponent() {
        super("Engine.Default__PrefabInstance:Sprite","EditorResources.PrefabSprite" , "Prefabs");
    }

    public String getText() {
        return text;
    }
    
    

}
