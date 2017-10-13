/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cuni.amis.pogamut.unreal.t3dgenerator;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

/**
 * Interface for any T3d generator.
 * @author Martin Cerny
 */
public interface IT3dGenerator {
    public void generateT3d(Object rootElement, OutputStreamWriter out) throws IOException;
    public void generateT3d(List elements, OutputStreamWriter out) throws IOException;
}
