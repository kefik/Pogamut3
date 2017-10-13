package cz.cuni.pogamut.shed.widget.editor;

import cz.cuni.amis.pogamut.sposh.elements.*;
import cz.cuni.amis.pogamut.sposh.exceptions.DuplicateNameException;
import cz.cuni.amis.pogamut.sposh.exceptions.InvalidNameException;
import cz.cuni.amis.pogamut.sposh.executor.PrimitiveData;
import cz.cuni.pogamut.shed.presenter.ShedPresenter;
import java.text.MessageFormat;
import org.netbeans.api.visual.action.ActionFactory;
import org.netbeans.api.visual.action.InplaceEditorProvider;
import org.netbeans.api.visual.action.TextFieldInplaceEditor;
import org.netbeans.api.visual.widget.Widget;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;

/**
 * Factroy for creating various {@link TextFieldInplaceEditor inplace editors}.
 * To use these editors in a widget, you must add them to the action chain of a
 * widget using {@link ActionFactory#createInplaceEditorAction(org.netbeans.api.visual.action.TextFieldInplaceEditor)
 * }.
 *
 * @author Honza
 */
public class ShedInplaceEditorFactory {

    /**
     * Create editor that will edit the name of the @drive.
     *
     * @param drive Drive that will have its name changed.
     * @return
     */
    public static TextFieldInplaceEditor createDriveEditor(DriveElement drive) {
        return new DriveInplaceEditor(drive);
    }

    /**
     * Create editor that will edit the name of the @choice.
     *
     * @param choice {@link CompetenceElement} that will have its name changed.
     * @return
     */
    public static TextFieldInplaceEditor createChoiceEditor(CompetenceElement choice) {
        return new ChoiceInplaceEditor(choice);
    }


    /**
     * Create inplace editor provider that can edit action, its argumenta passed
     * by the action downwards.
     *
     * @param action Action that will be edited.
     * @param presenter Used for getting list of parameters required by the
     * @action (from {@link PrimitiveData})
     * @param chain Variable chain from root to the action, incl.
     * @return
     */
    public static InplaceEditorProvider createActionEditorProvider(TriggeredAction action, ShedPresenter presenter, LapChain chain) {
        return new ActionEditorProvider(action, presenter, chain);
    }

    /**
     * Create inplace editor provider that can edit sense, its argumenta passed
     * by the sense downwards, predicate and value.
     *
     * @param sense Sense that will be edited.
     * @param presenter Used for getting list of parameters required by the
     * @sense (from {@link PrimitiveData})
     * @param chain Chain to the sense, inclusive.
     * @return
     */
    public static InplaceEditorProvider createSenseEditorProvider(Sense sense, ShedPresenter presenter, LapChain chain) {
        return new SenseEditorProvider(sense, presenter, chain);
    }    
    
    /**
     * Create editor for action referencing action pattern. The editor can
     * rename the AP, add/remove parameters to the AP and add/remove arguments
     * passed by the action to the AP.
     *
     * @param actionPattern Action pattern that is referenced by the action.
     * @param referencingAction action that is referencing the AP.
     * @param chain Chain for action pattern, inclusive.
     * @return Created editor provider.
     */
    public static InplaceEditorProvider createActionPatternEditor(ActionPattern actionPattern, TriggeredAction referencingAction, LapChain chain) {
        assert actionPattern.getName().equals(referencingAction.getName());
        return new NodeEditorProvider(actionPattern, referencingAction, chain);
    }

    /**
     * Create inplace editor for action referencing competence. The editor can
     * rename the competence, add/remove parameters of the competence and
     * add/remove arguments passed by the action to the competence.
     *
     * @param competence Competence that is referenced by the action.
     * @param referencingAction action that is referencing the competence.
     * @param chain Chain to competence, inclusive.
     * @return Created editor provider.
     */
    public static InplaceEditorProvider createCompetenceEditor(Competence competence, TriggeredAction referencingAction, LapChain chain) {
        assert competence.getName().equals(referencingAction.getName());
        return new NodeEditorProvider(competence, referencingAction, chain);
    }

    private static class DriveInplaceEditor implements TextFieldInplaceEditor {

        private final DriveElement drive;

        public DriveInplaceEditor(DriveElement drive) {
            this.drive = drive;
        }

        @Override
        public boolean isEnabled(Widget widget) {
            return true;
        }

        @Override
        public String getText(Widget widget) {
            return drive.getName();
        }

        private void notify(String message) {
            NotifyDescriptor.Message infoMessage = new NotifyDescriptor.Message(message, NotifyDescriptor.INFORMATION_MESSAGE);
            DialogDisplayer.getDefault().notify(infoMessage);
        }

        @Override
        public void setText(Widget widget, String newDriveName) {
            try {
                drive.setName(newDriveName);
            } catch (DuplicateNameException ex) {
                String message = MessageFormat.format("Drive with name \"{0}\" is already present.", newDriveName);
                notify(message);
            } catch (InvalidNameException ex) {
                String message = MessageFormat.format("Drive name \"{0}\" is not valid.", newDriveName);
                notify(message);
            }
        }
    }

    private static class ChoiceInplaceEditor implements TextFieldInplaceEditor {

        private final CompetenceElement choice;

        public ChoiceInplaceEditor(CompetenceElement choice) {
            this.choice = choice;
        }

        @Override
        public boolean isEnabled(Widget widget) {
            return true;
        }

        @Override
        public String getText(Widget widget) {
            return choice.getName();
        }

        private void notify(String message) {
            NotifyDescriptor.Message infoMessage = new NotifyDescriptor.Message(message, NotifyDescriptor.INFORMATION_MESSAGE);
            DialogDisplayer.getDefault().notify(infoMessage);
        }

        @Override
        public void setText(Widget widget, String newChoiceName) {
            try {
                choice.setName(newChoiceName);
            } catch (DuplicateNameException ex) {
                String message = MessageFormat.format("Choice name \"{0}\" already exists.", newChoiceName);
                notify(message);
            } catch (InvalidNameException ex) {
                String message = MessageFormat.format("Choice name \"{0}\" is not valid.", newChoiceName);
                notify(message);
            }
        }
    }
}
