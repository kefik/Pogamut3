package cz.cuni.amis.pogamut.sposh.elements;

import java.io.StringReader;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import cz.cuni.amis.pogamut.shady.ArgString;

/**
 *
 * @author HonzaH
 */
public class DocstringTest extends Assert {

    private PoshPlan plan;

    @Before
    public void setUp() {
        plan = LapElementsFactory.createPlan("testDocstringDC");
    }

    @After
    public void setDown() {
        plan = null;
    }

    private PoshParser createParser(String parsedText) {
        return new PoshParser(new StringReader(parsedText));
    }

    private PoshParser createDocstringParser(String name, String author, String info) {
        String docString = "documentation \"" + name + "\" \"" + author + "\" \"" + info + "\")";
        return createParser(docString);
    }

    private void testDocstring(String name, String author, String info) throws Exception {
        PoshParser parser = createDocstringParser(name, author, info);
        parser.docString(plan);

        assertEquals(ArgString.unescape(name), plan.getName());
        assertEquals(ArgString.unescape(author), plan.getAuthor());
        assertEquals(ArgString.unescape(info), plan.getInfo());
    }

    @Test
    public void testEmpty() throws Exception {
        testDocstring("", "", "");
    }

    @Test
    public void testMultipleWords() throws Exception {
        testDocstring("Fun bot", "Mr. Sparky", "This bot loves to have fun.");
    }

    @Test
    public void testEscapeSequences() throws Exception {
        String commonPart = "- \\b,\\t,\\n,\\r,\\f, \\n\\t__  \\u624A \\000\\377";
        testDocstring("Abc " + commonPart, "Def " + commonPart, "Ghi " + commonPart);
    }

    @Test
    public void testQuotes() throws Exception {
        String commonPart = "\\' \\\\";
        testDocstring("Abc " + commonPart, "Def " + commonPart, "Ghi " + commonPart);
    }

    @Test
    public void testDoubleQuotes() throws Exception {
        String common = "\\\\ \\\" ' \\'";
        String unescaped = "\\ \" ' '";
        testDocstring(common, common, common);
        assertEquals(unescaped, plan.getName());
    }
}
