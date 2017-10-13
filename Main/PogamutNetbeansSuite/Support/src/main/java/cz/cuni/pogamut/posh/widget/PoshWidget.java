package cz.cuni.pogamut.posh.widget;

import cz.cuni.amis.pogamut.sposh.elements.PoshElement;
import cz.cuni.amis.pogamut.sposh.elements.PoshElementListener;
import cz.cuni.amis.pogamut.sposh.elements.PoshPlan;
import cz.cuni.pogamut.posh.widget.accept.AbstractAcceptAction;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import org.netbeans.api.visual.action.ActionFactory;
import org.netbeans.api.visual.action.PopupMenuProvider;
import org.netbeans.api.visual.action.SelectProvider;
import org.netbeans.api.visual.anchor.Anchor;
import org.netbeans.api.visual.anchor.AnchorFactory;
import org.netbeans.api.visual.anchor.AnchorFactory.DirectionalAnchorKind;
import org.netbeans.api.visual.anchor.AnchorShape;
import org.netbeans.api.visual.router.Router;
import org.netbeans.api.visual.router.RouterFactory;
import org.netbeans.api.visual.widget.ConnectionWidget;
import org.netbeans.api.visual.widget.Widget;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.Sheet;
import org.openide.windows.TopComponent;

/**
 * This widget is for displaying two lines of text with two differents fonts
 * surrounded with border.
 *
 * Basic widget for representation of one element of posh tree
 *
 * What should I put here(in abstract sense)?
 * <ul>
 *  <li>headline</li>
 *  <li>comment</li>
 *  <li>color</li>
 *  <li>context menu</li>
 * </ul>
 *
 * @author Honza
 */
public abstract class PoshWidget<T extends PoshElement> extends Widget implements PoshElementListener, PopupMenuProvider {

    /**
     * Router for all connections between widgets.
     */
    private static final Router orthoRouter = RouterFactory.createOrthogonalSearchRouter();
    /**
     * DataNode associated with this widget.
     */
    private final T dataNode;
    /**
     * Connection widget to parent (POSH plan is a tree like structure).
     * Can be null in case of root.
     */
    private ConnectionWidget connection;
    /**
     * Parent widget of this widget in the widget tree. Is null for root.
     */
    private PoshWidget<? extends PoshElement> parent;
    private final List<PoshWidget<? extends PoshElement>> children = new LinkedList<PoshWidget<? extends PoshElement>>();
    private boolean isCollapsed = false;
    private String headlineString = "[NOT SET]";
    private String commentString = "[NOT SET]";

    /**
     * Create a widget representing <tt>PoshElement</tt>.
     *
     * @param scene
     * @param node associated data node of this widget. never null.
     * @param parentWidget Parent widget of this widget (parent node), can be null,
     *                     if it is root of tree representation of POSH plan.
     */
    protected PoshWidget(PoshScene scene, T node, PoshWidget<? extends PoshElement> parentWidget, String displayName) {
        super(scene);

        parent = parentWidget;
        dataNode = node;
        dataNode.addElementListener(this);

        // Set text that should be shown in the widget
        setHeadlineText(displayName);
        setCommentText(getType().toString());

        // Create action for collapsing/expanding tree
        SelectProvider collapseProvider = new SelectProvider() {

            @Override
            public boolean isAimingAllowed(Widget arg0, Point arg1, boolean arg2) {
                return false;
            }

            @Override
            public boolean isSelectionAllowed(Widget widget, Point localPosition, boolean invertSelection) {
                if (localPosition.x > width - arrowWidth) {
                    return true;
                }
                return false;
            }

            @Override
            public void select(Widget arg0, Point arg1, boolean arg2) {
                setCollapsed(!isCollapsed);
            }
        };

        // Add collapse/expand action for subtree
        this.getActions().addAction(ActionFactory.createSelectAction(collapseProvider));

        // Accept actioms for the widget
        for (AbstractAcceptAction provider : getAcceptProviders()) {
            this.getActions().addAction(ActionFactory.createAcceptAction(provider));
        }

        // If parent exists, set anchor to parent widget
        if (getParent() != null) {
            connection = new ConnectionWidget(scene);

            Anchor parentAnchor = AnchorFactory.createDirectionalAnchor(this.getParent(), DirectionalAnchorKind.HORIZONTAL);
            Anchor nodeAnchor = AnchorFactory.createDirectionalAnchor(this, DirectionalAnchorKind.HORIZONTAL);

            connection.setSourceAnchor(parentAnchor);
            connection.setTargetAnchor(nodeAnchor);
            connection.setTargetAnchorShape(AnchorShape.TRIANGLE_FILLED);

            connection.setRouter(orthoRouter);
        }

    }

    /**
     * Listener for changes of node properties (e.g. name). It is overriden,
     * because after the node will change its property, I want to update
     * sources. Use {@link PoshWidget#elementPropertyChange(java.beans.PropertyChangeEvent)
     * } instead.
     *
     * @param evt description of a change in the node
     */
    @Override
    public final void propertyChange(PropertyChangeEvent evt) {
        elementPropertyChange(evt);
        updateSources();
    }
    
    /**
     * This is a method that is supposed to handle property changes of the lap
     * element.
     *
     * @param evt description of change
     */
    protected abstract void elementPropertyChange(PropertyChangeEvent evt);
    
    /**
     * Update sources according to the state of the lap tree.
     */
    protected final void updateSources() {
        // XXX: I should use controller or something, but this is slated for replacement anyway, go spagethi code
        getPoshScene().updateSource();
    }
    
    /**
     * Get root widget of the tree this widget belongs to.
     * @return root, not necessaryly same {@link PoshPlan}.
     */
    public final PoshWidget<? extends PoshElement> getRootWidget() {
        PoshWidget<? extends PoshElement> highestElement = this;
        while (highestElement.getParent() != null) {
            highestElement = highestElement.getParent();
        }
        return highestElement;
    }
    
    /**
     * Get children of this node.
     * @return children of the node. Never null.
     */
    public final List<PoshWidget<? extends PoshElement>> getChildNodes() {
        return children;
    }

    /**
     * Get actual text of headline. It should be same as the text from
     * <tt>getName</tt>, but if name is too long, shorten it and append "..."
     * @return text of the headline label
     */
    public String getHeadlineText() {
        return headlineString;
    }

    /**
     * Return text of comment, actual type of <tt>PoshElement</tt> this
     * widget represents. If it is too long, it will be shortened to fit
     * the width of <tt>PoshWidget</tt>.
     *
     * @return text of the comment label
     */
    public String getCommentText() {
        return commentString;
    }

    protected final void setCommentText(String text) {
        this.commentString = text;
        updateToolTipText();
    }

    /**
     * Is passed <tt>ancestorWidget</tt> in path from this widget to the root?
     *
     * @param ancestorWidget widget that
     * @return true if this widget is offspring of ancestorWidget
     */
    public boolean isAncestor(PoshWidget ancestorWidget) {
        PoshWidget parentWidget = this;

        while (parentWidget != null) {
            if (parentWidget == ancestorWidget) {
                return true;
            }

            parentWidget = parentWidget.getParent();
        }
        return false;
    }

    /**
     * Set text that should appear in headline.
     *
     * @param text Text that should be shown in headline
     */
    public final void setHeadlineText(String text) {
        headlineString = text;

        updateToolTipText();
        doRepaint();
    }

    private void updateToolTipText() {
        setToolTipText(getHeadlineText() + "\n" + getCommentText());
    }

    /**
     * Return list of acceptors that will do something when proper
     * <tt>Transferable</tt> is dropped on the widget.
     *
     * @return List of DnD accepting providers. Never null.
     */
    abstract protected List<AbstractAcceptAction> getAcceptProviders();

    /**
     * Get type of widget, in this case type of <tt>PoshElement</tt>
     *
     * @return type of <tt>PoshElement</tt> this Widget represents.
     */
    protected abstract PoshNodeType getType();

    /**
     * Return menu associated with the widget (will be used as context menu).
     *
     * @param arg0 ignored
     * @param arg1 ignored
     * @return Menu, never null
     */
    @Override
    public abstract JPopupMenu getPopupMenu(Widget arg0, Point arg1);

    /**
     * Get associated <tt>PoshElement</tt>
     *
     * @return associated data node
     */
    public T getDataNode() {
        return dataNode;
    }

    /**
     * Return PoshScene the widget is in.
     * @return PoshScene this widget belongs to
     */
    public PoshScene getPoshScene() {
        return (PoshScene) super.getScene();
    }

    /**
     * Get parent widget
     *
     * @return widget that is parent or null, if this widget is root
     */
    public PoshWidget<? extends PoshElement> getParent() {
        return parent;
    }

    /**
     * Small class that is used to show properties of element in the properties window.
     * To show properties, I need to set the node into lookup and the properties of the
     * node are shown.
     */
    protected static class PropertyNode extends AbstractNode {
        private PoshWidget widget;
        public PropertyNode(PoshElement element, PoshWidget widget) {
            super(Children.LEAF);
            this.widget = widget;
        }
        @Override
        protected Sheet createSheet() {
            return widget.createSheet();
        }
    }

    protected PropertyNode propertyNode = null;

    /**
     * Create Node.Sheet for poroperties of this widget.
     * @return
     */
    protected /*abstract*/ Sheet createSheet() {
        return Sheet.createDefault();
    }

    /**
     * Return Node that contains properties associated with the widget.
     * In most cases it s getDataNode, but it is useful to override
     * when widget represent more than one datanode
     *
     * @return node that has the properties we want to show in Properties widow
     */
    final protected Node getPropertiesNode() {
        if (propertyNode == null) {
            propertyNode = createPropertiesNode();
        }
        return propertyNode;
    }

    protected PropertyNode createPropertiesNode() {
        return new PropertyNode(getDataNode(), this);
    }

    /**
     * What should I do if I am selected(=left clicked)?
     * In this case, change Properties widow to properties of associated DataNode
     */
    public void select(Widget arg0, Point arg1, boolean arg2) {
        TopComponent tc = TopComponent.getRegistry().getActivated();

        getPoshScene().setFocusedWidget(this);

        if (tc != null) {
            tc.setActivatedNodes(new Node[]{getPropertiesNode()});
        }
    }

    /**
     * Set ConnectionWidget that connects this PoshWidget to its parent.
     * @return can be null, if connection hasn't been set yet.
     */
    public final ConnectionWidget getConnection() {
        return this.connection;
    }

    @Override
    public String toString() {
        return this.getType().toString() + " " + headlineString;
    }

    /**
     * Is this node collapsed (children won't be seen)
     * @return True if this node is collapsed
     */
    public boolean isCollapsed() {
        return this.isCollapsed;
    }

    /**
     * Collapse or uncollapse the tree
     */
    public void setCollapsed(boolean newCollapsedState) {
        isCollapsed = newCollapsedState;

        this.getPoshScene().consolidate();
    }

    protected void doRepaint() {
        //The Nodes API can fire events outside the AWT Thread
        if (SwingUtilities.isEventDispatchThread()) {
            repaint();
            getScene().validate();
            //required or repaint() doesnâ€™t work
        } else {
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    repaint();
                    getScene().validate();
                }
            });
        }
    }
    public final int width = 150;
    public final int height = 35;
    private final int arrowWidth = 10;
    private static Font boldFont = new Font("Helvetica", Font.BOLD, 12);
    private static Font italicFont = new Font("Helvetica", Font.ITALIC, 10);

    @Override
    protected Rectangle calculateClientArea() {
        return new Rectangle(0, 0, width, height);
    }

    @Override
    protected void paintWidget() {
        Graphics2D g = getGraphics();

        // draw rectanlge
        g.setColor(getType().getColor());
        g.fillRect(0, 0, width, height);


        int collapseWidth = 0;
        // draw collapse block
        if (!getChildNodes().isEmpty()) {
            collapseWidth = arrowWidth;
            g.setColor(getType().getColor().darker());

            if (isCollapsed()) {
                g.fillPolygon(
                        new int[]{width - arrowWidth, width, width - arrowWidth},
                        new int[]{0, height / 2, height},
                        3);
            } else {
                g.fillPolygon(
                        new int[]{width, width - arrowWidth, width},
                        new int[]{0, height / 2, height},
                        3);
            }
        }
        g.setColor(Color.BLACK);
        int xofs = 4;

        int maxTextWidth = width - xofs - collapseWidth;
        // draw headline text
        FontMetrics boldMetrics = g.getFontMetrics(boldFont);
        String renderedHeadlineString = getFittingString(getHeadlineText(), boldMetrics, maxTextWidth);

        g.setFont(boldFont);
        g.drawString(renderedHeadlineString, xofs, boldMetrics.getHeight());

        // draw comment string
        FontMetrics italicMetrics = g.getFontMetrics(italicFont);
        String renderCommentString = getFittingString(getCommentText(), italicMetrics, maxTextWidth);

        g.setFont(italicFont);
        g.drawString(renderCommentString, xofs, boldMetrics.getHeight() + italicMetrics.getHeight());
    }

    /**
     * Get string that will be shorter than maxWidth. If Passed text is too long
     * return part of text with "..." at the end.
     * @param text Text we want to fit into maxWidth
     * @param metrics font metrics to measure length of strings
     * @param maxWidth maximal length the returned string can fit into.
     * @return text if it fits into maxWidth, otherwise maximal text.substring(0,X).concat("...")
     *              that will fit into maxWidth.
     */
    private String getFittingString(String text, FontMetrics metrics, int maxWidth) {
        if (metrics.stringWidth(text) < maxWidth) {
            return text;
        }

        for (int index = text.length() - 1; index > 0; index--) {
            String shorter = text.substring(0, index).concat("...");
            if (metrics.stringWidth(shorter) < maxWidth) {
                return shorter;
            }
        }
        return "...";
    }
}
