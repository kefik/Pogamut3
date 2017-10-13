package cz.cuni.pogamut.shed.widget.editor;

import cz.cuni.amis.pogamut.sposh.elements.*;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import org.netbeans.api.visual.action.InplaceEditorProvider;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;

/**
 * Basic editor for elements (AP and C) that can have passed arguments and also
 * have some parameters.
 *
 * @author Honza
 */
class ParametrizedNodeEditor extends JPanel {

    private final InplaceEditorProvider.EditorController editorController;
    private final JTextField nameTextField;
    private final JButton addVariable;
    private final JButton removeVariable;
    /**
     * Table that represents the parameters of the call
     */
    private final JTable parametersTable;
    private final ArgumentsTableModel argumentsModel;
    /**
     * This table represents arguments of the call.
     */
    private final JTable argumentsTable;
    private final ParametersTableModel parametersModel;
    /**
     * This listener handles situation when used adds new parameter
     */
    private final ActionListener addParameterListener = new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
            String title = "New parameter";
            NotifyDescriptor.InputLine parameterNameDialog = new NotifyDescriptor.InputLine("Specify name of the parameter (must start with $, e.g $foo)", title);

            if (DialogDisplayer.getDefault().notify(parameterNameDialog) != NotifyDescriptor.OK_OPTION) {
                return;
            }
            String parameterName = parameterNameDialog.getInputText().trim();
            if (!Result.isVariableName(parameterName)) {
                NotifyDescriptor.Message paramNameWrong = new NotifyDescriptor.Message("Wrong name of parameter.\nName must match regular expression: " + Result.variableNameRegexp);
                DialogDisplayer.getDefault().notify(paramNameWrong);
                return;
            }

            NotifyDescriptor.InputLine defaultParameterValue = new NotifyDescriptor.InputLine("Specify the default value of the new parameter(string, integer, double, bool or nil):", title);
            if (DialogDisplayer.getDefault().notify(defaultParameterValue) != NotifyDescriptor.OK_OPTION) {
                return;
            }
            Object defaultValue;
            try {
                String defaultValueString = defaultParameterValue.getInputText().trim();
                defaultValue = Result.parseValue(defaultValueString);
            } catch (ParseException ex) {
                NotifyDescriptor.Message paramNameWrong = new NotifyDescriptor.Message("Wrong default value of parameter.\nParsing error: " + ex.getMessage());
                DialogDisplayer.getDefault().notify(paramNameWrong);
                return;
            }

            int addIndex = parametersTable.getSelectedRow();
            if (addIndex == -1) {
                addIndex = parametersTable.getRowCount();
            }

            parametersModel.addParameter(addIndex, parameterName, defaultValue);
            editorController.notifyEditorComponentBoundsChanged();
        }
    };
    private final ActionListener removeVariableListener = new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
            int index = parametersTable.getSelectedRow();
            if (index != -1) {
                parametersModel.deleteVariable(index);
            }
        }
    };

    /**
     * 
     * @param parametrizedElement
     * @param action
     * @param controller
     * @param chain Chain to the node, inclusive
     */
    public ParametrizedNodeEditor(IParametrizedElement parametrizedElement, TriggeredAction action, InplaceEditorProvider.EditorController controller, LapChain chain) {
        String elementName = parametrizedElement.getName();
        assert elementName.equals(action.getName());

        this.editorController = controller;

        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        setBorder(BorderFactory.createLineBorder(Color.BLACK));

        nameTextField = new JTextField();
        nameTextField.setText(elementName);
        add(nameTextField);

        addVariable = new JButton("Add parameter");
        addVariable.addActionListener(addParameterListener);
        add(addVariable);

        removeVariable = new JButton("Remove parameter");
        removeVariable.addActionListener(removeVariableListener);
        add(removeVariable);

        FormalParameters params = parametrizedElement.getParameters();
        Arguments args = action.getArguments();
        parametersModel = new ParametersTableModel(params, args);
        parametersTable = new VariableTable(parametersModel);

        // by default, column header is not shown unless table is in JScrollPane
        add(parametersTable.getTableHeader());
        add(parametersTable);

        argumentsModel = ArgumentsTableModelFactory.createNodeModel(params, args);
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

    /**
     * Get name user has supplied in the textfield.
     *
     * @return
     */
    String getElementName() {
        return nameTextField.getText().trim();
    }

    public List<TableParameter> getParameters() {
        return parametersModel.getParameters();
    }

    public List<TableArgument> getArguments() {
        return argumentsModel.getArguments();
    }
}
