package cz.cuni.pogamut.posh.widget.accept;

import cz.cuni.amis.pogamut.sposh.elements.CompetenceElement;
import cz.cuni.amis.pogamut.sposh.elements.Goal;
import cz.cuni.amis.pogamut.sposh.elements.PoshElement;
import cz.cuni.amis.pogamut.sposh.elements.Sense;
import cz.cuni.amis.pogamut.sposh.elements.Triggers;

/**
 * When sense is dropped on the sense, add it to the list on the place
 * where passed poshsense was and move the rest of list down.
 * @author Honza
 */
public class AcceptSense2Sense extends AbstractAcceptAction<Sense, Sense> {

    public AcceptSense2Sense(Sense dataNode) {
        super(Sense.dataFlavor, dataNode, null);
    }

    @Override
    protected void performAction(Sense sense) {
        // get parent
        PoshElement parent = dataNode.getParent();
        if (parent == null) {
            return;
        }

        // Add new sense
        if (parent instanceof CompetenceElement) {
            CompetenceElement compAtom = (CompetenceElement) parent;
            compAtom.addUserTrigger(sense);

        } else if (parent instanceof Goal) {
            Goal goal = (Goal) parent;
            goal.addUserSense(sense);

        } else if (parent instanceof Triggers) {
            Triggers triggers = (Triggers) parent;
            triggers.addUserTrigger(sense);

        }
        // find out index, where is newly added sense
        int newlyAddedSenseIndex =
                this.getIndexInList(parent.getChildDataNodes(), sense);

        // find index, where should dropped sense be =
        //    index of sense new sense was dropped on
        int assignedSenseIndex =
                this.getIndexInList(parent.getChildDataNodes(), dataNode);

        int relativePositionChange = assignedSenseIndex - newlyAddedSenseIndex;
        if (relativePositionChange != 0) {
            parent.moveChild(sense, relativePositionChange);
        }
    }
}
