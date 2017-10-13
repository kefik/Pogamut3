/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cuni.amis.pogamut.ut2004.t3dgenerator.elements.map;

import cz.cuni.amis.pogamut.unreal.t3dgenerator.annotations.UnrealBean;
import cz.cuni.amis.pogamut.unreal.t3dgenerator.annotations.UnrealChild;
import cz.cuni.amis.pogamut.unreal.t3dgenerator.annotations.UnrealChildCollection;
import cz.cuni.amis.pogamut.unreal.t3dgenerator.elements.AbstractUnrealBean;
import cz.cuni.amis.pogamut.ut2004.t3dgenerator.elements.AbstractActor;
import java.util.ArrayList;
import java.util.List;

/**
 * A top level element for a map in UT2004.
 * @author Martin Cerny
 */
@UnrealBean("Map")
public class MapElement extends AbstractUnrealBean {

    @UnrealChildCollection
    List<AbstractActor> actors;
    
    @UnrealChild
    private Surface surface;
    
    public MapElement(String name, List<? extends AbstractActor> actors, Surface surface) {
        setName(name);
        this.actors = new ArrayList<AbstractActor>(actors.size());
        this.actors.addAll(actors);
        this.surface = surface;
    }

    public List<AbstractActor> getActors() {
        return actors;
    }

    

    public Surface getSurface() {
        return surface;
    }

    
}
