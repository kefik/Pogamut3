/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cuni.amis.pogamut.udk.t3dgenerator.elements.map;

/**
 *
 * @author Martin Cerny
 * @see <a href="http://wiki.beyondunreal.com/UE3:PathRenderingComponent_%28UDK%29">http://wiki.beyondunreal.com/UE3:PathRenderingComponent_%28UDK%29</a>
 */
public class PathRenderingComponent extends AbstractPrimitiveComponent {
    public PathRenderingComponent(String archetypeName){
        super("PathRenderer", archetypeName, "PathRenderingComponent");
    }
}
