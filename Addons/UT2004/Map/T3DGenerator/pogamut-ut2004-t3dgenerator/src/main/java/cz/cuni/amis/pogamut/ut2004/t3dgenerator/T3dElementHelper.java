/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cuni.amis.pogamut.ut2004.t3dgenerator;

import cz.cuni.amis.pogamut.unreal.t3dgenerator.datatypes.ECSGOperation;
import cz.cuni.amis.pogamut.unreal.t3dgenerator.datatypes.Vector3D;
import cz.cuni.amis.pogamut.ut2004.t3dgenerator.elements.AbstractActor;
import cz.cuni.amis.pogamut.ut2004.t3dgenerator.elements.map.BrushActor;
import cz.cuni.amis.pogamut.ut2004.t3dgenerator.elements.map.MapElement;
import cz.cuni.amis.pogamut.ut2004.t3dgenerator.elements.map.Surface;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

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
        //for some reason, the improt works wrong if do not add something to be considered builder brush at the first place
        BrushActor builderBrush = BrushActor.createCube(Vector3D.ZERO, 256, null, null);
        List<AbstractActor> actorsWithBuilderBrush = new ArrayList<AbstractActor>(actors.size());
        actorsWithBuilderBrush.add(builderBrush);
        actorsWithBuilderBrush.addAll(actors);
        return new MapElement(mapName, actorsWithBuilderBrush, new Surface());
    }
}
