package cz.cuni.amis.pogamut.sposh.elements;

import java.io.IOException;

import org.junit.Test;

/**
 * Test of parameter int he posh plan that is an link to java enum.
 */
public class EnumParam extends PlanTest {

    @Test
    public void parseEnumPlan() throws IOException, ParseException {
        parsePlan("testplans/009-enumOK.lap");
    }
    
    // specified enum has wrong syntax
    @Test(expected=ParseException.class)
    public void enumWrongSyntax() throws IOException, ParseException {
        parsePlan("testplans/010-enumWrongSyntax.lap");
    }
    
    // Incorrect syntax, there is a quote sign, but nothing behind it.
    @Test(expected=ParseException.class)
    public void enumNoEnum() throws IOException, ParseException {
        parsePlan("testplans/011-enumNoEnum.lap");
    }
    
    // specified enum has no quote sign before it
    @Test(expected=ParseException.class)
    public void enumMissingQuote() throws IOException, ParseException {
        parsePlan("testplans/012-enumMissingQuote.lap");
    }
}
