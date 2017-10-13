package cz.cuni.pogamut.shed.widget.editor;

import cz.cuni.amis.pogamut.sposh.elements.*;
import cz.cuni.amis.pogamut.sposh.elements.Arguments.Argument;
import cz.cuni.amis.pogamut.sposh.exceptions.FubarException;
import cz.cuni.amis.pogamut.sposh.executor.ParamInfo;
import cz.cuni.amis.pogamut.sposh.executor.PrimitiveData;
import java.util.*;
import javax.swing.table.AbstractTableModel;

class ArgumentsTableModel extends AbstractTableModel {

    /**
     * Columns of the model.
     */
    static final String[] columns = {"Argument name", "Value"};
    /**
     * All arguments in the model.
     */
    final List<TableArgument> arguments;
    final boolean areNamesEditable;

    /**
     * Create model for argument variables.
     *
     * @param modelArgs Initial list of arguments in the model
     * @param areArgNamesEditable Can user edit names of the arguments?
     */
    ArgumentsTableModel(List<TableArgument> modelArgs, boolean areArgNamesEditable) {
        this.arguments = new ArrayList<TableArgument>(modelArgs);
        this.areNamesEditable = areArgNamesEditable;
    }

    @Override
    public final int getColumnCount() {
        return columns.length;
    }

    @Override
    public final String getColumnName(int column) {
        return columns[column];
    }

    @Override
    public final int getRowCount() {
        return arguments.size();
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 0:
                return areNamesEditable;
            case 1:
                return true;
            default:
                throw new FubarException("Column " + columnIndex);
        }
    }

    @Override
    public final Object getValueAt(int rowIndex, int columnIndex) {
        if (columnIndex == 0) {
            return this.arguments.get(rowIndex).getName();
        } else if (columnIndex == 1) {
            return this.arguments.get(rowIndex).getValueString();
        } else {
            throw new FubarException("Unexpected column index " + columnIndex);
        }
    }

    @Override
    public final void setValueAt(Object value, int rowIndex, int columnIndex) {
        TableArgument argument = arguments.get(rowIndex);

        String valueString = (String) value;
        if (columnIndex == 0) {
            argument.setName(valueString);
        } else if (columnIndex == 1) {
            if (valueString.isEmpty()) {
                argument.setValueBlank();
            } else {
                try {
                    argument.setValueString(valueString);
                } catch (ParseException ex) {
                    return;
                }
            }
        } else {
            throw new FubarException("Unexpected position " + rowIndex + " " + columnIndex);
        }
        fireTableCellUpdated(rowIndex, columnIndex);
    }

    public final void deleteArgument(int deleteIndex) {
        this.arguments.remove(deleteIndex);
        fireTableRowsDeleted(deleteIndex, deleteIndex);
    }

    /**
     * Add new argument with specified value.
     *
     * @param addIndex Index at which should be new parameter added
     * @param argName Name of new parameter
     * @param value Object value of parameter
     */
    public final void addArgument(int addIndex, String argName, Object value) {
        TableArgument newVariable = TableArgumentFactory.createArgWithValue(argName, value);
        this.arguments.add(addIndex, newVariable);
        fireTableDataChanged();
    }

    /**
     * Add new argument that is reference to some other variable.
     *
     * @param addIndex Index at which should be new parameter added
     * @param argName Name of new parameter
     * @param variableName Name of variable whose value will be used
     */
    public final void addVariableArgument(int addIndex, String argName, String variableName) {
        TableArgument newVariable = TableArgumentFactory.createFromArgument(Argument.createVariableArgument(argName, variableName));
        this.arguments.add(addIndex, newVariable);
        fireTableDataChanged();
    }
    
    /**
     * @return unmodifiable collection of argfuments of this model.
     */
    public List<TableArgument> getArguments() {
        return Collections.unmodifiableList(arguments);
    }
}

class ArgumentsTableModelFactory {

    /**
     * This is factory, don't allow instantiation.
     */
    private ArgumentsTableModelFactory() {
    }

    /**
     * Create table model for arguments being passed to inner node of the posh
     * tree. The inner node has two editors for the arguments: one editor takes
     * can add/change/remove parameters of the node and the other one is for all
     * arguments that do not override some parameter of the node.
     *
     * For example, AP has parameter $time and is being passed three arguments:
     * $distance, $time and $method. The editor for parameters will take care of
     * parameter $time and passed argument $time. The rest, i.e. $distance and
     * $method are handled by argument editor. This method will create model for
     * the remaining argments (in our example $distance and $method, but not
     * $time).
     *
     * @param params Parameters of the inner node
     * @param args All arguments passed to the inner node.
     * @return Created model.
     */
    public static ArgumentsTableModel createNodeModel(FormalParameters params, Arguments args) {
        List<TableArgument> modelArgs = new LinkedList<TableArgument>();
        // TODO: remove args used in params some other way, this doesn;t do any checking
        for (Arguments.Argument arg : args) {
            String argName = arg.getName();
            // ignore indexed (unnamed) arguments, only named are further considered.
            if (!argName.startsWith("$")) {
                continue;
            }
            // args used to override some parameter are ignored
            boolean argOverridesParam = false;
            for (FormalParameters.Parameter param : params) {
                String paramName = param.getName();
                if (paramName.equals(argName)) {
                    argOverridesParam = true;
                }
            }
            if (argOverridesParam) {
                continue;
            }

            modelArgs.add(TableArgumentFactory.createFromArgument(arg));
        }
        return new ArgumentsTableModel(modelArgs, true);
    }

    /**
     * Create table model that will contain arguments with names from @argNames
     * and @args. If some argument from @argNames doesn't have specified value
     * in the @args, it will be blank in the model.
     *
     * @param params Names use by the primitive, the ones from {@link PrimitiveData}
     * @param args Arguments passed from the plan to the primitive
     * @return Created model
     */
    public static ArgumentsTableModel createLeafModel(ParamInfo[] params, Arguments args) {
        List<TableArgument> modelArgs = new LinkedList<TableArgument>();
        for (Arguments.Argument arg : args) {
            ParamInfo info = getParamInfo(params, arg.getName());
            modelArgs.add(TableArgumentFactory.createFromArgument(arg, info));
        }

        Set<String> argNamesSet = new HashSet<String>(Arrays.asList(args.getAllNames()));
        for (ParamInfo param : params) {
            boolean planPassesValue = argNamesSet.contains(param.name);
            if (!planPassesValue) {
                modelArgs.add(TableArgumentFactory.createBlank(param));
            }
        }
        return new ArgumentsTableModel(modelArgs, false);
    }
    
    private static ParamInfo getParamInfo(ParamInfo[] paramsInfo, String searchedParam) {
        for (ParamInfo info : paramsInfo) {
            if (info.name.equals(searchedParam)) {
                return info;
            }
        }
        return null;
    }
}
