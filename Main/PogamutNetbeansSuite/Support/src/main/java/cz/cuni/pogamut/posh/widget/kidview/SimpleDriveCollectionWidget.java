package cz.cuni.pogamut.posh.widget.kidview;

import cz.cuni.amis.pogamut.sposh.elements.*;
import cz.cuni.amis.pogamut.sposh.exceptions.DuplicateNameException;
import cz.cuni.amis.pogamut.sposh.exceptions.InvalidNameException;
import cz.cuni.pogamut.posh.widget.accept.AbstractAcceptAction;
import cz.cuni.pogamut.posh.widget.PoshNodeType;
import cz.cuni.pogamut.posh.widget.PoshScene;
import cz.cuni.pogamut.posh.widget.PoshWidget;
import cz.cuni.pogamut.posh.widget.accept.AcceptDrive2DC;
import cz.cuni.pogamut.posh.widget.accept.AcceptGoal2DC;
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
 * Widget representing DriveCollection in KidView.
 *
 * @author Honza
 */
public class SimpleDriveCollectionWidget extends NamedBasicWidget<DriveCollection> {

    public SimpleDriveCollectionWidget(PoshScene scene, DriveCollection associatedDataNode, PoshWidget<? extends PoshElement> parent) {
        super(scene, associatedDataNode, parent);

        getActions().addAction(ActionFactory.createInplaceEditorAction(
                new DCInplaceEditor(associatedDataNode)));
    }

    /**
     * Inplace editor for changing the name of the DC
     */
    private static class DCInplaceEditor implements TextFieldInplaceEditor {

        private DriveCollection dc;

        private DCInplaceEditor(DriveCollection dc) {
            this.dc = dc;
        }

        @Override
        public boolean isEnabled(Widget widget) {
            return true;
        }

        @Override
        public String getText(Widget widget) {
            return dc.getName();
        }

        @Override
        public void setText(Widget widget, String newDCName) {
            try {
                dc.setName(newDCName);
            } catch (InvalidNameException ex) {
                // ignore invalid names, don't change the name
                // XXX: Maybe add notification with IDENT pattern?
            }
        }
    }

    @Override
    protected PoshNodeType getType() {
        return PoshNodeType.DRIVE_COLLECTION_NODE;
    }

    @Override
    protected List<AbstractMenuAction> createMenuActions() {
        LinkedList<AbstractMenuAction> list = new LinkedList<AbstractMenuAction>();

        list.add(new AbstractMenuAction<DriveCollection>("Add drive", getDataNode(), this) {

            @Override
            public void actionPerformed(ActionEvent e) {
                String driveName = this.getIdentifierFromDialog("Name of the drive.");
                if (driveName == null) {
                    return;
                }
                try {
                    DriveElement drive = LapElementsFactory.createDriveElement(driveName);
                    // XXX: hack for 3.3.31, wrong factory method
                    drive.getTriggeredAction().setActionName(SimpleRoleActionWidget.DEFAULT_ACTION);
                    for (Sense sense  : drive.getTriggers().getSenses()) {
                        sense.setSenseName(SimpleSenseWidget.DEFAULT_SUCCEED_SENSE);
                    }
                    dataNode.addDrive(drive);
                } catch (DuplicateNameException ex) {
                    errorDialog(ex.getMessage());
                }
            }
        });

        return list;
    }

    /**
     * Create proper widget from child data node.
     *
     * @param dataNode DataNode that was added as child to associated data node
     */
    @Override
    protected void addChildWidget(PoshElement dataNode) {
        if (dataNode instanceof Goal) {
            SimpleGoalWidget goalWidget = new SimpleGoalWidget(getPoshScene(), (Goal) dataNode, this);
            this.getChildNodes().add(0, goalWidget);
            this.getPoshScene().addPoshWidget(goalWidget, true);
        } else if (dataNode instanceof DriveElement) {
            SimpleDriveElementWidget driveElementWidget = new SimpleDriveElementWidget(getPoshScene(), (DriveElement) dataNode, this);
            this.getChildNodes().add(driveElementWidget);
            this.getPoshScene().addPoshWidget(driveElementWidget, true);
        } else {
            throw new RuntimeException("Unexpected child class " + dataNode.getClass().getName());
        }
    }

    @Override
    public void elementPropertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(DriveCollection.dcName)) {
            this.setHeadlineText((String) evt.getNewValue());
            this.doRepaint();
        }
    }

    @Override
    protected List<AbstractAcceptAction> getAcceptProviders() {
        List<AbstractAcceptAction> list = new LinkedList<AbstractAcceptAction>();

        list.add(new AcceptDrive2DC(getDataNode()));
        list.add(new AcceptGoal2DC(getDataNode()));

        return list;
    }

    @Override
    protected Sheet createSheet() {
        Sheet sheet = Sheet.createDefault();
        Sheet.Set set = Sheet.createPropertiesSet();
        sheet.put(set);

        try {
            Property collectionNameProp = new PropertySupport.Reflection<String>(getDataNode(), String.class, "getDriveCollectionName", "setDriveCollectionName");

            collectionNameProp.setName(DriveCollection.dcName);
            collectionNameProp.setDisplayName("Name of drive collection");

            set.put(collectionNameProp);
        } catch (NoSuchMethodException ex) {
            Exceptions.printStackTrace(ex);
        }
        return sheet;
    }
}
