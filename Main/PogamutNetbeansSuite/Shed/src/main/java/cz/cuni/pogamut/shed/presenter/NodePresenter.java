package cz.cuni.pogamut.shed.presenter;

import cz.cuni.amis.pogamut.sposh.elements.*;
import cz.cuni.amis.pogamut.sposh.engine.VariableContext;
import cz.cuni.amis.pogamut.sposh.executor.ParamInfo;
import cz.cuni.pogamut.shed.widget.ShedScene;
import cz.cuni.pogamut.shed.widget.ShedWidget;
import cz.cuni.pogamut.shed.widget.ShedWidget;
import cz.cuni.pogamut.shed.widget.ShedWidget.Variable;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Presenter for widget representing a parametrized node widget. This presenter
 * presents only the widget of the node, not its children, e.g. only widget of
 * AP, but not its referenced actions.
 *
 * @author Honza H
 */
abstract class NodePresenter<NODE extends PoshElement & IParametrizedElement> extends AbstractPresenter implements IPresenter, PoshElementListener<NODE>, ILapChainListener {

    protected final TriggeredAction reference;
    protected final NODE node;
    protected final ShedWidget nodeWidget;
    protected final LapChain nodeChain;

    /**
     * Create presenter for the NODE widget, only the widget, not its children
     * ect.
     *
     * @param scene Scene of the widget.
     * @param presenter Main presenter
     * @param reference The action that references the @node
     * @param node The presented node
     * @param nodeWidget The widget that will represent the AP.
     * @param nodeChain Chain to the node incl.
     */
    NodePresenter(ShedScene scene, ShedPresenter presenter, TriggeredAction reference, NODE node, ShedWidget nodeWidget, LapChain nodeChain) {
        super(scene, presenter);

        assert reference.getName().equals(node.getName());

        this.reference = reference;
        this.node = node;
        this.nodeWidget = nodeWidget;
        this.nodeChain = nodeChain;

        updateWidget();
    }

    /**
     * Update widget to reflect current state of the the presneted node. It
     * takes {@link TriggeredAction#getName() name} of {@link #reference}, the
     * {@link TriggeredAction#getArguments() arguments  passed} to the node and {@link IParametrizedElement#getParameters() parameters}
     * of the {@link #node} and displays them in the widget.
     */
    protected final void updateWidget() {
        // Careful, while renaming the AP, referencing action and C can have different names.
        Arguments args = reference.getArguments();
        FormalParameters params = node.getParameters();
        VariableContext ctx = nodeChain.createContext();

        List<Variable> variables = getDefinedVariables(ctx, params, args);
        nodeWidget.setPresent(variables);
        nodeWidget.setError(Collections.<Variable>emptyList());

        List<Variable> argsRepresentation = getOtherVariables(ctx, params, args);
        nodeWidget.setUnused(argsRepresentation);

        nodeWidget.setDisplayName(reference.getName());
        nodeWidget.revalidate();
    }

    /**
     * Get representation (variableName = valueRepresentationFromContext) of all
     * parameters and arguments of the node.
     *
     * @param ctx Context is used to retrieve the values of params and args
     * @param params Parameters we are interested int
     * @param args Arguments we are interested in.
     * @return List of representations, first params, then args.
     */
    private List<Variable> getDefinedVariables(VariableContext ctx, FormalParameters params, Arguments args) {
        List<Variable> variables = new LinkedList<Variable>();

        for (FormalParameters.Parameter nodeParam : params) {
            String paramName = nodeParam.getName();
            Object paramValue = ctx.getValue(paramName);
            
            Variable param = new Variable(paramName, null, Result.toLap(paramValue), "");

            variables.add(param);
        }

        for (Arguments.Argument arg : args) {
            String argName = arg.getParameterName();
            if (!params.containsVariable(argName)) {
                Object argValue = ctx.getValue(argName);
                Variable argRepresentation = new Variable(argName, null, Result.toLap(argValue), "");
                variables.add(argRepresentation);
            }
        }
        return variables;
    }

    private List<Variable> getOtherVariables(VariableContext ctx, FormalParameters params, Arguments args) {
        List<Variable> otherVars = new LinkedList<Variable>();
        String[] allVars = ctx.getKeys();

        for (String variableName : allVars) {
            boolean nameIsUsed = false;
            for (Arguments.Argument arg : args) {
                if (arg.getName().equals(variableName)) {
                    nameIsUsed = true;
                }
            }

            if (params.containsVariable(variableName)) {
                nameIsUsed = true;
            }

            if (!nameIsUsed) {
                Object variableValue = ctx.getValue(variableName);
                Variable variableRepresentation = new Variable(variableName, null, Result.toLap(variableValue), "");
                otherVars.add(variableRepresentation);
            }
        }
        return otherVars;
    }
    
    /**
     * Ignore, children of the NODE are not job of this presenter.
     */
    @Override
    public final void childElementAdded(NODE parent, PoshElement child) {
    }

    /**
     * Ignore, children of the NODE are not job of this presenter.
     */
    @Override
    public final void childElementMoved(NODE parent, PoshElement child, int oldIndex, int newIndex) {
    }

    /**
     * Ignore, children of the NODE are not job of this presenter.
     */
    @Override
    public final void childElementRemoved(NODE parent, PoshElement child, int removedChildIndex) {
    }

    /**
     * When some link that make up the variables of the widget changes, update
     * the widget.
     */
    @Override
    public final void notifyLinkChanged() {
        updateWidget();
    }
}
