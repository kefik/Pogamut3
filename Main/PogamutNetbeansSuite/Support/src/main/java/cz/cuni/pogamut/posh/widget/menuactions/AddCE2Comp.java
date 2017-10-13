package cz.cuni.pogamut.posh.widget.menuactions;

import cz.cuni.amis.pogamut.sposh.elements.Competence;
import cz.cuni.amis.pogamut.sposh.elements.LapElementsFactory;
import cz.cuni.amis.pogamut.sposh.exceptions.DuplicateNameException;
import cz.cuni.pogamut.posh.widget.kidview.AbstractMenuAction;
import java.awt.event.ActionEvent;
import java.text.MessageFormat;

/**
 * Create a CompetencePriorityElement and add it as a child of Competence. TODO:
 * Proper dialog.
 *
 * @author Honza
 */
public class AddCE2Comp extends AbstractMenuAction<Competence> {

    public AddCE2Comp(Competence comp) {
        super("Add priority element", comp);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String name = getIdentifierFromDialog("Name of competence element");
        if (name == null) {
            return;
        }
        try {
            dataNode.addElement(LapElementsFactory.createCompetenceElement(name));
        } catch (DuplicateNameException ex) {
            errorDialog(MessageFormat.format("Name '%s' is already used.", name));
        }
    }
}
