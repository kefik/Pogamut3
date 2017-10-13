package cz.cuni.pogamut.shed.presenter;

import cz.cuni.amis.pogamut.sposh.elements.ActionPattern;
import cz.cuni.amis.pogamut.sposh.elements.Competence;
import cz.cuni.amis.pogamut.sposh.elements.LapChain;
import cz.cuni.amis.pogamut.sposh.elements.LapPath;
import cz.cuni.amis.pogamut.sposh.elements.INamedElement;
import cz.cuni.amis.pogamut.sposh.elements.PoshElement;
import cz.cuni.amis.pogamut.sposh.elements.PoshElementListener;
import cz.cuni.amis.pogamut.sposh.elements.PoshPlan;
import cz.cuni.amis.pogamut.sposh.elements.TriggeredAction;
import cz.cuni.amis.pogamut.sposh.executor.IWorkExecutor;
import cz.cuni.pogamut.shed.widget.*;
import java.beans.PropertyChangeEvent;
import javax.swing.Action;
import org.netbeans.api.visual.action.WidgetAction;
import org.netbeans.api.visual.widget.Scene;

/**
 * Presenter to update expanded {@link AttachmentEnvelope attachment}
 * representation of the {@link SlotEnvelope}.
 *
 * The {@link SlotEnvelope} wants accurate representation of its attachment. The
 * attachment by itself is a string identifier (in the lap tree represented by {@link TriggeredAction})
 * that can reference to {@link ActionPattern} or {@link Competence} of the
 * plan. When it does reference them, we want to see the visual representation
 * of the AP/C. If it doesn't reference them, it is plain action that is
 * executed by the {@link IWorkExecutor}.
 *
 * Since this is an editor, many things can change, e.g. the attachment doesn't
 * reference anything in the begiinning and then AP is added with same
 * identifier. In such case we need to update the atatchment. That is what this
 * presenter does.
 *
 * This presenter listenes to all AP/C for name change, on DC for
 * adding/removing AP/C (in such case, we add/remove listener for name) and
 * finally we listen on the attachment action itself for name change.
 *
 * @author Honza H
 */
class ExpandedActionPresenter extends AbstractPresenter implements IPresenter, IReplaceExpandedAction {

    /**
     * This updater is notified when name of some action in AP is changed
     */
    private final ExpandedActionUpdater<TriggeredAction> actionExpansionUpdater;
    /**
     * This updater handles change of a {@link Competence} name.
     */
    private final ExpandedActionUpdater<Competence> compatenceExpansionUpdater;
    /**
     * This updater handles change of a {@link ActionPattern} nname.
     */
    private final ExpandedActionUpdater<ActionPattern> actionPatternExpansionUpdater;
    private final CompetenceListenersManager competenceListenersUpdater;
    private final ActionPatternListenersManager actionPatternListenersUpdater;
    /**
     * Reference action for the attachment.
     */
    private final TriggeredAction action;
    /**
     * Envelope that contains attachment.
     */
    private final ExpandedActionEnvelope envelope;
    /**
     * Chain is up to the action but w/o the action.
     */
    private final LapChain chain;

    /**
     * Create new presenter for updating the attachment in the {@link SlotEnvelope}.
     *
     * @param scene Scene the @envelope is in and the one where attachment will
     * be.
     * @param presenter Main presenter.
     * @param envelope Envelope for displaying the attachment.
     * @param action Basically reference to {@link ActionPattern} or {@link Competence}.
     * This action is used to determine what attachment will be displayed. When
     * its name is changed, presenter will reflect that.
     * @param chain Chain up to the branch node right before the @action
     * (@action is not included in the chain.).
     */
    public ExpandedActionPresenter(ShedScene scene, ShedPresenter presenter, ExpandedActionEnvelope envelope, TriggeredAction action, LapChain chain) {
        super(scene, presenter);
        this.action = action;
        this.envelope = envelope;
        this.chain = chain;

        actionExpansionUpdater = new ExpandedActionUpdater<TriggeredAction>(this, action, TriggeredAction.taName);
        actionPatternExpansionUpdater = new ExpandedActionUpdater<ActionPattern>(this, action, ActionPattern.apName);
        compatenceExpansionUpdater = new ExpandedActionUpdater<Competence>(this, action, Competence.cnName);

        competenceListenersUpdater = new CompetenceListenersManager(this, action, compatenceExpansionUpdater);
        actionPatternListenersUpdater = new ActionPatternListenersManager(this, action, actionPatternExpansionUpdater);
    }

    @Override
    public void register() {
        envelope.setPresenter(this);

        PoshPlan lapPlan = presenter.getLapTree();
        assert lapPlan == action.getRootNode();

        lapPlan.addElementListener(competenceListenersUpdater);
        lapPlan.addElementListener(actionPatternListenersUpdater);

        action.addElementListener(actionExpansionUpdater);

        for (ActionPattern actionPattern : lapPlan.getActionPatterns()) {
            actionPattern.addElementListener(actionPatternExpansionUpdater);
        }
        for (Competence competence : lapPlan.getCompetences()) {
            competence.addElementListener(compatenceExpansionUpdater);
        }
    }

    @Override
    public void unregister() {
        PoshPlan lapPlan = presenter.getLapTree();

        for (Competence competence : lapPlan.getCompetences()) {
            competence.removeElementListener(compatenceExpansionUpdater);
        }
        for (ActionPattern actionPattern : lapPlan.getActionPatterns()) {
            actionPattern.removeElementListener(actionPatternExpansionUpdater);
        }

        action.removeElementListener(actionExpansionUpdater);

        lapPlan.removeElementListener(actionPatternListenersUpdater);
        lapPlan.removeElementListener(competenceListenersUpdater);

        envelope.setPresenter(null);
    }

    @Override
    public Action[] getMenuActions() {
        return null;
    }

    @Override
    public void replaceExpandedAction(TriggeredAction actionToExpand) {
        assert this.action == actionToExpand;

        // drive action - I get DC path
        // choice action - I get up to AP
        // AP action - I get up to C
        LapPath actionPathFragment = LapPath.getLinkPath(action);
        LapPath chainPath = chain.toPath();
        LapPath actionPath = chainPath.concat(actionPathFragment.subpath(1, actionPathFragment.length()));
        ShedCreationContainer<AttachmentEnvelope> newAttachment = scene.getWidgetFactory().createAttachmentEnvelope(actionPath, action);
        envelope.changeAttachmentWidget(newAttachment.getWidget());
        scene.update();
        scene.addArrows(newAttachment.getArrows());
        scene.update();
        scene.repaint();
    }

    @Override
    public WidgetAction getEditAction() {
        return null;
    }

    /**
     * Updater of attachment. Attachment is updated when name of the element is
     * changed and the name was/is referenced by the {@link #action}.
     */
    private static class ExpandedActionUpdater<T extends PoshElement> implements PoshElementListener<T> {

        private final IReplaceExpandedAction actionBranchReplacer;
        private final TriggeredAction action;
        private final String nameProperty;

        /**
         * Create new element listener that will listen for property change
         * events with property name @propertyName.
         *
         * @param actionBranchReplacer Action that will replace the action when
         * asked
         * @param action action that is supposed to be expanded
         * @param nameProperty name of property for tha "name" of element (e.g. {@link ActionPattern#apName}).
         */
        ExpandedActionUpdater(IReplaceExpandedAction actionBranchReplacer, TriggeredAction action, String nameProperty) {
            this.actionBranchReplacer = actionBranchReplacer;
            this.action = action;
            this.nameProperty = nameProperty;
        }

        @Override
        public void childElementAdded(T parent, PoshElement child) {
            // do nothing
        }

        @Override
        public void childElementMoved(T parent, PoshElement child, int oldIndex, int newIndex) {
            // do nothing
        }

        @Override
        public void childElementRemoved(T parent, PoshElement child, int removedChildIndex) {
            // do nothing
        }

        /**
         * When AP name is changed and the attachment action was referencing the
         * old name or is referencing the new name, update attachment.
         */
        @Override
        public void propertyChange(PropertyChangeEvent changeEvent) {
            if (changeEvent.getPropertyName().equals(nameProperty)) {
                String actionName = action.getName();

                String oldElementName = (String) changeEvent.getOldValue();
                boolean referencedOldName = actionName.equals(oldElementName);

                String newElementName = (String) changeEvent.getNewValue();
                boolean referencesNewName = actionName.equals(newElementName);

                if (referencedOldName || referencesNewName) {
                    actionBranchReplacer.replaceExpandedAction(action);
                }
            }
        }
    }

    /**
     * Updater of expanded action when new element of type @STRUCTURE is added,
     * e.g. when I have action with name search and new competence or AP with
     * name search si added.
     *
     * @param <STRUCTURE>
     */
    private static abstract class AbstractListenersManager<STRUCTURE extends PoshElement> extends AbstractLapElementListener<PoshPlan> {

        protected final IReplaceExpandedAction replaceActionWidget;
        protected final TriggeredAction action;
        protected final PoshElementListener<STRUCTURE> listener;

        public AbstractListenersManager(IReplaceExpandedAction replaceActionWidget, TriggeredAction action, PoshElementListener<STRUCTURE> listener) {
            this.replaceActionWidget = replaceActionWidget;
            this.action = action;
            this.listener = listener;
        }

        protected void updateAttachment(INamedElement modifiedElement) {
            String actionName = action.getName();
            String elementName = modifiedElement.getName();

            boolean actionReferencesElement = actionName.equals(elementName);
            if (actionReferencesElement) {
                replaceActionWidget.replaceExpandedAction(action);
            }
        }

        @Override
        public void childElementMoved(PoshPlan parent, PoshElement child, int oldIndex, int newIndex) {
            // Do nothing. Movement of elements won't affect action expansion.
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            // This is PoshPlan listener, its name can't be referenced
        }
    }

    /**
     * Listens on {@link PoshPlan} on added or removed {@link Competence}s and
     * adds/removes them as listeners of the passed listener.
     *
     * You need to add its as listener of {@link PoshPlan}, it won't register
     * @listener as listener to already existing competences (you need to do
     * that yourself).
     *
     * @author Honza Havlicek
     */
    private static class CompetenceListenersManager extends AbstractListenersManager<Competence> {

        CompetenceListenersManager(IReplaceExpandedAction replaceActionWidget, TriggeredAction action, PoshElementListener<Competence> listener) {
            super(replaceActionWidget, action, listener);
        }

        @Override
        public void childElementAdded(PoshPlan parent, PoshElement child) {
            if (isCompetence(child)) {
                Competence addedCompetence = extractCompetence(parent.getCompetences(), child);
                addedCompetence.addElementListener(listener);

                updateAttachment(addedCompetence); // what if added was expanded
            }
        }

        @Override
        public void childElementRemoved(PoshPlan parent, PoshElement child, int removedChildPosition) {
            if (isCompetence(child)) {
                Competence removedCompetence = (Competence) child;
                removedCompetence.removeElementListener(listener);

                updateAttachment(removedCompetence); // what if removed was expanded
            }
        }
    }

    private static class ActionPatternListenersManager extends AbstractListenersManager<ActionPattern> {

        ActionPatternListenersManager(IReplaceExpandedAction replaceActionWidget, TriggeredAction action, PoshElementListener<ActionPattern> listener) {
            super(replaceActionWidget, action, listener);
        }

        @Override
        public void childElementAdded(PoshPlan parent, PoshElement child) {
            if (isActionPattern(child)) {
                ActionPattern addedActionPattern = extractActionPattern(parent.getActionPatterns(), child);
                addedActionPattern.addElementListener(listener);

                updateAttachment(addedActionPattern); // what if added was expanded
            }
        }

        @Override
        public void childElementRemoved(PoshPlan parent, PoshElement child, int removedChildPosition) {
            if (isActionPattern(child)) {
                ActionPattern removedActionPattern = (ActionPattern) child;
                removedActionPattern.removeElementListener(listener);

                updateAttachment(removedActionPattern); // what if removed was expanded
            }
        }
    }
}

interface IReplaceExpandedAction {

    /**
     * Must call {@link Scene#validate() } at the end.
     *
     * @param actionToExpand
     */
    void replaceExpandedAction(TriggeredAction actionToExpand);
}
