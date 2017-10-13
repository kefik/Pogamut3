package cz.cuni.pogamut.shed.presenter;

import cz.cuni.amis.pogamut.sposh.elements.*;
import cz.cuni.amis.pogamut.sposh.engine.VariableContext;
import cz.cuni.amis.pogamut.sposh.exceptions.UnexpectedElementException;
import cz.cuni.amis.pogamut.sposh.executor.ParamInfo;
import cz.cuni.amis.pogamut.sposh.executor.ParamsAction;
import cz.cuni.amis.pogamut.sposh.executor.ParamsSense;
import cz.cuni.pogamut.shed.widget.ShedScene;
import cz.cuni.pogamut.shed.widget.ShedWidget;
import cz.cuni.pogamut.shed.widget.ShedWidget.Variable;
import java.util.LinkedList;
import java.util.List;

/**
 * Base class presenter for primitives, i.e. actions and senses. Since both
 * actions and senses presenters are quite similar, use this class as base class
 * for both of them.
 *
 * @param <PRIMITIVE_TYPE> Either {@link Sense} or {@link TriggeredAction}
 * depending on which primitive is used.
 * @author Honza
 */
public abstract class PrimitivePresenter<PRIMITIVE_TYPE extends PoshElement & INamedElement> extends AbstractPresenter implements PoshElementListener<PRIMITIVE_TYPE>, INameMapListener, ILapChainListener {

    /**
     * The primitive that is being presented by this presenter.
     */
    protected final PRIMITIVE_TYPE primitive;
    /**
     * Widget that represents the primitive in the scene.
     */
    protected final ShedWidget primitiveWidget;
    /**
     * Chain of variables from the root to the primitive.
     */
    protected final LapChain primitiveChain;

    /**
     * Create new presenter for @primitive, detect and project all changes on
     * the @primitive and @primitiveChain to the @primitiveWidget.
     *
     * @param scene Scene that will be manipulated by this presenter
     * @param presenter The basic presenter
     * @param primitive The primitiver that is being presented.
     * @param primitiveWidget Widget that is representing the @primitive in the
     * @scene.
     * @param primitiveChain Chain of variables from the root to the @primitive
     */
    protected PrimitivePresenter(ShedScene scene, ShedPresenter presenter, PRIMITIVE_TYPE primitive, ShedWidget primitiveWidget, LapChain primitiveChain) {
        super(scene, presenter);
        this.primitive = primitive;
        this.primitiveWidget = primitiveWidget;
        this.primitiveChain = primitiveChain;
    }

    @Override
    public void register() {
        primitiveWidget.setPresenter(this);
        primitive.addElementListener(this);

        presenter.addNameMapListener(this);

        primitiveChain.register();
        primitiveChain.addChainListener(this);

        updateWidget();
    }

    @Override
    public void unregister() {
        primitiveChain.removeChainListener(this);
        primitiveChain.unregister();

        presenter.removeNameMapListener(this);

        primitive.removeElementListener(this);
        primitiveWidget.setPresenter(null);
    }

    @Override
    public final void childElementAdded(PRIMITIVE_TYPE parent, PoshElement child) {
        throw UnexpectedElementException.create(child);
    }

    @Override
    public final void childElementMoved(PRIMITIVE_TYPE parent, PoshElement child, int oldIndex, int newIndex) {
        throw UnexpectedElementException.create(child);
    }

    @Override
    public final void childElementRemoved(PRIMITIVE_TYPE parent, PoshElement child, int removedChildIndex) {
        throw UnexpectedElementException.create(child);
    }

    @Override
    public final void nameMapChanged(String key, String oldName, String newName) {
        if (key.equals(primitive.getName())) {
            updateWidget();
        }
    }

    /**
     * When some link of the @primitiveChain is changed, this method is notified
     * and it will update the widget.
     */
    @Override
    public final void notifyLinkChanged() {
        updateWidget();
        scene.update();
    }

    /**
     * Method used by the {@link #updateWidget() } to
     *
     * @return
     */
    protected abstract String getTitleText();

    /**
     * Update widget to reflect current state of the {@link #action}.
     */
    protected final void updateWidget() {
        String titleText = getTitleText();
        primitiveWidget.setDisplayName(titleText);

        ParamInfo[] params = presenter.getPrimitiveParameters(primitive.getName());
        VariableContext ctx = primitiveChain.createContext();

        List<Variable> presentArgs = getPresentArgs(ctx, params);
        primitiveWidget.setPresent(presentArgs);

        List<Variable> missingArgs = getErrorArgs(ctx, params);
        primitiveWidget.setError(missingArgs);

        List<Variable> unusedArgs = getUnusedArgs(ctx, params);
        primitiveWidget.setUnused(unusedArgs);

        primitiveWidget.revalidate();
    }

    private boolean paramHasValue(ParamInfo param, VariableContext ctx) {
        return ctx.hasVariable(param.name);
    }

    private boolean parmValueHasCorrectType(ParamInfo param, VariableContext ctx) {
        Object paramValue = ctx.getValue(param.name);
        
        return param.isValueAssignable(paramValue);
    }

    /**
     * Get all params that are used as parameters in the parametrized primitves.
     *
     * @see ParamsAction
     * @see ParamsSense
     * @param primitiveParams The parameters that are requested to exist within
     * the chain.
     * @return logical conjuction of @primitiveParams and chain variables.
     */
    private List<ShedWidget.Variable> getPresentArgs(VariableContext ctx, ParamInfo[] primitiveParams) {
        List<ShedWidget.Variable> presentArgs = new LinkedList<ShedWidget.Variable>();

        for (ParamInfo param : primitiveParams) {
            if (paramHasValue(param, ctx) && parmValueHasCorrectType(param, ctx)) {
                Object paramValue = ctx.getValue(param.name);
                Variable var = new Variable(param.name, param.type, Result.toLap(paramValue), "");
                presentArgs.add(var);
            }
        }
        return presentArgs;
    }

    private List<Variable> getErrorArgs(VariableContext ctx, ParamInfo[] primitiveParams) {
        List<Variable> errorArgs = new LinkedList<Variable>();

        for (ParamInfo param : primitiveParams) {
            if (!paramHasValue(param, ctx)) {
                Variable var = new Variable(param.name, param.type, "", "Unspecified value");
                errorArgs.add(var);
            } else if (!parmValueHasCorrectType(param, ctx)) {
                Object paramValue = ctx.getValue(param.name);
                Variable var = new Variable(param.name, param.type, Result.toLap(paramValue), "Incorrect type");
                errorArgs.add(var);
            }
        }
        return errorArgs;
    }

    private List<Variable> getUnusedArgs(VariableContext ctx, ParamInfo[] params) {
        List<Variable> unusedVars = new LinkedList<Variable>();
        String[] allVariables = ctx.getKeys();
        for (String variableName : allVariables) {
            boolean isVariableParam = false;
            for (ParamInfo param : params) {
                if (param.name.equals(variableName)) {
                    isVariableParam = true;
                }
            }
            if (!isVariableParam) {
                Object variableValue = ctx.getValue(variableName);
                Variable var = new Variable(variableName, null, Result.toLap(variableValue), "");

                unusedVars.add(var);
            }
        }
        return unusedVars;
    }
}
