package cz.cuni.amis.pogamut.sposh.elements;

import cz.cuni.amis.pogamut.sposh.elements.Arguments.Argument;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author Honza Havlicek
 */
public class ArgumentsTest extends Assert {

    private Arguments args;
    private FormalParameters formal;

    @Before
    public void setUp() {
        args = new Arguments();
        formal = new FormalParameters();
        formal.add(new FormalParameters.Parameter("$intParam", 12));
        formal.add(new FormalParameters.Parameter("$doubleParam", Math.PI));
        formal.add(new FormalParameters.Parameter("$boolParam", Boolean.FALSE));
        formal.add(new FormalParameters.Parameter("$stringParam", "Hello World!"));

        args.addFormal(new Arguments.ValueArgument("$valInt", Integer.MIN_VALUE), formal);
        args.addFormal(new Arguments.ValueArgument("$valDouble", Math.E), formal);
        args.addFormal(new Arguments.ValueArgument("$valBool", Boolean.TRUE), formal);
        args.addFormal(new Arguments.ValueArgument("$valString", "Joe"), formal);

        args.addFormal(new Arguments.VariableArgument("$varDoubleMax", "$doubleParam"), formal);
        args.addFormal(new Arguments.VariableArgument("$varIntMin", "$intParam"), formal);
        args.addFormal(new Arguments.VariableArgument("$varBool", "$boolParam"), formal);
        args.addFormal(new Arguments.VariableArgument("$varString", "$stringParam"), formal);
    }

    @Test
    public void t() {
        String[] expectedNames = new String[]{ "$alpha", "$beta", "$gamma"};
        
        Arguments arguments = new Arguments();
        arguments.add(new Arguments.ValueArgument("$beta", null));
        arguments.add(new Arguments.ValueArgument("$gamma", null));
        arguments.add(new Arguments.ValueArgument("$alpha", null));
        String[] names = arguments.getAllNames();
        
        assertArrayEquals(expectedNames, names);
    }
    
    @Test
    public void size() {
        assertEquals(8, args.size());
    }

    @Test
    public void intValue() {
        Argument get = args.get(0);
        assertEquals(Integer.MIN_VALUE, get.getValue());
    }

    @Test
    public void doubleValue() {
        Argument get = args.get(1);
        assertEquals(Math.E, get.getValue());
    }

    @Test
    public void boolValue() {
        Argument get = args.get(2);
        assertEquals(Boolean.TRUE, get.getValue());
    }

    @Test
    public void stringValue() {
        Argument get = args.get(3);
        assertEquals("Joe", get.getValue());
    }

    @Test
    @Ignore
    public void intVariable() {
        Argument get = args.get(4);
        assertEquals(Math.PI, get.getValue());
    }

    @Test
    @Ignore
    public void doubleVariable() {
        Argument get = args.get(5);
        assertEquals(Math.E, get.getValue());
    }

    @Test
    @Ignore
    public void boolVariable() {
        Argument get = args.get(6);
        assertEquals(Boolean.FALSE, get.getValue());
    }

    @Test
    @Ignore
    public void stringVariable() {
        Argument get = args.get(7);
        assertEquals("Hello World!", get.getValue());
    }
}
