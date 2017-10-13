/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cuni.amis.nb.pogamut.unreal.map;

import java.awt.Color;
import java.util.Random;

/**
 * Generator of various colors that are suitable for representation of agents
 * in the map. They have to unique, different from each other for user to
 * differentiate and nice to look at.
 *
 * FIXME: Implement
 * @author Honza
 */
public class MapColorGenerator {
    private Random random = new Random();
    
    /**
     * Create a new color according to specification (see class description)
     * @return unique pretty color
     */
    public Color getUniqueColor() {
        return new Color(random.nextFloat(), random.nextFloat(), random.nextFloat());
    }

    /**
     * This is used to tell the generator that this particular color has been freed
     * and can be used again. Generator should try to generate colors so the are
     * as different as possible, so it can use similar shade (or even same color).
     *
     * @param color Color that is no longer needed by map.
     */
    public void freeColor(Color color) {

    }

}
