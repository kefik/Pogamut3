package cz.cuni.pogamut.posh.widget.menuactions;

import cz.cuni.amis.pogamut.sposh.elements.Sense;
import cz.cuni.amis.pogamut.sposh.elements.Triggers;
import cz.cuni.pogamut.posh.widget.kidview.AbstractMenuAction;
import java.awt.event.ActionEvent;

/**
 *
 * @author Honza
 */
public class AddSense2Trigger extends AbstractMenuAction<Triggers> {

    public AddSense2Trigger(Triggers trigger) {
        super("Add sense", trigger);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String newSense = getIdentifierFromDialog("Name of new sense");
        if (newSense != null) {
            this.dataNode.addUserTrigger(new Sense(newSense));
        }
    }
}
