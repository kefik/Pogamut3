package cz.cuni.amis.pogamut.ut2004.communication.messages.custom;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ControlMessageType {

	public String type();
	
}
