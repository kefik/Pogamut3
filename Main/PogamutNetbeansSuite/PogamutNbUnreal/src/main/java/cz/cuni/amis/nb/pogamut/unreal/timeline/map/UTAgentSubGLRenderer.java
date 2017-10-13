package cz.cuni.amis.nb.pogamut.unreal.timeline.map;

import com.sun.opengl.util.GLUT;
import cz.cuni.amis.nb.pogamut.unreal.map.BlendTriangle;
import cz.cuni.amis.nb.pogamut.unreal.services.IPogamutEnvironments;
import cz.cuni.amis.nb.pogamut.unreal.timeline.records.MapEvent;
import cz.cuni.amis.pogamut.base.utils.logging.marks.LogMapMark;
import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.base3d.worldview.object.Rotation;
import cz.cuni.amis.pogamut.unreal.communication.worldview.map.IUnrealMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;
import org.openide.util.*;

/**
 * Sub-renderer for object {@link IRenderableUTAgent}
 *
 * <b>Implementation note:</b> be careful when asking twice for same field of agent
 * (like agent.getRotation()), because it is dependant on time when you ask and
 * returned value is not guaranteed to be same (like first can be valid and second null).
 * @author Honza
 */
public class UTAgentSubGLRenderer implements ISubGLRenderer<IRenderableUTAgent> {

    private static Logger logger;
    private static GLU glu;
    private static GLUT glut;
    private static GLUquadric quadratic;

    static {
        glu = new GLU();
        glut = new GLUT();
        quadratic = glu.gluNewQuadric();

        logger = Logger.getLogger("TLMapRenderer");
        logger.setLevel(Level.INFO);
    }
    /**
     * The agent this class renders
     */
    private final IRenderableUTAgent agent;
    private final IUnrealMap map;
    private static final double SPHERE_RADIUS = 60;
    private static final int SPHERE_SLICES = 32;
    private static final int SPHERE_STACKS = 32;

    /**
     * Create a new subrenderer with passed agent as source of data.
     * @param renderableUTAgent agent used as source of data.
     */
    public UTAgentSubGLRenderer(IRenderableUTAgent utAgent, IUnrealMap map) {
        this.agent = utAgent;
        this.map = map;
    }

    @Override
    public void prepare(GL gl) {
    }

    @Override
    public IRenderableUTAgent getObject() {
        return agent;
    }

    @Override
    public void render(GL gl) {
        try {
            Location entityLocation = agent.getLocation();
            if (entityLocation == null) {
                return;
            }

            Location center = new Location(entityLocation.x, entityLocation.y, entityLocation.z + SPHERE_RADIUS * 1.1);
            GlColor color = new GlColor(agent.getColor());

            gl.glLoadName(agent.getGLName());

            renderAgent(gl, color, center);
            gl.glLoadName(-1);
            renderInfo(gl, color, center);
            renderFade(gl, color, agent.getFadeLine());

            for (MapEvent mapEvent : agent.getMapEvents()) {
                if (!mapEvent.shouldFollowPlayer()) {
                    renderPlacedMapEvent(gl, color, mapEvent);
                }
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
            // TODO handle situation when an agent disconnects
        }
    }

    /**
     * Render agent at specified position
     * @param gl
     * @param position
     */
    private void renderAgent(GL gl, GlColor color, Location position) {
        gl.glPushMatrix();
        {
            gl.glTranslated(position.x, position.y, position.z);
            // draw sphere
            gl.glColor4d(color.r, color.g, color.b, color.a);
            glu.gluSphere(quadratic, SPHERE_RADIUS, SPHERE_SLICES, SPHERE_STACKS);



            IPogamutEnvironments environments = Lookup.getDefault().lookup(IPogamutEnvironments.class);
            if (environments != null) {
                Collection c = environments.getEnvironmentSelection(map).lookupAll(this.agent.getDataSource().getClass());
                for (Object o : c) {
                    if (agent.getDataSource().equals(o)) {
                        gl.glColor3d(0.3, 0.3, 0.3);
                        glu.gluDisk(quadratic, SPHERE_RADIUS * 1.2, SPHERE_RADIUS * 1.5, 32, 3);
                    }
                }
            }
        }
        gl.glPopMatrix();

        Rotation rot = agent.getRotation();
        if (rot != null) {
            renderRotation(gl, new GlColor(1, 0, 0), position, rot);
        }

        /*        if (window == null) {
        window = new GLTextWindow(gl, 100, 20, 200, 50, "Hi, this is a test text 1234567890");
        }
        window.render();
         */
    }

//    private GLTextWindow window;
    /**
     * Draw rotation arrow 
     * @param gl
     * @param color What color should arrow be
     * @param center Where is center of arrow
     * @param rotation In what direction does arrow points
     */
    private void renderRotation(GL gl, GlColor color, Location center, Rotation rotation) {
        gl.glPushMatrix();
        {
            gl.glTranslated(center.x, center.y, center.z);

            Location endOfArrow = rotation.toLocation().getNormalized().scale(SPHERE_RADIUS * 2.5);

            gl.glBegin(GL.GL_LINES);
            gl.glColor4d(color.r, color.g, color.b, color.a);
            gl.glVertex3d(0, 0, 0);
            gl.glVertex3d(endOfArrow.x, endOfArrow.y, endOfArrow.z);
            gl.glEnd();

            gl.glTranslated(endOfArrow.x, endOfArrow.y, endOfArrow.z);
            // XXX: This works only in 2D, not 3D, because I am not in the mood
            // to figure out direction of Roll, Yaw and Pitch as well as order of
            // transformations. And rotation.toLocation() returns 2D coords anyway.


            double yaw = rotation.getYaw() / 32767 * 180; // left right, aka around z
            double roll = rotation.getRoll() / 32767 * 180; // clockwise/counter? around x
            double pitch = rotation.getPitch() / 32767 * 180; // up and down,  around y

            /*
            gl.glRotated(pitch, );
            gl.glRotated(yaw, );
            gl.glRotated(roll, );
             */
//            return res.mul(pitch).mul(yaw).mul(roll);

            if (logger.isLoggable(Level.FINE)) logger.fine(" Rotation: Yaw " + yaw + " roll " + roll + " pitch " + pitch);

            //gl.glRotated(roll, 1,0,0);
            gl.glRotated(yaw, 0, 0, 1);
            //gl.glRotated(pitch, 0,1,0);

            gl.glRotated(90, 0, 1, 0);

            glut.glutSolidCone(20, 40, 16, 16);
        }
        gl.glPopMatrix();

    }

    /**
     * Render passed fade line in passed color.
     * @param gl
     * @param fadeline Data about fadeline
     * @param color what color should fadeline be drawn
     */
    private void renderFade(GL gl, GlColor color, IFadeLine fadeline) {
        gl.glEnable(GL.GL_BLEND);
        gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);

        gl.glBegin(GL.GL_QUADS);

        Location lastLocation = null;
        double lastAlpha = 0; // how much is point opaque

        for (long ms = 0; ms < fadeline.getDuration(); ms += 100) {
            Location currentLocation = fadeline.getPosition(ms);
            double currentAlpha = ((double) ms) / fadeline.getDuration();

            if (lastLocation == null) {
                lastLocation = currentLocation;
                lastAlpha = currentAlpha;
                continue;
            }

            if (currentLocation != null) {
                pushFadeQuad(gl, color, currentLocation, lastLocation, currentAlpha, lastAlpha);

                lastLocation = currentLocation;
                lastAlpha = currentAlpha;
            }
        }

        gl.glEnd();

        gl.glDisable(GL.GL_BLEND);
    }

    /**
     * Queue proper OGL coordinates and colors to render part of fadeline
     * from start to end as quad.
     *
     * @param gl
     * @param color What color should be used fo quad.
     * @param start Starting location
     * @param end Ending location
     * @param startAlpha How much opque should be quad at start 0(transparent)-1(opaque)
     * @param endAlpha How much should quad be at end 0(transparent)-1(opaque)
     */
    private void pushFadeQuad(GL gl, GlColor color, Location start, Location end, double startAlpha, double endAlpha) {
        // directional vector from last to current
        Location dir = Location.sub(start, end).setZ(0);
        if (dir.getLength() == 0) {
            return;
        }

        dir = dir.getNormalized();

        // 90 degrees rotated vector
        Location trans = new Location(dir.y, -dir.x, 0);
        trans = trans.scale(6);

        // line at last location
        Location p1 = end.sub(trans);

        Location p2 = end.add(trans);

        gl.glColor4d(color.r, color.g, color.b, 1 - endAlpha);
        gl.glVertex3d(p1.x, p1.y, p1.z + SPHERE_RADIUS);
        gl.glVertex3d(p2.x, p2.y, p2.z + SPHERE_RADIUS);


        // line at current location
        Location p3 = start.add(trans);
        Location p4 = start.sub(trans);

        gl.glColor4d(color.r, color.g, color.b, 1 - startAlpha);
        gl.glVertex3d(p3.x, p3.y, p3.z + SPHERE_RADIUS);
        gl.glVertex3d(p4.x, p4.y, p4.z + SPHERE_RADIUS);
    }

    /**
     * Render info about agent (in most cases event messages) and its name.
     * TODO: Ugly code, refactor
     * @param gl
     * @param color What color should be used to render info.
     * @param location position of agent
     */
    private void renderInfo(GL gl, GlColor color, Location location) {
        // get text
        List<String> infos = new ArrayList<String>(agent.getAssociatedInfo());
        infos.add(0, '*' + agent.getName() + '*');

        Location topHead = new Location(location);
        topHead = new Location(topHead.x, topHead.y, topHead.z + 2 * SPHERE_RADIUS * 1.1);

        Location top2d = GLTools.getScreenCoordinates(gl, glu, topHead, null);

        int lineGap = 12;
        int font = GLUT.BITMAP_HELVETICA_10;

        int maxWidth = 0;
        for (String line : infos) {
            int lineWidth = glut.glutBitmapLength(font, line);
            if (lineWidth > maxWidth) {
                maxWidth = lineWidth;
            }
        }

        // update starting  position
        top2d = new Location(top2d.x - maxWidth / 2, top2d.y + (infos.size() - 1) * lineGap);        

        GlColor textColor = color.getMixedWith(new GlColor(0, 0, 0), 80);

        gl.glColor3d(textColor.r, textColor.g, textColor.b);
        for (int i = 0; i < infos.size(); i++) {
            String text = infos.get(i);
            if (i == 0) {
                gl.glColor3d(color.r, color.g, color.b);
            } else {
                gl.glColor3d(textColor.r, textColor.g, textColor.b);
//                gl.glColor3d(0, 0, 0);
            }
            Location textPos = GLTools.getWorldCoordinates(gl, glu, top2d, null);
            gl.glRasterPos3d(textPos.x, textPos.y, textPos.z);
            glut.glutBitmapString(font, text);

            top2d = top2d.setY(top2d.y - lineGap);
        }
    }

    /**
     * Render {@link MapEvent} that is placed at fixed place (=not at the player).
     * @param gl
     * @param color what color should be map event drawn.
     * @param mapEvent map event to render.
     */
    private void renderPlacedMapEvent(GL gl, GlColor color, MapEvent mapEvent) {
        LogMapMark mapMark = mapEvent.getMark();
        Location position = mapMark.getLocation();

        if (logger.isLoggable(Level.FINE)) logger.fine(" MSG: " + mapMark.getMessage() + ", LOC: " + position);
//      gl.glEnable(GL.GL_BLEND);
//      gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);

        gl.glPushMatrix();
        {
            gl.glTranslated(position.x, position.y, position.z + SPHERE_RADIUS * 1.1);

            gl.glColor4d(color.r, color.g, color.b, color.a);
    //        glu.gluSphere(quadratic, SPHERE_RADIUS * 3, SPHERE_SLICES, SPHERE_STACKS);
            glu.gluCylinder(quadratic, SPHERE_RADIUS, SPHERE_RADIUS, 2*SPHERE_RADIUS, 4, 1);
        }
        gl.glPopMatrix();

        gl.glDisable(GL.GL_DEPTH_TEST);
        {
            gl.glColor3d(0, 0, 0);
            gl.glRasterPos3d(position.x, position.y, position.z);
            glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12, mapEvent.getMessage());
            gl.glColor3d(1, 1, 1);
        }
        gl.glEnable(GL.GL_DEPTH_TEST);

//      gl.glDisable(GL.GL_BLEND);
    }

    @Override
    public List<BlendTriangle> getBlendedTris() {
        return new LinkedList<BlendTriangle>();
    }
}
