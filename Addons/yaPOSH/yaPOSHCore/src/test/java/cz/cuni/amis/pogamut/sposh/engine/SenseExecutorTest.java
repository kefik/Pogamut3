package cz.cuni.amis.pogamut.sposh.engine;

import cz.cuni.amis.pogamut.sposh.elements.LapPath;
import cz.cuni.amis.pogamut.sposh.elements.Sense;
import cz.cuni.amis.pogamut.sposh.elements.Sense.Predicate;
import cz.cuni.amis.pogamut.sposh.executor.IWorkExecutor;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Honza
 */
public class SenseExecutorTest {

    public SenseExecutorTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    /**
     * Test of evaluateComparison method, of class SenseExecutor.
     */
    @Test
    public void testEvaluateComparison() {
        System.out.println("evaluateComparison");

        assertTrue(SenseExecutor.evaluateComparison(true, Predicate.EQUAL, true));
        assertTrue(SenseExecutor.evaluateComparison(1, Predicate.EQUAL, true));
        assertTrue(SenseExecutor.evaluateComparison(false, Predicate.EQUAL, false));
        assertTrue(SenseExecutor.evaluateComparison(false, Predicate.EQUAL, 0));
        assertTrue(SenseExecutor.evaluateComparison(null, Predicate.EQUAL, null));

        assertFalse(SenseExecutor.evaluateComparison(-23.5, Predicate.EQUAL, true));
        assertFalse(SenseExecutor.evaluateComparison(6, Predicate.EQUAL, true));
        assertFalse(SenseExecutor.evaluateComparison(0, Predicate.EQUAL, 1));
        assertFalse(SenseExecutor.evaluateComparison(null, Predicate.EQUAL, "Ahoj"));

        assertTrue(SenseExecutor.evaluateComparison("aa", Predicate.EQUAL, "aa"));
        assertFalse(SenseExecutor.evaluateComparison("aa", Predicate.EQUAL, "aA"));

        assertTrue(SenseExecutor.evaluateComparison("aa", Predicate.LOWER,"ab"));
        assertTrue(SenseExecutor.evaluateComparison("cecialia", Predicate.GREATER_OR_EQUAL,"adam"));

        assertTrue(SenseExecutor.evaluateComparison(true, Predicate.GREATER_OR_EQUAL,false));
        assertTrue(SenseExecutor.evaluateComparison(true, Predicate.GREATER,false));
        assertFalse(SenseExecutor.evaluateComparison(true, Predicate.EQUAL,false));

        assertTrue(SenseExecutor.evaluateComparison(true, Predicate.GREATER,-1));
        assertTrue(SenseExecutor.evaluateComparison(false, Predicate.GREATER,-1));

        assertTrue(SenseExecutor.evaluateComparison(12, Predicate.LOWER,22.0));
    }

}