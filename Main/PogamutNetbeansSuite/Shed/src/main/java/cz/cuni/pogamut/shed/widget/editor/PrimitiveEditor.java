package cz.cuni.pogamut.shed.widget.editor;

import cz.cuni.amis.pogamut.sposh.elements.LapChain;
import cz.cuni.amis.pogamut.sposh.elements.Result;
import cz.cuni.amis.pogamut.sposh.elements.Sense;
import cz.cuni.amis.pogamut.sposh.elements.Sense.Predicate;
import cz.cuni.amis.pogamut.sposh.elements.TriggeredAction;
import cz.cuni.amis.pogamut.sposh.executor.ParamInfo;
import cz.cuni.amis.pogamut.sposh.executor.PrimitiveData;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.*;
import javax.swing.table.TableCellEditor;
import org.netbeans.api.visual.action.InplaceEditorProvider;

/**
 * Editor component for inplace editor for parametrized action. Allows to change
 * name of the action and its arguments.
 *
 * @author Honza
 */
class ActionEditor extends JPanel {

    private final JTextField nameTextField;
    private final ArgumentsTableModel argumentsModel;
    private final JTable argumentsTable;

    /**
     * Create new editor for action.
     *
     * @param action Primitive which will be edited
     * @param params Parameters of the primitive from {@link PrimitiveData}
     * @param chain Chain to the action, inclusive
     */
    ActionEditor(TriggeredAction action, ParamInfo[] params, InplaceEditorProvider.EditorController controller, LapChain chain) {
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        setBorder(BorderFactory.createLineBorder(Color.BLACK));

        nameTextField = new JTextField();
        nameTextField.setText(action.getName());
        add(nameTextField);

        argumentsModel = ArgumentsTableModelFactory.createLeafModel(params, action.getArguments());
        argumentsTable = new VariableTable(argumentsModel);

        JButton addArgBtn = new JButton("Add argument");
        ActionListener addArgumentListener = new AddArgumentAction(argumentsTable, argumentsModel, controller, chain);
        addArgBtn.addActionListener(addArgumentListener);
        add(addArgBtn);

        JButton removeArgBtn = new JButton("Remove argument");
        ActionListener removeArgumentListener = new RemoveArgumentAction(argumentsTable, argumentsModel);
        removeArgBtn.addActionListener(removeArgumentListener);
        add(removeArgBtn);

        add(argumentsTable.getTableHeader());
        add(argumentsTable);
    }

    String getActionName() {
        return nameTextField.getText().trim();
    }

    List<TableArgument> getArguments() {
        return this.argumentsModel.getArguments();
    }
    
    public void stopEditting() {
        Component editor = argumentsTable.getEditorComponent();
        if (editor instanceof JTextField) {
            String value = ((JTextField)editor).getText();
            int row = argumentsTable.getEditingRow();
            int column = argumentsTable.getEditingColumn();
            argumentsTable.setValueAt(value, row, column);           
        }
    }
}

/**
 * Editor component for inplace editor for parametrized sense.
 */
class SenseEditor extends JPanel {

    private final JTextField nameTextField;
    private final ArgumentsTableModel argumentsModel;
    private final JComboBox predicateCombo;
    private final JTextField operandText;
    private final JTable argumentsTable;

    /**
     * Create new editor for primitive.
     *
     * @param sense Sense which will be edited
     * @param params Parameters of the sense, from {@link PrimitiveData}
     * @param chain Chain to the sense, inclusive.
     */
    SenseEditor(Sense sense, ParamInfo[] params, InplaceEditorProvider.EditorController controller, LapChain chain) {
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        setBorder(BorderFactory.createLineBorder(Color.BLACK));

        nameTextField = new JTextField();
        nameTextField.setText(sense.getName());
        add(nameTextField);

        predicateCombo = new JComboBox(Sense.Predicate.getPredicates());
        predicateCombo.setSelectedItem(sense.getPredicate());
        add(predicateCombo);

        operandText = new JTextField(Result.toLap(sense.getOperand()));
        add(operandText);

        argumentsModel = ArgumentsTableModelFactory.createLeafModel(params, sense.getArguments());
        argumentsTable = new VariableTable(argumentsModel);

        JButton addArgBtn = new JButton("Add argument");
        ActionListener addArgumentListener = new AddArgumentAction(argumentsTable, argumentsModel, controller, chain);
        addArgBtn.addActionListener(addArgumentListener);
        add(addArgBtn);

        JButton removeArgBtn = new JButton("Remove argument");
        ActionListener removeArgumentListener = new RemoveArgumentAction(argumentsTable, argumentsModel);
        removeArgBtn.addActionListener(removeArgumentListener);
        add(removeArgBtn);

        add(argumentsTable.getTableHeader());
        add(argumentsTable);
    }

    String getSenseName() {
        return nameTextField.getText().trim();
    }

    Predicate getPredicate() {
        return (Predicate) predicateCombo.getSelectedItem();
    }

    String getOperandString() {
        return operandText.getText().trim();
    }

    List<TableArgument> getArguments() {
        return argumentsModel.getArguments();
    }
    
    public void stopEditting() {
        Component editor = argumentsTable.getEditorComponent();
        if (editor instanceof JTextField) {
            String value = ((JTextField)editor).getText();
            int row = argumentsTable.getEditingRow();
            int column = argumentsTable.getEditingColumn();
            argumentsTable.setValueAt(value, row, column);           
        }
    }
}
