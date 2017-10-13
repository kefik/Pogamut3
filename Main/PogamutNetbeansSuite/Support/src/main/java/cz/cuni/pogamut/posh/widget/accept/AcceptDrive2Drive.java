package cz.cuni.pogamut.posh.widget.accept;

import cz.cuni.amis.pogamut.sposh.elements.DriveCollection;
import cz.cuni.amis.pogamut.sposh.elements.DriveElement;
import cz.cuni.amis.pogamut.sposh.exceptions.DuplicateNameException;
import org.openide.NotifyDescriptor;

/**
 * AcceptProvider when {@link DriveElement} is dropped on the {@link SimpleDriveElementWidget}.
 * Add it in the list before DrivePriorityElement this widget is representing
 */
public class AcceptDrive2Drive extends AbstractAcceptAction<DriveElement, DriveElement> {

    public AcceptDrive2Drive(DriveElement drive) {
        super(DriveElement.dataFlavor, drive, null);
    }

    @Override
    protected void performAction(DriveElement newDrive) {
        DriveCollection dc = (DriveCollection) dataNode.getParent();
        try {
            dc.addDrive(newDrive);
        } catch (DuplicateNameException ex) {
            displayMessage(ex.getMessage(), NotifyDescriptor.ERROR_MESSAGE);
            return;
        }

        int newlyAddedElementIndex =
                getIndexInList(dc.getChildDataNodes(), newDrive);

        // find index, where should dropped sense be =
        //    index of sense new sense was dropped on
        int assignedCElementIndex =
                getIndexInList(dc.getChildDataNodes(), dataNode);

        int relativePositionChange =
                assignedCElementIndex - newlyAddedElementIndex;

        dc.moveChild(newDrive, relativePositionChange);
    }
}
