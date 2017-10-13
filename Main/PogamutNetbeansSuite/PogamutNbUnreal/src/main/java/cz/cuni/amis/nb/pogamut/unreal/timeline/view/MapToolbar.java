package cz.cuni.amis.nb.pogamut.unreal.timeline.view;

import cz.cuni.amis.nb.pogamut.unreal.timeline.map.TLMapGLPanel;
import cz.cuni.amis.nb.pogamut.unreal.timeline.map.MapRenderer;
import cz.cuni.amis.nb.pogamut.unreal.timeline.map.MapViewpoint;
import cz.cuni.amis.nb.pogamut.unreal.timeline.records.TLDatabase;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

/**
 * Toolbar shown in timelinne window, when the Map MV element is selected.
 * Provides buttons for easy change of default viewpoints.
 * @author Honza
 */
class MapToolbar extends TLToolbar {

    private MapViewpoint observer;
    private MapRenderer mapRenderer;
    private final AbstractAction setTopViewAction = new AbstractAction("Top") {

        @Override
        public void actionPerformed(ActionEvent e) {
            observer.setTopView(mapRenderer.getObject().getBox());
        }
    };
    private final AbstractAction setSideViewAction = new AbstractAction("Side") {

        @Override
        public void actionPerformed(ActionEvent e) {
            observer.setSideView(mapRenderer.getObject().getBox());
        }
    };
    private final AbstractAction setFrontViewAction = new AbstractAction("Front") {

        @Override
        public void actionPerformed(ActionEvent e) {
            observer.setFrontView(mapRenderer.getObject().getBox());
        }
    };

    public MapToolbar(TLDatabase db, TLMapGLPanel panel) {
        super(db);
        this.observer = panel.getMapViewpoint();
        this.mapRenderer = panel.getMapRenderer();

        add(setTopViewAction);
        add(setFrontViewAction);
        add(setSideViewAction);
    }
}
