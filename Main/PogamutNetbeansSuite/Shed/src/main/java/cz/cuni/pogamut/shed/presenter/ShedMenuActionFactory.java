package cz.cuni.pogamut.shed.presenter;

import cz.cuni.amis.pogamut.sposh.elements.*;
import cz.cuni.amis.pogamut.sposh.exceptions.CycleException;
import cz.cuni.amis.pogamut.sposh.exceptions.DuplicateNameException;
import cz.cuni.amis.pogamut.sposh.exceptions.MissingParameterException;
import cz.cuni.pogamut.posh.explorer.PGSupport;
import cz.cuni.pogamut.shed.widget.IPresentedWidget;
import java.awt.event.ActionEvent;
import java.io.StringReader;
import java.text.MessageFormat;
import java.util.regex.Pattern;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.netbeans.api.java.classpath.GlobalPathRegistry;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.cookies.EditorCookie;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.util.Exceptions;

/**
 * Factory that will provide various actions to the popup menu of the {@link IPresenter}
 * and thus to the {@link IPresentedWidget}.
 *
 * @author Honza Havlicek
 */
public class ShedMenuActionFactory {

    /**
     * When this action is invoken, it notifies user that it is not possible to
     * delete the node.
     */
    public static <T extends PoshElement & INamedElement> Action deleteNotPossible(final T node) {
        return new AbstractMenuAction("Unable to delete", node) {

            @Override
            public void actionPerformed(ActionEvent e) {
                errorDialog("Unable to delete " + node.getType() + " " + node.getName());
            }
        };
    }

    /**
     * Create action to delete competence
     *
     * @param competence Competence to delete
     * @return Action that when performed will delete the competence
     */
    public static Action deleteCompetence(Competence competence) {
        return new DeleteCompetenceAction(competence);
    }

    private static class DeleteCompetenceAction extends AbstractMenuAction<Competence> {

        public DeleteCompetenceAction(Competence dataNode) {
            super("Delete competence", dataNode);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            PoshPlan plan = dataNode.getRootNode();
            plan.removeCompetence(dataNode);
        }
    }

    /**
     * Create an action that will delete the action pattern.
     * @param actionPattern Pattern to be deleted
     * @return Created action
     */
    public static Action deleteActionPattern(ActionPattern actionPattern) {
        return new DeleteActionPatternAction(actionPattern);
    }

    private static class DeleteActionPatternAction extends AbstractMenuAction<ActionPattern> {

        public DeleteActionPatternAction(ActionPattern actionPattern) {
            super("Delete action pattern", actionPattern);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            PoshPlan plan = dataNode.getRootNode();
            plan.removeActionPattern(dataNode);
        }
    }
    
    /**
     * Ancestor of Shed menu actions actions. Basically every menu action will
     * modify underlying data node in some way, this class provides link to the
     * data node
     * ({@link AbstractMenuAction#dataNode}) on which the action is being
     * performed on. It also provides handy method ({@link AbstractMenuAction#errorDialog(java.lang.String)
     * }) for displaying message to the user if action is for some reason
     * impossible to perform.
     *
     * @author HonzaH
     * @param <T> Type of data node on which the menu action will be performed
     */
    abstract static class AbstractMenuAction<T extends PoshElement> extends AbstractAction {

        protected final T dataNode;

        /**
         * Create new action for context menu.
         *
         * @param name Name of action for menu/button
         * @param dataNode PoshElement on which is the action performed.
         */
        public AbstractMenuAction(String name, T dataNode) {
            super(name);
            this.dataNode = dataNode;
        }

        /**
         * Create an input line dialog and get a identifier from it.
         *
         * @param purposeTitle Title displayed above request for identifier.
         * @return null if inputted string is not identifier or user cancelled
         * action, the identifier otherwise
         */
        protected final String getIdentifierFromDialog(String purposeTitle) {
            NotifyDescriptor.InputLine desc = new NotifyDescriptor.InputLine("Please write new identifiew. Name cannot have whitespaces in it.", purposeTitle);
            DialogDisplayer.getDefault().notify(desc);

            if (desc.getValue() != NotifyDescriptor.OK_OPTION) {
                return null;
            }
            if (!Pattern.compile("[a-zA-Z0-9_-]+").matcher(desc.getInputText().trim()).matches()) {
                DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message("Identifier wasn't valid."));
                return null;
            }
            return desc.getInputText().trim();
        }

        /**
         * Display user an error that occured during
         *
         * @param message message with error
         */
        protected static final void errorDialog(String message) {
            NotifyDescriptor.Message error = new NotifyDescriptor.Message(message, NotifyDescriptor.ERROR_MESSAGE);
            DialogDisplayer.getDefault().notify(error);
        }
    }

    /**
     * Query for name of new sense and add it as a first trigger sense of the
     * drive.
     *
     * @param drive
     * @return
     */
    public static Action createSenseAction(DriveElement drive) {
        return new AddTriggerSenseMenuAction(drive);
    }

    /**
     * Return action that when invoked finds class (from FQN aka name of
     * primitive) and opens it in the editor.
     *
     * @param <PRIMITIVE_TYPE>
     * @param primitive
     * @return
     */
    public static <PRIMITIVE_TYPE extends PoshElement & INamedElement> Action goToSourceAction(PRIMITIVE_TYPE primitive) {
        return new GoToSourceAction(primitive);

    }

    private static class GoToSourceAction<PRIMITIVE_TYPE extends PoshElement & INamedElement> extends AbstractMenuAction<PRIMITIVE_TYPE> {

        public GoToSourceAction(PRIMITIVE_TYPE dataNode) {
            super("Go to source", dataNode);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            String javaFilePath = dataNode.getName().replace('.', '/') + ".java";
            for (FileObject curRoot : GlobalPathRegistry.getDefault().getSourceRoots()) {
                FileObject fileObject = curRoot.getFileObject(javaFilePath);
                if (fileObject != null) {
                    // do something, e.g. openEditor(fileObject, lineNumber);
                    DataObject dobj = null;
                    try {
                        dobj = DataObject.find(fileObject);
                    } catch (DataObjectNotFoundException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                    if (dobj != null) {
                        EditorCookie ec = (EditorCookie) dobj.getCookie(EditorCookie.class);
                        if (ec != null) {
                            ec.open();
                        }
                    }
                }
            }
        }
    }

    /**
     * Ask user for a name of new sense and add the sense as the first sense of
     * the drive.
     */
    private static class AddTriggerSenseMenuAction extends AbstractMenuAction<DriveElement> {

        public AddTriggerSenseMenuAction(DriveElement drive) {
            super("Add trigger", drive);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            String newSenseName = getIdentifierFromDialog("Name of the sense");
            if (newSenseName != null) {
                dataNode.getTrigger().add(0, LapElementsFactory.createSense(newSenseName));
            }
        }
    }

    /**
     * Create new drive and add it behind the specified drive.
     *
     * @param drive
     * @return
     */
    public static Action appendDriveAction(DriveElement drive) {
        return new AppendDriveMenuAction(drive);
    }

    private static class AppendDriveMenuAction extends AbstractMenuAction<DriveElement> {

        public AppendDriveMenuAction(DriveElement drive) {
            super("Append drive", drive);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            String newDriveName = getIdentifierFromDialog("Name of the drive");
            if (newDriveName == null) {
                return;
            }
            DriveElement newDrive = LapElementsFactory.createDriveElementNoTriggers(newDriveName);
            DriveCollection dc = dataNode.getRootNode().getDriveCollection();
            int invokerDriveIndex = dc.getDrives().indexOf(dataNode);
            try {
                dc.addDrive(invokerDriveIndex + 1, newDrive);
            } catch (DuplicateNameException ex) {
                String errorMessage = MessageFormat.format("Drive with name {0} already exists.", newDriveName);
                errorDialog(errorMessage);
            }
        }
    }

    /**
     * Create menu action that will delete the passed drive.
     *
     * @param drive Drive to be deleted.
     * @return
     */
    public static Action deleteDriveAction(DriveElement drive) {
        return new DeleteDriveMenuAction(drive);
    }

    private static class DeleteDriveMenuAction extends AbstractMenuAction<DriveElement> {

        public DeleteDriveMenuAction(DriveElement drive) {
            super("Delete drive", drive);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            DriveCollection dc = dataNode.getRootNode().getDriveCollection();
            dc.removeDrive(dataNode);
        }
    }

    /**
     * Create action that will add new choice to the competence as the first
     * choice.
     *
     * @param competence Comepetence that will have new choice at index 0.
     * @return
     */
    public static Action appendChoiceAction(Competence competence) {
        return new AppendFirstChoiceAction(competence);
    }

    private static class AppendFirstChoiceAction extends AbstractMenuAction<Competence> {

        public AppendFirstChoiceAction(Competence dataNode) {
            super("Append choice", dataNode);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            String newChoiceName = getIdentifierFromDialog("Name of the choice");
            if (newChoiceName != null) {
                try {
                    dataNode.addElement(0, LapElementsFactory.createCompetenceElement(newChoiceName));
                } catch (DuplicateNameException ex) {
                    String errorMessage = MessageFormat.format("There already is a choice with name {0}.", newChoiceName);
                    errorDialog(errorMessage);
                }
            }
        }
    }

    /**
     * Delete specified choice.
     *
     * @param choice Choice to be deleted.
     * @return
     */
    public static Action deleteChoiceAction(CompetenceElement choice) {
        return new DeleteChoiceAction(choice);
    }

    private static class DeleteChoiceAction extends AbstractMenuAction<CompetenceElement> {

        public DeleteChoiceAction(CompetenceElement competenceElement) {
            super("Delete choice", competenceElement);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Competence parentCompetence = dataNode.getParent();
            parentCompetence.removeElement(dataNode);
        }
    }

    /**
     * Append new sense as first trigger sense of @choice.
     *
     * @param choice Choice that will have new first trigger sense.
     * @return
     */
    public static Action appendSenseAction(CompetenceElement choice) {
        return new AppendChoiceTriggerSenseAction(choice);
    }

    private static class AppendChoiceTriggerSenseAction extends AbstractMenuAction<CompetenceElement> {

        public AppendChoiceTriggerSenseAction(CompetenceElement dataNode) {
            super("Add sense", dataNode);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            String newSenseName = this.getIdentifierFromDialog("Name of the sense");
            if (newSenseName != null) {
                Trigger<CompetenceElement> choiceTrigger = dataNode.getTrigger();
                choiceTrigger.add(0, LapElementsFactory.createSense(newSenseName));
            }
        }
    }

    /**
     * Delete specified sense from its parent.
     *
     * @param sense Sense to be deleted.
     * @return
     */
    public static Action deleteSenseAction(Sense sense) {
        return new DeleteSenseAction(sense);
    }

    private static final class DeleteSenseAction extends AbstractMenuAction<Sense> {

        public DeleteSenseAction(Sense sense) {
            super("Delete sense", sense);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            dataNode.removeFromParent();
        }
    }

    /**
     * Append a new sense at index one greater than is @sense.
     *
     * @param sense Sense used as an anchor for new sense.
     * @return
     */
    public static Action appendSenseAction(Sense sense) {
        return new AppendSenseAction(sense);
    }

    private static class AppendSenseAction extends AbstractMenuAction<Sense> {

        private AppendSenseAction(Sense sense) {
            super("Append sense", sense);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            String newSenseName = getIdentifierFromDialog("Name of new sense");
            if (newSenseName != null) {
                Trigger<?> trigger = dataNode.getTrigger();
                int position = trigger.indexOf(dataNode);
                trigger.add(position + 1, LapElementsFactory.createSense(newSenseName));
            }
        }
    }

    /**
     * Action that will add new choice right behind @choice.
     *
     * @param choice Anchor choice, the new choice will be appended right behind
     * this one
     * @return
     */
    public static Action appendChoiceAction(CompetenceElement choice) {
        return new AppendChoiceMenuAction(choice);
    }

    private static class AppendChoiceMenuAction extends AbstractMenuAction<CompetenceElement> {

        private AppendChoiceMenuAction(CompetenceElement choice) {
            super("Append choice", choice);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Competence parentCompetence = dataNode.getParent();
            int anchorChoiceIndex = parentCompetence.getChildDataNodes().indexOf(dataNode);

            String newChoiceName = getIdentifierFromDialog("Name of the choice");
            if (newChoiceName == null) {
                return;
            }
            try {
                CompetenceElement newChoice = LapElementsFactory.createCompetenceElement(newChoiceName);
                parentCompetence.addElement(anchorChoiceIndex + 1, newChoice);
            } catch (DuplicateNameException ex) {
                String errorMessage = MessageFormat.format("There already is a choice with name {0}.", newChoiceName);
                errorDialog(errorMessage);
            }
        }
    }

    /**
     * Add new action as a child of the AP.
     *
     * @param actionPattern AP that will have new child.
     * @param index index at which to insert the action.
     * @return
     */
    public static Action appendAction(ActionPattern actionPattern, int index) {
        return new AddAPActionAction(actionPattern, index);
    }

    private static class AddAPActionAction extends AbstractMenuAction<ActionPattern> {

        private final int index;

        public AddAPActionAction(ActionPattern actionPattern, int index) {
            super("Add action", actionPattern);
            this.index = index;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            String newActionName = getIdentifierFromDialog("Get name of action");
            if (newActionName != null) {
                try {
                    dataNode.addAction(index, LapElementsFactory.createAction(newActionName));
                } catch (CycleException ex) {
                    String errorMessage = MessageFormat.format("Action with name {0} would cause a cycle.", newActionName);
                    errorDialog(errorMessage);
                }
            }
        }
    }

    public static Action deleteAction(ActionPattern actionPattern, TriggeredAction action) {
        return new DeleteAPAction(actionPattern, action);
    }

    private static class DeleteAPAction extends AbstractMenuAction<ActionPattern> {

        private final TriggeredAction action;

        private DeleteAPAction(ActionPattern actionPattern, TriggeredAction action) {
            super("Delete action", actionPattern);
            this.action = action;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            int actionPosition = dataNode.getActions().indexOf(action);
            TriggeredAction actionToRemove = dataNode.getAction(actionPosition);
            dataNode.removeAction(actionToRemove);
        }
    }

    /**
     * Create action that will show dialog with parameters of AP and allows user
     * to modify them.
     *
     * @param actionPattern AP which parameters will be subject to change.
     * @return Created action
     */
    public static Action changeActionPatternParameters(ActionPattern actionPattern) {
        return new ChangeParameters<ActionPattern>("Change parameters of AP", "Parameters of action pattern", actionPattern);
    }

    /**
     * Create action that will show dialog with parameters of {@link Competence}
     * and allows user to modify them.
     *
     * @param actionPattern AP which parameters will be subject to change.
     * @return Created action
     */
    public static Action changeCompetenceParameters(Competence competence) {
        return new ChangeParameters<Competence>("Change parameters of competence", "Parameters of competence", competence);

    }

    /**
     * Edit parameters of passed node. Create input line dialog in whcih user
     * can directly manipulate parameters of the node, e.g. node has one
     * parameter $degrees=5, the dialog will show line
     * <code>vars ($degrees=5)</code> and user will change it to
     * <code>vars ($degrees=10, $animation="dance.ani")</code>.
     *
     * @param <NODE> Node with parameters.
     */
    private static class ChangeParameters<NODE extends PoshElement & IParametrizedElement> extends AbstractMenuAction<NODE> {

        private final String dialogTitle;

        /**
         * Create action for changing parameters.
         *
         * @param actionName Name of action used in menu or button.
         * @param dialogTitle Title for dialog, where user inputs parameters.
         * @param dataNode
         */
        public ChangeParameters(String actionName, String dialogTitle, NODE dataNode) {
            super(actionName, dataNode);
            this.dialogTitle = dialogTitle;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            String title = dialogTitle;
            String text = "Parameters:";

            NotifyDescriptor.InputLine input = new NotifyDescriptor.InputLine(text, title);
            FormalParameters params = dataNode.getParameters();
            input.setInputText("vars(" + params.toString() + ")");

            Object result = DialogDisplayer.getDefault().notify(input);

            if (result != NotifyDescriptor.OK_OPTION) {
                return;
            }

            String userInput = input.getInputText().trim();
            try {
                FormalParameters newParams;
                if (userInput.isEmpty() || userInput.matches("^vars *\\( *\\)$")) {
                    newParams = new FormalParameters();
                } else {
                    PoshParser parser = new PoshParser(new StringReader(userInput));
                    newParams = parser.parameters();
                }
                dataNode.setParameters(newParams);
            } catch (ParseException ex) {
                String msg = MessageFormat.format("Unable to parse parameters from:\n{0}\nError message: ", userInput, ex.getMessage());
                errorDialog(msg);
            }
        }
    }
}
