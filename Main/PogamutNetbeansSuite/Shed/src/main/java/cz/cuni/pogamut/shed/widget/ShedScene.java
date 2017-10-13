package cz.cuni.pogamut.shed.widget;

import cz.cuni.amis.pogamut.sposh.elements.DriveCollection;
import cz.cuni.amis.pogamut.sposh.elements.DriveElement;
import cz.cuni.amis.pogamut.sposh.elements.PoshPlan;
import cz.cuni.pogamut.posh.explorer.IPaletteActions;
import cz.cuni.pogamut.shed.presenter.IPresenter;
import cz.cuni.pogamut.shed.presenter.ShedPresenter;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.text.MessageFormat;
import java.util.*;
import org.netbeans.api.visual.action.ActionFactory;
import org.netbeans.api.visual.anchor.Anchor;
import org.netbeans.api.visual.layout.LayoutFactory;
import org.netbeans.api.visual.widget.LayerWidget;
import org.netbeans.api.visual.widget.Scene;
import org.netbeans.api.visual.widget.Widget;
import org.netbeans.spi.palette.PaletteActions;
import org.openide.util.Exceptions;

/**
 * Scene that shows the graph representation of {@link PoshPlan lap tree}. By
 * itself it is (nearly) only view. {@link ShedPresenter} is responsible for
 * manipulating the scene.
 *
 * @see LapSceneFactory factory to instantiate this scene.
 * @author HonzaH
 */
public class ShedScene extends Scene {

    /**
     * Presenter used by this scene.
     */
    private final ShedPresenter presenter;
    /**
     * Background layer, e.g. for cues where to DnD first sense trigger of DC,
     * if DC trigger is empty.
     */
    private final LayerWidget backgroundLayer;
    /**
     * Layer for widgets.
     */
    private final LayerWidget widgetLayer;
    /**
     * Layer for connections between widgets, separate layer so routing is
     * easier and faster.
     */
    private final LayerWidget connectionLayer;
    /**
     * Layer used to display movement of widget during drag and drop.
     */
    private final LayerWidget dragAndDropLayer;
    /**
     * Widget that is padding the scene at the right side, the padding area is
     * used to display arrows from the {@link DriveCollection} goal and {@link DriveElement}s
     * to the root anchor.
     */
    private final Widget dcPadding;
    /**
     * Root anchor fro drives and goal of DC.
     */
    private final Anchor dcAnchor;
    /**
     * Envelope for goal and drives of DC.
     */
    private final Widget dcEnvelope;
    /**
     * Envelope for triggers of goal of DC. Basically goal of the plan.
     */
    private final ShedTriggerEnvelope goalEnvelope;
    /**
     * Envelope containing visualization of all drives.
     */
    private final ShedDrivesEnvelope drivesEnvelope;

    /**
     * Factory that is used to create widgets for this scene.
     */
    private ShedWidgetFactory widgetFactory;
    /**
     * Reference to palette actions so actions can modify what palette shows and so on.
     */
    private IPaletteActions paletteActions;
    
    /**
     * Create initial visual configuration and set the presenter. This does not
     * draw the plan! Making sure that view (the scene) is correctly displaying
     * the model is responsibility of presenter.
     *
     * @param plan initial.
     */
    public ShedScene(PoshPlan plan) {
        backgroundLayer = createBackground();
        addChild(backgroundLayer);
        widgetLayer = new LayerWidget(this);
        addChild(widgetLayer);
        connectionLayer = new LayerWidget(this);
        addChild(connectionLayer);
        dragAndDropLayer = new LayerWidget(this);
        addChild(dragAndDropLayer);

        widgetLayer.setLayout(LayoutFactory.createHorizontalFlowLayout(LayoutFactory.SerialAlignment.LEFT_TOP, 0));
        
        dcPadding = new Widget(this);
        dcPadding.setMinimumSize(new Dimension(ShedWidgetFactory.HORIZONTAL_GAP, 1));
        widgetLayer.addChild(dcPadding);
        dcAnchor = new FixedWidgetAnchor(dcPadding, new Point(0, 0), Anchor.Direction.BOTTOM);
        
        dcEnvelope = new Widget(this);
        dcEnvelope.setLayout(LayoutFactory.createVerticalFlowLayout(LayoutFactory.SerialAlignment.LEFT_TOP, ShedWidgetFactory.VERTICAL_GAP));
        widgetLayer.addChild(dcEnvelope);

        goalEnvelope = new ShedTriggerEnvelope(this, dcAnchor);
        dcEnvelope.addChild(goalEnvelope);
        drivesEnvelope = new ShedDrivesEnvelope(this);
        dcEnvelope.addChild(drivesEnvelope);

        presenter = new ShedPresenter(this, plan);

        getActions().addAction(ActionFactory.createWheelPanAction());
        getActions().addAction(ActionFactory.createPanAction());
        getActions().addAction(ActionFactory.createZoomAction(1.25, false));
    }
    
    /**
     * Create background with visual cues what to do, if DC has no trigger sense
     * and/or no drive. Under normal situation, they should be overlayed by the
     * trigger and the drive.
     *
     * @return Created layer that will draw visual cues for the user.
     */
    private LayerWidget createBackground() {
        // XXX: Not yet implemented, issue m#1
        return new LayerWidget(this);
    }

    /**
     * Get presenter used by this scene.
     *
     * @return the presenter of the scene.
     */
    public ShedPresenter getPresenter() {
        return presenter;
    }

    /**
     * Get envelope for the goal of the drive collection.
     *
     * @return Envelope for goal senses of DC.
     */
    public ShedTriggerEnvelope getGoalEnvelope() {
        return goalEnvelope;
    }

    /**
     * @return Envelope with widgets representations of all drives in the plan.
     */
    public ShedDrivesEnvelope getDrivesEnvelope() {
        return drivesEnvelope;
    }

    /**
     * Get anchor for DC, it is used by goal of DC and drives.
     */
    public Anchor getRootAnchor() {
        return dcAnchor;
    }
    
    public void setPaletteActions(IPaletteActions paletteActions) {
        this.paletteActions = paletteActions;
    }

    public IPaletteActions getPaletteActions() {
        return paletteActions;
    }
    
    /**
     * Method for removing branches from the shed scene.
     *
     * @param removedBranchRoot
     */
    public final void removeBranch(Widget removedBranchRoot) {
        Widget[] children = removedBranchRoot.getChildren().toArray(new Widget[0]);
        for (Widget child : children) {
            removeBranch(child);
        }

        List<Widget> widgetsToRemove = findArrows(removedBranchRoot);
        connectionLayer.removeChildren(widgetsToRemove);

        if (removedBranchRoot instanceof IPresentedWidget) {
            IPresentedWidget presentedWidget = (IPresentedWidget) removedBranchRoot;
            IPresenter branchRootPresenter = presentedWidget.getPresenter();
            branchRootPresenter.unregister();
        }
        Widget rootParentWidget = removedBranchRoot.getParentWidget();
        rootParentWidget.removeChild(removedBranchRoot);
    }

    /**
     * Find all arrows in the scene that have one of anchors attached to the
     * @arrowEndpointWidget
     * @param arrowEndpointWidget Widget that is used for anchor of each returned arrow.
     * @return Set of found arrows
     */
    public List<Widget> findArrows(Widget arrowEndpointWidget) {
        return findArrows(Collections.singleton(arrowEndpointWidget));
    }
    
    /**
     * Find all arrows in the scene that have one of anchors attached to the
     * @arrowEndpointWidget
     * @param arrowEndpointWidget Widget that is used for anchor of each returned arrow.
     * @return Set of found arrows
     */
    List<Widget> findArrows(Set<Widget> arrowEndpointWidgets) {
        List<Widget> foundArrows = new LinkedList<Widget>();
        
        for (Widget arrow : connectionLayer.getChildren()) {
            ArrowWidget arrowWidget = (ArrowWidget) arrow;

            Anchor sourceAnchor = arrowWidget.getSourceAnchor();
            Widget sourceWidget = sourceAnchor.getRelatedWidget();

            Anchor targetAnchor = arrowWidget.getTargetAnchor();
            Widget targetWidget = targetAnchor.getRelatedWidget();

            if (arrowEndpointWidgets.contains(targetWidget) || arrowEndpointWidgets.contains(sourceWidget)) {
                foundArrows.add(arrow);
            }
        }
        return foundArrows;
    }
    
    
    /**
     * Find the {@link ShedWidget} int the widget layer that contains the passed
     * point.
     *
     * @param scenePoint Point in the scene coordinated that we are looking for.
     * @return Found widget or null if not found.
     */
    public ShedWidget findShedWidget(Point scenePoint) {
        for (Widget widget : widgetLayer.getChildren()) {
            ShedWidget foundWidget = findShedWidget(widget, scenePoint);
            if (foundWidget != null) {
                return foundWidget;
            }
        }
        return null;
    }

    private ShedWidget findShedWidget(Widget searchedEnvelope, Point scenePoint) {
        for (Widget searchedEnvelopeChild : searchedEnvelope.getChildren()) {
            ShedWidget foundWidget = findShedWidget(searchedEnvelopeChild, scenePoint);
            if (foundWidget != null) {
                return foundWidget;
            }
        }

        Rectangle localSearchedBounds = searchedEnvelope.getBounds();
        Rectangle sceneSearchedBounds = searchedEnvelope.convertLocalToScene(localSearchedBounds);

        if (sceneSearchedBounds.contains(scenePoint) && (searchedEnvelope instanceof ShedWidget)) {
            return (ShedWidget) searchedEnvelope;
        }
        return null;
    }

    /**
     * Create new arrow with specified anchors and add it the connection layer.
     * Layer is using anchors and they have to be added to the scene before the
     * scene is validated. IMPORTANT: I can't validate, until the anchors are
     * added to the scene, otherwise not possible to determine location of
     * anchor.
     *
     * @param sourceAnchor
     * @param targetAnchor
     */
    public final void addArrow(Anchor sourceAnchor, Anchor targetAnchor) {
        ArrowWidget arrowWidget = new ArrowWidget(this, sourceAnchor, targetAnchor);
        connectionLayer.addChild(arrowWidget);
    }
    
    /**
     * Add arrows to connection layer.
     * @param arrows Arrows to be added
     */
    public void addArrows(Set<ArrowWidget> arrows) {
        for (ArrowWidget arrow : arrows) {
            connectionLayer.addChild(arrow);
        }
    }
    
    /**
     * Find all arrows in the connectionLayer that have specified sourceAnchor 
     * and targetAnchor.
     * @param sourceAnchor Source anchor of arrows we are looking for.
     * @param targetAnchor Target anchor of arrows we are looking for.
     * @return Set of found arrows with desired anchors.
     */
    public Set<ArrowWidget> findArrows(Anchor sourceAnchor, Anchor targetAnchor) {
        Set<ArrowWidget> foundArrows = new HashSet<ArrowWidget>();
        
        for (Widget widget :  connectionLayer.getChildren()) {
            ArrowWidget arrow = (ArrowWidget) widget;
            if (arrow.getSourceAnchor() == sourceAnchor && arrow.getTargetAnchor() == targetAnchor) {
                foundArrows.add(arrow);
            }
        }
        return foundArrows;
    }

    /**
     * Remove all @arrowsToRemove from the connection layer. Do not update scene.
     * @param arrowsToRemove 
     */
    public void removeArrows(Set<ArrowWidget> arrowsToRemove) {
        for (ArrowWidget arrow : arrowsToRemove) {
            connectionLayer.removeChild(arrow);
        }
    }
    

    /**
     * Method that revalidates all widgets in @widgetLayer and reroutes all
     * arrows.
     */
    public void update() {
        revalidateBranch(widgetLayer);
        validate();
        // XXX: Why do I have to do this manually? When layout changes, shouldn't all 
        // widgets be revalidated, thus invoking Widget.Dependency
        revalidateBranch(connectionLayer);
        for (Widget connectionWidget : connectionLayer.getChildren()) {
            ArrowWidget arrow = (ArrowWidget) connectionWidget;
            arrow.reroute();
        }
        validate(); repaint();
    }
    
    private void revalidateBranch(Widget branchRoot) {
        for (Widget child : branchRoot.getChildren()) {
            revalidateBranch(child);
            //child.revalidate();
        }
        branchRoot.revalidate();
    }

    private String getLayerName(Widget layer) {
        if (layer == connectionLayer) {
            return "ConnectionLayer";
        } else if (layer == widgetLayer) {
            return "WidgetLayer";
        } else if (layer == backgroundLayer) {
            return "BackgroundLayer";
        } else if (layer == dragAndDropLayer) {
            return "DragAndDropLayer";
        } else {   
            return "UnknownLayer(" + layer + ")";
        }
    }
    
    int loop = 0;
    @Override
    protected void paintChildren() {
//        OutputWriter out = IOProvider.getDefault().getIO("ShedScene", false).getOut();
//        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss.SSS");
//        out.println(dateFormat.format(new Date(System.currentTimeMillis())) + " Paint" + loop);
        // FIXME: Horrible horrible hack. This shouldn't ever be done, but I am at my wits end.
        // In first ever componentShowing LapTreeMVElement I create new scene, synchronize it and call ShedSceen update.
        // but because scene graphics is at that time null, the scene validation process won't do anything and immediatly returns (see the code in Scene.java).
        // and I really need to validate sceen before painting it, otherwise bounds of widgets are null, resulting in Assertion error.
        // Basically: I don't know how to force validation after synchronization of PoshPlans
/*        if (!isValidated()) {
            OutputWriter out = IOProvider.getDefault().getIO("ShedScene", false).getOut();
            out.println("Scene is not validated at loop " + loop);
            if (this.getGraphics() == null) {
                out.println("Graphics is null at loop " + loop);
                JOptionPane.showMessageDialog(null, "Graphics is null");
            }
//            String layerInfo = getLayerStructureInfo(widgetLayer);
            update();
        }
*/        //String arrowInfo = getArrowInfo(); 
        
       // String structure = getLayerStructureInfo(widgetLayer);
        
        for (Widget child : getChildren()) {
            try {
                //if (child != widgetLayer)
                    child.paint();
            } catch (AssertionError error) {
                Exceptions.printStackTrace(error);
            }
        }
        loop++;
    }
    
    /**
     * @return String with informations about each arrow in the connectionLayer.
     */
    private String getArrowInfo() {
        List<Widget> connectionWidgets = connectionLayer.getChildren();
        StringBuilder sb = new StringBuilder("Number of arrows: " + connectionWidgets.size() + '\n');
        
        int arrowIndex = 0; 
        for (Widget connectionWidget : connectionWidgets) {
            ArrowWidget arrow = (ArrowWidget) connectionWidget;
            
            Widget sourceAnchorWidget = arrow.getSourceAnchor().getRelatedWidget();
            Point sourceAnchorSceneLocation = arrow.getSourceAnchor().getRelatedSceneLocation();
            
            Widget targetAnchorWidget = arrow.getTargetAnchor().getRelatedWidget();
            Point targetAnchorSceneLocation = arrow.getTargetAnchor().getRelatedSceneLocation();
            
            String arrowInfo = MessageFormat.format("Arrow {0,number,##}: {1} -> {2};  {3} -> {4}", 
                    arrowIndex, 
                    sourceAnchorWidget, targetAnchorWidget, 
                    sourceAnchorSceneLocation, targetAnchorSceneLocation);
            sb.append(arrowInfo).append('\n');

            ++arrowIndex;
        }
        return sb.toString();
    }
    
    private String getLayerStructureInfo(LayerWidget layer) {
        StringBuilder sb = new StringBuilder();
        sb.append("Structure of layer ");
        sb.append(getLayerName(layer));
        sb.append('\n');
        sb.append(getSubtreeInfo(layer, 0));
        
        return sb.toString();
    }
    
    private String getSubtreeInfo(Widget subroot, int prefixLength) {
        StringBuilder sb = new StringBuilder();
        for (int prefixIndex = 0; prefixIndex < prefixLength; ++prefixIndex) {
            sb.append(' ');
        }
        sb.append(subroot.getClass().getSimpleName());
        sb.append(": ");
        sb.append(subroot);
        sb.append('\n');
        int childPrefixLength = prefixLength + 2;
        for (Widget child : subroot.getChildren()) {
            sb.append(getSubtreeInfo(child, childPrefixLength));
        }
        return sb.toString();
    }

    /**
     * Add @newWidget to the drag and drop layer.
     * @param newWidget Widget that will be added to the drag and drop layer 
     */
    void addDragAndDropWidget(Widget newWidget) {
        dragAndDropLayer.addChild(newWidget);
    }

    /**
     * Remove @widget from the drag and drop layer.
     * @param newWidget Widget that will be removed from the drag and drop layer 
     */
    void removeDragAndDropWidget(Widget widget) {
        dragAndDropLayer.removeChild(widget);
    }

    /**
     * @return Widget factory for creating widgets representing other parts of
     * the plan.
     */
    public ShedWidgetFactory getWidgetFactory() {
        return widgetFactory;
    }

    /**
     * Set the factory for creation of widgets, it is not really used by the
     * scene, but presenters need it and this looks like a reasonable plance
     *
     * @param newWidgetFactory New factory for creation of widgets
     */
    public void setWidgetFactory(ShedWidgetFactory newWidgetFactory) {
        assert newWidgetFactory.getScene() == this;
        this.widgetFactory = newWidgetFactory;
    }

}
