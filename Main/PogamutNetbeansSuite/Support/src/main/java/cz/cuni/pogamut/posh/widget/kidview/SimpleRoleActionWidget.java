package cz.cuni.pogamut.posh.widget.kidview;

import cz.cuni.amis.pogamut.sposh.elements.PoshElement;
import cz.cuni.amis.pogamut.sposh.elements.TriggeredAction;
import cz.cuni.pogamut.posh.widget.accept.AbstractAcceptAction;
import cz.cuni.pogamut.posh.widget.PoshNodeType;
import cz.cuni.pogamut.posh.widget.PoshScene;
import cz.cuni.pogamut.posh.widget.PoshWidget;
import cz.cuni.pogamut.posh.widget.accept.AcceptAP2TA;
import cz.cuni.pogamut.posh.widget.accept.AcceptComp2TA;
import cz.cuni.pogamut.posh.widget.accept.AcceptTA2TA;
import cz.cuni.pogamut.posh.widget.menuactions.DeleteNodeAction;
import java.beans.PropertyChangeEvent;
import java.util.LinkedList;
import java.util.List;
import org.netbeans.api.visual.action.ActionFactory;
import org.netbeans.api.visual.action.TextFieldInplaceEditor;
import org.netbeans.api.visual.widget.Widget;
import org.openide.nodes.Node.Property;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.Exceptions;

/**
 * Representation of triggered action as a single action.
 *
 * @author Honza
 */
public class SimpleRoleActionWidget extends NamedBasicWidget<TriggeredAction> {

    public static final String DEFAULT_ACTION = "cz.cuni.amis.pogamut.sposh.ut2004.actions.DoNothing";
    
    public SimpleRoleActionWidget(PoshScene poshScene, TriggeredAction dataNode, PoshWidget<? extends PoshElement> parent) {
        super(poshScene, dataNode, parent);

        getActions().addAction(ActionFactory.createInplaceEditorAction(new ActionInplaceEditor(dataNode)));
    }


    protected static class ActionInplaceEditor implements TextFieldInplaceEditor {
        private TriggeredAction action;
        protected ActionInplaceEditor(TriggeredAction action) {
            this.action = action;
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
            try {
                action.setActionName(string);
            } catch (IllegalArgumentException ex) {
                // ignore, just don't change anything
                // XXX: Maybe add user notification with IDENT pattern?
            }
        }
    }

    @Override
    public String getHeadlineText() {
        String value = getPoshScene().getActionsFQNMapping().get(getDataNode().getName());
        if (value != null) return value;
        return getDataNode().getName();
    }
    
    @Override
    protected PoshNodeType getType() {
        return PoshNodeType.ACT;
    }

    @Override
    protected List<AbstractMenuAction> createMenuActions() {
        LinkedList<AbstractMenuAction> list = new LinkedList<AbstractMenuAction>();

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
     * @param dataNode DataNode that was added as child
     *                 to associated data node
     */
    @Override
    protected void addChildWidget(PoshElement dataNode) {
        throw new RuntimeException("No children accepted. Got " + dataNode.getClass().getName());
    }

    @Override
    public void elementPropertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(TriggeredAction.taName)) {
            this.changeTriggeredActionWidgets(getDataNode());
            this.doRepaint();
        } else {
            throw new RuntimeException("Unexpected property change: " + evt.getPropertyName());
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
    protected Sheet createSheet() {
        Sheet sheet = Sheet.createDefault();
        Sheet.Set set = Sheet.createPropertiesSet();
        sheet.put(set);

        try {
            Property nameProp = new PropertySupport.Reflection<String>(getDataNode(), String.class, "getActionName", "setActionName");
            nameProp.setName(TriggeredAction.taName);

            nameProp.setDisplayName("Name of the action");

            set.put(nameProp);
        } catch (NoSuchMethodException ex) {
            Exceptions.printStackTrace(ex);
        }
        return sheet;
    }
}
