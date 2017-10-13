package cz.cuni.pogamut.posh.widget.accept;

import cz.cuni.amis.pogamut.sposh.elements.Competence;
import cz.cuni.amis.pogamut.sposh.elements.PoshPlan;
import cz.cuni.amis.pogamut.sposh.elements.TriggeredAction;
import cz.cuni.amis.pogamut.sposh.exceptions.CycleException;
import cz.cuni.amis.pogamut.sposh.exceptions.DuplicateNameException;
import java.text.MessageFormat;
import org.openide.NotifyDescriptor;

/**
 * When competence is dropped to the triggered action, add competence to
 * the posh plan if it isn't already there and change name of triggered action
 * (that means triggered action will expand to the competence).
 *
 * @author Honza
 */
public class AcceptComp2TA extends AbstractAcceptAction<Competence, TriggeredAction> {

    public AcceptComp2TA(TriggeredAction dataNode) {
        super(Competence.dataFlavor, dataNode);
    }

    @Override
    protected void performAction(Competence competenceNode) {
        PoshPlan root = this.dataNode.getRootNode();
        // If a competence is not yet in the plan, add it...
        if (!root.getCompetences().contains(competenceNode)) {
            try {
                root.addCompetence(competenceNode);
            } catch (DuplicateNameException ex) {
                String message = MessageFormat.format("Plan already contains element with name {0}.", competenceNode.getName());
                displayMessage(message, NotifyDescriptor.ERROR_MESSAGE);
                return;
            } catch (CycleException ex) {
                String message = MessageFormat.format("Competence {0} would create a cycle in the plan.", competenceNode.getName());
                displayMessage(message, NotifyDescriptor.ERROR_MESSAGE);
                return;
            }
        }
        // ...and set name of the action to the competence
        this.dataNode.setActionName(competenceNode.getName());
    }
}
