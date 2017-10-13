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
 * If a field is annotated with this annotation, it is
 * added as a property to the parent, event if it is annotated with {@link UnrealChild},
 * {@link UnrealChildCollection}, {@link UnrealComponent} or {@link UnrealHeaderField}.
 * When used on a public method, designates that this method should be used for property generation,
 * eventhough there is no corresponding field.
 * @author Martin Cerny
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface UnrealProperty {
    
}
