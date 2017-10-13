package cz.cuni.amis.pogamut.sposh.elements;

import cz.cuni.amis.pogamut.sposh.PoshTreeEvent;
import java.awt.datatransfer.DataFlavor;
import java.util.*;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author Honza
 */
public class PoshDummyElementTest {

    private final String elementPrefix = "sense";
    int numTestElements = 10;
    int numTest = 100;
    List<Sense> elements;
    PoshDummyElementImpl<Sense> instance;

    @Before
    public void setUp() {
        elements = new LinkedList<Sense>();
        for (int i = 0; i < numTestElements; ++i) {
            elements.add(new Sense(elementPrefix + i));
        }
        instance = new PoshDummyElementImpl("test-posh-dummy-element", elements);
    }

    @After
    public void tearDown() {
    }

    /**
     * Test pattern used as check of names in lap plan.
     * ([a-zA-Z][_\-a-zA-Z0-9]*\.)*[a-zA-Z][_\-a-zA-Z0-9]*
     */
    @Test
    public void testPattern() {
        String pattern = PoshDummyElement.IDENT_PATTERN;
        String[] ok = new String[]{
            "succeed",
            "cz.cuni.pogamut.Fail",
            "prime3",
            "A-B.Nya12",
            "www.kernel.com",
            "welcome-home.Kill_All_Humans.Was-there-2"
        };
        String[] crash = new String[]{
            " succeed",
            "succ eed",
            "123ahoj",
            "Nya.123.ahoj",
            "Baka(Evil)",
            "Hello*World",
            "Durable/Goods",
            "red@envelope",
            ".hu",
            "one..two"
        };
        for (String test : ok) {
            assertTrue(test, test.matches(pattern));
        }
        for (String test : crash) {
            assertFalse(test, test.matches(pattern));
        }
    }
/*
    @Test(expected = NoSuchElementException.class)
    public void testMoveNodeWrongNode() {
        System.out.println("moveNodeInList - wrong node");
        instance.moveNodeInList(elements, new Sense(PoshDummyElement.getUnusedName(elementPrefix, instance.getChildDataNodes())), 1);
    }

    @Test
    public void testMoveChildPos() {
        System.out.println("moveChild - position");

        for (int pos = 0; pos < elements.size(); ++pos) {
            PoshElement element = instance.getChildDataNodes().get(pos);

            for (int j = -20; j < 0; ++j) {
                int relPos = -pos + j;
                assertFalse("Relative pos " + relPos + " from " + elements.indexOf(element) + " 0.." + (elements.size() - 1),
                        instance.moveNodeInList(elements, element, relPos));
            }
            for (int j = 0; j < elements.size(); ++j) {
                int relPos = -pos + j;
                assertTrue("Relative pos " + relPos + " from " + elements.indexOf(element) + " 0.." + (elements.size() - 1),
                        instance.moveNodeInList(elements, element, relPos));
                int newPos = pos + relPos;
                assertTrue(elements.get(newPos) == element);
                if (newPos != pos) {
                    assertTrue(elements.get(pos) != element);
                }
                // revert so I can loop and have same state at the beginning of the loop
                instance.moveNodeInList(elements, element, -relPos);
            }
            for (int j = elements.size(); j < elements.size() + 20; ++j) {
                int relPos = -pos + j;
                assertFalse("Relative pos " + relPos + " from " + elements.indexOf(element) + " 0.." + (elements.size() - 1),
                        instance.moveNodeInList(elements, element, relPos));
            }
        }

    }

    @Test
    public void testMoveChildListener() {
        System.out.println("moveChild - positive bust position");

        for (int i = 0; i < numTest; ++i) {
            CompetenceTest.DummyListener instanceListener = new CompetenceTest.DummyListener();
            instance.addElementListener(instanceListener);

            int startPos = new Random(0xbeef).nextInt(instance.getChildDataNodes().size());
            int endPos = new Random(0xbeef).nextInt(instance.getChildDataNodes().size());

            PoshElement element = instance.getChildDataNodes().get(startPos);
            instance.moveNodeInList(elements, element, endPos - startPos);

            assertEquals(element, instanceListener.lastMovedChild);
            assertEquals(element, elements.get(endPos));
            instance.removeElementListener(instanceListener);
        }
    }
*/
    @Test
    public void testIsUsedName() {
        System.out.println("isUsedName");

        for (int i = 0; i < numTestElements; ++i) {
            assertTrue(PoshDummyElement.isUsedName(elementPrefix + i, elements));
            assertFalse(PoshDummyElement.isUsedName(elementPrefix + (i + numTestElements), elements));
        }
        assertFalse(PoshDummyElement.isUsedName("World-is-doomed", elements));
        assertFalse(PoshDummyElement.isUsedName("walk-ahead", elements));
    }

    @Test
    public void testGetUnusedName() {
        System.out.println("getUnusedName");

        System.out.print("Unused names:");
        for (int i = 0; i < numTest; ++i) {
            String unusedName = PoshDummyElement.getUnusedName(elementPrefix, elements);
            System.out.print(" " + unusedName);
            assertFalse(PoshDummyElement.isUsedName(unusedName, elements));
            elements.add(new Sense(unusedName));
        }
        System.out.println();
    }

    public static class PoshDummyElementImpl<T extends PoshElement> extends PoshDummyElement {

        private final String name;
        private final List<T> elements;

        PoshDummyElementImpl(String name, List<T> elements) {
            this.name = name;
            this.elements = Collections.unmodifiableList(elements);
        }

        @Override
        public DataFlavor getDataFlavor() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public List<T> getChildDataNodes() {
            return elements;
        }

        public String getName() {
            return name;
        }

        @Override
        public boolean moveChild(int newIndex, PoshElement child) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public LapType getType() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

    }
}
