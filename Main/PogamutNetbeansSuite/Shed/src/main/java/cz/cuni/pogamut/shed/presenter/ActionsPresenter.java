package cz.cuni.pogamut.shed.presenter;

import cz.cuni.amis.pogamut.sposh.elements.ActionPattern;
import cz.cuni.amis.pogamut.sposh.elements.LapChain;
import cz.cuni.amis.pogamut.sposh.elements.LapPath;
import cz.cuni.amis.pogamut.sposh.elements.LapType;
import cz.cuni.amis.pogamut.sposh.elements.PoshElement;
import cz.cuni.amis.pogamut.sposh.elements.PoshElementListener;
import cz.cuni.amis.pogamut.sposh.elements.TriggeredAction;
import cz.cuni.pogamut.shed.widget.ExpandedActionEnvelope;
import cz.cuni.pogamut.shed.widget.ShedActionsEnvelope;
import cz.cuni.pogamut.shed.widget.ShedCreationContainer;
import cz.cuni.pogamut.shed.widget.ShedScene;
import java.beans.PropertyChangeEvent;
import javax.swing.Action;
import org.netbeans.api.visual.action.WidgetAction;
import org.netbeans.api.visual.anchor.Anchor;

/**
 * This presenter takes care about keeping adding/moving/removing expanded
 * actions in the {@link ShedActionsEnvelope} for {@link ActionPattern}.
 *
 * XXX: Maybe unify with {@link ChoicesPresenter}, maybe not. In this I have to
 * reflect the expansion replacement.
 *
 * @see ChoicesPresenter
 * @author Honza H
 */
final class ActionsPresenter extends AbstractPresenter implements IPresenter, PoshElementListener<ActionPattern> {

    private final ShedActionsEnvelope actionsEnvelope;
    private final ActionPattern actionPattern;
    private final LapChain chain;

    /**
     *
     * @param scene
     * @param presenter
     * @param actionsEnvelope
     * @param actionPattern
     * @param actionPatternChain Chain up to including AP.
     */
    ActionsPresenter(ShedScene scene, ShedPresenter presenter, ShedActionsEnvelope actionsEnvelope, ActionPattern actionPattern, LapChain actionPatternChain) {
        super(scene, presenter);

        this.actionsEnvelope = actionsEnvelope;
        this.actionPattern = actionPattern;
        this.chain = actionPatternChain;
    }

    @Override
    public void register() {
        actionsEnvelope.setPresenter(this);
        // for adding/removing new actions of AP
        actionPattern.addElementListener(this);
    }

    @Override
    public void unregister() {
        actionPattern.removeElementListener(this);
        actionsEnvelope.setPresenter(null);
    }

    @Override
    public Action[] getMenuActions() {
        return null;
    }

    @Override
    public void childElementAdded(ActionPattern parent, PoshElement child) {
        if (isAction(child)) {
            TriggeredAction addedAction = extractAction(parent.getActions(), child);
            int addedActionPosition = getPosition(parent.getActions(), addedAction);
            LapPath actionPatternPath = chain.toPath();
            LapPath addedActionPath = actionPatternPath.concat(LapType.ACTION, addedActionPosition);
            ShedCreationContainer<ExpandedActionEnvelope> addedExpandedActionContainer = scene.getWidgetFactory().createdExpandedActionEnvelope(addedActionPath);

            actionsEnvelope.add(addedExpandedActionContainer.getWidget(), addedActionPosition);
            scene.addArrows(addedExpandedActionContainer.getArrows());

            Anchor attachmentAnchor = addedExpandedActionContainer.getWidget().getAnchor();
            scene.addArrow(actionsEnvelope.getSourceAnchor(), attachmentAnchor);

            scene.update();
        } else {
            throw new IllegalArgumentException("Only action allowed, got " + child);
        }
    }

    @Override
    public void childElementMoved(ActionPattern parent, PoshElement child, int oldIndex, int newIndex) {
        if (isAction(child)) {
            ExpandedActionEnvelope actionWidget = actionsEnvelope.getChild(oldIndex);
            actionsEnvelope.move(newIndex, actionWidget);
            scene.update();
        } else {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public void childElementRemoved(ActionPattern parent, PoshElement child, int removedChildIndex) {
        if (isAction(child)) {
            ExpandedActionEnvelope removedAction = actionsEnvelope.getChild(removedChildIndex);
            actionsEnvelope.remove(removedAction);
            scene.update();
        } else {
            throw new IllegalArgumentException("Only action allowed, got " + child);
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        // Ignore all property changes of the AP
    }

    @Override
    public WidgetAction getEditAction() {
        return null;
    }
}
