package cz.cuni.amis.pogamut.sposh.engine;

import cz.cuni.amis.pogamut.sposh.elements.Arguments;
import org.junit.*;

/**
 * Tests checking how does {@link VariableContext} behaves with parent context.
 * @author Honza
 */
public class VariableContextTest extends Assert {
    
    private VariableContext parentCtx;
    private VariableContext ctx;
    
    @Before
    public void initialize() {
        parentCtx = new VariableContext();
        parentCtx.put("parent", "parentValue");
        parentCtx.put("override", "parentOverrideValue");

        ctx = new VariableContext(parentCtx, Arguments.EMPTY);
        ctx.put("child", "childValue");
        ctx.put("override", "childOverrideValue");
    }
    
    @Test
    public void childValue() {
        Object result = ctx.getValue("child");
        assertEquals("childValue", result);
    }

    @Test
    public void parentValue() {
        Object result = ctx.getValue("parent");
        assertEquals("parentValue", result);
    }

    @Test
    public void childOverridesParentValue() {
        Object result = ctx.getValue("override");
        assertEquals("childOverrideValue", result);
    }

    @Test
    public void parentKeepsOverridenValue() {
        Object result = parentCtx.getValue("override");
        assertEquals("parentOverrideValue", result);
    }
    
    
    @Test(expected=IllegalArgumentException.class)
    public void missingValue() {
        ctx.getValue("missing");
    }
    
    
}
