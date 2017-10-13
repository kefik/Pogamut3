package cz.cuni.pogamut.posh.widget.kidview;

import cz.cuni.amis.pogamut.sposh.PoshTreeEvent;
import cz.cuni.amis.pogamut.sposh.elements.*;
import cz.cuni.amis.pogamut.sposh.exceptions.DuplicateNameException;
import cz.cuni.pogamut.posh.widget.accept.AbstractAcceptAction;
import cz.cuni.pogamut.posh.widget.PoshNodeType;
import cz.cuni.pogamut.posh.widget.PoshScene;
import cz.cuni.pogamut.posh.widget.PoshWidget;
import cz.cuni.pogamut.posh.widget.accept.AcceptAP2TA;
import cz.cuni.pogamut.posh.widget.accept.AcceptComp2TA;
import cz.cuni.pogamut.posh.widget.accept.AcceptTA2TA;
import cz.cuni.pogamut.posh.widget.menuactions.DeleteNodeAction;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import org.netbeans.api.visual.action.ActionFactory;
import org.netbeans.api.visual.action.TextFieldInplaceEditor;
import org.netbeans.api.visual.widget.Widget;
import org.openide.nodes.Node.Property;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.Exceptions;

/**
 * Widget that is showing as competence, but its associated data node is a triggered action.
 * TriggerAction can be a multiple things and this widget is representing it as
 * competence.
 *
 * Because competence has multiple levels of widgets, this widget is rather nasty.
 *
 * @author Honza
 */
public class SimpleRoleCompetenceWidget extends NamedBasicWidget<TriggeredAction> {

    Competence compNode;

    public SimpleRoleCompetenceWidget(PoshScene poshScene, TriggeredAction dataNode, PoshWidget<? extends PoshElement> parent, Competence compNode) {
        super(poshScene, dataNode, parent);

        this.compNode = compNode;
        this.compNode.addElementListener(this);

        getActions().addAction(ActionFactory.createInplaceEditorAction(
                new CompInplaceEditor(dataNode, compNode)));
    }

    /**
     * Create inplace editor that changes name of competence and the action
     */
    protected static class CompInplaceEditor implements TextFieldInplaceEditor {

        private TriggeredAction action;
        private Competence competence;

        protected CompInplaceEditor(TriggeredAction action, Competence competence) {
            this.action = action;
            this.competence = competence;
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
            String compName = competence.getName();
            String actionName = action.getName();
            try {
                competence.setName(string);
                action.setActionName(string);
            } catch (Exception ex) {
                try {
                    action.setActionName(actionName);
                    competence.setName(compName);
                } catch (Exception ex1) {
                    // shouldn't ever happen, but if it doesn, show exception
                    Exceptions.printStackTrace(ex1);
                }
            }
        }
    }

    @Override
    protected PropertyNode createPropertiesNode() {
        return new PropertyNode(compNode, this);
    }

    /**
     * Remove all children widgers of this widget and
     * recreate widget children of this widget so user will see whole
     * competence instead of just one widget for action.
     */
    public void regenerate() {
        deleteChildrenWidgets();
        createCh();
    }

    /**
     * remove all widget children of this widget.
     */
    private void deleteChildrenWidgets() {
        for (PoshWidget widget : getChildNodes()) {
            getPoshScene().deletePoshWidget(widget);
        }
    }

    /**
     * Take a competence node that is connected to the triggered action
     * that is associated with this widget and create children so it looks
     * like this widget is root of competence.
     *
     * In most widgets, one widget is representing one node.
     * This one is representing competence and this function is regenerating competence structure to widgets.
     */
    private void createCh() {
        Queue<PoshElement> fringe = new LinkedList<PoshElement>();
        fringe.add(this.compNode);

        while (!fringe.isEmpty()) {
            PoshElement head = fringe.poll();

            Set<PoshElementListener> listeners = head.getElementListeners();

            for (PoshElementListener listener : listeners) {
                if (listener instanceof PoshWidget) {
                    PoshWidget widget = (PoshWidget) listener;
                    if (widget.isAncestor(this)) {
                        for (PoshElement child : head.getChildDataNodes()) {
                            widget.nodeChanged(PoshTreeEvent.NEW_CHILD_NODE, child);
                        }
                    }
                }
            }

            fringe.addAll(head.getChildDataNodes());
        }

    }

    @Override
    protected PoshNodeType getType() {
        return PoshNodeType.COMPETENCE;
    }

    @Override
    protected List<AbstractMenuAction> createMenuActions() {
        LinkedList<AbstractMenuAction> list = new LinkedList<AbstractMenuAction>();

        list.add(new AbstractMenuAction<Competence>("Add competence element", compNode) {

            @Override
            public void actionPerformed(ActionEvent e) {
                String newElement = this.getIdentifierFromDialog("Name of new choice");
                if (newElement == null) {
                    return;
                }

                try {
                    CompetenceElement element = LapElementsFactory.createCompetenceElement(newElement);
                    dataNode.addElement(element);
                } catch (DuplicateNameException ex) {
                    errorDialog(ex.getMessage());
                }
            }
        });

        // add delete action, if at least one action is available
        int numCEs = getDataNode().getParent().getNumberOfChildInstances(TriggeredAction.class);

        if (numCEs > 1) {
            list.add(new DeleteNodeAction<TriggeredAction>(
                    "Delete element", getDataNode()));
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
        if (dataNode instanceof Goal) {
            SimpleGoalWidget goalWidget =
                    new SimpleGoalWidget(getPoshScene(), (Goal) dataNode, this);

            this.getChildNodes().add(0, goalWidget);
            this.getPoshScene().addPoshWidget(goalWidget, true);
        } else if (dataNode instanceof CompetenceElement) {
            SimpleCompetenceElementWidget compElemWidget =
                    new SimpleCompetenceElementWidget(getPoshScene(), (CompetenceElement) dataNode, this);

            this.getChildNodes().add(compElemWidget);
            this.getPoshScene().addPoshWidget(compElemWidget, true);
        }
    }

    @Override
    public void elementPropertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(TriggeredAction.taName)) {
            this.changeTriggeredActionWidgets(getDataNode());
            this.doRepaint();
        }
        if (evt.getPropertyName().equals(Competence.cnName)) {
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
        compNode.removeElementListener(this);
    }
    
    @Override
    protected Sheet createSheet() {
        Sheet sheet = Sheet.createDefault();
        Sheet.Set set = Sheet.createPropertiesSet();
        sheet.put(set);

        try {

            Property nameProp = new PropertySupport.Reflection<String>(compNode, String.class, "getNodeName", "setNodeName");
            nameProp.setName(Competence.cnName);
            nameProp.setDisplayName("Name of competence node");
            
            set.put(new Property[]{nameProp});
        } catch (NoSuchMethodException ex) {
            Exceptions.printStackTrace(ex);
        }

        return sheet;
    }
}
