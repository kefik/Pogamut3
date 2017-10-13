/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cuni.amis.pogamut.base.utils;

/**
 * Thrown when the property wasn't found. This is usually exception that should 
 * terminate the program.
 * @author Ik
 */
public class PropertyNotFoundException extends RuntimeException {

    public PropertyNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
    
        public PropertyNotFoundException(String message) {
        super(message);
    }
}
