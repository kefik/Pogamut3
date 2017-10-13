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
 * Designates a field to be added as a child object and to the array property "Components" as well. The type of the field
 * must be descendant of {@link IUnrealReferencable}.
 * This field is by default not considered a property of the object. If it should be,
 * use {@link UnrealProperty} annotation to designate this.
 * @author Martin Cerny
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface UnrealComponent {
}
