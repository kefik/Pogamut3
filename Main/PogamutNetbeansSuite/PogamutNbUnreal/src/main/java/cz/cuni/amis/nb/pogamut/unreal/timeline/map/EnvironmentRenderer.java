package cz.cuni.amis.nb.pogamut.unreal.timeline.map;

import com.sun.opengl.util.BufferUtil;
import com.sun.opengl.util.GLUT;
import cz.cuni.amis.nb.pogamut.unreal.map.BlendTriangle;
import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import java.awt.Point;
import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.glu.GLU;
import javax.vecmath.Vector3d;

/**
 * Renderer renders the environment of the unreal map according to passed
 * arguments in the constructor.
 * It renders the map, agents inside from specified viewpoint.
 * @author Honza
 */
public class EnvironmentRenderer implements GLEventListener {
    private static Logger logger = Logger.getLogger("EnvironmentRenderer");
    private static GLU glu = new GLU();
    private static GLUT glut = new GLUT();

    private MapViewpoint viewpoint;
    private GLRendererCollection<IRenderableUTAgent> agentRenderes;
    private MapRenderer mapRenderer;

    public EnvironmentRenderer(MapViewpoint viewpoint, GLRendererCollection<IRenderableUTAgent> agentRenderes, MapRenderer mapRenderer) {
        this.viewpoint = viewpoint;
        this.agentRenderes = agentRenderes;
        this.mapRenderer = mapRenderer;
    }

    @Override
    public synchronized void init(GLAutoDrawable glDrawable) {
        GL gl = glDrawable.getGL();

        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glLoadIdentity();
        gl.glMatrixMode(GL.GL_MODELVIEW);
        gl.glLoadIdentity();

        gl.glEnable(GL.GL_DEPTH_TEST);
        gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);

        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        gl.glClearDepth(1.0);
        gl.glShadeModel(GL.GL_SMOOTH); // try setting this to GL_FLAT and see what happens.

/*        float[] lightAmbient = new float[]{0.5f, 0.5f, 0.5f, 1.0f};
        float[] lightDiffuse = new float[]{1.0f, 1.0f, 1.0f, 1.0f};
        float[] lightPosition = new float[]{
            0, 0, 0,
            //            (float)levelBox.getFlag().getCenterX(),
            //          (float)levelBox.getFlag().getCenterY(),
            //        (float)levelBox.getFlag().getCenterZ() + (float)levelBox.getFlag().getDeltaZ(),
            1.0f};
        gl.glLightfv(GL.GL_LIGHT0, GL.GL_AMBIENT, FloatBuffer.wrap(lightAmbient));
        gl.glLightfv(GL.GL_LIGHT0, GL.GL_DIFFUSE, FloatBuffer.wrap(lightDiffuse));
        gl.glLightfv(GL.GL_LIGHT0, GL.GL_POSITION, FloatBuffer.wrap(lightPosition));
        gl.glEnable(GL.GL_LIGHT0);
  */
        gl.glDisable(GL.GL_LIGHTING);

        // Now preprocess our data for rendering
        agentRenderes.prepare(gl);
        mapRenderer.prepare(gl);
    }

    @Override
    public synchronized void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        GL gl = drawable.getGL();
        setProjection(gl, x, y, width, height);
    }

    /**
     * Set projection, viewpoinbt and so on. Used every rendered frame.
     */
    private synchronized void setProjection(GL gl, int x, int y, int width, int height) {
        if (height <= 0) { // avoid a divide by zero error!
            height = 1;
        }
        float h = (float) width / (float) height;
        gl.glViewport(0, 0, width, height);
        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glLoadIdentity();
        glu.gluPerspective(viewpoint.getViewAngle(), h, 10.0, 100000.0);
        gl.glMatrixMode(GL.GL_MODELVIEW);
        gl.glLoadIdentity();
    }

    /**
     * Clear the screen, set up correct observer position and other stuff
     * @param gl
     */
    private synchronized void prepareCanvas(GL gl) {
        // Clear the drawing area
        gl.glEnable(GL.GL_DEPTH_TEST); // GL.GL_NORMALIZE|
        gl.glClearColor(0.45f, 0.45f, 0.45f, 0f);

        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

        // Reset the modelview to the "identity"
        gl.glMatrixMode(GL.GL_MODELVIEW);
        gl.glLoadIdentity();

        // Move the "drawing cursor" around
        Location observerLoc = viewpoint.getLocation();
        Location eyeLoc = viewpoint.getEye();
        Vector3d upVec = viewpoint.getUp();
        //    gl.glTranslated(observerLocation.x, observerLocation.y, observerLocation.z);
        glu.gluLookAt(
                eyeLoc.x,
                eyeLoc.y,
                eyeLoc.z,
                observerLoc.x,
                observerLoc.y,
                observerLoc.z,
                upVec.x,
                upVec.y,
                upVec.z);

        gl.glScaled (1., 1., -1.);
    }

    int loops = 0;

    @Override
    public synchronized void display(GLAutoDrawable glDrawable) {
        GL gl = glDrawable.getGL();

        int viewport[] = new int[4];
        gl.glGetIntegerv(GL.GL_VIEWPORT, viewport, 0);

        // render using selection mode

        // Assign a buffer for selection mode values, the buffer is later used
        // to retrieve name stack. Has to be prepared before GL_SELECT mode
        int[] selectBufferArray = new int[512];
        IntBuffer selectBuffer = BufferUtil.newIntBuffer(selectBufferArray.length);

        gl.glSelectBuffer(selectBufferArray.length, selectBuffer); // size of buffer, buffer itself

        gl.glRenderMode(GL.GL_SELECT); // has to be enabled before any manipulation with selection  buffer

        // initialize name stack to empty stack
        gl.glInitNames();
        gl.glPushName(-1); // because glLoadName replaces top position on stack, we have to have something there.

        // pick only in small view volume
        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glPushMatrix();
        {
            gl.glLoadIdentity();
            // synchronized because setSelectpoint can occur and we want only one thing
            glu.gluPickMatrix(selectPoint.x, viewport[3] - selectPoint.y, 1.0f, 1.0f, viewport, 0);
            glu.gluPerspective(viewpoint.getViewAngle(), (float) viewport[2] / (float) viewport[3], 10.0, 100000.0);

            prepareCanvas(gl);
            mapRenderer.render(gl);
            agentRenderes.render(gl);
            gl.glMatrixMode(GL.GL_PROJECTION);
        }
        gl.glPopMatrix();

        gl.glMatrixMode(GL.GL_MODELVIEW);
        // now that everything was rendered I should have all objects that would
        // be in view volume in selectBuffer in following format:
        //  * [i*4+0] - number of names on the name stack when the hit occured
        //  * [i*4+1] - minimum z
        //  * [i*4+2] - maximum z
        //  * [i*4+3] - name of object (integer id)
        int numOfHits = gl.glRenderMode(GL.GL_RENDER);
        selectBuffer.get(selectBufferArray);

        // stuff it to selectedObjects
        selectedObjects = new int[numOfHits];
        for (int hitIndex = 0; hitIndex < numOfHits; hitIndex++) {
            selectedObjects[hitIndex] = selectBufferArray[hitIndex * 4 + 3];
        }

        this.selectDirtyFlag = false;

        prepareCanvas(gl);

        // Render all stuff to screen
        mapRenderer.render(gl);
        agentRenderes.render(gl);

        gl.glEnable(GL.GL_BLEND);
        gl.glEnable(GL.GL_LIGHTING);
        gl.glBegin(GL.GL_TRIANGLES);

        // render blended triangles, unsorted for now
        List<BlendTriangle> blendPolys = painterSort(mapRenderer.getBlendedTris());
        
        for (BlendTriangle triangle : blendPolys) {
            for (int i=0;i<3;i++) {
                GlColor col = triangle.getVerts()[i].getColor();
                gl.glColor4d(col.r, col.g, col.b, col.a);

                Location loc = triangle.getVerts()[i].getLocation();
                gl.glVertex3d(loc.x, loc.y, loc.z);
            }
        }
        gl.glEnd();
        gl.glDisable(GL.GL_LIGHTING);
        gl.glDisable(GL.GL_BLEND);


        // render debug text
        String res = "[" + selectPoint.x + ", " + selectPoint.y + "] # " + numOfHits;
        for (int objId : selectedObjects) {
            res += ":" + objId;
        }
        renderText(gl, res, 0, 0, GLUT.BITMAP_HELVETICA_12);

        loops++;
    }

    /**
     * Sort polygons in the list so they can be correctly rendered from back-to-front.
     * Basically preparation for painter's algorithm.
     *
     * XXX: Implement painter correctly, this is mererly placeholder, based 
     *      on max z-value of poly
     * @param polys List of polygons that are supposed to be sorted
     * @return List of triangles that can be rendered back-to-front without problems
     */
    private List<BlendTriangle> painterSort(List<BlendTriangle> orgPolys) {
        List<BlendTriangle> polys = new LinkedList<BlendTriangle>(orgPolys);
        final Location eyeLoc = this.viewpoint.getEye();

        Collections.sort(polys, new Comparator<BlendTriangle>() {
            @Override
            public int compare(BlendTriangle o1, BlendTriangle o2) {
                if (maxDistance(o1) < maxDistance(o2))
                    return -1;
                if (maxDistance(o1) > maxDistance(o2))
                    return 1;

                return 0;
            }

            /**
             * Return max euclidian distance between eyeLoc and some vertex of triangle
             */
            private double maxDistance(BlendTriangle tri) {
                double max = Double.MIN_VALUE;
                
                for (int i=0; i < 3;i++) {
                    Location vertloc = tri.getVerts()[i].getLocation();
                    if (Location.getDistance(vertloc, eyeLoc) > max) {
                        max = Location.getDistance(vertloc, eyeLoc);
                    }
                }
                return max;
            }

        });

        return polys;
    }

    private void renderText(GL gl, String text, int x, int y, int font) {
        int viewport[] = new int[4];
        gl.glGetIntegerv(GL.GL_VIEWPORT, viewport, 0);

        gl.glMatrixMode(GL.GL_MODELVIEW);
        gl.glPushMatrix();
        gl.glLoadIdentity();
        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glPushMatrix();
        gl.glLoadIdentity();
        {
            gl.glOrtho(0, viewport[2], 0, viewport[3], -1, 1);
            gl.glColor3d(1, 1, 1);
            gl.glRasterPos3d(0, 0, 0);
            glut.glutBitmapString(font, text);
        }
        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glPopMatrix();
        gl.glMatrixMode(GL.GL_MODELVIEW);
        gl.glPopMatrix();
        
    }

    @Override
    public void displayChanged(GLAutoDrawable arg0, boolean arg1, boolean arg2) {
        // do nothing
    }

    private void glErrorTest(GL gl, String text) {
        int glError = gl.glGetError();
        if (glError != GL.GL_NO_ERROR) {
            if (logger.isLoggable(Level.SEVERE)) logger.severe("GL ERROR: " + text + " - " + glu.gluErrorString(glError));
        }
    }
    // Flag if the stored selected objects are valid for select point
    private boolean selectDirtyFlag = true;
    // what point in viewport do we want objects from. In window mode, so change when want to use
    private Point selectPoint = new Point();
    // list of objects from selectPoint, if selectDirtyFlag is false
    private int[] selectedObjects = new int[0];

    public synchronized int[] getSelectedObjects() throws IllegalStateException {
        if (selectDirtyFlag) {
            throw new IllegalStateException("Not objects from selected point. Did you call display() after setSelectPoint()?");
        }

        return Arrays.copyOf(selectedObjects, selectedObjects.length);
    }

    /**
     * Set select point you want rendered objects list from.
     * @param point point we want objects from. In Window mode = left top is [0,0]
     */
    public synchronized void setSelectPoint(Point point) {
        if (point == null) {
            throw new IllegalArgumentException("Point cannot be null");
        }

        this.selectPoint = new Point(point);
        this.selectDirtyFlag = true;
    }

    SelectionHit hi = new SelectionHit(loops);

    public static class SelectionHit {
/*        public int prev = selectBufferArray[hitIndex * 4 + 0];
        public int minZ = selectBufferArray[hitIndex * 4 + 1];
        public int maxZ = selectBufferArray[hitIndex * 4 + 2];
        public int name = selectBufferArray[hitIndex * 4 + 3];
*/
        private SelectionHit(int z) {
            
        }
    }
}
