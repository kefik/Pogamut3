package cz.cuni.pogamut.shed.presenter;

import cz.cuni.amis.pogamut.sposh.elements.Competence;
import cz.cuni.amis.pogamut.sposh.elements.CompetenceElement;
import cz.cuni.amis.pogamut.sposh.elements.LapChain;
import cz.cuni.amis.pogamut.sposh.elements.LapPath;
import cz.cuni.amis.pogamut.sposh.elements.LapType;
import cz.cuni.amis.pogamut.sposh.elements.PoshElement;
import cz.cuni.amis.pogamut.sposh.elements.PoshElementListener;
import cz.cuni.pogamut.shed.widget.*;
import java.beans.PropertyChangeEvent;
import java.util.List;
import javax.swing.Action;
import org.netbeans.api.visual.action.WidgetAction;
import org.netbeans.api.visual.anchor.Anchor;

/**
 * This {@link IPresenter presenter} is responsible for adding, moving and
 * removing {@link ShedChoiceEnvelope} in the {@link ShedChoicesEnvelope}.
 *
 * @see TriggerPresenter
 *
 * @author Honza H
 */
final class ChoicesPresenter extends AbstractPresenter implements IPresenter, PoshElementListener<Competence> {

    private final ShedChoicesEnvelope choicesEnvelope;
    private final Competence competence;
    private final LapChain chain;

    /**
     *
     * @param scene
     * @param presenter
     * @param choicesEnvelope
     * @param competence
     * @param competenceChain Chain up to incl. competence
     */
    ChoicesPresenter(ShedScene scene, ShedPresenter presenter, ShedChoicesEnvelope choicesEnvelope, Competence competence, LapChain competenceChain) {
        super(scene, presenter);

        this.choicesEnvelope = choicesEnvelope;
        this.competence = competence;
        this.chain = competenceChain;
    }

    @Override
    public void register() {
        choicesEnvelope.setPresenter(this);
        competence.addElementListener(this);
    }

    @Override
    public void unregister() {
        competence.removeElementListener(this);
        choicesEnvelope.setPresenter(null);
    }

    @Override
    public Action[] getMenuActions() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void childElementAdded(Competence parent, PoshElement child) {
        if (isChoice(child)) {
            Anchor envelopeAnchor = choicesEnvelope.getCommonAnchor();
            List<CompetenceElement> choices = parent.getChildDataNodes();
            CompetenceElement choice = extractChoice(choices, child);
            int choicePosition = getPosition(choices, choice);
            LapPath competencePath = chain.toPath();
            LapPath choicePath = competencePath.concat(LapType.COMPETENCE_ELEMENT, choicePosition);
            ShedCreationContainer<SlotEnvelope> choiceContainer = scene.getWidgetFactory().createChoiceEnvelope(choicePath, choice, envelopeAnchor);

            choicesEnvelope.add(choiceContainer.getWidget(), choicePosition);
            scene.update();
            scene.addArrows(choiceContainer.getArrows());
            scene.update();
        } else {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public void childElementMoved(Competence parent, PoshElement child, int oldIndex, int newIndex) {
        if (isChoice(child)) {
            SlotEnvelope choiceWidget = choicesEnvelope.getChild(oldIndex);
            choicesEnvelope.move(newIndex, choiceWidget);
            scene.update();
        } else {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public void childElementRemoved(Competence parent, PoshElement child, int removedChildIndex) {
        if (isChoice(child)) {
            SlotEnvelope removedChoiceWidget = choicesEnvelope.getChild(removedChildIndex);
            choicesEnvelope.remove(removedChoiceWidget);
            scene.update();
        } else {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        // ignore
    }

    @Override
    public WidgetAction getEditAction() {
        return null;
    }
}
