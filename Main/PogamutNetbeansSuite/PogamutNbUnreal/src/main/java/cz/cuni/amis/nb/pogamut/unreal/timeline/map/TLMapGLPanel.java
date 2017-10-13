package cz.cuni.amis.nb.pogamut.unreal.timeline.map;

import cz.cuni.amis.nb.pogamut.unreal.map.SelectableMapGLPanel;
import cz.cuni.amis.nb.pogamut.unreal.timeline.records.TLDatabase;
import cz.cuni.amis.nb.pogamut.unreal.timeline.records.TLEntity;
import cz.cuni.amis.pogamut.unreal.communication.worldview.map.IUnrealMap;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Panel that is rendering map structure and agents inside.
 *
 * @author Honza
 */
public class TLMapGLPanel extends SelectableMapGLPanel {

    private final TLDatabase database;
    /**
     * Listener that rerenders everything when current time is changed
     */
    private final TLDatabase.Adapter currentTimeListener = new TLDatabase.Adapter() {

        @Override
        public void currentTimeChanged(long previousCurrentTime, long currentTime) {
            updateEntityRenderers(currentTime);
            display();
        }
    };

    public TLMapGLPanel(IUnrealMap map, TLDatabase db) {
        super(map, Logger.getLogger("TLMapGLPanel"));

        // Create lookups and its dynamic content
/*        lookupContent = new InstanceContent();
        lookup = new AbstractLookup(lookupContent);
         */
        this.database = db;
        // get map
        //UT2004Map map = db.getMap();
/*
        mapViewpoint = new MapViewpoint();
        mapController = new MapController(this, mapViewpoint);

        mapRenderer = new MapRenderer(map, mapViewpoint, lastGLName++);
        agentRenderes = new GLRendererCollection<IRenderableUTAgent>();
        environmentRenderer = new EnvironmentRenderer(mapViewpoint, agentRenderes, mapRenderer);
        
        mapViewpoint.addViewpointListener(this);

        this.addGLEventListener(environmentRenderer);
         */
        // listen for added and removed entities
        db.addDBListener(currentTimeListener);

        // If i wont't do this, it will take a while later to change the views
/*        mapViewpoint.setFromViewedBox(map.getBox());

        this.addMouseListener(this);
         * */
    }

    private synchronized void updateEntityRenderers(long time) {
        /*        // get entities present at time
        List<TLEntity> entities = db.getEntities(time);

        // we have some old renderes we may be using or now.


        // now, some may overlap

        // some may be removed

        // and some added
         */

        // XXX: for now regenerate everything, later only something, this has easier testing

        Set<IRenderableUTAgent> toBeRemoved = agentRenderes.getDrawnObjects();
        Set<TLEntity> toBeAdded = database.getEntities(time);

        // remove entities that are supposed to be removed
        for (IRenderableUTAgent renderableUTAgent : toBeRemoved) {
            agentRenderes.removeRenderersOf(renderableUTAgent);
        }

        // Add new renderers from new objects
        for (TLEntity entity : toBeAdded) {
            IRenderableUTAgent renderableUTAgent = new TLRenderableUTAgent(entity, time, lastGLName++);
            UTAgentSubGLRenderer renderer = new UTAgentSubGLRenderer(renderableUTAgent, getMap());

            agentRenderes.addSubRenderer(renderer);
        }
    }

    public MapViewpoint getMapViewpoint() {
        return mapViewpoint;
    }

    public MapRenderer getMapRenderer() {
        return mapRenderer;
    }
}
