package cz.cuni.pogamut.posh.widget;

import cz.cuni.amis.pogamut.sposh.elements.NamedLapElement;
import cz.cuni.amis.pogamut.sposh.elements.PoshElement;
import cz.cuni.amis.pogamut.sposh.elements.Sense;
import cz.cuni.amis.pogamut.sposh.elements.TriggeredAction;
import cz.cuni.pogamut.posh.widget.accept.AbstractAcceptAction;
import cz.cuni.pogamut.posh.widget.accept.DataNodeExTransferable;
import cz.cuni.pogamut.posh.widget.kidview.SimpleRoleActionWidget;
import cz.cuni.pogamut.posh.widget.kidview.SimpleSenseWidget;
import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.datatransfer.DataFlavor;
import java.util.List;
import org.netbeans.api.visual.action.MoveProvider;
import org.netbeans.api.visual.border.Border;
import org.netbeans.api.visual.border.BorderFactory;
import org.netbeans.api.visual.widget.Widget;

/**
 * Move provider for moveAction of PoshWidget.
 *
 * On grab(click): show properties of dataNode of PoshWidget
 *                 and create temporary ghost widget of PoshWidget
 *                 so user can see essence of original widget.
 * While drag: move ghost widget and try to find overlapping PoshWidget in scene
 * On drop: delete ghost and if ghost have been overlapping som widget,
 *          move widget there.
 * @author Honza
 */
class DnDMoveProvider implements MoveProvider {

    /**
     * Widget that is representing dragged widget.
     */
    private GhostWidget ghost;

    private PoshScene scene;

    DnDMoveProvider(PoshScene scene) {
        this.scene = scene;
    }

    /**
     * Create a ghost widget and add it to the scene.
     * @param arg0
     */
    @Override
    public void movementStarted(Widget arg0) {
        PoshWidget widget = (PoshWidget) arg0;

        ghost = new GhostWidget(widget);
        scene.addGhostWidget(ghost);

        widget.select(widget, widget.getLocation(), false);
    }

    /**
     * Find widget that contains point <tt>center</tt>.
     *
     * @param center
     * @return widget that contains the center or null if no such widget exists.
     */
    private PoshWidget<? extends PoshElement> findContainingWidget(Point center) {

        for (Widget widget : scene.getPoshWidgets()) {
            Rectangle rect = (Rectangle) widget.getClientArea().clone();
            rect.setLocation(widget.getLocation());

            if (rect.contains(center) && widget instanceof PoshWidget) {
                return (PoshWidget<? extends PoshElement>) widget;
            }
        }
        return null;
    }

    /**
     * When movement is finished and widget is dropped, remove ghost
     * from the scene, try to find widget that was containing the center
     * of ghost widget and if containting widget has a data node, that has
     * accept provider for ghost dataflavout, containg widget accepts
     * the ghost load.
     *
     * @param arg0
     */
    @Override
    public void movementFinished(Widget arg0) {
        // remove ghost
        scene.removeGhostWidget(ghost);

        // Does ghost resides over some PoshWidget?
        Point center = getGhostCenter();
        PoshWidget<? extends PoshElement> hitWidget = this.findContainingWidget(center);

        // Is there a covered widget
        if (hitWidget == null) {
            return;
        }
        // Disallow DnD from widget A to A, ony drop to different widget.
        if (hitWidget == ghost.associatedWidget) {
            return;
        }

        // Can I move the ghost widget type to this widget?
        List<AbstractAcceptAction> acceptProviders = hitWidget.getAcceptProviders();

        for (AbstractAcceptAction acceptProvider : acceptProviders) {
            DataFlavor ghostDataFlavor = ghost.associatedWidget.getDataNode().getDataFlavor();
            DataFlavor acceptDataFlavor = acceptProvider.getDataFlavor();

            if (acceptDataFlavor.equals(ghostDataFlavor)) {
                dropGhostOnWidget(hitWidget, acceptProvider);
                break;
            }
        }

        return;
    }

    private void dropGhostOnWidget(PoshWidget destination, AbstractAcceptAction acceptAction) {
        DataNodeExTransferable tr = new DataNodeExTransferable(ghost.associatedWidget.getDataNode());

        // Neutralize old widget
        PoshElement widgetDataNode = ghost.associatedWidget.getDataNode();
        
        // XXX: Ugly hack, neutralize was removed in 3.3.1. 
        PoshElement originalParent = widgetDataNode.getParent();
        originalParent.neutralizeChild(widgetDataNode);
        
        for (PoshElement originalChild : originalParent.getChildDataNodes()) {
            if (originalChild instanceof TriggeredAction) {
                TriggeredAction actionChild = (TriggeredAction) originalChild;
                actionChild.setActionName(SimpleRoleActionWidget.DEFAULT_ACTION);
            }
            if (originalChild instanceof Sense) {
                Sense senseChild = (Sense) originalChild;
                senseChild.setSenseName(SimpleSenseWidget.DEFAULT_SUCCEED_SENSE);
            }
        }
        
        
        
        // Add to new place
        acceptAction.accept(destination, null, tr);

        scene.validate();
    }

    private Point getGhostCenter() {
        Point center = (Point) ghost.getLocation().clone();
        center.translate(
                (int) ghost.getBounds().getWidth() / 2,
                (int) ghost.getBounds().getHeight() / 2);

        return center;
    }

    @Override
    public Point getOriginalLocation(Widget widget) {
        return widget.getLocation();
    }
    Border FUTILE_BORDER =
            BorderFactory.createRoundedBorder(0, 0, new Color(223, 106, 109), Color.BLACK);
    Border ACCEPTING_BORDER =
            BorderFactory.createCompositeBorder(
            BorderFactory.createRoundedBorder(0, 0, new Color(109, 243, 86), Color.BLACK),
            BorderFactory.createLineBorder(1),
            BorderFactory.createLineBorder(1),
            BorderFactory.createLineBorder(1));

    @Override
    public void setNewLocation(Widget w, Point currentLocation) {
        ghost.setPreferredLocation(currentLocation);

        Point center = getGhostCenter();
        PoshWidget<? extends PoshElement> hitWidget = this.findContainingWidget(center);
        if (hitWidget == null) {
            ghost.setBorder(FUTILE_BORDER);
            return;
        }

        List<AbstractAcceptAction> acceptProviders = hitWidget.getAcceptProviders();

        for (AbstractAcceptAction acceptProvider : acceptProviders) {
            DataFlavor ghostDataFlavor = ghost.associatedWidget.getDataNode().getDataFlavor();
            DataFlavor acceptDataFlavor = acceptProvider.getDataFlavor();

            if (acceptDataFlavor.equals(ghostDataFlavor)) {
                ghost.setBorder(ACCEPTING_BORDER);
                return;
            }
        }
        ghost.setBorder(FUTILE_BORDER);
    }
}

