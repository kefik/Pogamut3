package cz.cuni.pogamut.shed.widget.editor;

import cz.cuni.amis.pogamut.sposh.elements.Arguments.Argument;
import cz.cuni.amis.pogamut.sposh.elements.*;
import cz.cuni.amis.pogamut.sposh.engine.VariableContext;
import cz.cuni.amis.pogamut.sposh.exceptions.CycleException;
import cz.cuni.amis.pogamut.sposh.exceptions.DuplicateNameException;
import cz.cuni.amis.pogamut.sposh.exceptions.InvalidNameException;
import cz.cuni.amis.pogamut.sposh.exceptions.MissingParameterException;
import java.awt.Rectangle;
import java.text.MessageFormat;
import java.util.EnumSet;
import java.util.List;
import javax.swing.JComponent;
import org.netbeans.api.visual.action.InplaceEditorProvider;
import org.netbeans.api.visual.widget.Widget;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.Exceptions;

abstract class ShedEditorProvider<NODE, EDITOR extends JComponent> implements InplaceEditorProvider<EDITOR> {

    final NODE node;
    final LapChain chain;

    public ShedEditorProvider(NODE node, LapChain chain) {
        this.node = node;
        this.chain = chain;
    }

    @Override
    public final void notifyOpened(EditorController ec, Widget widget, EDITOR c) {
    }

    @Override
    public final Rectangle getInitialEditorComponentBounds(InplaceEditorProvider.EditorController controller, Widget widget, EDITOR editor, Rectangle viewBounds) {
        return null;
    }

    @Override
    public final EnumSet<InplaceEditorProvider.ExpansionDirection> getExpansionDirections(InplaceEditorProvider.EditorController ec, Widget widget, EDITOR c) {
        return EnumSet.of(InplaceEditorProvider.ExpansionDirection.BOTTOM, InplaceEditorProvider.ExpansionDirection.RIGHT);
    }

    void notify(String message) {
        NotifyDescriptor.Message infoMessage = new NotifyDescriptor.Message(message, NotifyDescriptor.INFORMATION_MESSAGE);
        DialogDisplayer.getDefault().notify(infoMessage);
    }

    /**
     * Convert editor arguments to {@link Argument arguments} and if some are
     * not possible to convert, return string with list of them.
     *
     * @param editorArgs
     * @param args
     * @return Blank if all arguments were converted successfully, otherwise
     * list of all that weren't.
     */
    String convertEditorArg(List<TableArgument> editorArgs, Arguments args) {
        StringBuilder sb = new StringBuilder();
        VariableContext ctx = chain.subchain(0, chain.size() - 1).createContext();
        for (TableArgument editorArg : editorArgs) {
            try {
                Argument arg = editorArg.createArgument(ctx);
                args.add(arg);
            } catch (IllegalStateException ex) {
                // Argument editor shows blank lines for parameters of primitives,
                // ignore such cases
                boolean valueIsBlank = editorArg.getValueString().isEmpty();
                boolean variableIsDefined = ctx.hasVariable(editorArg.getName());
                if (!(valueIsBlank && variableIsDefined)) {
                    sb.append(editorArg.getName());
                    sb.append('\n');
                }
            }
        }
        return sb.toString();
    }
    
    String convertEditorParams(List<TableParameter> editorParams, Arguments args) {
        StringBuilder sb = new StringBuilder();
        for (TableParameter editorParam : editorParams) {
            if (editorParam.isOverriden()) {
                try {
                    VariableContext ctx = chain.subchain(0, chain.size() - 1).createContext();
                    Argument arg = editorParam.createOverrideArgument(ctx);
                    args.add(arg);
                } catch (IllegalStateException ex) {
                    sb.append(editorParam.getName());
                    sb.append('\n');
                }
            }
        }
        return sb.toString();
    }
}

/**
 * Editor provider for {@link PoshElement nodes} with {@link INamedElement name}
 * and {@link IParametrizedElement params}. The component that creates the
 * actual visible editor for user and sets data structures according to the
 * values set in the editor.
 *
 * @author Honza
 */
public final class NodeEditorProvider<NODE extends INamedElement & IParametrizedElement> extends ShedEditorProvider<NODE, ParametrizedNodeEditor> {

    private final TriggeredAction referencingAction;

    public NodeEditorProvider(NODE node, TriggeredAction referencingAction, LapChain chain) {
        super(node, chain);
        this.referencingAction = referencingAction;
    }

    @Override
    public void notifyClosing(EditorController ec, Widget widget, ParametrizedNodeEditor editor, boolean commit) {
        if (commit) {
            String oldNodeName = node.getName();
            String newNodeName = editor.getElementName();
            if (!oldNodeName.equals(newNodeName)) {
                try {
                    node.rename(newNodeName);
                } catch (InvalidNameException ex) {
                    String errorMessage = MessageFormat.format("Name \"{0}\" is not valid", newNodeName);
                    notify(errorMessage);
                } catch (CycleException ex) {
                    String errorMessage = MessageFormat.format("Changing name to \"{0}\" would cause a cycle.", newNodeName);
                    notify(errorMessage);
                } catch (DuplicateNameException ex) {
                    String errorMessage = MessageFormat.format("Name \"{0}\" is already used.", newNodeName);
                    notify(errorMessage);
                }
            }

            FormalParameters newParams = new FormalParameters();
            List<TableParameter> editorParams = editor.getParameters();
            for (TableParameter editorParam : editorParams) {
                String paramName = editorParam.getName();

                // default value can't be a parameter
                if (Result.isVariableName(editorParam.getDefaultValueString())) {
                    continue;
                }

                Object paramDefaultValue = editorParam.getDefaultValue();
                newParams.add(new FormalParameters.Parameter(paramName, paramDefaultValue));
            }
            node.setParameters(newParams);

            Arguments newNodeArgs = getNodeArgs(editor.getArguments(), editorParams);
            referencingAction.setArguments(newNodeArgs);
        }
    }

    /**
     * Get {@link Arguments} passed from the {@link #referencingAction} to the
     * node.
     *
     * @throws IllegalStateException
     */
    private Arguments getNodeArgs(List<TableArgument> editorArgs, List<TableParameter> editorParams) {
        Arguments newArgs = new Arguments();
        String unsuccessfullArgs = convertEditorArg(editorArgs, newArgs);
        String unsuccessfullParams = convertEditorParams(editorParams, newArgs);

        String unsuccessfull = unsuccessfullArgs + unsuccessfullParams;
        if (!unsuccessfull.isEmpty()) {
            notify("Following arguments had errors: \n" + unsuccessfull);
        } 
        
        return newArgs;
    }

    @Override
    public ParametrizedNodeEditor createEditorComponent(InplaceEditorProvider.EditorController controller, Widget widget) {
        return new ParametrizedNodeEditor(node, referencingAction, controller, chain);
    }
}
