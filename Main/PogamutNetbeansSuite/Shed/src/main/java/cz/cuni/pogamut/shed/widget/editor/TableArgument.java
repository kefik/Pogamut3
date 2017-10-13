package cz.cuni.pogamut.shed.widget.editor;

import cz.cuni.amis.pogamut.sposh.elements.Arguments;
import cz.cuni.amis.pogamut.sposh.elements.Arguments.Argument;
import cz.cuni.amis.pogamut.sposh.elements.ParseException;
import cz.cuni.amis.pogamut.sposh.elements.Result;
import cz.cuni.amis.pogamut.sposh.engine.VariableContext;
import cz.cuni.amis.pogamut.sposh.executor.ParamInfo;

/**
 * Class holding information about arguments, the passed arguments that do not
 * override parameter. Each argument is represented by a name and its value. The
 * value is a yaposh value in string form (i.e. it must be parsable by {@link Result#parseValue(java.lang.String) )
 * } or it is blank. When value of the argument is blank, it means it is not yet
 * specified.
 *
 * Arguments are used in editors when passing extra + in primitive.
 */
final class TableArgument {

    /**
     * Name of the argument
     */
    private String name;
    /**
     * Representation of the actual value in string form.
     */
    private String valueString;
    /**
     * The argument is a value of some parameter. This object contains info
     * about that parameter or null.
     */
    private final ParamInfo info;

    /**
     * Create argument for table model.
     *
     * @param name Name of the argument
     * @param valueString Value of the argument, string representation of the
     * value of the argument, serialized by {@link Result#toLap(java.lang.Object)
     * } or blank.
     * @param info Info about the argument, what should be its type and so on.
     * Can be null
     */
    TableArgument(String name, String valueString, ParamInfo info) {
        this.name = name;
        this.valueString = valueString;
        this.info = info;
    }

    public String getName() {
        return name;
    }

    public String getValueString() {
        return valueString;
    }

    /**
     * Sets new value string for the argument. must be parsable or variable.
     */
    public void setValueString(String newValueString) throws ParseException {
        if (!Result.isVariableName(newValueString)) {
            Result.parseValue(newValueString);
        }
        this.valueString = newValueString;
    }

    public void setValueBlank() {
        this.valueString = "";
    }

    /**
     * Create {@link Argument} that represents current state of this object.
     *
     * @param ctx Variable context, if value of the argument is a variable, it
     * must be in the ctx, otherwise exception.
     * @return
     * @throws IllegalStateException When valueString is not parsable or if
     * valueString is variable and isnt in ctx.
     */
    public Arguments.Argument createArgument(VariableContext ctx) throws IllegalStateException {
        if (Result.isVariableName(valueString)) {
            if (!ctx.hasVariable(valueString)) {
                throw new IllegalStateException("Variable " + valueString + " is not defined and can't be used as value.");
            }
            return Arguments.Argument.createVariableArgument(name, valueString);
        }

        try {
            Object value = Result.parseValue(valueString);
            return Arguments.Argument.createValueArgument(name, value);
        } catch (ParseException ex) {
            throw new IllegalStateException(ex);
        }
    }

    void setName(String newName) {
        this.name = newName;
    }

    /**
     * Get info about parameter the argument is filling with value.
     */
    ParamInfo getInfo() {
        return info;
    }
}

final class TableArgumentFactory {

    private TableArgumentFactory() {
    }

    public static TableArgument createBlank(ParamInfo info) {
        return new TableArgument(info.name, "", info);
    }

    public static TableArgument createArgWithValue(String name, Object value) {
        return new TableArgument(name, Result.toLap(value), null);
    }

    public static TableArgument createFromArgument(Arguments.Argument arg) {
        return createFromArgument(arg, null);
    }

    public static TableArgument createFromArgument(Argument arg, ParamInfo info) {
        String valueString;
        if (arg.getParameterVariable() != null) {
            valueString = arg.getParameterVariable();
        } else {
            valueString = Result.toLap(arg.getValue());
        }
        return new TableArgument(arg.getName(), valueString, info);
    }
}