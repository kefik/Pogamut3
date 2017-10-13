/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cuni.amis.pogamut.udk.t3dgenerator.elements.map;

import cz.cuni.amis.pogamut.unreal.t3dgenerator.annotations.UnrealBean;
import cz.cuni.amis.pogamut.unreal.t3dgenerator.datatypes.StaticReference;
import cz.cuni.amis.pogamut.udk.t3dgenerator.elements.AbstractBean;

/**
 * An object that must be present in a T3D for a level.
 * @author Martin Cerny
 */
@UnrealBean("TopLevelPackage")
public class TopLevelPackage extends AbstractBean{

    public TopLevelPackage(String name) {
        super("Package", new StaticReference("Package", "Core.Default__Package"), name);
    }

}
