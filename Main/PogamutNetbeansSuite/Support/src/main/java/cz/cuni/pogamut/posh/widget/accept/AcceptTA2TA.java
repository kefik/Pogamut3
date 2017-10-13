package cz.cuni.pogamut.posh.widget.accept;

import cz.cuni.amis.pogamut.sposh.elements.ActionPattern;
import cz.cuni.amis.pogamut.sposh.elements.PoshElement;
import cz.cuni.amis.pogamut.sposh.elements.TriggeredAction;

/**
 * Accept provider that says what to do when triggered action is dropped on triggered action.
 *
 * If parent of triggered action is action pattern, squeeze it to the list of action.
 * Otherwise change name of action to what transferable is saying.
 * @author Honza
 */
public class AcceptTA2TA extends AbstractAcceptAction<TriggeredAction, TriggeredAction> {

    public AcceptTA2TA(TriggeredAction dataNode) {
        super(TriggeredAction.dataFlavor, dataNode);
    }

    @Override
    protected void performAction(TriggeredAction action) {
        PoshElement parentNode = dataNode.getParent();
        if (parentNode == null) {
            return;			// Add new sense
        }

        if (parentNode instanceof ActionPattern) {
            if (action.getParent() != null) {
                action.getParent().neutralizeChild(action);
            }

            ActionPattern ap = (ActionPattern) parentNode;

            if (!ap.addTriggeredAction(action)) {
                return;
            }

            // find out index, where is newly added sense
            int newlyAddedSenseIndex =
                    this.getIndexInList(ap.getChildDataNodes(), action);

            // find index, where should dropped sense be =
            //    index of sense new sense was dropped on
            int assignedSenseIndex =
                    this.getIndexInList(ap.getChildDataNodes(), dataNode);

            int relativePositionChange = assignedSenseIndex - newlyAddedSenseIndex;
            ap.moveChild(action, relativePositionChange);

        } else {
            dataNode.setActionName(action.getName());
        }
    }
}

