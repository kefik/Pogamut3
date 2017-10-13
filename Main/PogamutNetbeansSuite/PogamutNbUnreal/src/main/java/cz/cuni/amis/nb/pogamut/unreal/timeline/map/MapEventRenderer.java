/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cuni.amis.nb.pogamut.unreal.timeline.map;

import com.sun.opengl.util.GLUT;
import cz.cuni.amis.nb.pogamut.unreal.timeline.records.MapEvent;
import cz.cuni.amis.nb.pogamut.unreal.timeline.records.TLEntity;
import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;

/**
 *
 * @author Honza
 */
class MapEventRenderer implements GLEventListener {

    private TLEntity entity;
    private MapEvent mapEvent;
    private long time;
    private Location location;
    private GLUT glut = new GLUT();
    private GLU glu = new GLU();
    public static final double SPHERE_RADIUS = 20;
    private static final int SPHERE_SLICES = 8;
    private static final int SPHERE_STACKS = 8;

    MapEventRenderer(TLEntity entity, MapEvent mapEvent, long time) {
        this.entity = entity;
        this.mapEvent = mapEvent;
        this.time = time;
        this.location = mapEvent.getLocation(time);
    }

    @Override
    public void init(GLAutoDrawable arg0) {
    }

    @Override
    public void display(GLAutoDrawable glAutoDrawable) {
       // System.out.println("MER.display " + time + " " + this.mapEvent.getMessage());

        GL gl = glAutoDrawable.getGL();

        gl.glPushMatrix();

        gl.glTranslated(location.x, location.y, location.z + 60 * 1.1);

        // display small
  //      gl.glEnable(GL.GL_BLEND);
        gl.glBlendFunc(GL.GL_SRC_ALPHA,GL.GL_ONE_MINUS_SRC_ALPHA);



        GlColor color = new GlColor(entity.getColor(), 0.5);
        gl.glColor4d(color.r, color.g, color.b, color.a);

        GLUquadric quadratic = glu.gluNewQuadric();
        glu.gluSphere(quadratic, SPHERE_RADIUS, SPHERE_SLICES, SPHERE_STACKS);

        gl.glPopMatrix();

        gl.glDisable(GL.GL_DEPTH_TEST);
        gl.glColor3d(1,1,1);
        gl.glRasterPos3d(location.x, location.y, location.z);
        glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12, this.mapEvent.getMessage());
        gl.glEnable(GL.GL_DEPTH_TEST);

//        gl.glDisable(GL.GL_BLEND);
    }

    /**
     * Not responsibility of this renderer, this is handled by MapRenderer.
     */
    @Override
    public void reshape(GLAutoDrawable arg0, int arg1, int arg2, int arg3, int arg4) {
    }

    /**
     * Nope, still no idea what to do here.
     */
    @Override
    public void displayChanged(GLAutoDrawable arg0, boolean arg1, boolean arg2) {
    }
}
