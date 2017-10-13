/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cuni.amis.pogamut.unreal.t3dgenerator;

/**
 * Exception raised while generating T3d.
 * @author Martin Cerny
 */
public class T3dGeneratorException extends RuntimeException{

    public T3dGeneratorException(Throwable cause) {
        super(cause);
    }

    public T3dGeneratorException(String message, Throwable cause) {
        super(message, cause);
    }

    public T3dGeneratorException(String message) {
        super(message);
    }

    public T3dGeneratorException() {
    }

}
