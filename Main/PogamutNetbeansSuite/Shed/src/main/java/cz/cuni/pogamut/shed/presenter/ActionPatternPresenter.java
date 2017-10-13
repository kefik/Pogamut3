package cz.cuni.pogamut.shed.presenter;

import cz.cuni.amis.pogamut.sposh.elements.*;
import cz.cuni.pogamut.shed.widget.ShedScene;
import cz.cuni.pogamut.shed.widget.ShedWidget;
import cz.cuni.pogamut.shed.widget.editor.ShedInplaceEditorFactory;
import java.beans.PropertyChangeEvent;
import javax.swing.Action;
import org.netbeans.api.visual.action.ActionFactory;
import org.netbeans.api.visual.action.WidgetAction;

/**
 * {@link IPresenter} for widget representing the {@link ActionPattern}. This
 * presenter updates only properties, no children ect.
 *
 * @see CompetencePresenter
 * @see DrivePresenter
 * @author Honza H.
 */
final class ActionPatternPresenter extends NodePresenter<ActionPattern> implements IPresenter, PoshElementListener<ActionPattern>, ILapChainListener {

    /**
     * Create presenter for the AP widget, only the widget, not its children
     * ect.
     *
     * @param scene Scene of the widget.
     * @param presenter Main presenter
     * @param referencingAction The action that references this AP expansion.
     * @param actionPattern The action pattern.
     * @param actionPatternWidget The widget that will represent the AP.
     * @param actionPatternChain Chain to the AP incl.
     */
    ActionPatternPresenter(ShedScene scene, ShedPresenter presenter, TriggeredAction referencingAction, ActionPattern actionPattern, ShedWidget actionPatternWidget, LapChain actionPatternChain) {
        super(scene, presenter, referencingAction, actionPattern, actionPatternWidget, actionPatternChain);
    }

    @Override
    public void register() {
        nodeWidget.setPresenter(this);
        node.addElementListener(this);
        reference.addElementListener(this);

        nodeChain.register();
        nodeChain.addChainListener(this);
        
        nodeWidget.getActions().addAction(FocusActionFactory.createActionPatternFocusAction(node));
        nodeWidget.getActions().addAction(new DeleteFocusedNodeAction(ShedMenuActionFactory.deleteActionPattern(node)));
    }

    @Override
    public void unregister() {
        nodeChain.removeChainListener(this);
        nodeChain.unregister();

        reference.removeElementListener(this);
        node.removeElementListener(this);
        nodeWidget.setPresenter(null);
    }

    @Override
    public Action[] getMenuActions() {
        PoshElement parentStructure = reference.getParent();
        if (isActionPattern(parentStructure)) {
            ActionPattern parentAP = (ActionPattern) parentStructure;
            return new Action[]{
                        ShedMenuActionFactory.appendAction(node, 0),
                        ShedMenuActionFactory.changeActionPatternParameters(node),
                        ShedMenuActionFactory.deleteAction(parentAP, reference)
                    };
        } else {
            return new Action[]{
                        ShedMenuActionFactory.appendAction(node, 0),
                        ShedMenuActionFactory.changeActionPatternParameters(node)
                    };
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String propertyName = evt.getPropertyName();

        if (propertyName.equals(TriggeredAction.taArgs)
                || propertyName.equals(TriggeredAction.taName)
                || propertyName.equals(ActionPattern.apName)
                || propertyName.equals(ActionPattern.apParams)) {
            updateWidget();
        } else {
            throw new IllegalArgumentException(propertyName);
        }
    }

    @Override
    public AbstractAcceptAction[] getAcceptProviders() {
        return new AbstractAcceptAction[]{
                    AcceptActionFactory.createCompetence2Action(reference),
                    AcceptActionFactory.createActionPatternAction(reference),
                    AcceptActionFactory.createAction2Action(reference)
                };
    }

    @Override
    public WidgetAction getEditAction() {
        return ActionFactory.createInplaceEditorAction(ShedInplaceEditorFactory.createActionPatternEditor(node, reference, nodeChain));
    }
}
