package cz.cuni.amis.dash;

import cz.cuni.amis.pogamut.sposh.dbg.engine.IDebugEngine;
import cz.cuni.amis.pogamut.sposh.dbg.engine.IDebugEngineListener;
import cz.cuni.amis.pogamut.sposh.dbg.lap.LapBreakpoint;
import cz.cuni.amis.pogamut.sposh.elements.*;
import cz.cuni.pogamut.shed.presenter.AbstractAcceptAction;
import cz.cuni.pogamut.shed.presenter.PrimitivePresenter;
import cz.cuni.pogamut.shed.presenter.ShedMenuActionFactory;
import cz.cuni.pogamut.shed.presenter.ShedPresenter;
import cz.cuni.pogamut.shed.widget.ShedScene;
import cz.cuni.pogamut.shed.widget.ShedWidget;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.netbeans.api.visual.action.ActionFactory;
import org.netbeans.api.visual.action.EditProvider;
import org.netbeans.api.visual.action.WidgetAction;
import org.netbeans.api.visual.widget.Widget;

/**
 * Presenter used in Dash for actions and sense.
 *
 * @author Honza
 */
abstract class DashPrimitivePresenter<PRIMITIVE_TYPE extends PoshElement & IReferenceElement> extends PrimitivePresenter<PRIMITIVE_TYPE> implements IDebugEngineListener {

    private final IDebugEngine engine;
    private final LapPath primitivePath;

    public DashPrimitivePresenter(IDebugEngine engine, LapPath primitivePath, ShedScene scene, ShedPresenter presenter, PRIMITIVE_TYPE primitive, ShedWidget primitiveWidget, LapChain primitiveChain) {
        super(scene, presenter, primitive, primitiveWidget, primitiveChain);

        this.engine = engine;
        this.primitivePath = primitivePath;
    }

    @Override
    public void register() {
        super.register();
        engine.addListener(this);
    }

    @Override
    public void unregister() {
        engine.removeListener(this);
        super.unregister();
    }

    @Override
    public Action[] getMenuActions() {
        return new Action[]{
                    new AbstractAction("Add single breakpoint") {

                        @Override
                        public void actionPerformed(ActionEvent e) {
                            engine.addBreakpoint(primitivePath, true);
                        }
                    },
                    new AbstractAction("Add permanent breakpoint") {

                        @Override
                        public void actionPerformed(ActionEvent e) {
                            engine.addBreakpoint(primitivePath, false);
                        }
                    },
                    new AbstractAction("Remove breakpoint") {

                        @Override
                        public void actionPerformed(ActionEvent e) {
                            engine.removeBreakpoint(primitivePath);
                        }
                    },
                    ShedMenuActionFactory.goToSourceAction(primitive)
                };
    }

    @Override
    public AbstractAcceptAction[] getAcceptProviders() {
        return new AbstractAcceptAction[0];
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        throw new IllegalStateException("Dash doesn't allow changes of the tree.");
    }

    @Override
    public WidgetAction getEditAction() {
        return ActionFactory.createEditAction(new EditProvider() {

            @Override
            public void edit(Widget widget) {
                ShedMenuActionFactory.goToSourceAction(primitive).actionPerformed(null);
            }
        });
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
    public void pathReached(LapPath path) {
        if (path.equals(primitivePath)) {
            pathReached = true;
            intensity = 100;
            primitiveWidget.setActiveIntensity(intensity);
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
            primitiveWidget.setActiveIntensity(intensity);
        }
    }

    @Override
    public void breakpointAdded(LapBreakpoint bp) {
        if (this.primitivePath.equals(bp.getPath())) {
            primitiveWidget.addBreakpoint(bp.isSingle());
        }
    }

    @Override
    public void breakpointRemoved(LapBreakpoint bp) {
        if (this.primitivePath.equals(bp.getPath())) {
            primitiveWidget.removeBreakpoint();
        }
    }

    @Override
    public void disconnected(String message, boolean error) {
    }
}
