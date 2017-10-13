package cz.cuni.amis.nb.pogamut.unreal.map;

import cz.cuni.amis.nb.pogamut.unreal.timeline.map.EnvironmentRenderer;
import cz.cuni.amis.nb.pogamut.unreal.timeline.map.GLRendererCollection;
import cz.cuni.amis.nb.pogamut.unreal.timeline.map.IRenderableUTAgent;
import cz.cuni.amis.nb.pogamut.unreal.timeline.map.ISubGLRenderer;
import cz.cuni.amis.nb.pogamut.unreal.timeline.map.MapController;
import cz.cuni.amis.nb.pogamut.unreal.timeline.map.MapRenderer;
import cz.cuni.amis.nb.pogamut.unreal.timeline.map.MapViewpoint;
import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.unreal.communication.worldview.map.IUnrealMap;
import java.awt.Point;
import java.beans.Beans;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLJPanel;

/**
 * This is a GLJPanel that displays UT2004Map. it is a base class, so it takes
 * care of some things, while others are left for derived class to do.
 * <p>
 * What it does:
 *  * Can render passed map
 *  * Can render all stuff in agentRenderers
 *  * Takes care of user interaction for map viewpoint
 *  * Selection of object
 * <p>
 * What it doesn't do:
 *  * automatically display map, someone else has to take care of that
 *    (adding/removing) from agentRenderers
 *  * fill in agentRenderers, derived class has to do that.
 *
 * In lookup are selected objects
 * @author Honza
 */
public abstract class MapGLPanel extends GLJPanel implements MapViewpoint.ViewpointListener {
    protected Logger logger;

    protected MapViewpoint mapViewpoint;
    protected MapController mapController;

    protected MapRenderer mapRenderer;
    protected GLRendererCollection<IRenderableUTAgent> agentRenderes;
    protected EnvironmentRenderer environmentRenderer;

    // iterator used to assign unique names for gl rendering, this enabling selection of objects
    protected int lastGLName = 1;

    private IUnrealMap map;

    /**
     * Create a panel for 
     * @param caps
     * @param map
     * @param log
     */
    protected MapGLPanel(GLCapabilities caps, IUnrealMap map, Logger log) {
        super(caps);

        if (Beans.isDesignTime()) {
            Beans.setDesignTime(false);
        }

        this.map = map;
        this.logger = log;

        Location mapFocus = new Location(
                map.getBox().getCenterX(),
                map.getBox().getCenterY(),
                map.getBox().getCenterZ());
        // Stuff for controlling viewpoint in map
        mapViewpoint = new MapViewpoint();
        mapController = new MapController(this, mapViewpoint, mapFocus);
        mapController.registerListeners();

        // Create renderers
        mapRenderer = new MapRenderer(map, lastGLName++);
        agentRenderes = new GLRendererCollection<IRenderableUTAgent>();
        environmentRenderer = new EnvironmentRenderer(mapViewpoint, agentRenderes, mapRenderer);

        // Add listener so this level is rendered
        this.addGLEventListener(environmentRenderer);

        // Listen for changes in viewpoint
        mapViewpoint.addViewpointListener(this);

        // Set initial position of view + thanks to listener display
        mapViewpoint.setFromViewedBox(map.getBox());

    }

    /**
     * Create a pane showing passed map
     * @param map Map this pane is supposed to show
     */
    protected MapGLPanel(IUnrealMap map, Logger log) {
        this(getCapabilities(), map, log);
    }
    
    /**
     * I require HW acceleration and double buffering.
     * @return Set of required capabilities
     */
    private static GLCapabilities getCapabilities() {
        GLCapabilities caps = new GLCapabilities();
        caps.setHardwareAccelerated(true);
        caps.setDoubleBuffered(true);
        return caps;
    }

    /**
     * When viewpoint is changed, render the map (call display()).
     * @param viewpoint
     */
    @Override
    public synchronized void onChangedViewpoint(MapViewpoint viewpoint) {
        display();
    }

    /**
     * Get agents at point p in the scene.
     * 
     * @param p in window coordiates system, [0,0] is left top
     * @return List of renderable agents that are at the passed point
     */
    public synchronized Set<IRenderableUTAgent> getAgentsAt(Point p) {
        environmentRenderer.setSelectPoint(p);
        display();
        int[] list = environmentRenderer.getSelectedObjects();

        Set<IRenderableUTAgent> selectedAgents = new HashSet<IRenderableUTAgent>();

        // find that miserable renderer
        for (ISubGLRenderer<IRenderableUTAgent> agentRenderer : agentRenderes.getSubRenderes()) {
            IRenderableUTAgent renderableAgent = agentRenderer.getObject();
            for (int glName : list) {
                if (glName == renderableAgent.getGLName()) {
                    selectedAgents.add(renderableAgent);
                }
            }
        }

        return selectedAgents;
    }


    /**
     * Remove listeners and basically clean up this map. Any call to this object after this method
     * should invoke exception (it doesn't but I can always hope in NullPointerException).
     */
    public synchronized void destroy() {
        this.removeGLEventListener(environmentRenderer);
        this.mapViewpoint.removeViewpointListener(this);

        mapRenderer = null;
        environmentRenderer = null;
        agentRenderes = null;

        mapController = null;
        mapViewpoint = null;
    }

    protected IUnrealMap getMap() {
        return map;
    }
}
