package cz.cuni.pogamut.posh.widget.kidview;

import cz.cuni.amis.pogamut.sposh.elements.DriveElement;
import cz.cuni.amis.pogamut.sposh.elements.Freq.FreqUnits;
import cz.cuni.amis.pogamut.sposh.elements.PoshElement;
import cz.cuni.amis.pogamut.sposh.elements.TriggeredAction;
import cz.cuni.amis.pogamut.sposh.elements.Triggers;
import cz.cuni.amis.pogamut.sposh.exceptions.UnexpectedElementException;
import cz.cuni.pogamut.posh.widget.accept.AbstractAcceptAction;
import cz.cuni.pogamut.posh.widget.PoshNodeType;
import cz.cuni.pogamut.posh.widget.PoshScene;
import cz.cuni.pogamut.posh.widget.PoshWidget;
import cz.cuni.pogamut.posh.widget.accept.AcceptDrive2Drive;
import cz.cuni.pogamut.posh.widget.menuactions.DeleteNodeAction;
import java.beans.PropertyChangeEvent;
import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.List;
import org.netbeans.api.visual.action.ActionFactory;
import org.netbeans.api.visual.action.TextFieldInplaceEditor;
import org.netbeans.api.visual.widget.Widget;
import org.openide.nodes.Node;
import org.openide.nodes.Node.Property;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.Exceptions;

/**
 * This is representation of DrivePriorityElement, but it has appearance of
 * DriveElement to save screen space, because we allow only one DriveElement in
 * DrivePriorityElement for now.
 *
 * Basically this widget is representing DrivePriorityElement and if the
 * DrivePriorityElement has DriveElement child, this widget represents it too.
 *
 * @author Honza
 */
class SimpleDriveElementWidget extends NamedBasicWidget<DriveElement> {

    SimpleDriveElementWidget(PoshScene scene, DriveElement associatedDataNode, PoshWidget<? extends PoshElement> parent) {
        super(scene, associatedDataNode, parent);
        
        getActions().addAction(ActionFactory.createInplaceEditorAction(new DriveInplaceEditor(associatedDataNode)));
    }


    protected static class DriveInplaceEditor implements TextFieldInplaceEditor {
        private DriveElement drive;
        protected DriveInplaceEditor(DriveElement drive) {
            this.drive = drive;
        }

        @Override
        public boolean isEnabled(Widget widget) {
            return true;
        }

        @Override
        public String getText(Widget widget) {
            return drive.getName();
        }

        @Override
        public void setText(Widget widget, String string) {
            try {
                drive.setDriveName(string);
            } catch (IllegalArgumentException ex) {
                // ignore, just don't change anything
                // XXX: Maybe add user notification with IDENT pattern?
            }
        }

    }

    @Override
    protected PoshNodeType getType() {
        return PoshNodeType.DRIVE_ELEMENT;
    }

    @Override
    protected List<AbstractMenuAction> createMenuActions() {
        LinkedList<AbstractMenuAction> list = new LinkedList<AbstractMenuAction>();

        // if more than one drive present, allow deletion
        int numDrives = getDataNode().getParent().getNumberOfChildInstances(DriveElement.class);

        if (numDrives > 1) {
            list.add(new DeleteNodeAction<DriveElement>("Delete drive", getDataNode()));
        }
        return list;
    }

    /**
     * Create proper child widget of this widget from child data node.
     *
     * @param dataNode DataNode that was added as child to associated data node
     */
    @Override
    protected void addChildWidget(PoshElement dataNode) {
        if (dataNode instanceof Triggers) {
            SimpleTriggersWidget triggersWidget = new SimpleTriggersWidget(getPoshScene(), (Triggers) dataNode, this);

            this.getChildNodes().add(triggersWidget);
            this.getPoshScene().addPoshWidget(triggersWidget, true);
        } else if (dataNode instanceof TriggeredAction) {
            addTriggeredActionWidgets((TriggeredAction) dataNode);
        } else {
            String msg = MessageFormat.format("Drive {0} got new child: {1} but no such class accepted.", getDataNode().getName(), dataNode.getClass().getSimpleName());
            throw new UnexpectedElementException(msg);
        }
    }

    @Override
    public void elementPropertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(DriveElement.deName)) {
            this.setHeadlineText((String) evt.getNewValue());
            this.doRepaint();
        }
    }

    @Override
    protected List<AbstractAcceptAction> getAcceptProviders() {
        List<AbstractAcceptAction> list = new LinkedList<AbstractAcceptAction>();

        list.add(new AcceptDrive2Drive(getDataNode()));

        return list;
    }

    @Override
    protected Sheet createSheet() {
        Sheet sheet = Sheet.createDefault();
        Sheet.Set set = Sheet.createPropertiesSet();
        sheet.put(set);

        try {
            Node.Property nameProp = new PropertySupport.Reflection<String>(getDataNode(), String.class, "getName", "setName");
            nameProp.setName(DriveElement.deName);
            nameProp.setDisplayName("Name of drive element");
            set.put(new Property[]{nameProp});
        } catch (NoSuchMethodException ex) {
            Exceptions.printStackTrace(ex);
        }
        return sheet;
    }
}
