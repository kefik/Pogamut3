package cz.cuni.amis.pogamut.sposh.elements;

import java.util.List;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author Honza
 */
public class CompetenceElementTest {

    private final String name = "testCE";
    private CompetenceElement instance;

    @Before
    public void setUp() {
        instance = LapElementsFactory.createCompetenceElement(name);

    }

    @After
    public void tearDown() {
    }

    @Test
    public void testToString() {
        System.out.println("toString");
        String expResult = "(" + name + " " + instance.getAction().toString() + ")";
        String result = instance.toString();
        assertEquals(expResult, result);
    }

    @Test
    public void testSetName() throws Exception {
        System.out.println("get/setName");
        String namePrefix = "repeat.ad.nauseum-";
        String oldName = name;
        for (int i = 0; i < 1000; ++i) {
            CompetenceTest.DummyListener l = new CompetenceTest.DummyListener();
            instance.addElementListener(l);
            instance.setName(namePrefix + i);
            String expResult = namePrefix + i;
            String newName = instance.getName();
            assertEquals(expResult, newName);
            assertEquals(CompetenceElement.ceName, l.lastPropertyChange.getPropertyName());
            assertEquals(oldName, l.lastPropertyChange.getOldValue());
            assertEquals(expResult, l.lastPropertyChange.getNewValue());
            instance.removeElementListener(l);
            oldName = instance.getName();
        }
    }

    @Test
    public void testGetRetries() {
        System.out.println("set/getRetries");
        instance.setRetries(CompetenceElement.INFINITE_RETRIES);
        int old = instance.getRetries();
        for (int i = 0; i < 1000; ++i) {
            CompetenceTest.DummyListener l = new CompetenceTest.DummyListener();
            instance.addElementListener(l);
            instance.setRetries(i);
            int expResult = i;
            int result = instance.getRetries();
            assertEquals(expResult, result);
            assertEquals(CompetenceElement.ceRetries, l.lastPropertyChange.getPropertyName());
            assertEquals(old, l.lastPropertyChange.getOldValue());
            assertEquals(expResult, l.lastPropertyChange.getNewValue());
            instance.removeElementListener(l);
            old = instance.getRetries();
        }
    }

    @Test
    public void testGetComment() {
        System.out.println("set/getComment");
        String commentSuffix = "] This world is made of love and peace!";
        String oldComment = instance.getComment();
        for (int i = 0; i < 1000; ++i) {
            CompetenceTest.DummyListener l = new CompetenceTest.DummyListener();
            instance.addElementListener(l);
            instance.setComment(i + commentSuffix);
            String expResult = i + commentSuffix;
            String newComment = instance.getComment();
            assertEquals(expResult, newComment);
            assertEquals(CompetenceElement.ceComment, l.lastPropertyChange.getPropertyName());
            assertEquals(oldComment, l.lastPropertyChange.getOldValue());
            assertEquals(expResult, l.lastPropertyChange.getNewValue());
            instance.removeElementListener(l);
            oldComment = instance.getComment();
        }
    }
}
