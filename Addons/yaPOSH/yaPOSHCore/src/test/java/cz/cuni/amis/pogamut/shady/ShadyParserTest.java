package cz.cuni.amis.pogamut.shady;

import cz.cuni.amis.pogamut.sposh.elements.PlanTest;
import cz.cuni.amis.pogamut.sposh.engine.ITestPrimitive;
import cz.cuni.amis.pogamut.sposh.engine.TestWorkExecutor;
import cz.cuni.amis.pogamut.sposh.executor.IWorkExecutor;
import java.io.StringReader;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Honza
 */
public class ShadyParserTest extends PlanTest {

    private static class Pair {

        public String input;
        public Object result;

        public Pair(String input, Object result) {
            this.input = input;
            this.result = result;
        }
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    private void testInfo(String testName) {
        System.out.println(testName);

    }

    /**
     * Test rule "name"
     */
    @Test
    public void testName() throws Exception {
        testInfo("name");
        ShadyParser parser = new ShadyParser(new StringReader(""));

        String[] okTests = {
            "fix",
            "ShadyParser",
            "cz.cuni.pogamut.shady.ShadyParser",
            "$love.$$.hate12and_more",};
        for (String test : okTests) {
            System.out.println(" - " + test);
            parser.ReInit(new StringReader(test));
            assertEquals(parser.name(), test);
        }
    }

    @Test
    public void testArg() throws Exception {
        testInfo("arg");
        ShadyParser parser = new ShadyParser(new StringReader(""));
        Pair[] okTests = {
            new Pair("123", new ArgInt(123)),
            new Pair("0", new ArgInt(0)),
            new Pair("-957", new ArgInt(-957)),
            new Pair("\"Hello World!\"", new ArgString("Hello World!")),
            new Pair("'a'", new ArgChar('a')),
            new Pair("'g'", new ArgChar('g')),
            new Pair("1.2345678900", new ArgFloat(1.2345678900)),
            new Pair(".1230001", new ArgFloat(.1230001)),
            new Pair("-42.78", new ArgFloat(-42.78)),
            new Pair("'g'", new ArgChar('g')),
            new Pair("'g'", new ArgChar('g')),
            new Pair("\"Hello\\nWorld\"", new ArgString("Hello\nWorld")),
            new Pair("'\\r'", new ArgChar('\r')),
            new Pair("'\\n'", new ArgChar('\n')),};

        for (Pair test : okTests) {
            System.out.println(" - " + test.input);
            parser.ReInit(new StringReader(test.input));
            assertEquals(parser.arg(), test.result);
        }
    }

    @Test
    public void testArgChar() throws Exception {
        testInfo("argString");
        ShadyParser parser = new ShadyParser(new StringReader(""));
        Pair[] okTests = {
            new Pair("'a'", new ArgChar('a')),
            new Pair("'Z'", new ArgChar('Z')),
            new Pair("'\"'", new ArgChar('"')),
            new Pair("'\\\"'", new ArgChar('\"')),
            new Pair("'\\0'", new ArgChar('\0')),
            new Pair("'\\00'", new ArgChar('\00')),
            new Pair("'\\000'", new ArgChar('\000')),
            new Pair("'\\7'", new ArgChar('\7')),
            new Pair("'\\77'", new ArgChar('\77')),
            new Pair("'\\377'", new ArgChar('\377')),
            new Pair("'\\5'", new ArgChar('\5')),
            new Pair("'\\42'", new ArgChar('\42')),
            new Pair("'\\123'", new ArgChar('\123')),
            new Pair("'\\b'", new ArgChar('\b')),
            new Pair("'\\t'", new ArgChar('\t')),
            new Pair("'\\n'", new ArgChar('\n')),
            new Pair("'\\f'", new ArgChar('\f')),
            new Pair("'\\r'", new ArgChar('\r')),
            new Pair("'\\''", new ArgChar('\'')),
            new Pair("'\\\"'", new ArgChar('\"')),
            new Pair("'\\\\'", new ArgChar('\\')),
            new Pair("'今'", new ArgChar('今')),
            new Pair("'\\u4eca'", new ArgChar('\u4eca')), // ima, now
            new Pair("'\\uuuuuu4eca'", new ArgChar('\uuu4eca')), // ima, now
            new Pair("' '", new ArgChar(' ')),};

        for (Pair test : okTests) {
            System.out.println(" - " + test.input);
            parser.ReInit(new StringReader(test.input));
            assertEquals(parser.arg(), test.result);
        }
    }

    @Test
    public void testArgString() throws Exception {
        testInfo("argString");
        ShadyParser parser = new ShadyParser(new StringReader(""));
        Pair[] okTests = {
            new Pair("\"\"", new ArgString("")),
            new Pair("\"Hello\\nWorld\"", new ArgString("Hello\nWorld")),
            new Pair("\"\\0\"", new ArgString("\0")),
            new Pair("\"\\127\"", new ArgString("\127")),
            new Pair("\"\\0\\11\\01\\00\\000\\001\\111\\377\\7\\2\\12\\32\\77\\255\"", new ArgString("\0\11\01\00\000\001\111\377\7\2\12\32\77\255")),
            new Pair("\"'\"", new ArgString("'")),
            new Pair("\"\\b\\t\\n\\f\\r\\'\\\"\\\\\"", new ArgString("\b\t\n\f\r\'\"\\")),
            new Pair("\"\\u306A\\u306b \\u30673059 \\u304B?\"", new ArgString("\u306A\u306b \u30673059 \u304B?")),
            new Pair("\"  今  A今\"", new ArgString("  今  A今")), // XXX: fails for 今今
            new Pair("\"\\n\"", new ArgString("\n")),};

        for (Pair test : okTests) {
            System.out.println(" - " + test.input);
            parser.ReInit(new StringReader(test.input));
            assertEquals(parser.arg(), test.result);
        }
    }

    @Test
    public void testArgs() throws Exception {
        testInfo("args");
        ShadyParser parser = new ShadyParser(new StringReader(""));
        Pair[] okTests = {
            new Pair("123", new ArgInt(123)),};
        for (Pair test : okTests) {
            System.out.println(" - " + test.input);
            parser.ReInit(new StringReader(test.input));
            assertEquals(parser.arg(), test.result);
        }
    }

    @Test
    public void testCall() throws Exception {
        testInfo("call");
        String input = "(test.fix 1 \"Hello world\" -.14 'z')";
        System.out.println(" - " + input);
        ShadyParser parser = new ShadyParser(new StringReader(input));
        NodeCall call = parser.call();

        assertEquals(call.getName(), "test.fix");
        assertEquals(call.getArgs().get(0), new ArgInt(1));
        assertEquals(call.getArgs().get(1), new ArgString("Hello world"));
        assertEquals(call.getArgs().get(2), new ArgFloat(-.14));
        assertEquals(call.getArgs().get(3), new ArgChar('z'));
    }

    private void testQueryCall(String input, String name, IArgument... args) throws Exception {
        System.out.println(" - " + input);
        ShadyParser parser = new ShadyParser(new StringReader(input));
        QueryCall call = parser.queryCall();

        assertEquals(call.getName(), name);
        for (int argIdx = 0; argIdx < args.length; ++argIdx) {
            assertEquals(call.getArgs().get(argIdx), args[argIdx]);
        }
    }

    @Test
    public void testQueryCall() throws Exception {
        testInfo("queryCall");

        testQueryCall("call9.one-one -1 \"Nya \"    )", "call9.one-one", new ArgInt(-1), new ArgString("Nya "));
        testQueryCall("runRabbitRun)", "runRabbitRun");
    }

    /**
     * Take the input, parse it using {@link ShadyParser#value() } and compare
     * the result of query with passed query.
     * @param input input to be parsed.
     * @param query query we expect from the input
     * @throws Exception
     */
    private void testValue(String input, IQuery query) throws Exception {
        System.out.println(" - " + input);
        ShadyParser parser = new ShadyParser(new StringReader(input));
        IQuery value = parser.value();

        IWorkExecutor executor = new TestWorkExecutor(new ITestPrimitive[]{});

        assertEquals(value.execute(executor), query.execute(executor));
    }

    @Test
    public void testValue() throws Exception {
        testInfo("value");

        testValue("123", new QueryInt(123));
        testValue("-982", new QueryInt(-982));
        testValue("0", new QueryInt(0));
        testValue("1", new QueryInt(1));

        testValue("-.5", new QueryFloat(-0.5));
        testValue("0.99", new QueryFloat(0.99));
        testValue("62.15", new QueryFloat(62.15));

        testValue("-.5", new QueryFloat(-0.5));
        testValue("0.99", new QueryFloat(0.99));
        testValue("62.15", new QueryFloat(62.15));

        ShadyParser parser = new ShadyParser(new StringReader(""));
        String[] okTriggers = {
            "(health \"Player Jakub\")",
            "(adrenaline)",
            "(love.to.ru12 1 'a')",
            "(call911)"
        };
        for (String triggers : okTriggers) {
            System.out.println(" - " + triggers);
            parser.ReInit(new StringReader(triggers));
            IQuery value = parser.value();
        }
    }

    @Test
    public void testTrigger() throws Exception {
        testInfo("trigger");
        ShadyParser parser = new ShadyParser(new StringReader(""));

        String[] okTriggers = {
            "(not (and 1 0 2 4))",
            "(not 1)",
            "(not (<= (health) 10))",
            "1",
            "-0.123",
            "(>= (health) 90)",
            "(= 90.0 90)",
            "(adrenaline \"Player Honza\")",
            "(or (> (health \"Player Honza\") 90) (> (adrenaline \"Player Honza\") 120))"
        };
        for (String triggerString : okTriggers) {
            System.out.println(" - " + triggerString);
            parser.ReInit(new StringReader(triggerString));
            IQuery trigger = parser.trigger();
        }
    }

    @Test
    public void testElement() throws Exception {
        testInfo("element");
        ShadyParser parser = new ShadyParser(new StringReader(""));

        String[] okTriggers = {
            "(10 1 (run-home))",
            "((how-scared \"Noise\") (and (can-run) (has-energy)) (hide-in-burrlow))",
            "(10 (food-in-proximity 10) (animate \"Rabbit.Sniff.rtm\"))",};
        for (String elementString : okTriggers) {
            System.out.println(" - " + elementString);
            parser.ReInit(new StringReader(elementString));
            NodeElement element = parser.element();
        }
    }

    @Test
    public void testTree() throws Exception {
        testInfo("element");
        String plan = loadPlan("testplans/tree.sde");
        ShadyParser parser = new ShadyParser(new StringReader(plan));
        parser.plan();
        System.out.println(plan);
    }
}
