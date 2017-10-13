/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cuni.amis.pogamut.unreal.t3dgenerator;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Collections;

/**
 * A simple utility implementation of {@link IT3dGenerator}
 * @author Martin Cerny
 */
public abstract class AbstractT3dGenerator implements IT3dGenerator {

    /**
     * Simply delegates to {@link #generateT3d(java.util.List, java.io.OutputStreamWriter) }
     * @param rootElement
     * @param out
     * @throws IOException 
     */
    @Override
    public void generateT3d(Object rootElement, OutputStreamWriter out) throws IOException {
        generateT3d(Collections.singletonList(rootElement), out);
    }


}
