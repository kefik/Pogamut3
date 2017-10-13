package cz.cuni.pogamut.posh.widget.accept;

import cz.cuni.amis.pogamut.sposh.elements.Sense;
import cz.cuni.amis.pogamut.sposh.elements.Triggers;

/**
 * When sense is dropped to the triggers widget, add it as last Sense
 * to the associted Triggers element.
 *
 * @author Honza
 */
public class AcceptSense2Triggers extends AbstractAcceptAction<Sense, Triggers> {

    public AcceptSense2Triggers(Triggers dataNode) {
        super(Sense.dataFlavor, dataNode);
    }

    @Override
    protected void performAction(Sense sense) {
        this.dataNode.addUserTrigger(sense);
    }
}

