package cz.cuni.pogamut.shed.widget.editor;

import cz.cuni.amis.pogamut.sposh.elements.Arguments;
import cz.cuni.amis.pogamut.sposh.elements.FormalParameters;
import cz.cuni.amis.pogamut.sposh.elements.ParseException;
import cz.cuni.amis.pogamut.sposh.elements.Result;
import cz.cuni.amis.pogamut.sposh.exceptions.FubarException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

/**
 * {@link TableModel} for parameters of AP or C.
 */
class ParametersTableModel extends AbstractTableModel {

    /**
     * List of variable that are shown to the user.
     */
    private final List<TableParameter> variables = new LinkedList<TableParameter>();
    private static final String[] columns = {"Parameter name", "Default value", "Argument value"};

    /**
     * Take the formal parameters and create the model. If the arguments
     * override the parameter, use the value in the column.
     *
     * @param params
     * @param args
     */
    public ParametersTableModel(FormalParameters params, Arguments args) {
        // for each parameter find appropriate argument
        for (int paramIndex = 0; paramIndex < params.size(); ++paramIndex) {
            FormalParameters.Parameter param = params.get(paramIndex);

            TableParameter tableParamerter = new TableParameter(param);
            // if user supplied value, get it and put it into ctxVariableValue instead of default value
            for (Arguments.Argument arg : args) {
                String argumentName = arg.getParameterName();

                if (isArgumentNameIndex(argumentName)) {
                    if (getArgumentNameIndex(argumentName) == paramIndex) {
                        // parameter has value overriden by argument with index
                        tableParamerter = createTableParameter(param, arg);
                    }
                } else if (isArgumentNameVariable(argumentName)) {
                    String paramName = param.getName();
                    if (argumentName.equals(paramName)) {
                        // parameter has value overriden by argument with name
                        tableParamerter = createTableParameter(param, arg);
                    }
                } else {
                    throw new FubarException("Illegal argument name " + argumentName);
                }
            }
            variables.add(tableParamerter);
        }
    }

    /**
     * Create table variable from
     *
     * @param that was overriden by the @arg.
     * @param param
     * @param arg
     * @return
     */
    private TableParameter createTableParameter(FormalParameters.Parameter param, Arguments.Argument arg) {
        TableParameter tableParameter;
        String defaultValueString = Result.toLap(param.getDefaultValue());
        if (arg.getParameterVariable() != null) {
            // arg is variable, not value.
            tableParameter = new TableParameter(param.getName(), defaultValueString, arg.getParameterVariable());
        } else {
            tableParameter = new TableParameter(param.getName(), defaultValueString, Result.toLap(arg.getValue()));
        }
        return tableParameter;
    }

    private boolean isArgumentNameIndex(String argumentName) {
        try {
            Integer.parseInt(argumentName);
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    private int getArgumentNameIndex(String argumentName) {
        int paramterIndex = Integer.parseInt(argumentName);
        return paramterIndex;
    }

    private boolean isArgumentNameVariable(String argumentName) {
        return argumentName.startsWith("$");
    }

    @Override
    public String getColumnName(int column) {
        return columns[column];
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }

    @Override
    public int getRowCount() {
        return variables.size();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        TableParameter variable = variables.get(rowIndex);

        if (columnIndex == 0) {
            return variable.getName();
        } else if (columnIndex == 1) {
            return variable.getDefaultValueString();
        } else if (columnIndex == 2) {
            return variable.getArgumentString();
        } else {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public boolean isCellEditable(int row, int col) {
        return true;
    }

    @Override
    public void setValueAt(Object value, int rowIndex, int columnIndex) {
        TableParameter variable = variables.get(rowIndex);

        String valueString = (String) value;
        if (columnIndex == 0) {
            variable.setName(valueString);
        } else if (columnIndex == 1) {
            try {
                variable.setDefaultValueString(valueString);
            } catch (ParseException ex) {
                return;
            }
        } else if (columnIndex == 2) {
            try {
                variable.setArgumentValueString(valueString);
            } catch (ParseException ex) {
                return;
            }
        } else {
            throw new IllegalArgumentException();
        }
        fireTableCellUpdated(rowIndex, columnIndex);
    }

    /**
     * Delete variable with specified index.
     *
     * @param deleteIndex Row index where is the variable to delete,
     */
    public void deleteVariable(int deleteIndex) {
        variables.remove(deleteIndex);
        fireTableRowsDeleted(deleteIndex, deleteIndex);
    }

    /**
     * Add new parameter.
     *
     * @param addIndex Index at which should be new parameter added
     * @param parameterName Name of new parameter
     * @param defaultValue Default value of parameter
     */
    public void addParameter(int addIndex, String parameterName, Object defaultValue) {
        TableParameter newVariable = new TableParameter(parameterName, Result.toLap(defaultValue));
        variables.add(addIndex, newVariable);
        fireTableDataChanged();
    }

    /**
     * @return unmodifiable list of all parameters.
     */
    public List<TableParameter> getParameters() {
        return Collections.unmodifiableList(variables);
    }
}
