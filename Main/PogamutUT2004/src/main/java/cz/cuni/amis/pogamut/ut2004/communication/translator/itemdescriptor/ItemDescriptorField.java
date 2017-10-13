package cz.cuni.amis.pogamut.ut2004.communication.translator.itemdescriptor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This descriptor tells the that the field is subject for the reflection when building
 * the descriptor for the particular item from the ItemCategory message.
 * @author Jimmy
 */
@Target(value={ElementType.FIELD})
@Retention(value=RetentionPolicy.RUNTIME)
public @interface ItemDescriptorField {

}
