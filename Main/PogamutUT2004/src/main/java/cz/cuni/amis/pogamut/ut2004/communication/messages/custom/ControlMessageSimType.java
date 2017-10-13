package cz.cuni.amis.pogamut.ut2004.communication.messages.custom;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks "simTime" field of the {@link ICustomControlMessage}.  
 * @author Jimmy
 */
@Target(value={ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ControlMessageSimType {

}
