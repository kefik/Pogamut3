package cz.cuni.pogamut.posh.widget.menuactions;

import cz.cuni.amis.pogamut.sposh.elements.*;
import cz.cuni.amis.pogamut.sposh.exceptions.DuplicateNameException;
import cz.cuni.pogamut.posh.widget.kidview.AbstractMenuAction;
import java.awt.event.ActionEvent;

/**
 * Add DE to DC.
 *
 * @author Honza
 */
public class AddDPE2DC extends AbstractMenuAction<DriveCollection> {

    public AddDPE2DC(DriveCollection dc) {
        super("Add drive", dc);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String driveName = getIdentifierFromDialog("Name of drive element");
        if (driveName == null) {
            return;
        }
        try {
            dataNode.addDrive(LapElementsFactory.createDriveElement(driveName));
        } catch (DuplicateNameException ex) {
            errorDialog(ex.getMessage());
        }
    }
}
