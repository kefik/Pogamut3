package cz.cuni.pogamut.posh.widget.accept;

import cz.cuni.amis.pogamut.sposh.elements.ActionPattern;
import cz.cuni.amis.pogamut.sposh.elements.PoshPlan;
import cz.cuni.amis.pogamut.sposh.elements.TriggeredAction;
import cz.cuni.amis.pogamut.sposh.exceptions.CycleException;
import cz.cuni.amis.pogamut.sposh.exceptions.DuplicateNameException;
import java.text.MessageFormat;
import org.openide.NotifyDescriptor;

/**
 * When action pattern is dropped to triggered action, add action pattern
 * to the POSH plan (if it isn't already there) and change name of triggered action
 * to name of action pattern and expand it. Also make sure it won't create a cycle.
 *
 * @author Honza
 */
public class AcceptAP2TA extends AbstractAcceptAction<ActionPattern, TriggeredAction> {

    public AcceptAP2TA(TriggeredAction dataNode) {
        super(ActionPattern.dataFlavor, dataNode);
    }

    @Override
    protected void performAction(ActionPattern ap) {
        PoshPlan root = this.dataNode.getRootNode();
        // If an AP is not yet in the plan, add it...
        if (!root.getActionPatterns().contains(ap)) {
            try {
                root.addActionPattern(ap);
            } catch (DuplicateNameException ex) {
                String message = MessageFormat.format("Plan already contains element with name {0}.", ap.getName());
                displayMessage(message, NotifyDescriptor.ERROR_MESSAGE);
                return;
            } catch (CycleException ex) {
                String message = MessageFormat.format("The AP {0} would create a cycle in the plan.", ap.getName());
                displayMessage(message, NotifyDescriptor.ERROR_MESSAGE);
                return;
            }
        }
        // ...and set name of the action to the AP.
        this.dataNode.setActionName(ap.getName());
    }
}
