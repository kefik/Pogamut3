/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cuni.amis.pogamut.udk.t3dgenerator.elements.map;

import cz.cuni.amis.pogamut.unreal.t3dgenerator.annotations.UnrealBean;
import cz.cuni.amis.pogamut.unreal.t3dgenerator.annotations.UnrealChildCollection;
import cz.cuni.amis.pogamut.unreal.t3dgenerator.annotations.UnrealHeaderField;
import cz.cuni.amis.pogamut.udk.t3dgenerator.elements.AbstractActor;
import java.util.ArrayList;
import java.util.List;

/**
 * A level element in T3D - wraps actors.
 * @author Martin Cerny
 */
@UnrealBean("Level")
public class Level {

    /**
     * For some reason Level uses NAME (in uppercase) for identification. This ensures it does.
     */
    @UnrealHeaderField("NAME")
    private final String persistentLevel = "PersistentLevel";

    @UnrealChildCollection
    List<AbstractActor> actors;
    
    public Level(List<? extends AbstractActor> actors) {
        this.actors = new ArrayList<AbstractActor>(actors);        
    }

    public String getPersistentLevel() {
        return persistentLevel;
    }
    
    

    public List<AbstractActor> getActors() {
        return actors;
    }

    


}
