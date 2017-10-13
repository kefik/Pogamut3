package cz.cuni.pogamut.shed.presenter;

import cz.cuni.amis.pogamut.sposh.elements.ActionPattern;
import cz.cuni.amis.pogamut.sposh.elements.Arguments;
import cz.cuni.amis.pogamut.sposh.elements.Competence;
import cz.cuni.amis.pogamut.sposh.elements.CompetenceElement;
import cz.cuni.amis.pogamut.sposh.elements.DriveCollection;
import cz.cuni.amis.pogamut.sposh.elements.DriveElement;
import cz.cuni.amis.pogamut.sposh.elements.LapElementsFactory;
import cz.cuni.amis.pogamut.sposh.elements.PoshElement;
import cz.cuni.amis.pogamut.sposh.elements.PoshPlan;
import cz.cuni.amis.pogamut.sposh.elements.Sense;
import cz.cuni.amis.pogamut.sposh.elements.Trigger;
import cz.cuni.amis.pogamut.sposh.elements.TriggeredAction;
import cz.cuni.amis.pogamut.sposh.exceptions.CycleException;
import cz.cuni.amis.pogamut.sposh.exceptions.DuplicateNameException;
import cz.cuni.amis.pogamut.sposh.exceptions.FubarException;
import cz.cuni.amis.pogamut.sposh.exceptions.InvalidNameException;
import java.text.MessageFormat;
import org.openide.NotifyDescriptor;
import org.openide.util.Exceptions;

/**
 * Factory for accept actions used for presenter ({@link IPresenter#getAcceptProviders()
 * }).
 *
 * @author Honza Havlicek
 */
public class AcceptActionFactory {

    /**
     * When sense is dropped to the @drive, remove the sense from its parent (it
     * it has one) and add it as first trigger sense of the drive.
     *
     * @param drive Drive whcih will accept the sense
     * @return
     */
    public static AbstractAcceptAction<Sense, DriveElement> createSense2Drive(DriveElement drive) {
        return new DriveAppendSenseAcceptAction(drive);
    }

    private static class DriveAppendSenseAcceptAction extends AbstractAcceptAction<Sense, DriveElement> {

        public DriveAppendSenseAcceptAction(DriveElement targetDrive) {
            super(Sense.dataFlavor, targetDrive);
        }

        @Override
        public void performAction(Sense droppedSense) {
            PoshElement senseParent = droppedSense.getParent();
            if (senseParent != null) {
                droppedSense.removeFromParent();
            }
            dataNode.getTrigger().add(0, droppedSense);
        }
    }

    /**
     * Create accept action for the @sense that will accept another sense at the
     * position of the target. If the dropped sense is in same trigger, use
     * {@link Trigger#moveSense(int, cz.cuni.amis.pogamut.sposh.elements.Sense)
     * }, if it is another trigger, delete dropped sense from the original
     * trigger and add it to the target trigger. If the sense is newly created
     * one, simply add it.
     *
     * @param sense Sense that will accept another sense that will be dropped at
     * it.
     * @return
     */
    public static AbstractAcceptAction<Sense, Sense> createSense2Sense(Sense sense) {
        return new InsertSenseAcceptAction(sense);
    }

    private static class InsertSenseAcceptAction extends AbstractAcceptAction<Sense, Sense> {

        private InsertSenseAcceptAction(Sense sense) {
            super(Sense.dataFlavor, sense);
        }

        @Override
        public void performAction(Sense droppedSense) {
            PoshElement droppedSenseParent = droppedSense.getParent();
            PoshElement targetSenseParent = dataNode.getParent();
            if (droppedSenseParent == targetSenseParent) {
                int targetIndex = dataNode.getTrigger().indexOf(dataNode);

                Trigger<?> targetTrigger = dataNode.getTrigger();
                targetTrigger.moveSense(targetIndex, droppedSense);
            } else if (droppedSenseParent == null) {
                Trigger<?> targetTrigger = dataNode.getTrigger();
                int targetIndex = targetTrigger.indexOf(dataNode);
                targetTrigger.add(targetIndex, droppedSense);
            } else {
                droppedSense.getTrigger().remove(droppedSense);

                Trigger<?> targetTrigger = dataNode.getTrigger();
                int targetIndex = targetTrigger.indexOf(dataNode);
                targetTrigger.add(targetIndex, droppedSense);
            }
        }
    }

    /**
     * Move the drive in the {@link DriveCollection}.
     *
     * @param drive Drive that will be the target, the one that when another
     * drive is dropped at it, will be movied to make place for the antoher
     * drive.
     * @return
     */
    public static AbstractAcceptAction<DriveElement, DriveElement> createDrive2Drive(DriveElement drive) {
        return new InsertDriveAcceptAction(drive);
    }

    private static class InsertDriveAcceptAction extends AbstractAcceptAction<DriveElement, DriveElement> {

        public InsertDriveAcceptAction(DriveElement drive) {
            super(DriveElement.dataFlavor, drive);
        }

        @Override
        public void performAction(DriveElement droppedDrive) {
            DriveCollection droppedDC = droppedDrive.getParent();
            DriveCollection targetDC = dataNode.getParent();

            assert droppedDC == targetDC;

            int targetIndex = targetDC.getDrives().indexOf(dataNode);
            targetDC.moveChild(targetIndex, droppedDrive);
        }
    }

    /**
     * Accept action for dropping choice to competence. Removes choice from its
     * original place and adds it as first child of @competence.
     *
     * @param competence Competence that will accept dropped chocie
     * @return Created action.
     */
    public static AbstractAcceptAction createChoice2Competence(Competence competence) {
        return new InsertChoice2CompetenceAcceptAction(competence);
    }

    private static class InsertChoice2CompetenceAcceptAction extends AbstractAcceptAction<CompetenceElement, Competence> {

        public InsertChoice2CompetenceAcceptAction(Competence competence) {
            super(CompetenceElement.dataFlavor, competence);
        }

        @Override
        public void performAction(CompetenceElement droppedChoice) {
            Competence droppedC = droppedChoice.getParent();

            if (droppedC == dataNode) {
                dataNode.moveChild(0, droppedChoice);
                return;
            }
            try {
                dataNode.addElement(0, LapElementsFactory.createCompetenceElement(droppedChoice));
            } catch (DuplicateNameException ex) {
                displayMessage("Competence " + dataNode.getName() + " already has a choice with name " + droppedChoice.getName() + ". Unable to convert choice to drive.", NotifyDescriptor.ERROR_MESSAGE);
                return;
            }
            droppedC.removeElement(droppedChoice);
        }
    }

    /**
     * Accept choice dropped onto a drive into a {@link DriveCollection}.
     *
     * @param drive Drive the choice is dropped onto.
     * @return Accepting action
     */
    public static AbstractAcceptAction createChoice2Drive(DriveElement drive) {
        return new InsertChoice2DriveAcceptAction(drive);
    }

    private static class InsertChoice2DriveAcceptAction extends AbstractAcceptAction<CompetenceElement, DriveElement> {

        public InsertChoice2DriveAcceptAction(DriveElement drive) {
            super(CompetenceElement.dataFlavor, drive);
        }

        @Override
        public void performAction(CompetenceElement droppedChoice) {
            Competence droppedC = droppedChoice.getParent();

            try {
                DriveCollection targetDC = dataNode.getParent();
                int targetIndex = targetDC.getDrives().indexOf(dataNode);
                targetDC.addDrive(targetIndex, LapElementsFactory.createDriveElement(droppedChoice));
            } catch (DuplicateNameException ex) {
                displayMessage("Drive collection already has a drive with name " + droppedChoice.getName() + ". Unable to convert choice to drive.", NotifyDescriptor.ERROR_MESSAGE);
                return;
            }
            droppedC.removeElement(droppedChoice);
        }
    }

    /**
     * Accept drive dropped onto a choice into a {@link Competence}.
     *
     * @param choice Choice the drive is dropped onto
     * @return Accepting action
     */
    public static AbstractAcceptAction createDrive2Choice(CompetenceElement choice) {
        return new InsertDrive2ChoiceAcceptAction(choice);
    }

    private static class InsertDrive2ChoiceAcceptAction extends AbstractAcceptAction<DriveElement, CompetenceElement> {

        public InsertDrive2ChoiceAcceptAction(CompetenceElement choice) {
            super(DriveElement.dataFlavor, choice);
        }

        @Override
        public void performAction(DriveElement droppedDrive) {
            DriveCollection droppedDC = droppedDrive.getParent();

            Competence targetC = dataNode.getParent();
            try {
                int targetIndex = targetC.getChildId(dataNode);
                targetC.addElement(targetIndex, LapElementsFactory.createCompetenceElement(droppedDrive));
            } catch (DuplicateNameException ex) {
                displayMessage("Competence " + targetC.getName() + " already has a choice with name " + droppedDrive.getName() + ". Unable to convert drive to choice.", NotifyDescriptor.ERROR_MESSAGE);
                return;
            }
            droppedDC.removeDrive(droppedDrive);
        }
    }

    /**
     * Allow drive to be dropped onto a competence, convert drive to choice and
     * add it as first child of competence.
     *
     * @param competence Competence at which the drive will be dropped
     * @return Created accept action
     */
    public static AbstractAcceptAction createDrive2Competence(Competence competence) {
        return new InsertDrive2CompetenceAcceptAction(competence);
    }

    private static class InsertDrive2CompetenceAcceptAction extends AbstractAcceptAction<DriveElement, Competence> {

        public InsertDrive2CompetenceAcceptAction(Competence competence) {
            super(DriveElement.dataFlavor, competence);
        }

        @Override
        public void performAction(DriveElement droppedDrive) {
            DriveCollection droppedDC = droppedDrive.getParent();

            try {
                dataNode.addElement(0, LapElementsFactory.createCompetenceElement(droppedDrive));
            } catch (DuplicateNameException ex) {
                displayMessage("Competence " + dataNode.getName() + " already has a choice with name " + droppedDrive.getName() + ". Unable to convert drive to choice.", NotifyDescriptor.ERROR_MESSAGE);
                return;
            }
            droppedDC.removeDrive(droppedDrive);
        }
    }

    /**
     * Create accept action for the @targetChoice for another {@link CompetenceElement}.
     * When user drops a {@link CompetenceElement} to the @targetChoice, it will
     * check if the dropped choice is in same {@link Competence} as the
     * @targetChoice and if it is, it moves the choice. If dropped choice is
     * from another competence, it removes it from its comeptence and drops it
     * at the index of the @targetChoice.
     *
     * @param targetChoice Choice that will be accepting another choice to be
     * dropped on it.
     * @return
     */
    public static AbstractAcceptAction<CompetenceElement, CompetenceElement> createChoice2Choice(CompetenceElement targetChoice) {
        return new InsertChoiceAcceptAction(targetChoice);
    }

    private static class InsertChoiceAcceptAction extends AbstractAcceptAction<CompetenceElement, CompetenceElement> {

        public InsertChoiceAcceptAction(CompetenceElement targetChoice) {
            super(CompetenceElement.dataFlavor, targetChoice);
        }

        @Override
        public void performAction(CompetenceElement droppedChoice) {
            Competence droppedCompetence = droppedChoice.getParent();
            Competence targetCompetence = dataNode.getParent();

            int targetIndex = targetCompetence.getChildDataNodes().indexOf(dataNode);
            if (droppedCompetence == targetCompetence) {
                targetCompetence.moveChild(targetIndex, droppedChoice);
            } else {
                String droppedChoiceName = droppedChoice.getName();
                if (choiceNameIsUsed(targetCompetence, droppedChoiceName)) {
                    String errorMessage = MessageFormat.format("Choice with name \"{0}\" is already present in the competence \"{1}\"", droppedChoiceName, targetCompetence.getName());
                    displayMessage(errorMessage, NotifyDescriptor.ERROR_MESSAGE);
                    return;
                }

                droppedCompetence.removeElement(droppedChoice);
                try {
                    targetCompetence.addElement(targetIndex, droppedChoice);
                } catch (DuplicateNameException ex) {
                    throw new FubarException("Thgis is checked before.", ex);
                }
            }
        }

        private boolean choiceNameIsUsed(Competence testedCompetence, String name) {
            for (CompetenceElement choice : testedCompetence.getChildDataNodes()) {
                if (choice.getName().equals(name)) {
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * Create an accept action for the @targetAction that will accept an
     * competence, when dropped on it. If competence is a new one, it will also
     * be added to the plan.
     *
     * @param targetAction
     * @return
     */
    public static AbstractAcceptAction<Competence, TriggeredAction> createCompetence2Action(TriggeredAction targetAction) {
        return new CompetenceAcceptAction(targetAction);
    }

    private static class CompetenceAcceptAction extends AbstractAcceptAction<Competence, TriggeredAction> {

        public CompetenceAcceptAction(TriggeredAction targetAction) {
            super(Competence.dataFlavor, targetAction);
        }

        @Override
        public void performAction(Competence droppedCompetence) {
            String competenceName = droppedCompetence.getName();
            PoshPlan lapTree = dataNode.getRootNode();
            if (droppedCompetence.getParent() == null) {
                try {
                    lapTree.addCompetence(droppedCompetence);
                } catch (DuplicateNameException ex) {
                    String errorMessage = MessageFormat.format("Competence with name \"{0}\" is already present in the plan.", competenceName);
                    displayMessage(errorMessage, NotifyDescriptor.ERROR_MESSAGE);
                    return;
                } catch (CycleException ex) {
                    String errorMessage = MessageFormat.format("Adding a competence with name \"{0}\" would create a cycle.", competenceName);
                    displayMessage(errorMessage, NotifyDescriptor.ERROR_MESSAGE);
                    return;
                }
            } else {
                assert droppedCompetence.getParent() == lapTree;
                assert lapTree.getC(competenceName) == droppedCompetence;
            }

            try {
                dataNode.setActionName(competenceName);
            } catch (InvalidNameException ex) {
                String errorMessage = MessageFormat.format("Action can't have name \"{0}\", but the competence has it. This seriously shouldn't happen. Report.", competenceName);
                displayMessage(errorMessage, NotifyDescriptor.ERROR_MESSAGE);
            } catch (CycleException ex) {
                String errorMessage = MessageFormat.format("Referencing the competence with name \"{0}\" would create a cycle.", competenceName);
                displayMessage(errorMessage, NotifyDescriptor.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Create accept action the the {@link TriggeredAction} that will accept the {@link ActionPattern}.
     * If the pattern is not part of the {@link PoshPlan}, add it. Once we are
     * sure that AP is in the plan, change reference of the @targetAction to the
     * AP.
     *
     * @param targetAction Target action that will accept dropped AP.
     * @return
     */
    public static AbstractAcceptAction<ActionPattern, TriggeredAction> createActionPatternAction(TriggeredAction targetAction) {
        return new ActionPatternAcceptAction(targetAction);
    }

    private static class ActionPatternAcceptAction extends AbstractAcceptAction<ActionPattern, TriggeredAction> {

        public ActionPatternAcceptAction(TriggeredAction targetAction) {
            super(ActionPattern.dataFlavor, targetAction);
        }

        @Override
        public void performAction(ActionPattern droppedActionPattern) {
            String actionPatternName = droppedActionPattern.getName();
            PoshPlan lapTree = dataNode.getRootNode();
            if (droppedActionPattern.getParent() == null) {
                try {
                    lapTree.addActionPattern(droppedActionPattern);
                } catch (DuplicateNameException ex) {
                    String errorMessage = MessageFormat.format("Action pattern with name \"{0}\" is already present in the plan.", actionPatternName);
                    displayMessage(errorMessage, NotifyDescriptor.ERROR_MESSAGE);
                    return;
                } catch (CycleException ex) {
                    String errorMessage = MessageFormat.format("Adding an action pattern with name \"{0}\" would create a cycle.", actionPatternName);
                    displayMessage(errorMessage, NotifyDescriptor.ERROR_MESSAGE);
                    return;
                }
            } else {
                assert droppedActionPattern.getParent() == lapTree;
                assert lapTree.getAP(actionPatternName) == droppedActionPattern;
            }

            try {
                dataNode.setActionName(actionPatternName);
            } catch (InvalidNameException ex) {
                String errorMessage = MessageFormat.format("Action can't have name \"{0}\", but the action pattern has it. This seriously shouldn't happen. Report.", actionPatternName);
                displayMessage(errorMessage, NotifyDescriptor.ERROR_MESSAGE);
            } catch (CycleException ex) {
                String errorMessage = MessageFormat.format("Referencing the action pattern with name \"{0}\" would create a cycle.", actionPatternName);
                displayMessage(errorMessage, NotifyDescriptor.ERROR_MESSAGE);
            }
        }
    }

    /**
     * This is complex accept action for an action with any parent.
     *
     * It is obvious that accept action for action in AP (insert action) should
     * have different behavior from action in the DE (change action reference).
     * This unified accept action can be used no matter what is the parent of
     * the target action and no matter what is the parent of the source action.
     *
     * Possible parents of source action: {@link ActionPattern}, {@link CompetenceElement}, {@link DriveElement}
     * and none for newly created action from palette.
     *
     * Possible parents of target action: {@link ActionPattern}, {@link CompetenceElement}, {@link DriveElement}.
     *
     * That gives us 12 combinations, that can be categorized like this: If
     * source is AP, the source action will be removed from the AP, otherwise
     * nothing will happen to the source. If target is AP, action will be
     * inserted at the index of the target action, otherwise the target action
     * will be synchronized with the source.
     *
     * If both target and source are from same AP, move source to the index of
     * the target.
     *
     * @param action
     * @return
     */
    public static AbstractAcceptAction createAction2Action(TriggeredAction action) {
        return new ActionAcceptAction(action);
    }

    private static class ActionAcceptAction extends AbstractAcceptAction<TriggeredAction, TriggeredAction> {

        public ActionAcceptAction(TriggeredAction targetAction) {
            super(TriggeredAction.dataFlavor, targetAction);
        }

        @Override
        public void performAction(TriggeredAction droppedAction) {
            PoshElement droppedParent = droppedAction.getParent();
            boolean droppedFromAP = droppedParent instanceof ActionPattern;
            boolean droppedFromCE = droppedParent instanceof CompetenceElement;
            boolean droppedFromDE = droppedParent instanceof DriveElement;
            boolean droppedFromNowhere = droppedParent == null;

            PoshElement targetParent = dataNode.getParent();
            boolean targetInAP = dataNode.getParent() instanceof ActionPattern;

            boolean actionMoveInAP = droppedFromAP && targetInAP && droppedParent == targetParent;

            // the simplest case, move in the AP, special because of smooth animation.
            if (actionMoveInAP) {
                ActionPattern actionPattern = (ActionPattern) dataNode.getParent();
                int targetIndex = actionPattern.getChildDataNodes().indexOf(dataNode);
                actionPattern.moveChild(targetIndex, droppedAction);
            } else if (droppedFromAP) {

                /*
                 * NOTE: Write only code!
                 *
                 * This is tricky, see pages in Shed notebook. Basically there
                 * are 4 possible sources and 3 possible targets -> 12
                 * combination. When you make a table with possible case when
                 * cycle can happen, you will see that when removing action from
                 * AP, it will never cause a cycle (expanded action is branch,
                 * removal from AP-tree is still w/o cycle, adding it to
                 * DE/CE/AP will only add the branch), thus the removal from
                 * source AP will never need to be reversed. The rest (when
                 * source is CE/AP/None) doesn't do anything I need to anything
                 * that would have to be reversed if cycle happens.
                 */
                ActionPattern droppedActionPattern = (ActionPattern) droppedParent;
                droppedActionPattern.removeAction(droppedAction);
                try {
                    placeDroppedAction(droppedAction);
                } catch (CycleException ex) {
                    throw new FubarException("Cycle should never happen when dropping an action from AP. Report.", ex);
                }
            } else if (droppedFromCE || droppedFromDE || droppedFromNowhere) {
                try {
                    placeDroppedAction(droppedAction);
                } catch (CycleException ex) {
                    String errorMessage = MessageFormat.format("Referencing \"{0}\" would cause a cycle.", droppedAction.getName());
                    displayMessage(errorMessage, NotifyDescriptor.ERROR_MESSAGE);
                }
            } else {
                throw new IllegalArgumentException("Unknown dropped parent " + droppedParent.getClass());
            }
        }

        private void placeDroppedAction(TriggeredAction droppedAction) throws CycleException {
            PoshElement targetParent = dataNode.getParent();
            boolean targetInAP = targetParent instanceof ActionPattern;
            boolean targetInCE = targetParent instanceof CompetenceElement;
            boolean targetInDE = targetParent instanceof DriveElement;

            if (targetInCE || targetInDE) {
                dataNode.synchronize(droppedAction);
            } else if (targetInAP) {
                ActionPattern targetActionPattern = (ActionPattern) targetParent;
                int targetIndex = targetActionPattern.getActions().indexOf(dataNode);
                // the @droppedAction is actual action used somehwere in the plan. 
                // I don't want to reference it from two places.
                TriggeredAction droppedActionCopy = LapElementsFactory.createAction(droppedAction);
                targetActionPattern.addAction(targetIndex, droppedActionCopy);
            } else {
                throw new IllegalArgumentException("Unknown target parent " + dataNode.getClass());
            }
        }
    }

    /**
     * Create accept action where target choice will acc dropped sense as its
     * first trigger sense.
     *
     * @param targetChoice Choice that will accept having sense dropped at it.
     * @return
     */
    public static AbstractAcceptAction createSense2Choice(CompetenceElement targetChoice) {
        return new ChoiceAppendSenseAcceptAction(targetChoice);
    }

    // TODO: Unify with DriveAppendSenseAcceptAction, once we have parent of sense as triggerlike element.
    private static class ChoiceAppendSenseAcceptAction extends AbstractAcceptAction<Sense, CompetenceElement> {

        public ChoiceAppendSenseAcceptAction(CompetenceElement targetChoice) {
            super(Sense.dataFlavor, targetChoice);
        }

        @Override
        public void performAction(Sense droppedSense) {
            PoshElement senseParent = droppedSense.getParent();
            if (senseParent != null) {
                droppedSense.removeFromParent();
            }
            dataNode.getTrigger().add(0, droppedSense);
        }
    }
}
