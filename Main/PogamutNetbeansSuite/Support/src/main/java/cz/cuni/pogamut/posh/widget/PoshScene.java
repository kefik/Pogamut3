package cz.cuni.pogamut.posh.widget;

import cz.cuni.amis.pogamut.sposh.elements.PoshElement;
import cz.cuni.pogamut.posh.view.KidViewElement;
import java.awt.Point;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.netbeans.api.visual.action.ActionFactory;
import org.netbeans.api.visual.graph.GraphScene;
import org.netbeans.api.visual.widget.LayerWidget;
import org.netbeans.api.visual.widget.Widget;
import org.netbeans.api.visual.widget.EventProcessingType;

/**
 * Scene where all PoshWidgets are placed into.
 * <p>
 * Use clearPoshWidgets to remove all PoshWidgets and its connections from the scene
 * Set new root widget by <tt>setRootWidget</tt> and the rest should be handled by
 * root widget that will recieve informations about new children, moved children or
 * about deletions.
 * <p>
 * How to create:
 * <ul>
 *  <li>JScrollPane shapePane = new JScrollPane();</li>
 *  <li>PoshScene scene = new PoshScene();</li>
 *  <li>JCompoenent myView = scene.createView();</li>
 *  <li>shapePane.setViewportView(myView);</li>
 * </ul>
 * 
 * @author Honza Havlicek
 */
public class PoshScene extends GraphScene {

    /**
     * Taransparent layer that contains <tt>PoshWidgets</tt>
     */
    private LayerWidget mainLayer;
    /**
     * Transparent layer that contains connections between widget and its parent.
     */
    private LayerWidget connectionLayer;
    /**
     * Transparent layer that is used during Drag and Drop operations.
     */
    private LayerWidget dndLayer;
    /**
     * Root widget of this representation of POSH tree.
     */
    private PoshWidget<? extends PoshElement> rootWidget;
    private Map<String, String> actionsFQNMapping = new HashMap<String, String>();
    private Map<String, String> sensesFQNMapping = new HashMap<String, String>();

    /**
     * Create a new PoshScene, add default Pan and Zoom actions to the scene
     */
    public PoshScene() {
        this.setKeyEventProcessingType(EventProcessingType.FOCUSED_WIDGET_AND_ITS_CHILDREN);

        getActions().addAction(ActionFactory.createPanAction());
        getActions().addAction(ActionFactory.createZoomAction(1.25, false));

        mainLayer = new LayerWidget(this);
        this.addChild(mainLayer);

        connectionLayer = new LayerWidget(this);
        this.addChild(connectionLayer);

        dndLayer = new LayerWidget(this);
        this.addChild(dndLayer);

        validate();
    }
    
    public Map<String, String> getActionsFQNMapping() {
        return actionsFQNMapping;
    }
    
    public Map<String, String> getSensesFQNMapping() {
        return sensesFQNMapping;
    }

    /**
     * Clear the scene, remove all widgets from the PoshScene.
     */
    public void clearPoshWidgets() {
        if (getRootWidget() != null) {
            clearPoshSubTree(getRootWidget());
            rootWidget = null;
        }
        validate();
    }

    /**
     * Check if the widget is in the main layer.
     *
     * @param widget widget that will be checked.
     * @return true if widget is in main layer, false else.
     */
    public boolean isInMainLayer(PoshWidget widget) {
        return this.mainLayer.getChildren().contains(widget);
    }

    /**
     * Remove all widgets that are children of passed <tt>node</tt> and the
     * node widget itself.
     *
     * @param node node that is root of subtree that will be deleted from the scene.
     */
    private void clearPoshSubTree(PoshWidget<? extends PoshElement> node) {
        List<PoshWidget<? extends PoshElement>> childNodes = node.getChildNodes();

        if (childNodes != null) {
            for (PoshWidget<? extends PoshElement> childNode : childNodes) {
                clearPoshSubTree(childNode);
            }
        }
        this.deletePoshWidget(node);
        validate();
    }

    /**
     * In case of parsing error, hide all widgets in the scene.
     *
     * It will be later regenerated from correct source.
     */
    public void setParsingError() {
        this.connectionLayer.setVisible(false);
        this.mainLayer.setVisible(false);

        validate();
    }

    /**
     * Order all widgets to form a tree, root widget is in left top corner.
     */
    public void consolidate() {
        validate();
        this.consolidate(rootWidget, 0, 0, false);
        validate();
    }

    /**
     * Remove widget and its connection from scene, doesn't consolidate
     * BEWARE: if it is not leaf that is removed, results are unpredictable.
     * @param widget widget to delete
     */
    public void deletePoshWidget(PoshWidget widget) {

        widget.getDataNode().getRootNode().removeListenersFromTree(widget);

        if (this.mainLayer.getChildren().contains(widget)) {
            this.mainLayer.removeChild(widget);
        } else {
            System.out.println("Widget is not child of mainLayer. "
                    + widget.getHeadlineText() + " "
                    + widget.getType()
                    + ". Probably widget representing multiple data nodes.");
            validate();
            return;
        }
        if (widget.getConnection() != null) {
            this.connectionLayer.removeChild(widget.getConnection());
        }
        validate();
    }

    private final static int slotInsetX = 35;
    private final static int slotInsetY = 15;


    /**
     * Order all widgets to form a tree.
     *
     * @param root  root of tree
     * @param x x slot of root
     * @param y y slot of root
     * @param start how many slots does this tree take (slots in width). One slot is one widget
     * @return
     */
    private int consolidate(PoshWidget<? extends PoshElement> root, int x, int y, boolean ancestorCollapsed) {
        // first set position of the root
        Point origin = new Point(x * (root.width + slotInsetX), y * (root.height + slotInsetY));
//        root.setPreferredLocation(origin);
        if (root.getPreferredLocation() == null) {
            root.setPreferredLocation(origin);
        } else {
            getScene().getSceneAnimator ().animatePreferredLocation (root, origin);
        }

        // Am I leaf?
        int height = 0;

        boolean rootCollapsed = ancestorCollapsed;
        if (root.isCollapsed()) {
            rootCollapsed = true;
        }

        for (PoshWidget<? extends PoshElement> child : root.getChildNodes()) {
            height += consolidate(child, x + 1, y + height, rootCollapsed);
        }

        if (root.getConnection() != null) {
            root.getConnection().setVisible(!ancestorCollapsed);
            validate();
        }

        root.setVisible(!ancestorCollapsed);
        validate();

        if (root.getChildNodes().isEmpty()) {
            return 1;		// consolidate the children
        }
        if (root.isCollapsed()) {
            return 1;
        }

        return height;
    }

    public PoshWidget<? extends PoshElement> getRootWidget() {
        return rootWidget;
    }

    /**
     * Set root widget of the scene.
     *
     * TODO: maybe clearsubtree instead of just widget.
     * @param node
     */
    public void setRootWidget(PoshWidget<? extends PoshElement> node) {
        this.connectionLayer.setVisible(true);
        this.mainLayer.setVisible(true);

        if (rootWidget != null) {
            this.deletePoshWidget(rootWidget);
        }
        rootWidget = node;

        // add menu
        node.getActions().addAction(ActionFactory.createPopupMenuAction(node));
        node.getActions().addAction(ActionFactory.createMoveAction(ActionFactory.createFreeMoveStrategy(), new DnDMoveProvider(this)));

        mainLayer.addChild(node);
        // validate scene
        validate();
    }

    /**
     * Add widget and its connection widget to the scene.
     * Validate it afterwards, consolidate if parameter set.
     *
     * @param newWidget widget that is being added to the tree
     * @param consolidate should the method automaticaly consolidate the tree?
     */
    public void addPoshWidget(PoshWidget newWidget, boolean consolidate) {
        // add menu provider
        newWidget.getActions().addAction(ActionFactory.createPopupMenuAction(newWidget));
        // Add DnD move provider so I can drag widget from one part of tree to another and drop it there
        newWidget.getActions().addAction(ActionFactory.createMoveAction(ActionFactory.createFreeMoveStrategy(), new DnDMoveProvider(this)));

        // add widget
        mainLayer.addChild(newWidget);
        connectionLayer.addChild(newWidget.getConnection());

        this.validate();

        if (consolidate) {
            this.consolidate();
        }
    }

    /**
     * Called by the addNode method before the node is registered to acquire
     * a widget that is going to represent the node in the scene.
     * The method is responsible for creating the widget, adding it into
     * the scene and returning it from the method.
     *
     * Not implemented, not usefull.
     *
     * @param node the node that is going to be added
     * @return the widget representing the node; null, if the node is non-visual
     */
    @Override
    protected Widget attachNodeWidget(Object node) {
        return null;
    }

    /**
     * Called by the addEdge method before the edge is registered to acquire
     * a widget that is going to represent the edge in the scene. The method
     * is responsible for creating the widget, adding it into the scene and
     * returning it from the method.
     *
     * Not implemented, not usefull.
     *
     * @param node the edge that is going to be added
     * @return the widget representing the edge; null, if the edge is non-visual
     */
    @Override
    protected Widget attachEdgeWidget(Object node) {
        return null;
    }

    /**
     * Not implemented, not usefull.
     */
    @Override
    protected void attachEdgeSourceAnchor(Object arg0, Object arg1, Object arg2) {
    }

    /**
     * Not implemented, not usefull.
     */
    @Override
    protected void attachEdgeTargetAnchor(Object arg0, Object arg1, Object arg2) {
    }

    /**
     * Add widget to ghost layer, used for DnD
     * @param ghost Widget to be added to the layer
     */
    protected void addGhostWidget(Widget ghost) {
        dndLayer.addChild(ghost);
    }

    /**
     * Remove the widget from DnD layer
     * @param ghost widget to be removed from the DnD layer
     */
    protected void removeGhostWidget(Widget ghost) {
        dndLayer.removeChild(ghost);
    }

    /**
     * Return list of PoshWidgets that are in the main layer.
     * @return
     */
    protected List<Widget> getPoshWidgets() {
        return mainLayer.getChildren();
    }

    private KidViewElement.SourceUpdater sourceUpdater;
    /**
     * Source updater will take the lap plan in its current tree form and serializes it into the document form
     * This is set every time I switch from text to graph view
     * XXX: My eyes are bleeding. Kill this code with fire.
     * @param sourceUpdater 
     */
    public void setSourceUpdater(KidViewElement.SourceUpdater sourceUpdater) {
        this.sourceUpdater = sourceUpdater;
    }
    
    public void updateSource() {
        sourceUpdater.update();
    }
    
}
