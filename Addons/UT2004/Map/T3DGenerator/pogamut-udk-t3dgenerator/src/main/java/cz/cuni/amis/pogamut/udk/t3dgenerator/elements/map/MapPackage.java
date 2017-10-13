/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cuni.amis.pogamut.udk.t3dgenerator.elements.map;

import cz.cuni.amis.pogamut.unreal.t3dgenerator.annotations.UnrealBean;
import cz.cuni.amis.pogamut.unreal.t3dgenerator.annotations.UnrealChild;

/**
 * An element that wraps toplevelpackage in T3d
 * @author Martin Cerny
 */
@UnrealBean("Package")
public class MapPackage {

    @UnrealChild
    private TopLevelPackage topLevelPackage;
    
    public MapPackage(TopLevelPackage topLevelPackage) {
        this.topLevelPackage =topLevelPackage;
    }

    public TopLevelPackage getTopLevelPackage() {
        return topLevelPackage;
    }
    
    

}
