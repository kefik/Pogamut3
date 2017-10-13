package cz.cuni.amis.pogamut.ut2004.communication.messages.custom;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.ControlMessage;

@Target(value={ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ControlMessageField {

	/**
	 * Can be either 1, 2 or 3. Type of the field (whether to use {@link ControlMessage#getPF1()} or {@link ControlMessage#getPI1()} etc.) 
	 * is inferred automatically via reflection on the annotated field type.
	 * @return
	 */
	public int index();
	
}
