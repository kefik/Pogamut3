package cz.cuni.pogamut.posh.widget.menuactions;

import cz.cuni.amis.pogamut.sposh.elements.Goal;
import cz.cuni.amis.pogamut.sposh.elements.Sense;
import cz.cuni.pogamut.posh.widget.kidview.AbstractMenuAction;
import java.awt.event.ActionEvent;

/**
 *
 * @author Honza
 */
public class AddSense2Goal extends AbstractMenuAction<Goal> {

    public AddSense2Goal(Goal goal) {
        super("Add sense", goal);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String newSense = getIdentifierFromDialog("Name of new sense");
        if (newSense != null) {
            this.dataNode.addUserSense(new Sense(newSense));
        }
    }
}

