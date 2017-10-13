package cz.cuni.pogamut.posh.widget.accept;

import cz.cuni.amis.pogamut.sposh.elements.Goal;
import cz.cuni.amis.pogamut.sposh.elements.Sense;

/**
 * What to do when sense is dropped onto the goal: add sense to end of
 * list of goal senses.
 * @author havlj3am
 */
public class AcceptSense2Goal extends AbstractAcceptAction<Sense, Goal> {

    public AcceptSense2Goal(Goal dataNode) {
        super(Sense.dataFlavor, dataNode);
    }

    @Override
    protected void performAction(Sense sense) {
        this.dataNode.addUserSense(sense);
    }
}
