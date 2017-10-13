package cz.cuni.amis.pogamut.sposh.elements;

import org.junit.Assert;
import org.junit.Test;

/**
 * Testing parsing capability for {@link LapPath#parse(java.lang.String) }.
 *
 * @author Honza
 */
public class LapPathParseTest extends Assert {

    @Test(expected = ParseException.class)
    public void emptyPath() throws ParseException {
        LapPath.parse("");
    }

    @Test
    public void planPath() throws ParseException {
        LapPath parsedPath = LapPath.parse("/P:0");
        LapPath expectedPath = new LapPath().concat(LapType.PLAN, 0);

        assertEquals(expectedPath, parsedPath);
    }

    @Test
    public void equalPaths() throws ParseException {
        String path = "/P:0/DC:0/DE:5/A:0/AP:4";
        LapPath expectedPath = LapPath.parse(path);
        LapPath parsedPath = LapPath.parse(path);

        assertEquals(expectedPath, parsedPath);
    }

    @Test
    public void unequalPaths() throws ParseException {
        String path = "/P:0/DC:0/DE:5/A:0/AP:4";
        LapPath expectedPath = LapPath.parse(path);
        String differentPath = "/P:0/DC:0/DC:5/A:0/AP:4";
        LapPath parsedPath = LapPath.parse(differentPath);

        assertNotSame(expectedPath, parsedPath);
    }

    @Test(expected = ParseException.class)
    public void incorrentId() throws ParseException {
        LapPath.parse("/P:-1");
    }

    @Test(expected = ParseException.class)
    public void incorrentType() throws ParseException {
        LapPath.parse("/P:0/DC:0/DE:4/ACTION:0/AP:2");
    }

    @Test(expected = ParseException.class)
    public void extraSpace() throws ParseException {
        LapPath.parse("/P: 0");
    }

    @Test(expected = ParseException.class)
    public void missingType() throws ParseException {
        LapPath.parse("/P:0/:5");
    }

    @Test(expected = ParseException.class)
    public void missingId() throws ParseException {
        LapPath.parse("/P:0/DC:");
    }

    @Test(expected = ParseException.class)
    public void missingTypeSeparator() throws ParseException {
        LapPath.parse("/P0");
    }

    /**
     * Path is not restricted by traversal of actual tree. This is clearly
     * incorrect path, but it should be parsed anyway, because we have no idea
     * what tree looks like, e.g. we could have references to nonexistent APs
     * and so on, so we ignpore correctness in all cases.
     */
    @Test
    public void parseEvenIncorrectOrder() throws ParseException {
        LapPath.parse("/DC:2/P:8/AP:5");
    }
}
