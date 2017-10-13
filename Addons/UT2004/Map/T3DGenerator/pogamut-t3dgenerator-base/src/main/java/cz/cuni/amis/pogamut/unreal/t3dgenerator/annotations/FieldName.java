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
 * Used to anotate fields of unreal datatypes (annotated with {@link UnrealDataType})
 * or unreal beans (annotated with {@link UnrealBean})
 * and declare
 * a different field name than the name of the java field.
 * Might be also applied on public getter methods in associtaion with {@link UnrealProperty}.
 * @author Martin Cerny
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface FieldName {
    /**
     * Field name for unreal.
     * @return 
     */
    String value();
}
