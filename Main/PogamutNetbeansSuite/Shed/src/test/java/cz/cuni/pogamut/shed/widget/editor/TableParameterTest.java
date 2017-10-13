package cz.cuni.pogamut.shed.widget.editor;

import cz.cuni.amis.pogamut.sposh.elements.Arguments.Argument;
import cz.cuni.amis.pogamut.sposh.engine.VariableContext;
import org.junit.Assert;
import org.junit.Test;
import org.junit.Before;

/**
 *
 * @author Honza
 */
public class TableParameterTest extends Assert {
    private VariableContext ctx;
    
    private TableParameter stringParameter;
    private TableParameter nilParameter;
    private TableParameter intParameter;
    private TableParameter boolParameter;

    private TableParameter overridenValue;

    @Before
    public void setUp() {
        ctx = new VariableContext();
        ctx.put("$variable", "\"value\"");
        
        overridenValue = new TableParameter("$overridenValueParam", "\"original\"", "\"overriden\"");
        stringParameter = new TableParameter("$stringParam", "\"Hello\"", "");
        nilParameter = new TableParameter("$nilParam", "nil", "");
        intParameter = new TableParameter("$intParam", "15", "");
        boolParameter = new TableParameter("$boolParam", "true", "");
    }

    @Test
    public void stringParam() {
        assertEquals("Hello", stringParameter.getDefaultValue());
    }

    @Test
    public void nilParam() {
        assertEquals(null, nilParameter.getDefaultValue());
    }

    @Test
    public void intParam() {
        assertEquals(15, intParameter.getDefaultValue());
    }

    @Test
    public void boolParam() {
        assertEquals(true, boolParameter.getDefaultValue());
    }
    
    @Test
    public void overridenValueParamArgument() {
        Argument argument = overridenValue.createOverrideArgument(ctx);

        assertEquals("$overridenValueParam", argument.getName());
        assertNull(argument.getParameterVariable());
        assertEquals("overriden", argument.getValue());
    }

    @Test
    public void overridenVariable() {
        TableParameter overridenVariable = new TableParameter("$overridenParam", "\"original\"", "$variable");
        Argument argument = overridenVariable.createOverrideArgument(ctx);

        assertEquals("$overridenParam", argument.getName());
        assertEquals("$variable", argument.getParameterVariable());
    }

    @Test(expected=IllegalStateException.class)
    public void nonexistentOverridenVariable() {
        TableParameter overridenVariable = new TableParameter("$overridenParam", "\"original\"", "$nonexistent");
        overridenVariable.createOverrideArgument(ctx);
    }

    @Test(expected=IllegalStateException.class)
    public void unparsableOverridenValue() {
        TableParameter overridenVariable = new TableParameter("$overridenParam", "\"original\"", "wrong\" Syntax");
        overridenVariable.createOverrideArgument(ctx);
    }
    
}
