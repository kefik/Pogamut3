package cz.cuni.pogamut.shed.presenter;

import cz.cuni.amis.pogamut.sposh.elements.ActionPattern;
import cz.cuni.amis.pogamut.sposh.elements.IConditionElement;
import cz.cuni.amis.pogamut.sposh.elements.LapPath;
import cz.cuni.amis.pogamut.sposh.elements.PoshElement;
import cz.cuni.pogamut.shed.widget.*;

/**
 * Interface for factory creating the presenters for yaposh scene. Presenters
 * are responsible for updating the scene according to underlying changes of the
 * plan.
 *
 *
 * Be careful when implementing the presenters, you can't store {@link LapPath}
 * in them, because stuff can move around.
 *
 * @author Honza
 */
public interface IPresenterFactory {

    /**
     * Create presenter for primitive action. The action doesn't have any
     * children. Presenter reflects changes in name and arguments of action.
     *
     * @param actionPath Path to the action in the plan, ends with ../A:?.
     * @param actionWidget The widget that represents the @action in the scene.
     * @return Presenter for action
     */
    IPresenter createActionPresenter(LapPath actionPath, ShedWidget actionWidget);

    /**
     * Create presenter for expanded action, i.e. the whole expanded structure
     * that can happen from unknown reference. The presenter for expanded action
     * should handle when the action is changed (the name of referenced action). {@link ExpandedActionEnvelope}
     * is container for one widget: {@link AttachmentEnvelope} &mdash; the
     * widget that really contains expanded action.
     *
     * When action reference changes its name, the whole {@link AttachmentEnvelope}
     * in the {@link ExpandedActionEnvelope} is removed and replaced with new
     * one.
     *
     * @param actionPath Path to the @action.
     * @param envelope Envelope that contains expanded action in the scene
     * @return Presenter for expanded action.
     */
    IPresenter createExpandedActionPresenter(LapPath actionPath, ExpandedActionEnvelope envelope);

    /**
     * Create presenter for sense at @sensePath that will present the sense in
     * the @senseWidget. Presenter should reflect changes in name and arguments
     * of the sense.
     *
     * @param sensePath Path to the sense in the plan we want to present.
     * @param senseWidget Widget that represents the sense in the scene.
     * @return Presenter of sense
     */
    IPresenter createSensePresenter(LapPath sensePath, ShedSenseWidget senseWidget);

    // TODO: Once jenkins have JDK7, use TRIGGER_PARENT extends PoshElement & IConditionElement
    /**
     * Create presenter for trigger, it is responsible for keepeing track of
     * added/moved/removed senses.
     *
     * @param <TRIGGER_PARENT> Owner of the trigger, e.g. for a trigger of a
     * drive, the drive would be the owner.
     * @param triggerOwnerPath Path to the node the trigger belongs to.
     * @param triggerEnvelope Envelope representing the trigger in the scene.
     * @return
     */
    <TRIGGER_PARENT extends PoshElement> IPresenter createTriggerPresenter(LapPath triggerOwnerPath, ShedTriggerEnvelope triggerEnvelope);

    /**
     * Create presenter for drive collection, responsible for keeping track of
     * added/moved/removed drives.
     *
     * @param driveCollectionPath Path to the DC
     * @return
     */
    IPresenter createDriveCollectionPresenter(LapPath driveCollectionPath);

    /**
     * Create presenter for action pattern widget. This doesn't affect anything
     * else(APs actions...), only the @actionPatternWidget. The presenter should
     * update widget when its properties, e.g. parameters are changed.
     *
     * The presenter will take referencing action and the AP object from the
     * @actionPatternPath.
     *
     * @param actionPatternPath Path to the action pattern, path ends with
     * <tt>../A:?/AP:?</tt>
     * @param actionPatternWidget Widget representing the AP.
     * @return
     */
    IPresenter createActionPatternPresenter(LapPath actionPatternPath, ShedWidget actionPatternWidget);

    /**
     * Create presenter for competence widget. The presenter is responsible only
     * for the widget of competence, not for its choices ect. It should for
     * example update the widget when arguments or parameters of link are
     * changed.
     *
     * @param competencePath Path to the competence, path ends with
     * <tt>../A:?/C:?</tt>
     * @param competenceWidget Widget representing the competence in the scene.
     * @return
     */
    IPresenter createCompetencePresenter(LapPath competencePath, ShedWidget competenceWidget);

    /**
     * Create presenter for choice widget, only the widget, not its trigger nor
     * action. Basically it is responsible only for showing changes of choice
     * name in the widget.
     *
     * @param choicePath Path to the choice, it will end with
     * <tt>../A:?/C:?/CE:?</tt>
     * @param choiceWidget Widget representing the choice in the scene.
     * @return
     */
    IPresenter createChoicePresenter(LapPath choicePath, ShedWidget choiceWidget);

    /**
     * Create presenter for drive widget. It reflects only changes of drive
     * widget, not its trigger or action. Overall it only reflects chganges of
     * name.
     *
     * @param drivePath Path to drive, <tt>../DE:?</tt>
     * @param widget Widget representing the drive in the scene.
     * @return
     */
    IPresenter createDrivePresenter(LapPath drivePath, ShedWidget widget);

    /**
     * Create presenter that will take care about keeping adding/moving/removing
     * expanded actions in the {@link ShedActionsEnvelope} for {@link ActionPattern}.
     *
     * @param actionPatternPath Path to the action pattern, ends with
     * <tt>../AP:?</tt>.
     * @param actionsEnvelope Envelope that contains representations of the
     * actions.
     * @return
     */
    IPresenter createActionsPresenter(LapPath actionPatternPath, ShedActionsEnvelope actionsEnvelope);

    /**
     * Create presenter responsible for adding, moving and removing {@link ShedChoiceEnvelope choices widgets}
     * in the {@link ShedChoicesEnvelope}.
     *
     * @param competencePath Path to the competence whose choices are managed.
     * Ends with <tt>../A:?/C:?</tt>.
     * @param choicesEnvelope Envelope containing the representations of
     * presented choices.
     */
    IPresenter createChoicesPresenter(LapPath competencePath, ShedChoicesEnvelope choicesEnvelope);
}
