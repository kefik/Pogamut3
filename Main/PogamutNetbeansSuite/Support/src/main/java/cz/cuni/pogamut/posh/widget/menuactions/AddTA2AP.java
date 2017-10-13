package cz.cuni.pogamut.posh.widget.menuactions;

import cz.cuni.amis.pogamut.sposh.elements.ActionPattern;
import cz.cuni.amis.pogamut.sposh.elements.TriggeredAction;
import cz.cuni.pogamut.posh.widget.kidview.AbstractMenuAction;
import java.awt.event.ActionEvent;

/**
 * Create new TA from dialog and add it to AP.
 * @author Honza
 */
public class AddTA2AP extends AbstractMenuAction<ActionPattern> {

    public AddTA2AP(ActionPattern ap) {
        super("Add action", ap);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String newAction = this.getIdentifierFromDialog("Add triggered action");
        if (newAction != null) {
            dataNode.addTriggeredAction(new TriggeredAction(newAction));
        }
    }
}
