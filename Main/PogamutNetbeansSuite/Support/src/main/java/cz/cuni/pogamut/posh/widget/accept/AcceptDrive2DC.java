package cz.cuni.pogamut.posh.widget.accept;

import cz.cuni.amis.pogamut.sposh.elements.DriveCollection;
import cz.cuni.amis.pogamut.sposh.elements.DriveElement;
import cz.cuni.amis.pogamut.sposh.exceptions.DuplicateNameException;
import org.openide.NotifyDescriptor;

/**
 * What to do when drive is dropped on drive collection node. Add new drive as
 * last child.
 *
 * @author Honza
 */
public class AcceptDrive2DC extends AbstractAcceptAction<DriveElement, DriveCollection> {

    public AcceptDrive2DC(DriveCollection dataNode) {
        super(DriveElement.dataFlavor, dataNode);
    }

    @Override
    protected void performAction(DriveElement drive) {
        try {
            dataNode.addDrive(drive);
        } catch (DuplicateNameException ex) {
            displayMessage(ex.getMessage(), NotifyDescriptor.ERROR_MESSAGE);
        }
    }
}
