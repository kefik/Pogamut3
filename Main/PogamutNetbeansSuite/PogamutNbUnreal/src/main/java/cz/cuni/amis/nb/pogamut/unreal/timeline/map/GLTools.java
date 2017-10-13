/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cuni.amis.nb.pogamut.unreal.timeline.map;

import com.sun.opengl.util.GLUT;
import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureIO;
import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.nio.DoubleBuffer;
import java.nio.IntBuffer;
import javax.media.opengl.GL;
import javax.media.opengl.GLException;
import javax.media.opengl.glu.GLU;
import org.openide.util.Exceptions;

/**
 *
 * @author Honza
 */
public class GLTools {

    private static GLU glu = new GLU();
    private static GLUT glut = new GLUT();

    public static Location getWorldCoordinates(GL gl, GLU glu, Location screen, Location store) {
        if (store == null) {
            store = new Location(0,0,0);
        }

        // Modelview matrix
        DoubleBuffer mvBuffer = DoubleBuffer.allocate(16);
        gl.glGetDoublev(GL.GL_MODELVIEW_MATRIX, mvBuffer);
        // Projection_matrix
        DoubleBuffer prBuffer = DoubleBuffer.allocate(16);
        gl.glGetDoublev(GL.GL_PROJECTION_MATRIX, prBuffer);
        // Viewport matrix
        IntBuffer vpBuffer = IntBuffer.allocate(16); // 4 is necessary
        gl.glGetIntegerv(GL.GL_VIEWPORT, vpBuffer);

        // 3d coordinates
        DoubleBuffer result = DoubleBuffer.allocate(3);
        glu.gluUnProject(screen.x,
                screen.y,
                screen.z,
                mvBuffer,
                prBuffer,
                vpBuffer,
                result);

        store = new Location(result.get(0), result.get(1), result.get(2));

        return store;
    }

    public static Location getScreenCoordinates(GL gl, GLU glu, Location worldPosition, Location store) {
        if (store == null) {
            store = new Location(0,0,0);
        }

        // Modelview matrix
        DoubleBuffer mvBuffer = DoubleBuffer.allocate(16);
        gl.glGetDoublev(GL.GL_MODELVIEW_MATRIX, mvBuffer);
        // Projection_matrix
        DoubleBuffer prBuffer = DoubleBuffer.allocate(16);
        gl.glGetDoublev(GL.GL_PROJECTION_MATRIX, prBuffer);
        // Viewport matrix
        IntBuffer vpBuffer = IntBuffer.allocate(16);
        gl.glGetIntegerv(GL.GL_VIEWPORT, vpBuffer);

        DoubleBuffer result = DoubleBuffer.allocate(3);

        glu.gluProject(worldPosition.x,
                worldPosition.y,
                worldPosition.z,
                mvBuffer,
                prBuffer,
                vpBuffer,
                result);

        store = new Location(result.get(0), result.get(1), result.get(2));
        
        return store;
    }

    private static Texture texture;
    /**
     * Render window for text to be drawn into it.
     * @param x
     * @param y
     * @param width
     * @param height
     */
    public static void renderWindow(GL gl, int x, int y, int width, int height) {
        pushMatrixMode(gl);
        setOrthoViewport(gl);
        
        try {
            double z = 0;
            if (texture == null) {
                texture = TextureIO.newTexture(new File("c:/temp/windowTexture.PNG"), false);
            }
            texture.bind();
            texture.enable();

            gl.glBegin(GL.GL_QUADS);
            {
                gl.glColor3d(1,1,1);
                gl.glTexCoord2d(0, 0);
                gl.glVertex3d(x, y, z);

                gl.glTexCoord2d(1, 0);
                gl.glVertex3d(x + width, y, z);

                gl.glTexCoord2d(1, 1);
                gl.glVertex3d(x + width, y + height, z);

                gl.glTexCoord2d(0, 1);
                gl.glVertex3d(x, y + height, z);
            }
            gl.glEnd();

            texture.disable();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        } catch (GLException ex) {
            Exceptions.printStackTrace(ex);
        }
        
        popMatrixMode(gl);
    }

    /**
     * Set mode according to viewport. 0,0 is at left top
     */
    public static void setOrthoViewport(GL gl) {
        Rectangle viewport = getViewport(gl);
        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glLoadIdentity();
        gl.glOrtho(viewport.getMinX(), viewport.getMaxX(), viewport.getMinY(), viewport.getMaxY(), -1, 1);
        gl.glMatrixMode(GL.GL_MODELVIEW);
    }

    public static Rectangle getViewport(GL gl) {
        int viewport[] = new int[4];
        gl.glGetIntegerv(GL.GL_VIEWPORT, viewport, 0);
        return new Rectangle(viewport[0], viewport[1], viewport[2], viewport[3]);
    }

    public static void pushMatrixMode(GL gl) {
        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glPushMatrix();
        gl.glLoadIdentity();
        gl.glMatrixMode(GL.GL_MODELVIEW);
        gl.glPushMatrix();
        gl.glLoadIdentity();
    }

    public static void popMatrixMode(GL gl) {
        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glPopMatrix();
        gl.glMatrixMode(GL.GL_MODELVIEW);
        gl.glPopMatrix();
    }
}
