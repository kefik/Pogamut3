/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cuni.amis.introspection;

/**
 *
 * @author Ik
 */
public class IntrospectionException extends Exception {
    
    /**
     * Creates a new instance of <code>IntrospectionException</code> without detail message.
     */
    public IntrospectionException() {
    }


    /**
     * Constructs an instance of <code>IntrospectionException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public IntrospectionException(String msg) {
        super(msg);
    }
    
    public IntrospectionException(Throwable cause) {
        super(cause);
    }
}
