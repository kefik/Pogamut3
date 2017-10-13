package cz.cuni.pogamut.shed.presenter;

import cz.cuni.amis.pogamut.sposh.elements.LapChain;
import cz.cuni.amis.pogamut.sposh.elements.Result;
import cz.cuni.amis.pogamut.sposh.elements.Sense;
import cz.cuni.amis.pogamut.sposh.elements.TriggeredAction;
import cz.cuni.pogamut.shed.widget.ShedScene;
import cz.cuni.pogamut.shed.widget.ShedSenseWidget;
import cz.cuni.pogamut.shed.widget.editor.ShedInplaceEditorFactory;
import java.beans.PropertyChangeEvent;
import javax.swing.Action;
import org.netbeans.api.visual.action.ActionFactory;
import org.netbeans.api.visual.action.WidgetAction;

/**
 * Presenter for sense widget, it is responsible for menu actions and accept
 * actions. Sense shouldn't have a children, so no adding/moving/removing.
 *
 * It updates widget on {@link ShedPresenter#getNameMapping(java.lang.String) name mapping},
 * {@link ParametersChain chain changes} and property changes of the {@link TriggeredAction action}.
 *
 * @author HonzaH
 */
final class SensePresenter extends PrimitivePresenter<Sense> {

    public SensePresenter(ShedScene scene, ShedPresenter presenter, Sense sense, ShedSenseWidget senseWidget, LapChain senseChain) {
        super(scene, presenter, sense, senseWidget, senseChain);
    }

    @Override
    public void register() {
        super.register();

        primitiveWidget.getActions().addAction(FocusActionFactory.createPrimitiveFocusAction(primitive, presenter));
        primitiveWidget.getActions().addAction(new DeleteFocusedNodeAction(ShedMenuActionFactory.deleteSenseAction(primitive)));
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        boolean isName = evt.getPropertyName().equals(Sense.psSenseName);
        boolean isValue = evt.getPropertyName().equals(Sense.psValue);
        boolean isPredicate = evt.getPropertyName().equals(Sense.psPredicateIndex);
        boolean isArgs = evt.getPropertyName().equals(Sense.psArgs);

        if (isName || isValue || isPredicate || isArgs) {
            updateWidget();
        } else {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public Action[] getMenuActions() {
        return new Action[]{
                    ShedMenuActionFactory.goToSourceAction(primitive),
                    ShedMenuActionFactory.appendSenseAction(primitive),
                    ShedMenuActionFactory.deleteSenseAction(primitive)
                };
    }

    @Override
    public AbstractAcceptAction[] getAcceptProviders() {
        return new AbstractAcceptAction[]{
                    AcceptActionFactory.createSense2Sense(primitive)
                };
    }

    @Override
    protected String getTitleText() {
        String mappedName = presenter.getNameMapping(primitive.getName());
        if (mappedName == null) {
            mappedName = primitive.getName();
        }
        Object operand = primitive.getOperand();
        Sense.Predicate predicate = primitive.getPredicate();

        String senseRepresentation;
        boolean predicateIsEqual = predicate.equals(Sense.Predicate.DEFAULT) || predicate.equals(Sense.Predicate.EQUAL);
        if (predicateIsEqual && Boolean.TRUE.equals(operand)) {
            return mappedName;
        } else {
            String operandString = Result.toLap(operand);
            senseRepresentation = mappedName + predicate.toString() + operandString;
            return senseRepresentation;
        }
    }

    @Override
    public WidgetAction getEditAction() {
        return ActionFactory.createInplaceEditorAction(ShedInplaceEditorFactory.createSenseEditorProvider(primitive, presenter, primitiveChain));
    }
}
