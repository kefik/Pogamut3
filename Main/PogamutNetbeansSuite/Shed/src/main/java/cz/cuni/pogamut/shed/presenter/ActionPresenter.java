package cz.cuni.pogamut.shed.presenter;

import cz.cuni.amis.pogamut.sposh.elements.ActionPattern;
import cz.cuni.amis.pogamut.sposh.elements.LapChain;
import cz.cuni.amis.pogamut.sposh.elements.PoshElement;
import cz.cuni.amis.pogamut.sposh.elements.TriggeredAction;
import cz.cuni.pogamut.shed.widget.ShedScene;
import cz.cuni.pogamut.shed.widget.ShedWidget;
import cz.cuni.pogamut.shed.widget.editor.ShedInplaceEditorFactory;
import java.beans.PropertyChangeEvent;
import java.util.List;
import javax.swing.Action;
import org.netbeans.api.visual.action.ActionFactory;
import org.netbeans.api.visual.action.WidgetAction;

/**
 * Presenter for primitive action represented by {@link TriggeredAction}.
 * {@link TriggeredAction} can't have any children, so no methods for
 * adding/moving/removing of children, this presenter only updates properties of
 * the {@link TriggeredAction}.
 *
 * It updates widget on {@link ShedPresenter#getNameMapping(java.lang.String) name mapping},
 * {@link ParametersChain chain changes} and property changes of the {@link TriggeredAction action}.
 *
 * @author Honza H.
 */
final class ActionPresenter extends PrimitivePresenter<TriggeredAction> {

    ActionPresenter(ShedScene scene, ShedPresenter presenter, TriggeredAction action, ShedWidget actionWidget, LapChain actionChain) {
        super(scene, presenter, action, actionWidget, actionChain);
    }

    @Override
    public void register() {
        super.register();

        primitiveWidget.getActions().addAction(FocusActionFactory.createPrimitiveFocusAction(primitive, presenter));

        PoshElement parentStructure = primitive.getParent();
        if (isActionPattern(parentStructure)) {
            List<ActionPattern> allActionPatterns = presenter.getLapTree().getActionPatterns();
            ActionPattern actionPattern = extractActionPattern(allActionPatterns, parentStructure);
            primitiveWidget.getActions().addAction(new DeleteFocusedNodeAction(ShedMenuActionFactory.deleteAction(actionPattern, primitive)));
        } else {
            primitiveWidget.getActions().addAction(new DeleteFocusedNodeAction(ShedMenuActionFactory.deleteNotPossible(primitive)));
        }
    }

    @Override
    public Action[] getMenuActions() {
        PoshElement parentStructure = primitive.getParent();
        if (isActionPattern(parentStructure)) {
            List<ActionPattern> allActionPatterns = presenter.getLapTree().getActionPatterns();
            ActionPattern actionPattern = extractActionPattern(allActionPatterns, parentStructure);
            int actionPosition = getPosition(actionPattern.getActions(), primitive);
            int nextActionPosition = actionPosition + 1;

            return new Action[]{
                        ShedMenuActionFactory.goToSourceAction(primitive),
                        ShedMenuActionFactory.appendAction(actionPattern, nextActionPosition),
                        ShedMenuActionFactory.deleteAction(actionPattern, primitive)
                    };
        } else {
            return new Action[]{ShedMenuActionFactory.goToSourceAction(primitive),};
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(TriggeredAction.taName)) {
            updateWidget();
        } else if (evt.getPropertyName().equals(TriggeredAction.taArgs)) {
            updateWidget();
        } else {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public AbstractAcceptAction[] getAcceptProviders() {
        return new AbstractAcceptAction[]{
                    AcceptActionFactory.createCompetence2Action(primitive),
                    AcceptActionFactory.createActionPatternAction(primitive),
                    AcceptActionFactory.createAction2Action(primitive)
                };
    }

    @Override
    protected String getTitleText() {
        String mappedActionName = presenter.getNameMapping(primitive.getName());
        if (mappedActionName == null) {
            mappedActionName = primitive.getName();
        }
        return mappedActionName;
    }

    @Override
    public WidgetAction getEditAction() {
        return ActionFactory.createInplaceEditorAction(ShedInplaceEditorFactory.createActionEditorProvider(primitive, presenter, primitiveChain));
    }
}
