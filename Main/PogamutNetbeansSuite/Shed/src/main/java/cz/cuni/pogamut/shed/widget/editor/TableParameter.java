package cz.cuni.pogamut.shed.widget.editor;

import cz.cuni.amis.pogamut.sposh.elements.Arguments;
import cz.cuni.amis.pogamut.sposh.elements.Arguments.Argument;
import cz.cuni.amis.pogamut.sposh.elements.FormalParameters;
import cz.cuni.amis.pogamut.sposh.elements.ParseException;
import cz.cuni.amis.pogamut.sposh.elements.Result;
import cz.cuni.amis.pogamut.sposh.engine.VariableContext;
import cz.cuni.amis.pogamut.sposh.exceptions.FubarException;
import org.openide.util.Exceptions;

/**
 * Class for holding variables of the called node.
 */
class TableParameter {

    /**
     * Name of parameter in the node.
     */
    private String name;
    /**
     * String representation of the default value of the parameter.
     */
    private String defaultValueString;
    /**
     * String representation of the argument value. Argument overrides default
     * value. Can be value, variable or blank, if argument does not override.
     */
    private String argumentValueString;

    /**
     *
     * @param name
     * @param defaultValueString
     * @param argumentString variable name or value in lap forma. If empty
     * string, parameter is not overriden.
     */
    public TableParameter(String name, String defaultValueString, String argumentString) {
        this.name = name;
        this.defaultValueString = defaultValueString;
        this.argumentValueString = argumentString;
    }

    TableParameter(String name, String defaultValueString) {
        this(name, defaultValueString, "");;
    }

    TableParameter(FormalParameters.Parameter param) {
        this(param.getName(), Result.toLap(param.getDefaultValue()));
    }

    public String getName() {
        return name;
    }

    public String getDefaultValueString() {
        return defaultValueString;
    }

    public String getArgumentString() {
        return argumentValueString;
    }

    boolean isOverriden() {
        return !argumentValueString.isEmpty();
    }

    void setName(String newName) {
        this.name = newName;
    }

    /**
     * Get default value of parameter in object form. Default value is always
     * value, never empty or variable.
     *
     * @return
     */
    Object getDefaultValue() {
        try {
            return Result.parseValue(defaultValueString);
        } catch (ParseException ex) {
            throw new FubarException("Default value " + defaultValueString + " should always be parsable.", ex);
        }
    }

    /**
     * Create overriding argument of the parameter. Basically create argument
     * with same name as parameter (thus if used it overrides the parameter).
     *
     * Since argument can be value or variable, if empty, throw an exception.
     * This method should be called only if {@link #isOverriden() } returns
     * true.
     *
     * @throws IllegalStateException When created argument wouldn't be valid.
     */
    Argument createOverrideArgument(VariableContext ctx) throws IllegalStateException {
        assert !argumentValueString.isEmpty();

        if (Result.isVariableName(argumentValueString)) {
            if (!ctx.hasVariable(argumentValueString)) {
                throw new IllegalStateException("Variable " + argumentValueString + " is not defined and can't be used as value.");
            }
            return Arguments.Argument.createVariableArgument(name, argumentValueString);
        }
        try {
            Object value = Result.parseValue(argumentValueString);
            return Arguments.Argument.createValueArgument(name, value);
        } catch (ParseException ex) {
            throw new IllegalStateException("Argument string " + argumentValueString + " should be either variable or value.", ex);
        }
    }

    /**
     * Set new default value string.
     *
     * @param valueString New default value of parameter, string form. Can be
     * only value, not blank or variable.
     * @throws ParseException If passed string is not value string.
     */
    void setDefaultValueString(String valueString) throws ParseException {
        // must pass the parser.
        Result.parseValue(valueString);
        this.defaultValueString = valueString;
    }

    /**
     * Set argument that overrides the parameter to something else. Argument can
     * be blank, if parameter is not overriden, it can be variable name or it
     * can be value in string form.
     *
     * @param valueString value or variable or blank.
     * @throws ParseException If @valueString is not in expected format.
     */
    void setArgumentValueString(String valueString) throws ParseException {
        if (valueString.isEmpty()) {
            this.argumentValueString = valueString;
        } else if (Result.isVariableName(valueString)) {
            this.argumentValueString = valueString;
        } else {
            Result.parseValue(valueString);
            this.argumentValueString = valueString;
        }
    }
}
