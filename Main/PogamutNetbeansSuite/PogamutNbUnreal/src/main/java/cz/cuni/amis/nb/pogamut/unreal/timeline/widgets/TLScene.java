package cz.cuni.amis.nb.pogamut.unreal.timeline.widgets;

import cz.cuni.amis.nb.pogamut.unreal.timeline.records.TLDatabase;
import org.netbeans.api.visual.action.ActionFactory;
import org.netbeans.api.visual.layout.Layout;
import org.netbeans.api.visual.layout.LayoutFactory;
import org.netbeans.api.visual.widget.LayerWidget;
import org.netbeans.api.visual.widget.Scene;

/**
 * This is a scene that is used to show timeline.
 * <p>
 * It has an time axis at the top and {{@link EntityWidget}}s can be added
 * through addEntityWidget.
 * 
 * @author Honza
 */
public class TLScene extends Scene {

    /**
     * Main transparent layer that is used to add all widgets
     */
    private final LayerWidget mainLayer;
    /**
     * Overlay layer
     */
    private final LayerWidget overlayLayer;
    /**
     * Widget for showing time axis at the top of scene
     */
    private final TimeTicsWidget ticsWidgety;
    /**
     * Widget used to show current time of db
     */
    private CurrentTimeWidget currentTimeWidget;

    private TLDatabase db;

    public TLScene(TLDatabase db) {
        LayoutFactory.SerialAlignment ALIGNMENT = LayoutFactory.SerialAlignment.LEFT_TOP;
        Layout flowLayout = LayoutFactory.createVerticalFlowLayout(ALIGNMENT, 10);

        getActions().addAction(ActionFactory.createPanAction());

        // add main layer
        this.mainLayer = new LayerWidget(this);
        this.mainLayer.setLayout(flowLayout);
        this.addChild(mainLayer);

        // add overlay layer
        this.overlayLayer = new LayerWidget(this);
        this.addChild(overlayLayer);

        // add CurrentTimeWidget to over
//        currentTimeWidget = new CurrentTimeWidget(this, db);
//        overlayLayer.addChild(currentTimeWidget);

        // add widget showing ticks above
        ticsWidgety = new TimeTicsWidget(this);
        this.mainLayer.addChild(ticsWidgety);
    }

    /**
     * Add new entity to the scene. The widget will listen for changes in
     * <tt>TLEntity</tt>
     *
     * @param entity
     */
    public void addEntityWidget(EntityWidget entityWidget) {
        this.mainLayer.addChild(entityWidget);
        this.validate();
    }

    /**
     * Set time interval that will be displayed in the widget showing time
     * @param time
     */
    public void setTimeAxisLength(long time) {
        ticsWidgety.setTime(time);
    }
}
