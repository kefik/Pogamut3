package cz.cuni.pogamut.shed.presenter;

import cz.cuni.amis.pogamut.sposh.elements.CompetenceElement;
import cz.cuni.amis.pogamut.sposh.elements.PoshElement;
import cz.cuni.amis.pogamut.sposh.elements.PoshElementListener;
import cz.cuni.pogamut.shed.widget.ShedScene;
import cz.cuni.pogamut.shed.widget.ShedWidget;
import cz.cuni.pogamut.shed.widget.editor.ShedInplaceEditorFactory;
import java.beans.PropertyChangeEvent;
import javax.swing.Action;
import org.netbeans.api.visual.action.ActionFactory;
import org.netbeans.api.visual.action.WidgetAction;

/**
 * This widget is responsible only for the choice widget, not its trigger nor
 * expanded action.
 *
 * @author HonzaH
 */
final class ChoicePresenter extends AbstractPresenter implements IPresenter, PoshElementListener<CompetenceElement> {

    private final CompetenceElement choice;
    private final ShedWidget choiceWidget;
    
    ChoicePresenter(ShedScene scene, ShedPresenter presenter, CompetenceElement choice, ShedWidget choiceWidget) {
        super(scene, presenter);
        this.choice = choice;
        this.choiceWidget = choiceWidget;
    }

    @Override
    public void register() {
        choiceWidget.setPresenter(this);
        choice.addElementListener(this);
        
        choiceWidget.getActions().addAction(FocusActionFactory.createFocusAction());
        choiceWidget.getActions().addAction(new DeleteFocusedNodeAction(ShedMenuActionFactory.deleteChoiceAction(choice)));
    }

    @Override
    public void unregister() {
        choice.removeElementListener(this);
        choiceWidget.setPresenter(null);
    }

    @Override
    public Action[] getMenuActions() {
        return new Action[]{
                    ShedMenuActionFactory.appendSenseAction(choice),
                    ShedMenuActionFactory.appendChoiceAction(choice),
                    ShedMenuActionFactory.deleteChoiceAction(choice)
                };
    }

    @Override
    public AbstractAcceptAction[] getAcceptProviders() {
        return new AbstractAcceptAction[]{
                    AcceptActionFactory.createSense2Choice(choice),
                    AcceptActionFactory.createChoice2Choice(choice),
                    AcceptActionFactory.createDrive2Choice(choice)
                };
    }

    @Override
    public void childElementAdded(CompetenceElement parent, PoshElement child) {
        // do nothing, not your job
    }

    @Override
    public void childElementMoved(CompetenceElement parent, PoshElement child, int oldIndex, int newIndex) {
        // do nothing, not your job
    }

    @Override
    public void childElementRemoved(CompetenceElement parent, PoshElement child, int removedChildIndex) {
        // senses are taken care of by trigger, action is never added/removed
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(CompetenceElement.ceName)) {
            String newChoiceName = evt.getNewValue().toString();
            choiceWidget.setDisplayName(newChoiceName);
        }
    }

    @Override
    public WidgetAction getEditAction() {
        return ActionFactory.createInplaceEditorAction(ShedInplaceEditorFactory.createChoiceEditor(choice));
    }
}
