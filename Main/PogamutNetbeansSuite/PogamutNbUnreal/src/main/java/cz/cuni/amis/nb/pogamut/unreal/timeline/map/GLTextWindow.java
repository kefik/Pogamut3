/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cuni.amis.nb.pogamut.unreal.timeline.map;

import com.sun.opengl.util.j2d.TextRenderer;
import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureIO;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.media.opengl.GL;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;

/**
 * Window for text
 * @author Honza
 */
public class GLTextWindow {

    private GL gl;
    private double borderWidth = 8;
    private Texture topLeftCornerTexture;
    private Texture topLineTexture;
    private Texture insideTexture;
    private Rectangle rect;
    private String text = "";

    private TextRenderer textRenderer = new TextRenderer(new Font("SansSerif", Font.BOLD, 36));


    public GLTextWindow(GL gl, int x, int y, int width, int height) {
        try {
            this.gl = gl;
            rect = new Rectangle(x, y, width, height);
            loadWindowTextures(gl);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public GLTextWindow(GL gl, int x, int y, int width, int height, String text) {
        this(gl, x, y, width, height);
        this.text = text;
    }

    public synchronized void setText(String text) {
        this.text = text;
    }

    /**
     * Read textures used for rendering of the window
     */
    private void loadWindowTextures(GL gl) throws IOException {
        topLeftCornerTexture = TextureIO.newTexture(getBufferedImage("cz/cuni/amis/nb/pogamut/ut2004/timeline/map/windowTL.png", BufferedImage.TYPE_4BYTE_ABGR), true);
        topLineTexture = TextureIO.newTexture(getBufferedImage("cz/cuni/amis/nb/pogamut/ut2004/timeline/map/windowTop.png", BufferedImage.TYPE_4BYTE_ABGR), true);
        insideTexture = TextureIO.newTexture(getBufferedImage("cz/cuni/amis/nb/pogamut/ut2004/timeline/map/windowInside.png", BufferedImage.TYPE_4BYTE_ABGR), true);

    }

    /**
     * Convert image to buffered image.
     * @param image source image
     * @param type type of buffered image (like TYPE_4BYTE_ABGR)
     * @return
     */
    @SuppressWarnings("empty-statement")
    private BufferedImage getBufferedImage(String imageJarPath, int type) {
        Image image = ImageUtilities.loadImage(imageJarPath);
        BufferedImage bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null), type);
        Graphics2D g = bufferedImage.createGraphics();
        g.drawImage(image, null, null);
        return bufferedImage;
    }

    public void render() {
        GLTools.pushMatrixMode(gl);
        GLTools.setOrthoViewport(gl);

        float z = 0;
        // Draw TL corner
        topLeftCornerTexture.enable();
        topLeftCornerTexture.bind();

        // Render corners
        gl.glBegin(GL.GL_QUADS);
        {
            gl.glColor4d(1, 1, 1, 1);

            // top left corner
            gl.glTexCoord2d(0, 0);
            gl.glVertex3d(rect.x - borderWidth, rect.y - borderWidth, z);

            gl.glTexCoord2d(1, 0);
            gl.glVertex3d(rect.x, rect.y - borderWidth, z);

            gl.glTexCoord2d(1, 1);
            gl.glVertex3d(rect.x, rect.y, z);

            gl.glTexCoord2d(0, 1);
            gl.glVertex3d(rect.x - borderWidth, rect.y, z);

            // top right
            gl.glTexCoord2d(1, 0);
            gl.glVertex3d(rect.x + rect.width, rect.y - borderWidth, z);

            gl.glTexCoord2d(0, 0);
            gl.glVertex3d(rect.x + rect.width + borderWidth, rect.y - borderWidth, z);

            gl.glTexCoord2d(0, 1);
            gl.glVertex3d(rect.x + rect.width + borderWidth, rect.y, z);

            gl.glTexCoord2d(1, 1);
            gl.glVertex3d(rect.x + rect.width, rect.y, z);

            // bottom right
            gl.glTexCoord2d(1, 0);
            gl.glVertex3d(rect.x + rect.width, rect.y + rect.height + borderWidth, z);

            gl.glTexCoord2d(0, 0);
            gl.glVertex3d(rect.x + rect.width + borderWidth, rect.y + rect.height + borderWidth, z);

            gl.glTexCoord2d(0, 1);
            gl.glVertex3d(rect.x + rect.width + borderWidth, rect.y + rect.height, z);

            gl.glTexCoord2d(1, 1);
            gl.glVertex3d(rect.x + rect.width, rect.y + rect.height, z);

            // bottom right
            gl.glTexCoord2d(0, 0);
            gl.glVertex3d(rect.x - borderWidth, rect.y + rect.height + borderWidth, z);

            gl.glTexCoord2d(1, 0);
            gl.glVertex3d(rect.x, rect.y + rect.height + borderWidth, z);

            gl.glTexCoord2d(1, 1);
            gl.glVertex3d(rect.x, rect.y + rect.height, z);

            gl.glTexCoord2d(0, 1);
            gl.glVertex3d(rect.x - borderWidth, rect.y + rect.height, z);

        }
        gl.glEnd();

        topLineTexture.bind();
        gl.glBegin(GL.GL_QUADS);
        {
            // Render lines
            // top line
            gl.glTexCoord2d(0, 0);
            gl.glVertex3d(rect.x, rect.y - borderWidth, z);

            gl.glTexCoord2d(1, 0);
            gl.glVertex3d(rect.x + rect.width, rect.y - borderWidth, z);

            gl.glTexCoord2d(1, 1);
            gl.glVertex3d(rect.x + rect.width, rect.y, z);

            gl.glTexCoord2d(0, 1);
            gl.glVertex3d(rect.x, rect.y, z);

            // left line
            gl.glTexCoord2d(0, 1);
            gl.glVertex3d(rect.x + rect.width, rect.y, z);

            gl.glTexCoord2d(0, 0);
            gl.glVertex3d(rect.x + rect.width + borderWidth, rect.y, z);

            gl.glTexCoord2d(1, 0);
            gl.glVertex3d(rect.x + rect.width + borderWidth, rect.y + rect.height, z);

            gl.glTexCoord2d(1, 1);
            gl.glVertex3d(rect.x + rect.width, rect.y + rect.height, z);

            // bottom line
            gl.glTexCoord2d(0, 1);
            gl.glVertex3d(rect.x, rect.y + rect.height, z);

            gl.glTexCoord2d(1, 1);
            gl.glVertex3d(rect.x + rect.width, rect.y + rect.height, z);

            gl.glTexCoord2d(1, 0);
            gl.glVertex3d(rect.x + rect.width, rect.y + rect.height + borderWidth, z);

            gl.glTexCoord2d(0, 0);
            gl.glVertex3d(rect.x, rect.y + rect.height + borderWidth, z);

            // right line
            gl.glTexCoord2d(1, 0);
            gl.glVertex3d(rect.x - borderWidth, rect.y, z);

            gl.glTexCoord2d(1, 1);
            gl.glVertex3d(rect.x, rect.y, z);

            gl.glTexCoord2d(0, 1);
            gl.glVertex3d(rect.x, rect.y + rect.height, z);

            gl.glTexCoord2d(0, 0);
            gl.glVertex3d(rect.x - borderWidth, rect.y + rect.height, z);
        }
        gl.glEnd();

        // Inside
        insideTexture.bind();
        gl.glBegin(GL.GL_QUADS);

        gl.glTexCoord2d(0, 0);
        gl.glVertex3d(rect.x, rect.y, z);

        gl.glTexCoord2d(1, 0);
        gl.glVertex3d(rect.x + rect.width, rect.y, z);

        gl.glTexCoord2d(1, 1);
        gl.glVertex3d(rect.x + rect.width, rect.y + rect.height, z);

        gl.glTexCoord2d(0, 1);
        gl.glVertex3d(rect.x, rect.y + rect.height, z);

        gl.glEnd();
        insideTexture.disable();

        // Now render text
        synchronized (this) {
            textRenderer.begin3DRendering();

            textRenderer.draw3D(text, rect.x, rect.y, z +0.5f , 1);

            textRenderer.end3DRendering();
        }

        GLTools.popMatrixMode(gl);
    }

    /**
     * When finished with the window, use this to free resources used for it
     */
    public void dispose() {
        topLeftCornerTexture.dispose();
        topLineTexture.dispose();
        insideTexture.dispose();
    }
}
