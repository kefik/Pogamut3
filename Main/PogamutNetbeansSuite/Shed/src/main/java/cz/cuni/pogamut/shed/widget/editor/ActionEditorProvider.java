package cz.cuni.pogamut.shed.widget.editor;

import cz.cuni.amis.pogamut.sposh.elements.*;
import cz.cuni.amis.pogamut.sposh.elements.Arguments.Argument;
import cz.cuni.amis.pogamut.sposh.engine.VariableContext;
import cz.cuni.amis.pogamut.sposh.exceptions.CycleException;
import cz.cuni.amis.pogamut.sposh.exceptions.InvalidNameException;
import cz.cuni.amis.pogamut.sposh.executor.ParamInfo;
import cz.cuni.amis.pogamut.sposh.executor.PrimitiveData;
import cz.cuni.pogamut.shed.presenter.ShedPresenter;
import java.awt.Rectangle;
import java.text.MessageFormat;
import java.util.EnumSet;
import java.util.List;
import org.netbeans.api.visual.action.InplaceEditorProvider;
import org.netbeans.api.visual.widget.Widget;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;

/**
 *
 * @author Honza
 */
final class ActionEditorProvider extends ShedEditorProvider<TriggeredAction, ActionEditor> {

    private final ShedPresenter presenter;

    /**
     * Create provider of primtive editor.
     *
     * @param primitive Presented primitive
     * @param presenter Used to get current crawled {@link PrimitiveData} when
     * editor is created.
     * @param chain Variable chain from root to the action (incl)
     */
    public ActionEditorProvider(TriggeredAction primitive, ShedPresenter presenter, LapChain chain) {
        super(primitive, chain);
        this.presenter = presenter;
    }

    @Override
    public void notifyClosing(EditorController ec, Widget widget, ActionEditor editor, boolean commit) {
        if (commit) {
            editor.stopEditting();
            String oldName = node.getName();
            String newName = editor.getActionName();
            if (!oldName.equals(newName)) {
                try {
                    node.setActionName(newName);
                } catch (InvalidNameException ex) {
                    String errorMessage = MessageFormat.format("Name \"{0}\" is not valid", newName);
                    notify(errorMessage);
                } catch (CycleException ex) {
                    String errorMessage = MessageFormat.format("Changing name to \"{0}\" would cause a cycle.", newName);
                    notify(errorMessage);
                }
            }

            Arguments args = new Arguments();
            String unsuccessfull = convertEditorArg(editor.getArguments(), args);
            if (!unsuccessfull.isEmpty()) {
                notify("Following arguments had errors: \n" + unsuccessfull);
            }
            node.setArguments(args);
        }

    }

    @Override
    public ActionEditor createEditorComponent(InplaceEditorProvider.EditorController controller, Widget widget) {
        ParamInfo[] paramNames = presenter.getPrimitiveParameters(node.getName());
        return new ActionEditor(node, paramNames, controller, chain);
    }
}

final class SenseEditorProvider extends ShedEditorProvider<Sense, SenseEditor> {

    private final ShedPresenter presenter;

    public SenseEditorProvider(Sense sense, ShedPresenter presenter, LapChain chain) {
        super(sense, chain);
        this.presenter = presenter;
    }

    @Override
    public void notifyClosing(InplaceEditorProvider.EditorController ec, Widget widget, SenseEditor editor, boolean commit) {
        if (commit) {
            editor.stopEditting();
            String oldName = node.getName();
            String newName = editor.getSenseName();
            if (!oldName.equals(newName)) {
                try {
                    node.setSenseName(newName);
                } catch (InvalidNameException ex) {
                    String errorMessage = MessageFormat.format("Name \"{0}\" is not valid", newName);
                    notify(errorMessage);
                }
            }

            node.setPredicate(editor.getPredicate());

            String operandString = editor.getOperandString();
            Object operand;
            try {
                operand = Result.parseValue(operandString);
                node.setOperand(operand);
            } catch (ParseException ex) {
                notify("\"" + operandString + "\" is not a valid value.");
            }

            Arguments args = new Arguments();
            String unsuccessfull = convertEditorArg(editor.getArguments(), args);
            if (!unsuccessfull.isEmpty()) {
                notify("Following arguments had errors: \n" + unsuccessfull);
            }

            node.setArguments(args);
        }

    }

    @Override
    public SenseEditor createEditorComponent(InplaceEditorProvider.EditorController controller, Widget widget) {
        ParamInfo[] params = presenter.getPrimitiveParameters(node.getName());
        return new SenseEditor(node, params, controller, chain);
    }
}
