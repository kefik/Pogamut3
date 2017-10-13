package cz.cuni.amis.nb.pogamut.unreal.timeline.view;

import cz.cuni.amis.nb.pogamut.unreal.timeline.records.TLDatabase;
import cz.cuni.amis.nb.pogamut.unreal.timeline.records.TLEntity;
import cz.cuni.amis.nb.pogamut.unreal.timeline.widgets.EntityWidget;
import cz.cuni.amis.nb.pogamut.unreal.timeline.widgets.TLScene;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import org.netbeans.core.spi.multiview.CloseOperationState;
import org.netbeans.core.spi.multiview.MultiViewElement;
import org.netbeans.core.spi.multiview.MultiViewElementCallback;
import org.openide.awt.UndoRedo;
import org.openide.util.Lookup;

/**
 * MV element that is showing the timeline, the events that had happend in the
 * environment on the timeline.
 * 
 * @author Honza
 */
public class MVTimelineElement extends JScrollPane implements MultiViewElement {

    /**
     * Database that is used as model of this view
     */
    private final TLDatabase database;
    /**
     * Toolbar for actions in the timeline.
     */
    private JToolBar toolbar;
    /**
     * Scene that contains the widgets representing data from database
     */
    private final TLScene scene;
    /**
     * Lookup of this view
     */
    private Lookup lookup;
    /** 
     * Listener for changes in db and update view accordingly
     */
    private final TLDatabase.Adapter dbListener = new TLDatabase.Adapter() {

        /** when new entity enters the environemt, create a widget for it.
         */
        @Override
        public void onEntityEntered(TLDatabase db, TLEntity entity) {
            scene.addEntityWidget(new EntityWidget(scene, entity));
        }

        /** when end time is changed, update time axis and if necessary, move
         * viewport at the end.
         */
        @Override
        public void endTimeChanged(long previousEndTime, long endTime) {
            scene.setTimeAxisLength(database.getElapsedTime());

            // FIXME: if we are at the end and
            int panelWidth = MVTimelineElement.this.getWidth();
            JComponent view = scene.getView();
            if (view.getWidth() > panelWidth) {
                if (view.getX() + view.getWidth() - panelWidth < 10) {
                    view.setLocation(-(view.getWidth() - panelWidth), view.getY());
                }
            }
        }
    };

    /**
     * Create a MV element with scene inside that is showing {@link TLEntity entities}
     * in the {@link TLDatabase database} and updates itself when database changes.
     * @param database
     */
    public MVTimelineElement(TLDatabase database, Lookup lookup) {
        this.database = database;
        this.lookup = lookup;

        scene = new TLScene(database);
        // listen for changes and update scene accordingly
        database.addDBListener(dbListener);

        // fill scene from data from DB
        for (TLEntity entity : database.getEntities()) {
            scene.addEntityWidget(new EntityWidget(scene, entity));
        }
    }

    @Override
    public JComponent getVisualRepresentation() {
        if (scene.getView() == null) {
            this.setViewportView(scene.createView());
        }
        return this;
    }

    @Override
    public JComponent getToolbarRepresentation() {
        if (toolbar == null) {
            toolbar = new TLToolbar(database);
        }

        return toolbar;
    }

    @Override
    public Action[] getActions() {
        return new Action[]{};
    }

    @Override
    public Lookup getLookup() {
        return lookup;
    }

    @Override
    public void componentOpened() {
    }

    @Override
    public void componentClosed() {
    }

    @Override
    public void componentShowing() {
    }

    @Override
    public void componentHidden() {
    }

    @Override
    public void componentActivated() {
    }

    @Override
    public void componentDeactivated() {
    }

    @Override
    public UndoRedo getUndoRedo() {
        return UndoRedo.NONE;
    }

    @Override
    public void setMultiViewCallback(MultiViewElementCallback callback) {
        // I don't use callback.
    }

    @Override
    public CloseOperationState canCloseElement() {
        return CloseOperationState.STATE_OK;
    }
}
