/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cuni.amis.pogamut.unreal.t3dgenerator.annotations;

import cz.cuni.amis.pogamut.unreal.t3dgenerator.elements.IUnrealReferencable;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Designates a field of collection type. All memebers
 * of the collection are added as children of the element. 
 * This field is by default not considered a property of the object. If it should be,
 * use {@link UnrealProperty} annotation to designate this.
 * @author Martin Cerny
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface UnrealChildCollection {
    /**
     * If set to anything but empty string, the child collection is enclosed in a
     * Begin XXX ... End XXX block with given name
     * @return 
     */
    String encloseIn() default "";
}
