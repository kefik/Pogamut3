/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cuni.amis.pogamut.base.utils;

/**
 *
 * @author Ik
 */
public class Pogamut {

    private static PogamutPlatform platform = null;

    /**
     * 
     * @return PogamutPlatform containing Pogamut specific settings.
     */
    public synchronized static PogamutPlatform getPlatform() {
        if(platform == null) {
            // TODO extend this mechanism so that the user can supply his own 
            // implementation of platform
            platform = new DefaultPogamutPlatform();
        }
        return platform;
    }
}
