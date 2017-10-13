/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cuni.amis.pogamut.udk.t3dgenerator;

import cz.cuni.amis.pogamut.udk.t3dgenerator.elements.AbstractActor;
import cz.cuni.amis.pogamut.udk.t3dgenerator.elements.map.Level;
import cz.cuni.amis.pogamut.udk.t3dgenerator.elements.map.MapElement;
import cz.cuni.amis.pogamut.udk.t3dgenerator.elements.map.MapPackage;
import cz.cuni.amis.pogamut.udk.t3dgenerator.elements.map.Surface;
import cz.cuni.amis.pogamut.udk.t3dgenerator.elements.map.TopLevelPackage;
import java.util.List;

/**
 * Helper utils  for creating T3D.
 * @author Martin Cerny
 */
public class T3dElementHelper {

    /**
     * Wraps a list of actors into a map, so that it can be seamlessly imported into UDK editor.
     * @param mapName
     * @param actors
     * @return 
     */
    public static MapElement wrapActorsIntoMap(String mapName,  List<? extends AbstractActor> actors){
        return wrapActorsIntoMap(mapName, actors, null);
    }

    /**
     * 
     * @param mapName
     * @param actors
     * @param killZ currently ignored, since there are some trouble with importing the WorldInfo object
     * @return 
     */ 
    public static MapElement wrapActorsIntoMap(String mapName,  List<? extends AbstractActor> actors, Float killZ){
        return new MapElement(mapName, new MapPackage(new TopLevelPackage(mapName)), new Level(actors), new Surface());
    }
}
