package cz.cuni.amis.pogamut.sposh.elements;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Honza
 */
public class SenseTest extends Assert {

    private Sense sense;
    private String initialName = "initialSenseName";

    @Before
    public void setUp() {
        sense = LapElementsFactory.createSense(initialName);
    }

    @Test
    public void changeName() throws ParseException {
        String newName = "other";
        sense.parseSense(newName);

        assertEquals(newName, sense.getName());
        assertEquals(Sense.Predicate.DEFAULT, sense.getPredicate());
        assertEquals(Boolean.TRUE, sense.getOperand());
    }

    @Test
    public void fqn() throws ParseException {
        String fqn = "cz.cuni.Run4Ever";
        sense.parseSense(fqn);
        assertEquals(fqn, sense.getName());
        assertEquals(Sense.Predicate.DEFAULT, sense.getPredicate());
        assertEquals(Boolean.TRUE, sense.getOperand());
    }

    @Test
    public void nameAndValue() throws ParseException {
        String senseName = "cz.cuni.Run4Ever";
        int value = 52;
        String input = senseName + ' ' + value;

        sense.parseSense(input);

        assertEquals(senseName, sense.getName());
        assertEquals(Sense.Predicate.DEFAULT, sense.getPredicate());
        assertEquals(value, sense.getOperand());
    }

    @Test(expected = ParseException.class)
    public void fourTokens() throws ParseException {
        sense.parseSense("cz.cuni.Health > 90 point 2");
    }

    @Test
    public void sensePredicateBoolean() throws ParseException {
        String senseName = "cz.Running";
        Sense.Predicate predicate = Sense.Predicate.EQUAL;
        Boolean value = Boolean.TRUE;
        String input = senseName  + ' ' + "True"+ ' ' + predicate;

        sense.parseSense(input);

        assertEquals(senseName, sense.getName());
        assertEquals(Sense.Predicate.EQUAL, sense.getPredicate());
        assertEquals(value, sense.getOperand());
    }

    @Test
    public void senseBoolstringPredicate() throws ParseException {
        String senseName = "cz.Running";
        Sense.Predicate predicate = Sense.Predicate.EQUAL;
        Boolean value = Boolean.TRUE;
        String input = senseName + ' ' + "True" + ' ' + predicate;

        sense.parseSense(input);

        assertEquals(senseName, sense.getName());
        assertEquals(Sense.Predicate.EQUAL, sense.getPredicate());
        assertEquals(value, sense.getOperand());
    }

    @Test(expected = ParseException.class)
    public void nothing() throws ParseException {
        sense.parseSense("");
    }

    @Test(expected = ParseException.class)
    public void invalidSenseToken() throws ParseException {
        sense.parseSense("cz..cuni.4ever");
    }

    @Test(expected = ParseException.class)
    public void invalidName() throws ParseException {
        sense.parseSense("cz.cuni.4ever");
    }

    @Test(expected = ParseException.class)
    public void sensePredicateValue() throws ParseException {
        sense.parseSense("cz.cuni.Health > 90");
    }

    @Test
    public void senseStringPredicate() throws ParseException {
        sense.parseSense("cz.cuni.Health \"Hello World!\" > ");
        assertEquals("cz.cuni.Health", sense.getName());
        assertEquals(Sense.Predicate.GREATER, sense.getPredicate());
        assertEquals("Hello World!", sense.getOperand());
    }

    @Test
    public void senseDoublePredicate() throws ParseException {
        sense.parseSense("cz.cuni.Distance -8.14e+5 <=");
        assertEquals("cz.cuni.Distance", sense.getName());
        assertEquals(Sense.Predicate.LOWER_OR_EQUAL, sense.getPredicate());
        assertEquals(-8.14e+5, sense.getOperand());
    }

    @Test(expected = ParseException.class)
    public void wrongPredicate() throws ParseException {
        sense.parseSense("name 9 =!=");
    }
}
