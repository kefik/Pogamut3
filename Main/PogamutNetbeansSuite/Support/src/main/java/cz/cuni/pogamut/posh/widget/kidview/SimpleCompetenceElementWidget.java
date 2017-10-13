package cz.cuni.pogamut.posh.widget.kidview;

import cz.cuni.amis.pogamut.sposh.elements.*;
import cz.cuni.amis.pogamut.sposh.exceptions.UnexpectedElementException;
import cz.cuni.pogamut.posh.widget.accept.AbstractAcceptAction;
import cz.cuni.pogamut.posh.widget.PoshNodeType;
import cz.cuni.pogamut.posh.widget.PoshScene;
import cz.cuni.pogamut.posh.widget.PoshWidget;
import cz.cuni.pogamut.posh.widget.accept.AcceptCE2CE;
import cz.cuni.pogamut.posh.widget.accept.AcceptSense2CE;
import cz.cuni.pogamut.posh.widget.menuactions.DeleteNodeAction;
import java.awt.event.ActionEvent;
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
 * Widget representing Competence Element and its only possible child CompetenceElement
 * (so this widget represents two data nodes) in KidView.
 *
 * @author Honza
 */
public class SimpleCompetenceElementWidget extends NamedBasicWidget<CompetenceElement> {

    public SimpleCompetenceElementWidget(PoshScene poshScene, CompetenceElement dataNode, PoshWidget<? extends PoshElement> parent) {
        super(poshScene, dataNode, parent);
        getActions().addAction(ActionFactory.createInplaceEditorAction(new CompetenceElementInplaceEditor(dataNode)));
    }

    /**
     * Inplace editor for changing the name of the CompetenceElement in the enclosing widget
     */
    private static class CompetenceElementInplaceEditor implements TextFieldInplaceEditor {

        private CompetenceElement ce;

        private CompetenceElementInplaceEditor(CompetenceElement ce) {
            this.ce = ce;
        }

        @Override
        public boolean isEnabled(Widget widget) {
            return true;
        }

        @Override
        public String getText(Widget widget) {
            return ce.getName();
        }

        @Override
        public void setText(Widget widget, String newCEName) {
            ce.setName(newCEName);
        }
    }

    @Override
    protected PoshNodeType getType() {
        return PoshNodeType.COMPETENCE_ELEMENT;
    }

    @Override
    protected List<AbstractMenuAction> createMenuActions() {
        LinkedList<AbstractMenuAction> list = new LinkedList<AbstractMenuAction>();
        // XXX: Separate class + factory?
        list.add(new AbstractMenuAction<CompetenceElement>("Add trigger sense", getDataNode(), this) {

                @Override
                public void actionPerformed(ActionEvent e) {
                    String senseName = getIdentifierFromDialog("Name of new trigger");
                    if (senseName != null) {
                        dataNode.addTriggerAct(LapElementsFactory.createSense(senseName));
                    }
                }
            });

        int numEl = getDataNode().getParent().getNumberOfChildInstances(CompetenceElement.class);
        if (numEl > 1) {
            list.add(new DeleteNodeAction<CompetenceElement>("Delete choice",getDataNode()));
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
        if (dataNode instanceof Sense) {
            SimpleSenseWidget poshSenseWidget =
                    new SimpleSenseWidget(getPoshScene(), (Sense) dataNode, this);

            // correct order when adding
            int index = 0;
            for (PoshWidget pw : this.getChildNodes()) {
                if (!(pw instanceof SimpleSenseWidget)) {
                    break;
                }
                index++;
            }

            this.getChildNodes().add(0/*index*/, poshSenseWidget);
            this.getPoshScene().addPoshWidget(poshSenseWidget, true);

        } else if (dataNode instanceof TriggeredAction) {
            addTriggeredActionWidgets((TriggeredAction) dataNode);
        } else {
            throw new UnexpectedElementException("Not expecting " + dataNode.getClass().getSimpleName());
        }
    }

    @Override
    public void elementPropertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(CompetenceElement.caName)) {
            this.setHeadlineText((String) evt.getNewValue());
            this.doRepaint();
        }
    }

    @Override
    protected List<AbstractAcceptAction> getAcceptProviders() {
        List<AbstractAcceptAction> list = new LinkedList<AbstractAcceptAction>();

        list.add(new AcceptCE2CE(getDataNode()));
        list.add(new AcceptSense2CE(getDataNode()));

        return list;
    }

    @Override
    protected Sheet createSheet() {
        Sheet sheet = Sheet.createDefault();
        Sheet.Set set = Sheet.createPropertiesSet();
        sheet.put(set);

        try {
            Property nameProp = new PropertySupport.Reflection<String>(getDataNode(), String.class, "getName", "setName");
            nameProp.setName(CompetenceElement.caName);
            nameProp.setDisplayName("Name of competence atom");
            
            Property retriesProp = new PropertySupport.Reflection<Integer>(getDataNode(), Integer.class, "getRetries", "setRetries");
            retriesProp.setName(CompetenceElement.caRetries);
            retriesProp.setDisplayName("Number of retries");

            Property commentProp = new PropertySupport.Reflection<String>(getDataNode(), String.class, "getComment", "setComment");
            commentProp.setName(CompetenceElement.caComment);
            commentProp.setDisplayName("Comment");

            set.put(new Property[]{nameProp, retriesProp, commentProp});
        } catch (NoSuchMethodException ex) {
            Exceptions.printStackTrace(ex);
        }
        return sheet;
    }
}
