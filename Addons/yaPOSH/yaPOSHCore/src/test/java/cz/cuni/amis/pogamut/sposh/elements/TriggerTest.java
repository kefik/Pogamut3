package cz.cuni.amis.pogamut.sposh.elements;

import cz.cuni.amis.pogamut.sposh.PoshTreeEvent;
import cz.cuni.amis.pogamut.sposh.elements.CompetenceTest.DummyListener;
import cz.cuni.amis.pogamut.sposh.exceptions.UnexpectedElementException;
import java.awt.datatransfer.DataFlavor;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author Honza
 */
public class TriggerTest {
/*    
    private Trigger instance;
    private List<Sense> senses;
    private int numSenses = 10;
    private String prefix = "sense-";
    private Random r = new Random(0xdead);

    @Before
    public void setUp() {
        senses = new ArrayList<Sense>();
        for (int i = 0; i < numSenses; ++i) {
            senses.add(LapElementsFactory.createSense(prefix + i));
        }
        instance = new Trigger(senses);
    }

    @Test
    public void testGetDataFlavor() {
        System.out.println("getDataFlavor");
        DataFlavor expResult = Trigger.dataFlavor;
        DataFlavor result = instance.getDataFlavor();
        assertEquals(expResult, result);
    }

    @Test
    public void testNeutralizeChild() {
        System.out.println("neutralizeChild");
        while (!instance.getChildDataNodes().isEmpty()) {
            int size = instance.getChildDataNodes().size();
            int pos = r.nextInt(size);
            DummyListener l = new DummyListener();
            PoshElement child = instance.getChildDataNodes().get(pos);
            child.addElementListener(l);
            instance.neutralizeChild(child);
            
            assertTrue(l.lastChild == child & l.lastEvent == PoshTreeEvent.NODE_DELETED);
            assertTrue(instance.getChildDataNodes().size() ==  size-1);
            assertFalse(instance.getChildDataNodes().contains(child));
        }
    }

    @Test
    public void testAddSense() {
        System.out.println("addSense");

        Sense child = LapElementsFactory.createSense("new-sense");
        DummyListener l = new DummyListener();
        instance.addElementListener(l);
        instance.addSense(child);

        assertTrue(l.lastChild == child && l.lastEvent == PoshTreeEvent.NEW_CHILD_NODE);
    }
    */

    private DriveCollection owner;
    private DummyListener listener;
    private Trigger<?> trigger;
    private String[] senseNames;

    @Before
    public void setUp() {
        owner = LapElementsFactory.createDriveCollection();
        listener = new DummyListener();
        owner.addElementListener(listener);
        trigger = owner.getGoal();
        assert trigger.isEmpty();

        senseNames = new String[]{
            "first",
            "second",
            "third",
            "fourth",
            "fifth"};
        
        for (String senseName : senseNames) {
            trigger.add(LapElementsFactory.createSense(senseName));
        }
    }

    private static void assertTriggerNames(Trigger<?> trigger, String[] senseNames, int...nameIndexes) {
        for (int senseIndex = 0; senseIndex < trigger.size(); ++senseIndex) {
            String expectedName = senseNames[nameIndexes[senseIndex]];
            String senseName = trigger.get(senseIndex).getName();
            assertEquals(expectedName, senseName);
        }
    }
    
    @Test
    public void moveSenseForward() {
        Sense movedSense = trigger.get(1);
        int newSenseIndex = 3;
        trigger.moveSense(newSenseIndex, movedSense);
        
        assertTriggerNames(trigger, senseNames, 0, 2, 3, 1, 4, 5);
        assertEquals(movedSense, listener.lastMovedChild);
        assertEquals(newSenseIndex, listener.lastMovedNewPosition.intValue());
    }

    @Test
    public void moveSenseBack() {
        Sense movedSense = trigger.get(3);
        int newSenseIndex = 1;
        trigger.moveSense(newSenseIndex, movedSense);
        
        assertTriggerNames(trigger, senseNames, 0, 3, 1, 2, 4, 5);
        assertEquals(movedSense, listener.lastMovedChild);
        assertEquals(newSenseIndex, listener.lastMovedNewPosition.intValue());
    }
    
    @Test
    public void moveSenseNoIndexChange() {
        Sense movedSense = trigger.get(3);
        int newSenseIndex = 3;
        trigger.moveSense(newSenseIndex, movedSense);
        
        assertTriggerNames(trigger, senseNames, 0, 1, 2, 3, 4, 5);
        assertNull(listener.lastMovedChild);
        assertNull(listener.lastMovedNewPosition);
    }
    
}
