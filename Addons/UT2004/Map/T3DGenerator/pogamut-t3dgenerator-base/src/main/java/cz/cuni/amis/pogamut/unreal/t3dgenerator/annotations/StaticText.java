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
 * Interface to annotate string fields. Their value will then be treated
 * as static text when generating T3D and simply prepended to the property string.
 * If a method is annotated with this annotation, it is invoked to return a string
 * that is prepended to the property string.
 * @author Martin Cerny
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface StaticText {
    
}
