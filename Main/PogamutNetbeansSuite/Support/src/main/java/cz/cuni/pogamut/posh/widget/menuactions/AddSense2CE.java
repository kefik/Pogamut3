package cz.cuni.pogamut.posh.widget.menuactions;

import cz.cuni.amis.pogamut.sposh.elements.CompetenceElement;
import cz.cuni.amis.pogamut.sposh.elements.Sense;
import cz.cuni.pogamut.posh.widget.kidview.AbstractMenuAction;
import java.awt.event.ActionEvent;

/**
 *
 * @author Honza
 */
public class AddSense2CE extends AbstractMenuAction<CompetenceElement> {

    public AddSense2CE(CompetenceElement competenceElement) {
        super("Add sense", competenceElement);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String newSense = getIdentifierFromDialog("Name of new sense");
        if (newSense != null) {
            this.dataNode.addUserTrigger(new Sense(newSense));
        }
    }
}