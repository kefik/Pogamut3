package cz.cuni.pogamut.posh.widget.accept;

import cz.cuni.amis.pogamut.sposh.elements.DriveCollection;
import cz.cuni.amis.pogamut.sposh.elements.Goal;

/**
 * When goal is dropped on the DC, replace the current goal.
 * @author Honza
 */
public class AcceptGoal2DC extends AbstractAcceptAction<Goal, DriveCollection> {

    public AcceptGoal2DC(DriveCollection dc) {
        super(Goal.dataFlavor, dc);
    }

    @Override
    protected void performAction(Goal goal) {
        this.dataNode.setGoalNode(goal);
    }
}
