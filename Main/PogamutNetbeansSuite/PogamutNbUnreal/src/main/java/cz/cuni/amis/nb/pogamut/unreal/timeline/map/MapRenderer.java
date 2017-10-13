package cz.cuni.amis.nb.pogamut.unreal.timeline.map;

import cz.cuni.amis.nb.pogamut.options.TimelinePanel;
import cz.cuni.amis.nb.pogamut.options.TimelinePanel.MapFlag;
import cz.cuni.amis.nb.pogamut.unreal.map.BlendTriangle;
import cz.cuni.amis.nb.pogamut.unreal.map.BlendVertex;
import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.unreal.communication.worldview.map.Box;
import cz.cuni.amis.pogamut.unreal.communication.worldview.map.IUnrealMap;
import cz.cuni.amis.pogamut.unreal.communication.worldview.map.IUnrealMapInfo;
import cz.cuni.amis.pogamut.unreal.communication.worldview.map.IUnrealWaylink;
import cz.cuni.amis.pogamut.unreal.communication.worldview.map.IUnrealWaypoint;
import java.awt.Color;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;
import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import org.openide.util.NbPreferences;

/**
 * Renderer of map. Because of uncomplete server implementation
 * I have no way of knowing state of the server, so I hope this will work.
 *
 * I *assume* that server is up and running and I can get worldmap from it.
 *
 * @author Honza
 */
public class MapRenderer implements ISubGLRenderer<IUnrealMap>, PreferenceChangeListener {

    private GLU glu = new GLU();
    public final int GRID_SCALE = 1000;
    public final double NAVPOINT_RADIUS = 30;

    /**
     * OpenGL display lists
     */
    private int gridList = -1;
    private int pathDisplayList = -1;
    private int waypointDisplayList = -1;
    private int backgroundList = -1;

    /**
     * Flag that we should update display lists according to new data from preferences.
     */
    private boolean updateLists = true;

    private IUnrealMap map;
    /**
     * List of all triangles used for rendering paths between
     */
    private List<PathTriangle> pathTris;
    /**
     * List of triangles used waypoints
     */
    private List<BlendTriangle> waypointsTris;
    private int glName;

    /**
     * GameBots provides us with flags about pathways between navpoints.
     * This class is human readable names for flags.
     *
     * The reason why is this class and not enum is that it is a bitmap mask
     * so they can easily combine. For now.
     */
    public MapRenderer(IUnrealMap map, /*MapViewpoint observer,*/ int glName) {
        this.map = map;
        this.glName = glName;
        NbPreferences.forModule(TimelinePanel.class).addPreferenceChangeListener(this);
    }

    @Override
    public IUnrealMap getObject() {
        return map;
    }

    /**
     * Precreate display lists for map, because map can be quite large and it
     * would be troublesome to do it every rendering.
     * @param gl
     */
    @Override
    public void prepare(GL gl) {
        gridList = createGridList(gl);
        backgroundList = createMapBackground(gl);

        // Create data for map
        waypointsTris = createWaypointsList();
        pathTris = createPathsList();

    }

    /**
     * Create a diplay list for map background image, i.e. image that is show
     * above grid to give a better idea about world. It is mostly screenshot 
     * from UnrealEd.
     * @param gl
     * @return id of display list
     */
    public int createMapBackground(GL gl) {
        IUnrealMapInfo info = map.getInfo();

        if (info == null) {
            return -1;
        }

        Location[] pos2D = info.getImagePoints();//new Location[3];
        Location[] posMap = info.getWorldPoints();//new Location[3];

        // REFACTORED
//        for (Location pos : posMap) {
//            pos.z = 0;
//        }
        for (int i = 0; i < posMap.length; ++i) {
        	posMap[i] = posMap[i].setZ(0);
        }

        // Chci pozice rohu obrazku ve 3d
        Location vec1stTo2nd = Location.sub(pos2D[1], pos2D[0]);
        Location vec1stTo3rd = Location.sub(pos2D[2], pos2D[0]);

        // get position in 3d for every corner
        Coeficients[] coef = new Coeficients[4];
        // tlc ~ top left corner
        coef[0] = solveEquation(new Location(0, 0, 0), pos2D[0], vec1stTo2nd, vec1stTo3rd);
        // trc
        coef[1] = solveEquation(new Location(info.getWidth(), 0, 0), pos2D[0], vec1stTo2nd, vec1stTo3rd);
        // brc
        coef[2] = solveEquation(new Location(info.getWidth(), info.getHeight(), 0), pos2D[0], vec1stTo2nd, vec1stTo3rd);
        // blc
        coef[3] = solveEquation(new Location(0, info.getHeight(), 0), pos2D[0], vec1stTo2nd, vec1stTo3rd);

        Location map1stTo2nd = Location.sub(posMap[1], posMap[0]);
        Location map1stTo3rd = Location.sub(posMap[2], posMap[0]);

        Location[] mapLoc = new Location[4];

        for (int i = 0; i < 4; i++) {
            mapLoc[i] = Location.add(posMap[0], Location.add(map1stTo2nd.scale(coef[i].a), map1stTo3rd.scale(coef[i].b)));
        }


        int texture = genTexture(gl);

        gl.glBindTexture(GL.GL_TEXTURE_2D, texture);

        glu.gluBuild2DMipmaps(
                GL.GL_TEXTURE_2D,
                GL.GL_RGB8,
                info.getWidth(),
                info.getHeight(),
                GL.GL_RGB,
                GL.GL_UNSIGNED_BYTE,
                ByteBuffer.wrap(info.getImgRGBData()));

        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);

        int list = gl.glGenLists(1);

        // start new list
        gl.glNewList(list, GL.GL_COMPILE);

        gl.glEnable(GL.GL_TEXTURE_2D);

        gl.glBindTexture(GL.GL_TEXTURE_2D, texture);

        gl.glBegin(GL.GL_QUADS);

        gl.glColor3d(1, 1, 1);

        int i = 0;
        double z = getFloorZ() + 5;
        gl.glTexCoord2d(0, 1);
        gl.glVertex3d(mapLoc[i].x, mapLoc[i].y, z);

        i = 1;
        gl.glTexCoord2d(1, 1);
        gl.glVertex3d(mapLoc[i].x, mapLoc[i].y, z);

        i = 2;
        gl.glTexCoord2d(1, 0);
        gl.glVertex3d(mapLoc[i].x, mapLoc[i].y, z);

        i = 3;
        gl.glTexCoord2d(0, 0);
        gl.glVertex3d(mapLoc[i].x, mapLoc[i].y, z);

        gl.glEnd();

        gl.glDisable(GL.GL_TEXTURE_2D);

        gl.glEndList();

        return list;
    }

    /**
     * Generate one texture
     * @param gl
     * @return id of texture
     */
    private int genTexture(GL gl) {
        int[] textures = new int[1];
        gl.glGenTextures(1, textures, 0);
        return textures[0];
    }

    private double getFloorZ() {
        Box box = map.getBox();
        return box.minZ - box.getMinDelta() * 0.20;
    }

    private double getGridZ() {
        Box box = map.getBox();
        return box.minZ - box.getMinDelta() * 0.35;
    }

    private static class Coeficients {

        public Coeficients(double a, double b) {
            this.a = a;
            this.b = b;
        }
        double a, b;
    }

    /**
     * get vector components so R = P+a*U +b*V
     *
     * @return
     */
    private Coeficients solveEquation(Location r, Location p, Location u, Location v) {
        /*        r.x = p.x + a*u.x + b*v.x
        r.y = p.y + a*u.y + b*v.y
         */
        if (u.x != 0) {
            double multi = u.y / u.x;
            /*        0 = (p.x - r.x) * multi + a*u.x * multi + b*v.x * multi
            0 = (p.y - r.y) + a*u.y + b*v.y

            0 = (p.y - r.y) - (p.x - r.x) * multi +
            b*v.y - b*v.x * multi
             */
            double b = (-((p.y - r.y) - (p.x - r.x) * multi)) / (v.y - v.x * multi);

            //0 = (p.x - a.x) + a*u.x + b*v.x
            double a = -((p.x - r.x) + b * v.x) / u.x;

            return new Coeficients(a, b);
        } else {
            /*            r.x = p.x + b*v.x
            r.y = p.y + a*u.y + b*v.y
             */
            double b = (r.x - p.x) / v.x;
            double a = (r.y - p.y - b * v.y) / u.y;
            return new Coeficients(a, b);
        }

    }

    /**
     * Return list of triangles that (if rendered) displays all waypoints
     * in the map. Waypoints are represented as circle with radius
     * {@link NAVPOINT_RADIUS} at every waypoint.
     * 
     * @return List of triangles 
     */
    private LinkedList<BlendTriangle> createWaypointsList() {
        LinkedList<BlendTriangle> triangles = new LinkedList<BlendTriangle>();
        Collection<IUnrealWaypoint> navs = map.vertexSet();

        GlColor color = new GlColor(new Color(NbPreferences.forModule(TimelinePanel.class).getInt(TimelinePanel.MapColor.WAYPOINTS_COLOR_KEY.getPrefKey(), TimelinePanel.MapColor.WAYPOINTS_COLOR_KEY.getDegaultARGB())));
        for (IUnrealWaypoint nav : navs) {
            Location loc = nav.getLocation();

            Location[] points = createCirclePoints(loc, NAVPOINT_RADIUS);

            for (int pointIndex = 0; pointIndex < points.length; pointIndex++) {
                BlendTriangle triangle = new BlendTriangle();

                triangle.setVertex(0, loc, color);
                triangle.setVertex(1, points[pointIndex], color);
                triangle.setVertex(2, points[(pointIndex + 1) % points.length], color);

                triangles.add(triangle);
            }
        }

        return triangles;
    }

    /**
     * Return list of points that form a circle with center loc and radius radius.
     * @param loc Center of circle
     * @param radius Radius of circle
     * @return List of points that form the circle
     */
    private Location[] createCirclePoints(Location loc, double radius) {
        Location[] points = new Location[12];

        double stepAngle = 2 * Math.PI / points.length;

        for (int pointIndex = 0; pointIndex < points.length; pointIndex++) {
            double angle = stepAngle * pointIndex;
            double xPos = loc.x + radius * Math.cos(angle);
            double yPos = loc.y + radius * Math.sin(angle);
            points[pointIndex] = new Location(xPos, yPos, loc.z);
        }

        return points;
    }

    /**
     * Create OpenGl list for grid lines of the map.
     * @return id of a list
     */
    private int createGridList(GL gl) {
        Box mapBox = map.getBox();
        int numMainLines = getNumGridLines(mapBox);

        double lineLength = 2 * GRID_SCALE * numMainLines;
        // TODO: Is 10 good arbiotrary number?
        double floorZ = getGridZ();
        int list = gl.glGenLists(1);

        gl.glNewList(list, GL.GL_COMPILE);
        {
            gl.glBegin(GL.GL_LINES);
            {
                // central X lines, red
                double minY = mapBox.getCenterY() - lineLength / 2;
                double maxY = mapBox.getCenterY() + lineLength / 2;

                gl.glColor3d(0.45, 0.29, 0.32);
                gl.glVertex3d(mapBox.getCenterX(), minY, floorZ);
                gl.glVertex3d(mapBox.getCenterX(), maxY, floorZ);

                // central Y line, green
                double minX = mapBox.getCenterX() - lineLength / 2;
                double maxX = mapBox.getCenterX() + lineLength / 2;

                gl.glColor3d(0.3, 0.57, 0.31);
                gl.glVertex3d(minX, mapBox.getCenterY(), floorZ);
                gl.glVertex3d(maxX, mapBox.getCenterY(), floorZ);

                // other lines for better idea about size of level
                // main lines
                for (int line = 1; line <= numMainLines; line++) {
                    for (int coef = -1; coef <= 1; coef += 2) {
                        // main lines (darker grey)
                        gl.glColor3d(0.34, 0.34, 0.34);
                        // draw along Y axis
                        gl.glVertex3d(mapBox.getCenterX() + coef * line * GRID_SCALE, minY, floorZ);
                        gl.glVertex3d(mapBox.getCenterX() + coef * line * GRID_SCALE, maxY, floorZ);
                        // draw along X axis
                        gl.glVertex3d(minX, mapBox.getCenterY() + coef * line * GRID_SCALE, floorZ);
                        gl.glVertex3d(maxX, mapBox.getCenterY() + coef * line * GRID_SCALE, floorZ);

                        // minor lines (lighter grey)
                        gl.glColor3d(0.41, 0.41, 0.41);
                        for (int minority = 1; minority < 10; minority++) {
                            // draw along Y axis
                            gl.glVertex3d(mapBox.getCenterX() + coef * ((line - 1) * GRID_SCALE + minority * GRID_SCALE / 10), minY, floorZ);
                            gl.glVertex3d(mapBox.getCenterX() + coef * ((line - 1) * GRID_SCALE + minority * GRID_SCALE / 10), maxY, floorZ);
                            // draw along X axis
                            gl.glVertex3d(minX, mapBox.getCenterY() + coef * ((line - 1) * GRID_SCALE + minority * GRID_SCALE / 10), floorZ);
                            gl.glVertex3d(maxX, mapBox.getCenterY() + coef * ((line - 1) * GRID_SCALE + minority * GRID_SCALE / 10), floorZ);
                        }
                    }
                }
            }
            gl.glEnd();
        }
        gl.glEndList();

        return list;
    }

    /**
     * How many squares (each square is GRID_SCALE big) will grid have from
     * center to border in every direction.
     * @return Number of squares of the box
     */
    private int getNumGridLines(Box mapBox) {
        double max = mapBox.getDeltaX() > mapBox.getDeltaY() ? mapBox.getDeltaX() : mapBox.getDeltaY();

        max /= 2;
        return (int) Math.ceil(max / GRID_SCALE);
    }

    /**
     * Return list of locations that will represent quads
     * @param path array of location[i*4], i is some number, currently 3, but it depends on structure of connection
     */
    private Location[] createQuadPath(IUnrealWaylink path) {
        Location[] quads = new Location[3 * 4];

        IUnrealWaypoint from = path.getStart();
        IUnrealWaypoint to = path.getEnd();

        Location fromLoc = from.getLocation();
        Location toLoc = to.getLocation();

        // get normalized direction from fromNav to toNav in x,y plane.
        Location dir = Location.sub(toLoc, fromLoc).setZ(0);
        dir = dir.getNormalized();

        Location trans = new Location(dir.y, -dir.x, 0);

        // now do the quad to cover circle part of fromNav
        quads[0] = Location.add(fromLoc, trans.scale(NAVPOINT_RADIUS));
        quads[1] = Location.add(quads[0], dir.scale(NAVPOINT_RADIUS));
        quads[2] = Location.add(quads[1], trans.scale(-2 * NAVPOINT_RADIUS));
        quads[3] = Location.add(quads[2], dir.scale(-NAVPOINT_RADIUS));

        // quad that is covering to toNav
        quads[4] = Location.add(toLoc, trans.scale(-NAVPOINT_RADIUS));
        quads[5] = Location.add(quads[4], dir.scale(-NAVPOINT_RADIUS));
        quads[6] = Location.add(quads[5], trans.scale(2 * NAVPOINT_RADIUS));
        quads[7] = Location.add(quads[6], dir.scale(NAVPOINT_RADIUS));

        quads[8] = quads[2];
        quads[9] = quads[1];
        quads[10] = quads[6];
        quads[11] = quads[5];

        return quads;
    }

    private static class PathTriangle extends BlendTriangle {

        private int flags;

        public PathTriangle(int flags) {
            this.flags = flags;
        }

        public int getFlags() {
            return flags;
        }
    }

    private LinkedList<PathTriangle> createPathsList() {
        LinkedList<PathTriangle> triangles = new LinkedList<PathTriangle>();
        Collection<IUnrealWaylink> paths = map.edgeSet();

        double deltaZ = this.map.getBox().getDeltaZ();
        double displaceZ = this.map.getBox().minZ;

        for (IUnrealWaylink path : paths) {
            Location[] quads = createQuadPath(path);

            GlColor lowColor = new GlColor(0.8, 0.8, 0.8);
            GlColor highColor = new GlColor(0.8, 0, 0);

            // put quads to the list of blend triangles
            int quadNum = quads.length / 4;
            for (int quad = 0; quad < quadNum; quad++) {
                PathTriangle triOne = new PathTriangle(path.getFlags());

                for (int i = 0; i < 3; i++) {
                    Location vertexLoc = quads[quad * 4 + i];
                    GlColor color = lowColor.getMixedWith(highColor, (vertexLoc.z - displaceZ) / deltaZ);
                    triOne.setVertex(i, vertexLoc, color);
                }

                PathTriangle triTwo = new PathTriangle(path.getFlags());
                for (int i = 0; i < 3; i++) {
                    int quadIndex = quad * 4 + ((i + 2) % 4);
                    Location vertexLoc = quads[quadIndex];
                    GlColor color = lowColor.getMixedWith(highColor, (vertexLoc.z - displaceZ) / deltaZ);
                    triTwo.setVertex(i, vertexLoc, color);
                }

                triangles.add(triOne);
                triangles.add(triTwo);
            }
        }
        return triangles;
    }

    @Override
    public synchronized void render(GL gl) {
        /*
         * After a while it seems that using lighting and blending produces worse results
         * than this, at least in lucidity.
         *
        float[] lightAmbient = new float[]{0.5f, 0.5f, 0.5f, 1.0f};
        float[] lightDiffuse = new float[]{1.0f, 1.0f, 1.0f, 1.0f};
        float[] lightPosition = new float[]{
        0, 0, 1210,//0, 0, 0,
        //            (float)levelBox.getFlag().getCenterX(),
        //          (float)levelBox.getFlag().getCenterY(),
        //        (float)levelBox.getFlag().getCenterZ() + (float)levelBox.getFlag().getDeltaZ(),
        1.0f};
        gl.glLightfv(GL.GL_LIGHT0, GL.GL_AMBIENT, FloatBuffer.wrap(lightAmbient));
        gl.glLightfv(GL.GL_LIGHT0, GL.GL_DIFFUSE, FloatBuffer.wrap(lightDiffuse));
        gl.glLightfv(GL.GL_LIGHT0, GL.GL_POSITION, FloatBuffer.wrap(lightPosition));
        gl.glEnable(GL.GL_LIGHT0);

        gl.glLightfv(GL.GL_LIGHT0, GL.GL_SPOT_DIRECTION, new float[]{0, 0, 1}, 0);
         */

        
        if (updateLists || ! gl.glIsList(waypointDisplayList) || !gl.glIsList(pathDisplayList)) {
            updateMapDisplayLists(gl);
            updateLists = false;
        }

        gl.glLoadName(glName);

        // Rendering like this is too CPU intensive, so using display lists
        // renderPaths(gl, pathTris);
        // renderWaypoints(gl, waypointsTris);


        // render grid and background
        gl.glShadeModel(GL.GL_FLAT);
        gl.glCallList(gridList);
        gl.glCallList(backgroundList);
        gl.glCallList(pathDisplayList);
        gl.glCallList(waypointDisplayList);

        gl.glLoadName(-1);
    }

    /**
     * Render triangles stored in pathTriangles with info from settings
     * @param gl
     */
    private synchronized void renderPaths(GL gl, List<PathTriangle> triangles) {
        // Get colors from preferences
        GlColor lowColor = new GlColor(new Color(NbPreferences.forModule(TimelinePanel.class).getInt(TimelinePanel.MapColor.LOW_COLOR_KEY.getPrefKey(), TimelinePanel.MapColor.LOW_COLOR_KEY.getDegaultARGB())));
        GlColor highColor = new GlColor(new Color(NbPreferences.forModule(TimelinePanel.class).getInt(TimelinePanel.MapColor.HIGH_COLOR_KEY.getPrefKey(), TimelinePanel.MapColor.HIGH_COLOR_KEY.getDegaultARGB())));

        boolean includeFlagBehavior = NbPreferences.forModule(TimelinePanel.class).getBoolean(TimelinePanel.INCLUDE_FLAG_KEY, true);

        double deltaZ = this.map.getBox().getDeltaZ();
        double displaceZ = this.map.getBox().minZ;

        gl.glEnable(GL.GL_COLOR_MATERIAL);
        gl.glShadeModel(GL.GL_SMOOTH);

        gl.glBegin(GL.GL_TRIANGLES);
        for (PathTriangle triangle : triangles) {
            boolean render = false;
            if (includeFlagBehavior) {
                render = includeCanRenderPathFlag(triangle.getFlags());
            } else {
                render = excludeCanRenderFlag(triangle.getFlags());
            }
            
            if (render) {
                for (BlendVertex v : triangle.getVerts()) {
                    Location vertexLoc = v.getLocation();
                    GlColor color = lowColor.getMixedWith(highColor, (vertexLoc.z - displaceZ) / deltaZ);

                    gl.glColor4d(color.r, color.g, color.b, color.a);
                    gl.glVertex3d(vertexLoc.x, vertexLoc.y, vertexLoc.z);
                }
            }
        }
        gl.glEnd();
    }

    /**
     * If testedFlags has flag that according to preferences should be drawn,
     * draw it
     * @param flag
     * @return true if at least one of flags in testedFlag is set and preferences says that we should render it
     */
    private synchronized boolean includeCanRenderPathFlag(int testedFlag) {
        for (MapFlag flag : MapFlag.values()) {
            // if tested flag has enabled the flag
            if ((flag.getFlag() & testedFlag) != 0) {
                // if it does, does user says we should render such paths
                boolean shouldRender = NbPreferences.forModule(TimelinePanel.class).getBoolean(flag.getPrefKey(), flag.getDefault());
                if (shouldRender) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * If testedFlags has flag that according to preferences shouldn't be drawn,
     * don't render.
     * @param testedFlag
     * @return
     */
    private synchronized boolean excludeCanRenderFlag(int testedFlag) {
        for (MapFlag flag : MapFlag.values()) {
            // if tested flag has enabled the flag
            if ((flag.getFlag() & testedFlag) != 0) {
                // if it does, does user says we should render such paths
                boolean shouldRender = NbPreferences.forModule(TimelinePanel.class).getBoolean(flag.getPrefKey(), flag.getDefault());
                if (!shouldRender) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Render all waypoints (the circles in the map), data generated in createWaypointsList
     * @param gl
     * @param triangles
     */
    private synchronized void renderWaypoints(GL gl, List<BlendTriangle> triangles) {
        GlColor color = new GlColor(new Color(NbPreferences.forModule(TimelinePanel.class).getInt(TimelinePanel.MapColor.WAYPOINTS_COLOR_KEY.getPrefKey(), TimelinePanel.MapColor.WAYPOINTS_COLOR_KEY.getDegaultARGB())));

        gl.glEnable(GL.GL_COLOR_MATERIAL);
        gl.glShadeModel(GL.GL_SMOOTH);

        gl.glBegin(GL.GL_TRIANGLES);
        for (BlendTriangle triangle : triangles) {
            for (BlendVertex v : triangle.getVerts()) {
                // take color according to width
                gl.glColor4d(color.r, color.g, color.b, color.a);
                gl.glVertex3d(v.getLocation().x, v.getLocation().y, v.getLocation().z + 0.1);
            }
        }
        gl.glEnd();
    }


    @Override
    public synchronized void preferenceChange(PreferenceChangeEvent evt) {
        updateLists = true;
    }

    
    /**
     * Update display lists for waypoints and path quads according to preferences
     */
    private synchronized void updateMapDisplayLists(GL gl) {
        // delete old
        gl.glDeleteLists(pathDisplayList, 1);
        gl.glDeleteLists(waypointDisplayList, 1);

        // create new
        pathDisplayList = gl.glGenLists(1);
        gl.glNewList(pathDisplayList, GL.GL_COMPILE);
        renderPaths(gl, pathTris);
        gl.glEndList();

        waypointDisplayList = gl.glGenLists(1);
        gl.glNewList(waypointDisplayList, GL.GL_COMPILE);
        renderWaypoints(gl, waypointsTris);
        gl.glEndList();
    }

    @Override
    public List<BlendTriangle> getBlendedTris() {
        List<BlendTriangle> list = new LinkedList<BlendTriangle>();
        return list;
    }

    /**
     * Clean up the component (listeners, contexts ect.)
     */
    public void destroy() {
        NbPreferences.forModule(TimelinePanel.class).removePreferenceChangeListener(this);
    }
}


