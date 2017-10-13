package cz.cuni.pogamut.posh.widget.accept;

import cz.cuni.amis.pogamut.sposh.elements.ActionPattern;
import cz.cuni.amis.pogamut.sposh.elements.PoshPlan;
import cz.cuni.amis.pogamut.sposh.exceptions.CycleException;
import cz.cuni.amis.pogamut.sposh.exceptions.DuplicateNameException;
import java.text.MessageFormat;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;

/**
 * Add AP to Posh plan.
 * @author Honza
 */
public class AcceptAP2Plan extends AbstractAcceptAction<ActionPattern, PoshPlan> {

    public AcceptAP2Plan(PoshPlan plan) {
        super(ActionPattern.dataFlavor, plan);
    }

    @Override
    protected void performAction(ActionPattern ap) {
        try {
            dataNode.addActionPattern(ap);
        } catch (DuplicateNameException ex) {
            String message = MessageFormat.format("Plan already contains element with name {0}.", ap.getName());
            displayMessage(message, NotifyDescriptor.ERROR_MESSAGE);
        } catch (CycleException ex) {
            String message = MessageFormat.format("The AP {0} would create a cycle in the plan.", ap.getName());
            displayMessage(message, NotifyDescriptor.ERROR_MESSAGE);
        }
    }
}
