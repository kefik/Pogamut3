package cz.cuni.pogamut.posh.widget.accept;

import cz.cuni.amis.pogamut.sposh.elements.Competence;
import cz.cuni.amis.pogamut.sposh.elements.CompetenceElement;
import cz.cuni.amis.pogamut.sposh.exceptions.DuplicateNameException;
import org.openide.NotifyDescriptor;

/**
 * AcceptProvider for CompetencePriorityElement. When CompElem is dropped,
 * add it as a sibling above.
 */
public final class AcceptCE2CE extends AbstractAcceptAction<CompetenceElement, CompetenceElement> {

    public AcceptCE2CE(CompetenceElement dataNode) {
        super(CompetenceElement.dataFlavor, dataNode, null);
    }

    @Override
    protected void performAction(CompetenceElement element) {
        Competence competence = (Competence) dataNode.getParent();
        try {
            competence.addElement(element);
        } catch (DuplicateNameException ex) {
            displayMessage(ex.getMessage(), NotifyDescriptor.ERROR_MESSAGE);
            return;
        }

        int newlyAddedCElementIndex =
                this.getIndexInList(competence.getChildDataNodes(), element);

        // find index, where should dropped sense be =
        //    index of sense new sense was dropped on
        int assignedCElementIndex =
                this.getIndexInList(competence.getChildDataNodes(), dataNode);

        int relativePositionChange =
                assignedCElementIndex - newlyAddedCElementIndex;

        competence.moveChild(element, relativePositionChange);
    }
}

