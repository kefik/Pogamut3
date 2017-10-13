/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cuni.amis.nb.pogamut.unreal.timeline.records;

import java.awt.Color;
import java.util.Random;

/**
 * Get nice colors used for different objects.
 *
 * @author Honza
 */
public class ColorManager {
    
    /**
     * Create new beautiful color.
     */
    public Color getNewColor() {
        Color newColor = Color.getHSBColor(getRandom(), 0.94f, 0.78f);
        return newColor;
    }

    private Random random = null;

    private float getRandom() {
        if (random == null) {
            random = new Random(0xdead);
        }
        random.nextFloat();
        return random.nextFloat();
    }
}
