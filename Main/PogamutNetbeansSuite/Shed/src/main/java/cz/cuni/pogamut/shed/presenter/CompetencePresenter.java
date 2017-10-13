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
 * {@link CompetencePresenter} is responsible for updating properties from the {@link Competence}
 * to the correct widget.
 *
 * @author Honza Havlicek
 */
final class CompetencePresenter extends NodePresenter<Competence> {

    CompetencePresenter(ShedScene scene, ShedPresenter presenter, TriggeredAction referencingAction, Competence competence, ShedWidget competenceWidget, LapChain competenceChain) {
        super(scene, presenter, referencingAction, competence, competenceWidget, competenceChain);
    }

    @Override
    public void register() {
        nodeWidget.setPresenter(this);
        node.addElementListener(this);
        reference.addElementListener(this);

        nodeChain.register();
        nodeChain.addChainListener(this);

        nodeWidget.getActions().addAction(FocusActionFactory.createCompetenceFocusAction(node));
        nodeWidget.getActions().addAction(new DeleteFocusedNodeAction(ShedMenuActionFactory.deleteCompetence(node)));
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
                        ShedMenuActionFactory.appendChoiceAction(node),
                        ShedMenuActionFactory.changeCompetenceParameters(node),
                        ShedMenuActionFactory.deleteAction(parentAP, reference)
                    };

        } else {
            return new Action[]{
                        ShedMenuActionFactory.appendChoiceAction(node),
                        ShedMenuActionFactory.changeCompetenceParameters(node)
                    };
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String propertyName = evt.getPropertyName();

        if (propertyName.equals(TriggeredAction.taArgs)
                || propertyName.equals(TriggeredAction.taName)
                || propertyName.equals(Competence.cnName)
                || propertyName.equals(Competence.cnParams)) {
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
                    AcceptActionFactory.createAction2Action(reference),
                    AcceptActionFactory.createDrive2Competence(node),
                    AcceptActionFactory.createChoice2Competence(node),
                };
    }

    @Override
    public WidgetAction getEditAction() {
        return ActionFactory.createInplaceEditorAction(ShedInplaceEditorFactory.createCompetenceEditor(node, reference, nodeChain));
    }
}
