/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cuni.amis.pogamut.unreal.t3dgenerator.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Fields with this annotation are added to the object header
 * This field is by default not considered a property of the object. If it should be,
 * use {@link UnrealProperty} annotation to designate this.
 * @author Martin Cerny
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface UnrealHeaderField {

    /**
     * If set to an unempty string, overrides the default name of the property generated for this child.
     * @return 
     */
    String value() default "";         
}
