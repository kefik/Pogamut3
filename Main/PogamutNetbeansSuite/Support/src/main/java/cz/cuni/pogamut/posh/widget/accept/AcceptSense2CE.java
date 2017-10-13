package cz.cuni.pogamut.posh.widget.accept;

import cz.cuni.amis.pogamut.sposh.elements.CompetenceElement;
import cz.cuni.amis.pogamut.sposh.elements.Sense;

/**
 * Class for accepting dropped Sense to the SimpleCompetenceElementWidget.
 * Creates a new CE in CPE with action doNothing and is triggered with
 * passed sense.
 * @author Honza
 */
public class AcceptSense2CE extends AbstractAcceptAction<Sense, CompetenceElement> {

    public AcceptSense2CE(CompetenceElement dataNode) {
        super(Sense.dataFlavor, dataNode);
    }

    @Override
    protected void performAction(Sense sense) {
        dataNode.addUserTrigger(sense);
    }
}
