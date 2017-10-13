package cz.cuni.amis.dash;

import cz.cuni.amis.pogamut.sposh.dbg.engine.IDebugEngine;
import cz.cuni.amis.pogamut.sposh.dbg.engine.IDebugEngineListener;
import cz.cuni.amis.pogamut.sposh.dbg.lap.LapBreakpoint;
import cz.cuni.amis.pogamut.sposh.elements.*;
import cz.cuni.amis.pogamut.sposh.executor.PrimitiveInfo;
import cz.cuni.pogamut.shed.presenter.AbstractAcceptAction;
import cz.cuni.pogamut.shed.presenter.IPresenter;
import cz.cuni.pogamut.shed.presenter.IPresenterFactory;
import cz.cuni.pogamut.shed.presenter.ShedPresenter;
import cz.cuni.pogamut.shed.widget.*;
import javax.swing.Action;
import org.netbeans.api.visual.action.WidgetAction;


/**
 * Presenter factory for Dash. This factory creates presenters for widgets in
 * the Dash, presents are desponsible for highlighting the widgets evaluiated by
 * the engine. The presenters created by this factory don't reflect changes of
 * underlying structures, so plan can't be changed (the displayed plan in the
 * scene would be out of sync).
 *
 * Presenters for action and sense also have context menu with possiblity of
 * adding breakpoint or go to source of primitive. Other elements have quite
 * simple presenter that only updates intensity, when notified that {@link YaposhEngine}
 * evaluated its node.
 *
 * @author Honza
 */
public class DashPresenterFactory implements IPresenterFactory {

    private final PoshPlan plan;
    private final YaposhEngine engine;
    private final EngineData engineData;
    private final ShedScene scene;
    private final ShedPresenter presenter;
    /**
     * Presenter for parts that don't require to set presenter for the widget
     */
    private static final IPresenter EMPTY_PRESENTER = new IPresenter() {

        @Override
        public void register() {
        }

        @Override
        public void unregister() {
        }

        @Override
        public Action[] getMenuActions() {
            return null;
        }

        @Override
        public AbstractAcceptAction[] getAcceptProviders() {
            return new AbstractAcceptAction[0];
        }

        @Override
        public WidgetAction getEditAction() {
            return null;
        }
    };

    /**
     * Create new presenter factory.
     *
     * @param engine Engine presenter will listen on to determine, if his widget
     * should be highlighted.
     * @param engineData Data from execution of @engine from the past. Not
     * currently used.
     * @param scene Scene where are the presented widgets.
     * @param presenter Presenter providing name mapping so we can display names
     * from {@link PrimitiveInfo} annotation.
     */
    public DashPresenterFactory(PoshPlan plan, YaposhEngine engine, EngineData engineData, ShedScene scene, ShedPresenter presenter) {
        this.plan = plan;
        this.engine = engine;
        this.engineData = engineData;
        this.scene = scene;
        this.presenter = presenter;
    }

    @Override
    public IPresenter createActionPresenter(LapPath actionPath, ShedWidget actionWidget) {
        TriggeredAction action = actionPath.traversePath(plan);
        LapChain actionChain = LapChain.fromPath(plan, actionPath);
        return new DashActionPresenter(engine, actionPath, scene, presenter, action, actionWidget, actionChain);
    }

    @Override
    public IPresenter createExpandedActionPresenter(LapPath actionPath, ExpandedActionEnvelope envelope) {
        return EMPTY_PRESENTER;
    }

    @Override
    public IPresenter createSensePresenter(LapPath sensePath, ShedSenseWidget senseWidget) {
        Sense sense = sensePath.traversePath(plan);
        LapChain senseChain = LapChain.fromPath(plan, sensePath);
        return new DashSensePresenter(engine, sensePath, scene, presenter, sense, senseWidget, senseChain);
    }

    @Override
    public <TRIGGER_PARENT extends PoshElement> IPresenter createTriggerPresenter(LapPath triggerOwnerPath, ShedTriggerEnvelope triggerEnvelope) {
        return EMPTY_PRESENTER;
    }

    @Override
    public IPresenter createDriveCollectionPresenter(LapPath driveCollectionPath) {
        return EMPTY_PRESENTER;
    }

    @Override
    public IPresenter createActionPatternPresenter(LapPath actionPatternPath, ShedWidget actionPatternWidget) {
        return new WidgetDummyPresenter(engine, actionPatternPath, actionPatternWidget);
    }

    @Override
    public IPresenter createCompetencePresenter(LapPath competencePath, ShedWidget competenceWidget) {
        return new WidgetDummyPresenter(engine, competencePath, competenceWidget);
    }

    @Override
    public IPresenter createChoicePresenter(LapPath competencePath, ShedWidget choiceWidget) {
        return new WidgetDummyPresenter(engine, competencePath, choiceWidget);
    }

    @Override
    public IPresenter createDrivePresenter(LapPath drivePath, ShedWidget widget) {
        return new WidgetDummyPresenter(engine, drivePath, widget);
    }

    @Override
    public IPresenter createActionsPresenter(LapPath actionPatternPath, ShedActionsEnvelope actionsEnvelope) {
        return EMPTY_PRESENTER;
    }

    @Override
    public IPresenter createChoicesPresenter(LapPath competencePath, ShedChoicesEnvelope choicesEnvelope) {
        return EMPTY_PRESENTER;
    }
}

/**
 * {@link ShedWidget} requires {@link IPresenter} because it provides menu
 * actions and so on. The provider is set during {@link #register() } method.
 *
 * @author Honza
 */
class WidgetDummyPresenter implements IPresenter, IDebugEngineListener {

    private final IDebugEngine engine;
    private final LapPath path;
    private final ShedWidget widget;

    public WidgetDummyPresenter(IDebugEngine engine, LapPath path, ShedWidget widget) {
        this.engine = engine;
        this.path = path;
        this.widget = widget;
    }

    @Override
    public void register() {
        widget.setPresenter(this);
        engine.addListener(this);
    }

    @Override
    public void unregister() {
        engine.removeListener(this);
        widget.setPresenter(null);
    }

    @Override
    public Action[] getMenuActions() {
        return null;
    }

    @Override
    public AbstractAcceptAction[] getAcceptProviders() {
        return null;
    }

    @Override
    public WidgetAction getEditAction() {
        return null;
    }

    @Override
    public void connected() {
    }

    @Override
    public void planRecieved(String name, PoshPlan plan) {
    }
    private boolean pathReached = false;
    private int intensity = 0;

    @Override
    public void evaluationReached() {
        pathReached = false;
    }

    @Override
    public void pathReached(LapPath reachedPath) {
        if (reachedPath.equals(path)) {
            pathReached = true;
            intensity = 100;
            widget.setActiveIntensity(intensity);
        }
    }

    @Override
    public void evaluationFinished() {
        if (!pathReached) {
            if (intensity > 50) {
                intensity = 50;
            } else {
                intensity -= 5;
            }

            intensity = Math.max(0, intensity);
            intensity = Math.min(intensity, 100);
            widget.setActiveIntensity(intensity);
        }
    }

    @Override
    public void breakpointAdded(LapBreakpoint bp) {
    }

    @Override
    public void breakpointRemoved(LapBreakpoint bp) {
    }

    @Override
    public void disconnected(String message, boolean error) {
    }
}
