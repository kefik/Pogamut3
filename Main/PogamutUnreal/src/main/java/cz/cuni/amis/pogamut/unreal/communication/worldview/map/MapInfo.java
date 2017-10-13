/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cuni.amis.pogamut.unreal.communication.worldview.map;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * Info for visualization of maps background
 * @author Honza
 */
public class MapInfo implements IUnrealMapInfo {
    private String name = "";
    public Location[] imagePoints = new Location[3];
    public Location[] worldPoints = new Location[3];

    public int width;
    public int height;

    public byte[] imgData;

    public int getHeight() {
        return height;
    }

    public String getName() {
        return name;
    }

    public int getWidth() {
        return width;
    }

    public byte[] getImgRGBData() {
        return imgData;
    }

    public void setImage(String path) throws IOException {
        BufferedImage img = null;
        img = ImageIO.read(new File(path));

        this.width = img.getWidth();
        this.height = img.getHeight();

        imgData = new byte[img.getHeight() * img.getWidth() * 3];

        int pos = 0;
        // go from bottom to top because Image has origin in top left, but texture in bottom left
        for (int row = img.getHeight() - 1; row >= 0; row--) {
            for (int col = 0; col < img.getWidth(); col++) {
                if (true) {
                    int pixel = img.getRGB(col, row);
                    imgData[pos++] = (byte) ((pixel >> 16) & 0xFF);
                    imgData[pos++] = (byte) ((pixel >> 8) & 0xFF);
                    imgData[pos++] = (byte) ((pixel >> 0) & 0xFF);

/*                    buffer.put((byte) ((pixel >> 16) & 0xFF));
                    buffer.put((byte) ((pixel >> 8) & 0xFF));
                    buffer.put((byte) ((pixel >> 0) & 0xFF));
  */              }
            }
        }
    }

    public void setImagePoint(int i, Location l) {
        this.imagePoints[i] = new Location(l);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setWorldPos(int i, Location l) {
        this.worldPoints[i] = new Location(l);
    }

    public Location[] getImagePoints() {
        return imagePoints;
    }

    public Location[] getWorldPoints() {
        return worldPoints;
    }


}
