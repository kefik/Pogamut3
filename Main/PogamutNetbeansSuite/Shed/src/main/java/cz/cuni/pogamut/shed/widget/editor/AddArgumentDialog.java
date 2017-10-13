package cz.cuni.pogamut.shed.widget.editor;

import cz.cuni.amis.pogamut.sposh.elements.LapChain;
import cz.cuni.amis.pogamut.sposh.elements.ParseException;
import cz.cuni.amis.pogamut.sposh.elements.Result;
import cz.cuni.amis.pogamut.sposh.engine.VariableContext;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JTable;
import org.netbeans.api.visual.action.InplaceEditorProvider;
import org.netbeans.api.visual.action.InplaceEditorProvider.EditorController;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;

/**
 * Base class for adding an argument. It is a listener that when notified, askes
 * user about name of argument and its value and if both are provided and
 * correct, calls {@link #addArgument(java.lang.String, java.lang.Object) }
 * method.
 *
 * @author Honza H
 */
final class AddArgumentAction implements ActionListener {

    private final JTable argumentsTable;
    private final ArgumentsTableModel argumentsModel;
    private final InplaceEditorProvider.EditorController controller;
    private final LapChain chain;
    
    AddArgumentAction(JTable argumentsTable, ArgumentsTableModel argumentsModel, EditorController controller, LapChain chain) {
        this.argumentsTable = argumentsTable;
        this.argumentsModel = argumentsModel;
        this.controller = controller;
        this.chain = chain;
    }

    @Override
    public final void actionPerformed(ActionEvent e) {
        String title = "New argument";
        NotifyDescriptor.InputLine argumentNameDialog = new NotifyDescriptor.InputLine("Specify name of the argument (must start with $)", title);

        if (DialogDisplayer.getDefault().notify(argumentNameDialog) != NotifyDescriptor.OK_OPTION) {
            return;
        }
        String argumentName = argumentNameDialog.getInputText().trim();
        if (!Result.isVariableName(argumentName)) {
            NotifyDescriptor.Message paramNameWrong = new NotifyDescriptor.Message("Wrong name of argument.\nName must match regular expression: " + Result.variableNameRegexp);
            DialogDisplayer.getDefault().notify(paramNameWrong);
            return;
        }

        NotifyDescriptor.InputLine valueInput = new NotifyDescriptor.InputLine("Specify the value of the argument(String \"Hello?\", integer 125, double 1.57, nil or other variable $var", title);
        if (DialogDisplayer.getDefault().notify(valueInput) != NotifyDescriptor.OK_OPTION) {
            return;
        }

        int addIndex = getAddIndex();
        
        String defaultValueString = valueInput.getInputText().trim();
        if (Result.isVariableName(defaultValueString)) {
            String variableName = defaultValueString;
            VariableContext ctx = chain.subchain(0, chain.size() - 1).createContext();
            if (!ctx.hasVariable(variableName)) {
                NotifyDescriptor.Message paramNameWrong = new NotifyDescriptor.Message("Variable " + variableName + " is not in defined in the node.");
                DialogDisplayer.getDefault().notify(paramNameWrong);
                return;
            }
            argumentsModel.addVariableArgument(addIndex, argumentName, variableName);
        } else {
            Object value;
            try {
                value = Result.parseValue(defaultValueString);
            } catch (ParseException ex) {
                NotifyDescriptor.Message paramNameWrong = new NotifyDescriptor.Message("Wrong value of argument.\nParsing error: " + ex.getMessage());
                DialogDisplayer.getDefault().notify(paramNameWrong);
                return;
            }
            argumentsModel.addArgument(addIndex, argumentName, value);
        }
        controller.notifyEditorComponentBoundsChanged();
    }

    
    private int getAddIndex() {
        int addIndex = argumentsTable.getSelectedRow();
        if (addIndex == -1) {
            addIndex = argumentsTable.getRowCount();
        }
        return addIndex;
    }
}

final class RemoveArgumentAction implements ActionListener {

    final JTable argumentsTable;
    final ArgumentsTableModel argumentsModel;

    public RemoveArgumentAction(JTable argumentsTable, ArgumentsTableModel argumentsModel) {
        this.argumentsTable = argumentsTable;
        this.argumentsModel = argumentsModel;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        int index = argumentsTable.getSelectedRow();
        if (index != -1) {
            argumentsModel.deleteArgument(index);
        }
    }
};
