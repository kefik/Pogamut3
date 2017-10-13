package cz.cuni.pogamut.shed.widget;

import cz.cuni.amis.pogamut.sposh.elements.PoshElement;
import cz.cuni.pogamut.shed.presenter.AbstractAcceptAction;
import cz.cuni.pogamut.shed.presenter.IPresenter;
import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.datatransfer.DataFlavor;
import org.netbeans.api.visual.action.ActionFactory;
import org.netbeans.api.visual.action.MoveProvider;
import org.netbeans.api.visual.widget.Widget;

/**
 * Move provider for some {@link Widget}s. This provider simluates drag and drop
 * action that starts by dragging @sourceWidget somewhere and dropping it. If
 * the widget it is dropped to a {@link IPresentedWidget} (target widget) and
 * take {@link AbstractAcceptAction}s from its its {@link IPresenter}. If {@link AbstractAcceptAction#getDataFlavor()
 * } of target widget is same as the {@link DataFlavor} of @sourceWidget and
 * they are not the same {@link Widget}, perform the action of the accept
 * action.
 *
 * @author Honza Havlicek
 */
class DragAndDropMoveProvider<LAP_ELEMENT extends PoshElement> implements MoveProvider {

    private static final Color GHOST_IS_ACCEPTED_COLOR = Color.GREEN;
    private static final Color GHOST_IS_NOT_ACCEPTED_COLOR = Color.RED;
    
    private final ShedScene scene;
    private final ShedWidget sourceWidget;
    private final LAP_ELEMENT element;
    private ShedWidget ghostWidget;

    /**
     * Create a drag and drop provider that will (if registered using {@link ActionFactory#createMoveAction(org.netbeans.api.visual.action.MoveStrategy, org.netbeans.api.visual.action.MoveProvider)
     * } to the source widget) enable user to drag and drop the source widget in
     * the scene.
     *
     * XXX: Maybe I could get rid of sourceWidget and use the widget.
     *
     * @param scene
     * @param sourceWidget The widget that user will drag and later drop elsewhere.
     * Used to determine the initial location of the DnD widget, checks if the
     * target widget is same,
     * @param element When user drops the widget, the actual dropped element.
     * The target will determine if it accepts it based on its {@link DataFlavor} ({@link PoshElement#getDataFlavor()
     * }).
     */
    DragAndDropMoveProvider(ShedScene scene, ShedWidget sourceWidget, LAP_ELEMENT element) {
        this.scene = scene;
        this.sourceWidget = sourceWidget;
        this.element = element;
    }

    @Override
    public Point getOriginalLocation(Widget widget) {
        Point sourceWidgetLeftTopCorner = new Point();
        Point sceneLocation = sourceWidget.convertLocalToScene(sourceWidgetLeftTopCorner);
        return sceneLocation;
    }

    @Override
    public void movementStarted(Widget widget) {
        ghostWidget = ShedWidgetFactory.createWidgetCopy(sourceWidget);
        ghostWidget.setPreferredLocation(getOriginalLocation(widget));
        scene.addDragAndDropWidget(ghostWidget);
    }

    @Override
    public void setNewLocation(Widget widget, Point point) {
        ghostWidget.setPreferredLocation(point);
        if (canBeElementDropped()) {
            ghostWidget.setBorderColor(GHOST_IS_ACCEPTED_COLOR);
        } else {
            ghostWidget.setBorderColor(GHOST_IS_NOT_ACCEPTED_COLOR);
        }
    }

    private boolean canBeElementDropped() {
        return getTargetAcceptAction() != null;
    }
    
    private AbstractAcceptAction getTargetAcceptAction() {
        Point sceneCenter = getGhostSceneCenter();
        ShedWidget foundWidget = scene.findShedWidget(sceneCenter);
        if (foundWidget == null) {
            return null;
        }
        if (foundWidget == sourceWidget) {
            return null;
        }

        DataFlavor sourceDataFlavor = this.element.getDataFlavor();
        IPresenter targetWidgetPresenter = foundWidget.getPresenter();
        AbstractAcceptAction[] targetAcceptActions = targetWidgetPresenter.getAcceptProviders();
        if (targetAcceptActions == null) {
            return null;
        }
        for (AbstractAcceptAction targetAcceptAction : targetAcceptActions) {
            DataFlavor targetDataFlavor = targetAcceptAction.getDataFlavor();
            if (targetDataFlavor.equals(sourceDataFlavor)) {
                return targetAcceptAction;
            }
        }
        return null;
    }
    
    @Override
    public void movementFinished(Widget widget) {
        scene.removeDragAndDropWidget(ghostWidget);

        AbstractAcceptAction targetAcceptAction = getTargetAcceptAction();
        if (targetAcceptAction != null) {
            targetAcceptAction.performAction(element);
        }
        ghostWidget = null;
    }

    private Point getGhostSceneCenter() {
        Rectangle ghostBounds = ghostWidget.getBounds();
        Rectangle sceneBounds = sourceWidget.convertLocalToScene(ghostBounds);

        Point ghostSceneLocation = ghostWidget.getLocation();
        int x = ghostSceneLocation.x + sceneBounds.width / 2;
        int y = ghostSceneLocation.y + sceneBounds.height / 2;

        return new Point(x, y);
    }
}
