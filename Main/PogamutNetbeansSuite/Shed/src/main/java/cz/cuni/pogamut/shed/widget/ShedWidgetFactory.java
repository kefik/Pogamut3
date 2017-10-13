package cz.cuni.pogamut.shed.widget;

import cz.cuni.amis.pogamut.sposh.elements.ActionPattern;
import cz.cuni.amis.pogamut.sposh.elements.Competence;
import cz.cuni.amis.pogamut.sposh.elements.CompetenceElement;
import cz.cuni.amis.pogamut.sposh.elements.DriveElement;
import cz.cuni.amis.pogamut.sposh.elements.LapPath;
import cz.cuni.amis.pogamut.sposh.elements.LapType;
import cz.cuni.amis.pogamut.sposh.elements.PoshElement;
import cz.cuni.amis.pogamut.sposh.elements.PoshPlan;
import cz.cuni.amis.pogamut.sposh.elements.Sense;
import cz.cuni.amis.pogamut.sposh.elements.Trigger;
import cz.cuni.amis.pogamut.sposh.elements.TriggeredAction;
import cz.cuni.pogamut.shed.presenter.AbstractAcceptAction;
import cz.cuni.pogamut.shed.presenter.IPresenter;
import cz.cuni.pogamut.shed.presenter.IPresenterFactory;
import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.JOptionPane;
import org.netbeans.api.visual.action.ActionFactory;
import org.netbeans.api.visual.action.WidgetAction;
import org.netbeans.api.visual.anchor.Anchor;
import org.netbeans.api.visual.layout.LayoutFactory;
import org.netbeans.api.visual.model.ObjectState;
import org.netbeans.api.visual.widget.Widget;

/**
 * Color scheme of the Shed in one place, provides default colors for the
 * widgets.
 *
 * @author HonzaH
 */
enum ShedWidgetColors {

    DRIVE(new Color(84, 118, 162)),
    SENSE(new Color(123, 191, 118)),
    ACTION_PATTERN(Color.PINK),
    COMPETENCE(Color.CYAN),
    ACTION(Color.ORANGE),
    CHOICE(new Color(142, 82, 165));
    public final Color color;

    private ShedWidgetColors(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return this.color;
    }
}

/**
 * Factory for creating various {@link ShedWidget shed widgets}.
 *
 * @author HonzaH
 */
public class ShedWidgetFactory {

    /**
     * Horizontal gap between {@link ShedWidget widgets}, generally also right
     * padding of widget envelope
     */
    protected static final int HORIZONTAL_GAP = 30;
    /**
     * Vertical gap between {@link ShedWidget widgets}, generally also bottom
     * padding of widget envelope
     */
    public static final int VERTICAL_GAP = 10;
    /**
     * Scene this factory is making widgets for.
     */
    private final ShedScene lapScene;
    /**
     * Factory that is instantiating presenters for widgets.
     */
    private final IPresenterFactory presenterFactory;
    /**
     * Plan that is this factory creating widgets for.
     */
    private final PoshPlan plan;

    /**
     * Create new factory.
     *
     * @param scene Scene for which the widgets will be created.
     * @param plan Plan whose elements are represented by the widgets.
     */
    ShedWidgetFactory(ShedScene scene, PoshPlan plan, IPresenterFactory presenterFactory) {
        this.plan = plan;
        this.lapScene = scene;
        this.presenterFactory = presenterFactory;
    }

    /**
     * Create a widget representing the sense and the presenter. Register
     * presenter.
     *
     * @param sensePath Path to the sense.
     * @param sense Sense the widget will represent
     * @return Newly created widget
     */
    public ShedSenseWidget createSenseWidget(LapPath sensePath) {
        Sense sense = sensePath.traversePath(plan);
        ShedSenseWidget senseWidget = new ShedSenseWidget(lapScene, sense, ShedWidgetColors.SENSE.getColor());

        IPresenter sensePresenter = presenterFactory.createSensePresenter(sensePath, senseWidget);

        attachPresenterToWidget(senseWidget, sensePresenter, sense);

        return senseWidget;
    }

    /**
     * Create envelope for the trigger of some element. The envelope will have
     * newly created {@link ShedSenseWidget}s and {@link SensePresenter}.
     *
     * @param parentPath Path to the @parent element with trigger, e.g.
     * <tt>../DE:1</tt>, <tt>../CE:0</tt> or <tt>../AD:4</tt>.
     * @param parent Posh element with the trigger.
     * @param trigger Trigger from which the {@link SensePresenter}s and {@link ShedSenseWidget}s
     * will be created and connected to.
     * @return Newly created envelope for the trigger sense widgets.
     */
    private <TRIGGER_PARENT extends PoshElement> ShedCreationContainer<ShedTriggerEnvelope> createTriggerEnvelope(LapPath parentPath, TRIGGER_PARENT parent, Anchor firstSenseAnchor, Trigger<TRIGGER_PARENT> trigger) {
        assert parentPath.traversePath(plan) == parent;

        ShedTriggerEnvelope envelope = new ShedTriggerEnvelope(lapScene, firstSenseAnchor);

        IPresenter triggerPresenter = presenterFactory.createTriggerPresenter(parentPath, envelope);
        triggerPresenter.register();

        ShedCreationContainer<ShedTriggerEnvelope> creationContainer = new ShedCreationContainer<ShedTriggerEnvelope>(envelope);

        Anchor sourceAnchor = firstSenseAnchor;
        // TODO: check that it works
        for (int senseId = 0; senseId < trigger.size(); senseId++) {
            LapPath sensePath = parentPath.concat(LapType.SENSE, senseId);
            ShedSenseWidget senseWidget = createSenseWidget(sensePath);
            envelope.add(senseWidget);

            ArrowWidget arrowWidget = new ArrowWidget(lapScene, sourceAnchor, senseWidget.getCommonAnchor());
            creationContainer.addArrow(arrowWidget);

            sourceAnchor = senseWidget.getCommonAnchor();
        }

        return creationContainer;
    }

    /**
     * Create proper attachment widget for the {@code action} and return it. The
     * created attachment is complete (i.e. all references are expanded).
     *
     * @param actionPath Path to the @action, ends with <tt>../A:?</tt>
     * @param action Action for which to create attachment. Must be part of the
     * lap tree.
     */
    public ShedCreationContainer<AttachmentEnvelope> createAttachmentEnvelope(LapPath actionPath, TriggeredAction action) {
        assert actionPath.traversePath(plan) == action;

        String actionName = action.getName();
        if (plan.isAP(actionName)) {
            ActionPattern actionPattern = plan.getAP(actionName);
            ShedWidget actionPatternWidget = new ShedWidget(lapScene, actionName, ShedWidgetColors.ACTION_PATTERN.getColor());

            int actionPatternId = plan.getActionPatternId(actionPattern);
            LapPath actionPatternPath = actionPath.concat(LapType.ACTION_PATTERN, actionPatternId);
            IPresenter apPresenter = presenterFactory.createActionPatternPresenter(actionPatternPath, actionPatternWidget);

            attachPresenterToWidget(actionPatternWidget, apPresenter, action);

            Anchor rightAPAnchor = new RightWidgetAnchor(actionPatternWidget);

            ShedCreationContainer<ShedActionsEnvelope> actionsContainer = createActionsEnvelope(actionPatternPath, rightAPAnchor);

            AttachmentEnvelope actionPatternEnvelope = new AttachmentEnvelope(lapScene);
            actionPatternEnvelope.setLayout(LayoutFactory.createHorizontalFlowLayout(LayoutFactory.SerialAlignment.LEFT_TOP, HORIZONTAL_GAP));

            actionPatternEnvelope.addChild(actionPatternWidget);
            actionPatternEnvelope.addChild(actionsContainer.getWidget());

            ShedCreationContainer<AttachmentEnvelope> actionPatternContainer = new ShedCreationContainer<AttachmentEnvelope>(actionPatternEnvelope);
            actionPatternContainer.addArrows(actionsContainer.getArrows());
            return actionPatternContainer;
        } else if (plan.isC(actionName)) {
            Competence competence = plan.getC(actionName);
            ShedWidget competenceWidget = new ShedWidget(lapScene, competence.getName(), ShedWidgetColors.COMPETENCE.getColor());
            int competenceId = plan.getCompetenceId(competence);
            LapPath competencePath = actionPath.concat(LapType.COMPETENCE, competenceId);
            IPresenter competencePresenter = presenterFactory.createCompetencePresenter(competencePath, competenceWidget);

            attachPresenterToWidget(competenceWidget, competencePresenter, action);

            Anchor rightCompetenceAnchor = new RightWidgetAnchor(competenceWidget);
            ShedCreationContainer<ShedChoicesEnvelope> choicesContainer = createChoicesEnvelope(competencePath, competence, rightCompetenceAnchor);

            AttachmentEnvelope competenceEnvelope = new AttachmentEnvelope(lapScene);
            competenceEnvelope.setLayout(LayoutFactory.createHorizontalFlowLayout(LayoutFactory.SerialAlignment.LEFT_TOP, HORIZONTAL_GAP));

            competenceEnvelope.addChild(competenceWidget);
            competenceEnvelope.addChild(choicesContainer.getWidget());

            ShedCreationContainer<AttachmentEnvelope> competenceContainer = new ShedCreationContainer<AttachmentEnvelope>(competenceEnvelope);
            competenceContainer.addArrows(choicesContainer.getArrows());
            return competenceContainer;
        } else { // action is normal action
            ShedWidget actionWidget = new ShedWidget(lapScene, actionName, ShedWidgetColors.ACTION.getColor());

            IPresenter actionPresenter = presenterFactory.createActionPresenter(actionPath, actionWidget);

            attachPresenterToWidget(actionWidget, actionPresenter, action);

            AttachmentEnvelope actionAttachment = new AttachmentEnvelope(lapScene);
            actionAttachment.addChild(actionWidget);

            return new ShedCreationContainer<AttachmentEnvelope>(actionAttachment);
        }
    }

    /**
     * Register widget as the moveable drag and drop plus register accept
     * providers from presenter.
     *
     * @param <LAP_ELEMENT>
     * @param widget widget that will be moveable and on whcih can be
     * @param presenter presenter that is used to get accept actions for widget.
     * @param element element that is used for drag and drop.
     */
    private <LAP_ELEMENT extends PoshElement> void attachPresenterToWidget(ShedWidget widget, IPresenter presenter, LAP_ELEMENT element) {
        WidgetAction editAction = presenter.getEditAction();
        if (editAction != null) {
            widget.getActions().addAction(editAction);
        }

        widget.getActions().addAction(ActionFactory.createMoveAction(
                ActionFactory.createFreeMoveStrategy(),
                new DragAndDropMoveProvider<LAP_ELEMENT>(lapScene, widget, element)));

        AbstractAcceptAction[] acceptActions = presenter.getAcceptProviders();
        if (acceptActions != null) {
            for (AbstractAcceptAction acceptAction : acceptActions) {
                widget.getActions().addAction(ActionFactory.createAcceptAction(acceptAction));
            }
        }

        presenter.register();
    }

    /**
     * Create envelope that contains all choices of the competence.
     *
     * @param competencePath Path to the @competence, ends with <tt>../C:?</tt>
     * @param competence Competence from which we extract necessary data
     * @param sourceAnchor source anchor for arrows in the scene. All created
     * choices will have an arrow from sourceAnchor to the choice envelope.
     * @return created envelope.
     */
    private ShedCreationContainer<ShedChoicesEnvelope> createChoicesEnvelope(LapPath competencePath, Competence competence, Anchor sourceAnchor) {
        assert competencePath.traversePath(plan) == competence;
        ShedChoicesEnvelope choicesEnvelope = new ShedChoicesEnvelope(lapScene, sourceAnchor);

        IPresenter choicesPresenter = presenterFactory.createChoicesPresenter(competencePath, choicesEnvelope);
        choicesPresenter.register();

        ShedCreationContainer<ShedChoicesEnvelope> choicesContainer = new ShedCreationContainer<ShedChoicesEnvelope>(choicesEnvelope);
        int choiceId = 0;
        for (CompetenceElement choice : competence.getChildDataNodes()) {
            LapPath choicePath = competencePath.concat(LapType.COMPETENCE_ELEMENT, choiceId++);
            ShedCreationContainer<SlotEnvelope> choiceContainer = createChoiceEnvelope(choicePath, choice, sourceAnchor);
            choicesEnvelope.add(choiceContainer.getWidget());

            choicesContainer.addArrows(choiceContainer.getArrows());
        }

        return choicesContainer;
    }

    /**
     * Create envelope containing visualized expanded actions of the AP.
     *
     * @param actionPatternPath Path to the AP for which we want to create
     * envelope (ends with ../A:?/AP:?)
     * @param sourceAnchor TODO: not used, implement proper arrows in the scene.
     * @return
     */
    private ShedCreationContainer<ShedActionsEnvelope> createActionsEnvelope(LapPath actionPatternPath, Anchor sourceAnchor) {
        ActionPattern actionPattern = actionPatternPath.traversePath(plan);
        ShedActionsEnvelope actionsEnvelope = new ShedActionsEnvelope(lapScene, sourceAnchor);

        IPresenter actionsPresenter = presenterFactory.createActionsPresenter(actionPatternPath, actionsEnvelope);
        actionsPresenter.register();

        ShedCreationContainer<ShedActionsEnvelope> actionsContainer = new ShedCreationContainer<ShedActionsEnvelope>(actionsEnvelope);
        int actionId = 0;
        for (TriggeredAction action : actionPattern.getActions()) {
            LapPath actionPath = actionPatternPath.concat(LapType.ACTION, actionId++);
            ShedCreationContainer<ExpandedActionEnvelope> expandedActionContainer = createdExpandedActionEnvelope(actionPath);
            actionsEnvelope.add(expandedActionContainer.getWidget());

            actionsContainer.addArrows(expandedActionContainer.getArrows());

            Anchor expandedActionAnchor = expandedActionContainer.getWidget().getAnchor();
            ArrowWidget arrowToParent = new ArrowWidget(lapScene, sourceAnchor, expandedActionAnchor);
            actionsContainer.addArrow(arrowToParent);
        }
        return actionsContainer;
    }

    /**
     * @return Get scene for which is this factory creating widgets.
     */
    ShedScene getScene() {
        return lapScene;
    }

    /**
     * Widget action that collapses attachment of the {@link AttachmentEnvelope}.
     */
    private static class CollapseAction extends WidgetAction.Adapter {

        /**
         * Envelope that is being collapsed by this action.
         */
        private final ShedCreationContainer<ExpandedActionEnvelope> envelope;

        /**
         * Create new action that will collapse attachment of the envelope.
         *
         * @param envelope envelope whos attachment will be collapsed.
         */
        private CollapseAction(ShedCreationContainer<ExpandedActionEnvelope> envelope) {
            this.envelope = envelope;
        }

        @Override
        public WidgetAction.State mouseClicked(Widget widget, WidgetAction.WidgetMouseEvent event) {
            ShedCollapseWidget collapsableWidget = (ShedCollapseWidget) widget;
            Point localPoint = event.getPoint();
            if (event.getButton() == MouseEvent.BUTTON1 && collapsableWidget.isCollapseArea(localPoint)) {
                boolean isEnvelopeVisible = envelope.getWidget().isVisible();
                collapsableWidget.setCollapsed(isEnvelopeVisible);
                envelope.getWidget().setVisible(!isEnvelopeVisible);

                // Hide arrows
                Set<Widget> envelopeSubtree = getSubtreeWidgets(envelope.getWidget());
                ShedScene scene = (ShedScene) envelope.getWidget().getScene();
                List<Widget> arrows = scene.findArrows(envelopeSubtree);
                for (Widget arrow : arrows) {
                    arrow.setVisible(!isEnvelopeVisible);
                }
                scene.update();
                return WidgetAction.State.CONSUMED;
            }
            return WidgetAction.State.REJECTED;
        }

        private Set<Widget> getSubtreeWidgets(Widget subroot) {
            Set<Widget> allChildren = new HashSet<Widget>();

            allChildren.add(subroot);
            for (Widget child : subroot.getChildren()) {
                allChildren.addAll(getSubtreeWidgets(child));
            }
            return allChildren;
        }
    }

    /**
     * Create drive widget along with its {@link DrivePresenter}, only the
     * widget representing the {@link DriveElement}, not its {@link Trigger} or {@link TriggeredAction}
     *
     * @param drivePath Path tot the @drive.
     * @param drive drive the widget will represent (will listen on name changes
     * and others)
     * @return newly created widget representing the {@link DriveElement} in the {@link ShedScene}.
     */
    ShedCollapseWidget createDriveWidget(LapPath drivePath, DriveElement drive) {
        assert drivePath.traversePath(plan) == drive;
        ShedCollapseWidget driveWidget = new ShedCollapseWidget(lapScene, drive.getName(), ShedWidgetColors.DRIVE.getColor());
        IPresenter drivePresenter = presenterFactory.createDrivePresenter(drivePath, driveWidget);

        attachPresenterToWidget(driveWidget, drivePresenter, drive);

        return driveWidget;
    }

    /**
     * Create choice widget along with its {@link ChoicePresenter}, only the
     * widget representing the {@link CompetenceElement}, not its {@link Trigger}
     * or {@link TriggeredAction}
     *
     * @param choice drive the widget will represent (will listen on name
     * changes and others)
     * @return newly created widget representing the {@link DriveElement} in the {@link ShedScene}.
     */
    ShedCollapseWidget createChoiceWidget(LapPath choicePath, CompetenceElement choice) {
        assert choicePath.traversePath(plan) == choice;
        ShedCollapseWidget choiceWidget = new ShedCollapseWidget(lapScene, choice.getName(), ShedWidgetColors.CHOICE.getColor());
        IPresenter choicePresenter = presenterFactory.createChoicePresenter(choicePath, choiceWidget);

        attachPresenterToWidget(choiceWidget, choicePresenter, choice);

        return choiceWidget;
    }

    /**
     * Create envelope containing expanded @action.
     *
     * @param actionPath Path to the @action, ends with <tt>../A:?</tt>
     * @param action Action that will be expanded
     * @return created envelope.
     */
    public ShedCreationContainer<ExpandedActionEnvelope> createdExpandedActionEnvelope(LapPath actionPath) {
        TriggeredAction action = actionPath.traversePath(plan);
        ShedCreationContainer<AttachmentEnvelope> attachmentContainer = createAttachmentEnvelope(actionPath, action);
        ExpandedActionEnvelope expandedAction = new ExpandedActionEnvelope(lapScene, attachmentContainer.getWidget());
        IPresenter expandedActionPresenter = presenterFactory.createExpandedActionPresenter(actionPath, expandedAction);
        expandedActionPresenter.register();

        ShedCreationContainer<ExpandedActionEnvelope> expandedActionContainer = new ShedCreationContainer<ExpandedActionEnvelope>(expandedAction);
        expandedActionContainer.addArrows(attachmentContainer.getArrows());
        return expandedActionContainer;
    }

    /**
     * Create envelope containing fully expanded drive along with all inside
     * widgets and presenters.
     *
     * @param scene Scene into which the widget belongs to.
     * @param drive Drive that will be used to create the envelope and all
     * elements inside.
     * @return Newly created envelope for the drive
     */
    public ShedCreationContainer<SlotEnvelope> createDriveEnvelope(LapPath drivePath, DriveElement drive) {
        ShedCollapseWidget driveWidget = createDriveWidget(drivePath, drive);
        Anchor driveAnchor = new LeveledHorizontalAnchor(driveWidget);
        ShedCreationContainer<ShedTriggerEnvelope> driveTriggerContainer = createTriggerEnvelope(drivePath, drive, driveAnchor, drive.getTrigger());

        LapPath driveActionPath = drivePath.concat(LapType.ACTION, 0);
        ShedCreationContainer<ExpandedActionEnvelope> expandedActionEnvelope = createdExpandedActionEnvelope(driveActionPath);

        SlotEnvelope driveEnvelope = new SlotEnvelope(lapScene, driveWidget, driveTriggerContainer.getWidget(), expandedActionEnvelope.getWidget());

        ShedCreationContainer<SlotEnvelope> driveContainer = new ShedCreationContainer<SlotEnvelope>(driveEnvelope);
        driveContainer.addArrows(driveTriggerContainer.getArrows());
        driveContainer.addArrows(expandedActionEnvelope.getArrows());
        driveContainer.addArrow(new ArrowWidget(lapScene, driveAnchor, expandedActionEnvelope.getWidget().getAnchor()));
        
        addCollapseAction(driveWidget, expandedActionEnvelope);

        return driveContainer;
    }

    /**
     * Create envelope with fully expanded choice along with all inside widgets
     * and presenters. The presenters are already registered.
     *
     * @param choicePath Path to the @choice
     * @param choice {@link CompetenceElement} used to create the expanded
     * envelope
     * @param sourceAnchor Source anchor for {@link ArrowWidget} between the
     * expanded choice widget and its parent.
     * @return Created visual representation.
     */
    public ShedCreationContainer<SlotEnvelope> createChoiceEnvelope(LapPath choicePath, CompetenceElement choice, Anchor sourceAnchor) {
        ShedCollapseWidget choiceWidget = createChoiceWidget(choicePath, choice);
        Anchor choiceAnchor = new LeveledHorizontalAnchor(choiceWidget);//new FixedWidgetAnchor(choiceWidget, new Point(ShedWidget.width, ShedWidget.height / 2), Anchor.Direction.RIGHT);
        ShedCreationContainer<ShedTriggerEnvelope> choiceTriggerContainer = createTriggerEnvelope(choicePath, choice, choiceAnchor, choice.getTrigger());
        LapPath choiceActionPath = choicePath.concat(LapType.ACTION, 0);
        ShedCreationContainer<ExpandedActionEnvelope> expandedActionEnvelope = createdExpandedActionEnvelope(choiceActionPath);

        SlotEnvelope choiceEnvelope = new SlotEnvelope(lapScene, choiceWidget, choiceTriggerContainer.getWidget(), expandedActionEnvelope.getWidget());

        ShedCreationContainer<SlotEnvelope> choiceContainer = new ShedCreationContainer<SlotEnvelope>(choiceEnvelope);
        Anchor leftChoiceAnchor = new FixedWidgetAnchor(choiceWidget, new Point(0, ShedWidget.height / 2), Anchor.Direction.LEFT);

        ArrowWidget arrowToSource = new ArrowWidget(lapScene, sourceAnchor, leftChoiceAnchor);
        choiceContainer.addArrow(arrowToSource);
        choiceContainer.addArrows(choiceTriggerContainer.getArrows());
        choiceContainer.addArrows(expandedActionEnvelope.getArrows());
        choiceContainer.addArrow(new ArrowWidget(lapScene, choiceAnchor, expandedActionEnvelope.getWidget().getAnchor()));

        addCollapseAction(choiceWidget, expandedActionEnvelope);

        return choiceContainer;
    }

    /**
     * Add collapse action to the @widget. The action is added as first action,
     * otherwise inplace editor will consume event.
     *
     * @param widget Widget that will be collapsible
     * @param envelope Envelope that will be collapsed
     */
    private static void addCollapseAction(ShedCollapseWidget widget, ShedCreationContainer<ExpandedActionEnvelope> envelope) {
        widget.getActions().addAction(0, new CollapseAction(envelope));
    }
    
    static ShedWidget createWidgetCopy(ShedWidget sourceWidget) {
        ShedWidget copy = new ShedWidget((ShedScene)sourceWidget.getScene(), sourceWidget.getDisplayName(), sourceWidget.color);
        copy.setPresent(sourceWidget.presentVars);
        copy.setError(sourceWidget.errorVars);
        copy.setUnused(sourceWidget.unusedVars);
        
        return copy;
    }
}
