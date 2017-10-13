package cz.cuni.amis.pogamut.sposh.elements;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

/**
 * Test some methods of value.
 *
 * @author Honza
 */
public class ValueTest {

    private void testValueFail(String value, Class clazz) {
        Object parsedValue;
        try {
            parsedValue = Result.parseValue(value);
        } catch (ParseException ex) {
            return;
        }
        // value was parsed, but is correct?
        if (clazz.isInstance(parsedValue)) {
            fail("Value created from string \"" + value + "\" is not " + clazz + " but " + parsedValue.getClass());
        }
    }

    private void testValueSucceed(String value, Class clazz) throws ParseException {
        Object parsedValue = Result.parseValue(value);
        if (!clazz.isInstance(parsedValue)) {
            fail("Value created from string \"" + value + "\" is not " + clazz + " but " + parsedValue.getClass());
        }
    }

    @Test
    public void parseNil() throws ParseException {
        Object expectedValue = null;
        Object parsedValue = Result.parseValue("nil");

        assertEquals(expectedValue, parsedValue);
    }

    @Test
    public void parseTrue() throws ParseException {
        testValueSucceed("true", Boolean.class);
        testValueSucceed("True", Boolean.class);
        testValueSucceed("TRue", Boolean.class);

        testValueFail("1", Boolean.class);
    }

    @Test
    public void parseFals() throws ParseException {
        testValueSucceed("False", Boolean.class);
        testValueSucceed("false", Boolean.class);
        testValueSucceed("falSe", Boolean.class);
    }

    @Test
    public void testInt() throws ParseException {
        testValueSucceed("-12", Integer.class);
        testValueSucceed("12", Integer.class);
        testValueFail("12.0", Integer.class);
        testValueFail("12.0d", Integer.class);
    }

    @Test
    public void testDouble() throws ParseException {
        testValueSucceed("99.0", Double.class);
        testValueSucceed("-345.75", Double.class);
        testValueSucceed(".54", Double.class);
        testValueSucceed("-.88", Double.class);
        testValueFail(".8.4", Double.class);
    }

    @Test
    public void stringInQuotes() throws ParseException {
        String expectedText = "Hello World!";
        String planText = '"' + expectedText + '"';

        Object parsedText = Result.parseValue(planText);

        assertEquals(expectedText, parsedText);
    }
    
    @Test
    public void parseEnum() throws ParseException {
        String enumValue = "cz.cuni.TestEnum.FOO";
        Object parsedValue = Result.parseValue('\''  + enumValue);
        EnumValue expected = new EnumValue(enumValue);
        
        assertEquals(expected, parsedValue);
    }

    @Test(expected=ParseException.class)
    public void failToParseEnum() throws ParseException {
        Result.parseValue("'cz.cuni.TestE  num.FOO");
    }
    
    @Test(expected=ParseException.class)
    public void enumWithoutQuote() throws ParseException {
        Result.parseValue("cz.cuni.TestEnum.FOO");
    }
    
    @Test
    public void serializeEnum() {
        String enumValueString = "cz.cuni.TestEnum.FOO";
        EnumValue enumValue = new EnumValue(enumValueString);
        String serializedEnumValue = Result.toLap(enumValue);
        
        String expected = "'cz.cuni.TestEnum.FOO";
        assertEquals(expected, serializedEnumValue);
    }
}
