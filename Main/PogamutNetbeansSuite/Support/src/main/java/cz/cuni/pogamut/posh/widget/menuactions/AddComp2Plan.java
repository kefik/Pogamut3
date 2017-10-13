package cz.cuni.pogamut.posh.widget.menuactions;

import cz.cuni.amis.pogamut.sposh.elements.*;
import cz.cuni.amis.pogamut.sposh.exceptions.CycleException;
import cz.cuni.amis.pogamut.sposh.exceptions.DuplicateNameException;
import cz.cuni.pogamut.posh.widget.kidview.AbstractMenuAction;
import java.awt.event.ActionEvent;
import java.text.MessageFormat;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.Exceptions;

/**
 * Menu action for adding a new competence into a plan.
 *
 * @author HonzaH
 */
public class AddComp2Plan extends AbstractMenuAction<PoshPlan> {

    public AddComp2Plan(PoshPlan plan) {
        super("Add competence node", plan);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String competenceName = getIdentifierFromDialog("Name of new competence");
        if (competenceName == null) {
            return;
        }

        String elementName = getIdentifierFromDialog("Name of first choice");
        if (elementName == null) {
            return;
        }
        try {
            dataNode.addCompetence(LapElementsFactory.createCompetence(competenceName, elementName));
        } catch (DuplicateNameException ex) {
            errorDialog(ex.getMessage());
        } catch (CycleException ex) {
            errorDialog(ex.getMessage());
        }
    }
}
