/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cuni.amis.pogamut.unreal.communication.worldview.map;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import java.io.IOException;
import java.io.Serializable;

/**
 *
 * @author ik
 */
public interface IUnrealMapInfo extends Serializable {

    int getHeight();

    byte[] getImgRGBData();

    String getName();

    int getWidth();

    void setImage(String path) throws IOException;

    void setImagePoint(int i, Location l);

    void setName(String name);

    void setWorldPos(int i, Location l);

    Location[] getImagePoints();

    Location[] getWorldPoints();
}
