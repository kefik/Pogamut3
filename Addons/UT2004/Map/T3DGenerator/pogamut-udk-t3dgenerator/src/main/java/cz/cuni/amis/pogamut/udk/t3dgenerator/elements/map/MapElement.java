/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cuni.amis.pogamut.udk.t3dgenerator.elements.map;

import cz.cuni.amis.pogamut.unreal.t3dgenerator.annotations.UnrealBean;
import cz.cuni.amis.pogamut.unreal.t3dgenerator.annotations.UnrealChild;
import cz.cuni.amis.pogamut.udk.t3dgenerator.elements.AbstractBean;

/**
 * A top level element for a map.
 * @author Martin Cerny
 */
@UnrealBean("Map")
public class MapElement extends AbstractBean {

    @UnrealChild
    private MapPackage mapPackage;
    @UnrealChild
    private Level level;
    @UnrealChild
    private Surface surface;
    
    public MapElement(String name, MapPackage mapPackage, Level level, Surface surface) {
        setName(name);
        this.mapPackage = mapPackage;
        this.level = level;
        this.surface = surface;
    }

    public Level getLevel() {
        return level;
    }

    public MapPackage getMapPackage() {
        return mapPackage;
    }

    public Surface getSurface() {
        return surface;
    }

    
}
