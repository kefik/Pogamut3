package cz.cuni.pogamut.posh.widget.accept;

import cz.cuni.amis.pogamut.sposh.elements.Competence;
import cz.cuni.amis.pogamut.sposh.elements.PoshPlan;
import cz.cuni.amis.pogamut.sposh.exceptions.CycleException;
import cz.cuni.amis.pogamut.sposh.exceptions.DuplicateNameException;
import java.text.MessageFormat;
import org.openide.NotifyDescriptor;

/**
 * Add competence to the plan. Check for cycles and duplicate names.
 * @author Honza
 */
public class AcceptComp2Plan extends AbstractAcceptAction<Competence, PoshPlan> {

    public AcceptComp2Plan(PoshPlan plan) {
        super(Competence.dataFlavor, plan);
    }

    @Override
    protected void performAction(Competence competence) {
        try {
            dataNode.addCompetence(competence);
        } catch (DuplicateNameException ex) {
            String message = MessageFormat.format("Plan already contains element with name {0}.", competence.getName());
            displayMessage(message, NotifyDescriptor.ERROR_MESSAGE);
        } catch (CycleException ex) {
            String message = MessageFormat.format("The competence {0} would create a cycle in the plan.", competence.getName());
            displayMessage(message, NotifyDescriptor.ERROR_MESSAGE);
        }
    }
}
