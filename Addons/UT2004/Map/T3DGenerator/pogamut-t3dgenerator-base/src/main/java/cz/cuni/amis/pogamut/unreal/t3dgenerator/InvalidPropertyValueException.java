/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cuni.amis.pogamut.unreal.t3dgenerator;

/**
 * Thrown when a value of property of serialized object is not valid for
 * serialization.
 * @author Martin Cerny
 */
public class InvalidPropertyValueException extends T3dGeneratorException{

    public InvalidPropertyValueException() {
    }

    public InvalidPropertyValueException(String message) {
        super(message);
    }

    public InvalidPropertyValueException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidPropertyValueException(Throwable cause) {
        super(cause);
    }
    
}
