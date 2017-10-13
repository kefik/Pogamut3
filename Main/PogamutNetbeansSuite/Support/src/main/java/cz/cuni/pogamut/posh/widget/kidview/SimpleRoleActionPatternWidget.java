package cz.cuni.pogamut.posh.widget.kidview;

import cz.cuni.amis.pogamut.sposh.PoshTreeEvent;
import cz.cuni.amis.pogamut.sposh.elements.ActionPattern;
import cz.cuni.amis.pogamut.sposh.elements.PoshElement;
import cz.cuni.amis.pogamut.sposh.elements.TriggeredAction;
import cz.cuni.pogamut.posh.widget.accept.AbstractAcceptAction;
import cz.cuni.pogamut.posh.widget.*;
import cz.cuni.pogamut.posh.widget.accept.AcceptAP2TA;
import cz.cuni.pogamut.posh.widget.accept.AcceptComp2TA;
import cz.cuni.pogamut.posh.widget.accept.AcceptTA2TA;
import cz.cuni.pogamut.posh.widget.menuactions.AddTA2AP;
import cz.cuni.pogamut.posh.widget.menuactions.DeleteNodeAction;
import java.beans.PropertyChangeEvent;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import org.netbeans.api.visual.action.ActionFactory;
import org.netbeans.api.visual.action.TextFieldInplaceEditor;
import org.netbeans.api.visual.widget.Widget;
import org.openide.nodes.Node.Property;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.Exceptions;

/**
 * Widget that is showing as AP, but it is a triggered action. TriggerAction can
 * be a multiple things and this widget is representing it as action pattern.
 *
 * @author Honza
 */
public class SimpleRoleActionPatternWidget extends NamedBasicWidget<TriggeredAction> {

    ActionPattern apNode;

    public SimpleRoleActionPatternWidget(PoshScene poshScene, TriggeredAction dataNode, PoshWidget<? extends PoshElement> parent, ActionPattern apNode) {
        super(poshScene, dataNode, parent);

        this.apNode = apNode;
        this.apNode.addElementListener(this);

        getActions().addAction(ActionFactory.createInplaceEditorAction(
                new APInplaceEditor(dataNode, apNode)));
    }

    /**
     * Create inplace editor that changes name of action patten and the action
     */
    protected static class APInplaceEditor implements TextFieldInplaceEditor {

        private TriggeredAction action;
        private ActionPattern ap;

        protected APInplaceEditor(TriggeredAction action, ActionPattern ap) {
            this.action = action;
            this.ap = ap;
        }

        @Override
        public boolean isEnabled(Widget widget) {
            return true;
        }

        @Override
        public String getText(Widget widget) {
            return action.getName();
        }

        @Override
        public void setText(Widget widget, String string) {
            String apName = ap.getName();
            String actionName = action.getName();
            try {
                ap.setName(string);
                action.setActionName(string);
            } catch (Exception ex) {
                try {
                    action.setActionName(actionName);
                    ap.setName(apName);
                } catch (Exception ex1) {
                    // Shouldn't happen, but if it doesn, show exception
                    Exceptions.printStackTrace(ex1);
                }
            }
        }
    }

    @Override
    protected PropertyNode createPropertiesNode() {
        return new PropertyNode(apNode, this);
    }

    /**
     * Remove all widget children of Ap and add them.
     */
    public void regenerate() {
        deleteChildrenWidgets();
        createCh();
    }

    /**
     * Take all direct decending children widgets of this widget and delete them
     * from the scene.
     */
    private void deleteChildrenWidgets() {
        for (PoshWidget widget : getChildNodes()) {
            getPoshScene().deletePoshWidget(widget);
        }
    }

    /**
     * Create children of AP. In most widgets, one widget is representing one
     * node. This one is representing AP and this function is regenerating AP
     * structure to widgets.
     */
    private void createCh() {
        Queue<PoshWidget<? extends PoshElement>> fringe = new LinkedList<PoshWidget<? extends PoshElement>>(createWidgetChildren(this, apNode));

        while (!fringe.isEmpty()) {
            PoshWidget<? extends PoshElement> headWidget = fringe.poll();
            PoshElement headDataNode = headWidget.getDataNode();

            if (!(headDataNode instanceof TriggeredAction)) {
                fringe.addAll(createWidgetChildren(headWidget, headDataNode));
            }
        }
    }

    /**
     * Take all children of data node dn and notify widget w that every child of
     * data node is its child.
     *
     * @param w Widget we are going to notify
     * @param dn datanode from which we will get childre
     * @return list of children of widget after it was notified about all
     * children of data node.
     */
    private List<PoshWidget<? extends PoshElement>> createWidgetChildren(PoshWidget<? extends PoshElement> w, PoshElement dn) {
        List<? extends PoshElement> dataChildren = dn.getChildDataNodes();

        for (PoshElement dnch : dataChildren) {
            w.nodeChanged(PoshTreeEvent.NEW_CHILD_NODE, dnch);
        }

        return w.getChildNodes();
    }

    @Override
    protected PoshNodeType getType() {
        return PoshNodeType.ACTION_PATTERN;
    }

    @Override
    protected List<AbstractMenuAction> createMenuActions() {
        LinkedList<AbstractMenuAction> list = new LinkedList<AbstractMenuAction>();

        list.add(new AddTA2AP(apNode));

        // add delete action, if at least one action is available
        int numTAs = getDataNode().getParent().getNumberOfChildInstances(TriggeredAction.class);

        if (numTAs > 1) {
            list.add(new DeleteNodeAction<TriggeredAction>(
                    "Delete action", getDataNode()));
        }

        return list;
    }

    /**
     * Create proper widget from child data node.
     *
     * @param dataNode DataNode that was added as child to associated data node
     */
    @Override
    protected void addChildWidget(PoshElement dataNode) {
        if (dataNode instanceof TriggeredAction) {
            addTriggeredActionWidgets((TriggeredAction) dataNode);
        } else {
            throw new RuntimeException("Child of this type not permitted: " + dataNode.getClass().getName());
        }
    }

    @Override
    public void elementPropertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(TriggeredAction.taName)) {
            this.changeTriggeredActionWidgets(getDataNode());
            this.doRepaint();
        }
        if (evt.getPropertyName().equals(ActionPattern.apName)) {
            this.changeTriggeredActionWidgets(getDataNode());
            this.doRepaint();
        }
    }

    @Override
    protected List<AbstractAcceptAction> getAcceptProviders() {
        List<AbstractAcceptAction> list = new LinkedList<AbstractAcceptAction>();

        list.add(new AcceptTA2TA(getDataNode()));
        list.add(new AcceptAP2TA(getDataNode()));
        list.add(new AcceptComp2TA(getDataNode()));

        return list;
    }

    @Override
    protected void deleteWidgetFromScene(PoshElement dataNode) {
        super.deleteWidgetFromScene(dataNode);
        apNode.removeElementListener(this);
    }

    @Override
    protected Sheet createSheet() {
        Sheet sheet = Sheet.createDefault();
        Sheet.Set set = Sheet.createPropertiesSet();
        sheet.put(set);

        try {
            Property nameProp = new PropertySupport.Reflection<String>(apNode, String.class, "getNodeName", "setNodeName");
            nameProp.setName(ActionPattern.apName);
            nameProp.setDisplayName("Name of action pattern");

            Property commentProp = new PropertySupport.Reflection<String>(apNode, String.class, "getNodeComment", "setNodeComment");
            commentProp.setName(ActionPattern.apComment);
            commentProp.setDisplayName("Comment about node");
            commentProp.setShortDescription("It is difficult to keep track about what part of POSH plan does what and that is where comments come in.");

            set.put(new Property[]{nameProp, commentProp});
        } catch (NoSuchMethodException ex) {
            Exceptions.printStackTrace(ex);
        }
        return sheet;
    }
}
