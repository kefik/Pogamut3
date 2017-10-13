package cz.cuni.pogamut.shed.presenter;

import cz.cuni.amis.pogamut.sposh.elements.*;
import cz.cuni.pogamut.shed.widget.ExpandedActionEnvelope;
import cz.cuni.pogamut.shed.widget.ShedActionsEnvelope;
import cz.cuni.pogamut.shed.widget.ShedChoicesEnvelope;
import cz.cuni.pogamut.shed.widget.ShedScene;
import cz.cuni.pogamut.shed.widget.ShedSenseWidget;
import cz.cuni.pogamut.shed.widget.ShedTriggerEnvelope;
import cz.cuni.pogamut.shed.widget.ShedWidget;

/**
 * Factory that is creating various presenters used in the Shed visual view.
 *
 * @author Honza
 */
public class ShedPresenterFactory implements IPresenterFactory {

    /**
     * Scene that is showing all widgets and other stuff. Passed to presenters
     * so they can update scene and create other widgets.
     */
    private final ShedScene scene;
    /**
     * Presenter is giving access to name mapping of primitives.
     */
    private final ShedPresenter presenter;
    /**
     * Plan that is being presented.
     */
    private final PoshPlan plan;

    public ShedPresenterFactory(ShedScene scene, PoshPlan plan, ShedPresenter presenter) {
        this.plan = plan;
        this.scene = scene;
        this.presenter = presenter;
    }

    @Override
    public IPresenter createActionPresenter(LapPath actionPath, ShedWidget actionWidget) {
        LapChain actionChain = LapChain.fromPath(plan, actionPath);
        TriggeredAction action = actionPath.traversePath(plan);
        return new ActionPresenter(scene, presenter, action, actionWidget, actionChain);
    }

    @Override
    public IPresenter createExpandedActionPresenter(LapPath actionPath, ExpandedActionEnvelope envelope) {
        LapChain chainWithoutAction = LapChain.fromPath(plan, actionPath.subpath(0, actionPath.length() - 1));
        TriggeredAction action = actionPath.traversePath(plan);
        return new ExpandedActionPresenter(scene, presenter, envelope, action, chainWithoutAction);
    }

    @Override
    public IPresenter createSensePresenter(LapPath sensePath, ShedSenseWidget senseWidget) {
        LapChain senseChain = LapChain.fromPath(plan, sensePath);
        Sense sense = sensePath.traversePath(plan);
        return new SensePresenter(scene, presenter, sense, senseWidget, senseChain);
    }

    @Override
    public <TRIGGER_PARENT extends PoshElement> IPresenter createTriggerPresenter(LapPath triggerOwner, ShedTriggerEnvelope triggerEnvelope) {
        LapChain ownerChain = LapChain.fromPath(plan, triggerOwner);
        PoshElement owner = triggerOwner.traversePath(plan);
        Trigger<TRIGGER_PARENT> condition = ((IConditionElement)owner).getCondition();
        return new TriggerPresenter(scene, presenter, triggerEnvelope, owner, condition, ownerChain);
    }

    @Override
    public IPresenter createDriveCollectionPresenter(LapPath driveCollectionPath) {
        DriveCollection driveCollection = driveCollectionPath.traversePath(plan);
        return new DCPresenter(scene, presenter, driveCollection);
    }

    @Override
    public IPresenter createActionPatternPresenter(LapPath actionPatternPath, ShedWidget actionPatternWidget) {
        ActionPattern actionPattern = actionPatternPath.traversePath(plan); 
        LapPath referencePath = actionPatternPath.subpath(0, actionPatternPath.length() - 1);
        TriggeredAction referencingAction = referencePath.traversePath(plan);
        LapChain actionPatternChain = LapChain.fromPath(plan, actionPatternPath);

        return new ActionPatternPresenter(scene, presenter, referencingAction, actionPattern, actionPatternWidget, actionPatternChain);
    }

    @Override
    public IPresenter createCompetencePresenter(LapPath competencePath, ShedWidget competenceWidget) {
        LapChain competenceChain = LapChain.fromPath(plan, competencePath);
        Competence competence = competencePath.traversePath(plan);
        LapPath referencePath = competencePath.subpath(0, competencePath.length() - 1);
        TriggeredAction referencingAction = referencePath.traversePath(plan);

        return new CompetencePresenter(scene, presenter, referencingAction, competence, competenceWidget, competenceChain);
    }

    @Override
    public IPresenter createChoicePresenter(LapPath choicePath, ShedWidget choiceWidget) {
        CompetenceElement choice = choicePath.traversePath(plan);
        return new ChoicePresenter(scene, presenter, choice, choiceWidget);
    }

    @Override
    public IPresenter createDrivePresenter(LapPath drivePath, ShedWidget widget) {
        DriveElement drive = drivePath.traversePath(plan);
        return new DrivePresenter(scene, presenter, drive, widget);
    }

    @Override
    public IPresenter createActionsPresenter(LapPath actionPatternPath, ShedActionsEnvelope actionsEnvelope) {
        LapChain actionPatternChain = LapChain.fromPath(plan, actionPatternPath);
        ActionPattern actionPattern = actionPatternPath.traversePath(plan);
        return new ActionsPresenter(scene, presenter, actionsEnvelope, actionPattern, actionPatternChain);
    }

    @Override
    public IPresenter createChoicesPresenter(LapPath competencePath, ShedChoicesEnvelope choicesEnvelope) {
        LapChain competenceChain = LapChain.fromPath(plan, competencePath);
        Competence competence = competencePath.traversePath(plan);
        return new ChoicesPresenter(scene, presenter, choicesEnvelope, competence, competenceChain);
    }
}
