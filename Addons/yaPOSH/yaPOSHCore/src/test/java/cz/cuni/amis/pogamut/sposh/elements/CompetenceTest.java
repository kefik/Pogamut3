package cz.cuni.amis.pogamut.sposh.elements;

import cz.cuni.amis.pogamut.sposh.exceptions.DuplicateNameException;
import cz.cuni.amis.pogamut.sposh.exceptions.InvalidNameException;
import java.beans.PropertyChangeEvent;
import java.util.*;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 * Tests for {@link Competence}. Some are trivial/wrappers and thus skipped.
 *
 * @author HonzaH
 */
public class CompetenceTest {

    @Test
    public void testAddElement() throws DuplicateNameException {
        System.out.println("addElement");
        Competence instance = new Competence("test", new FormalParameters(), Collections.<CompetenceElement>emptyList());
        DummyListener listener = new DummyListener();
        instance.addElementListener(listener);

        CompetenceElement element = LapElementsFactory.createCompetenceElement("choice-1");
        instance.addElement(element);

        assertTrue(listener.lastAddedChild == element);
    }

    @Test(expected = DuplicateNameException.class)
    public void testAddElementDuplicateName() throws DuplicateNameException {
        System.out.println("addElement - duplicate name");
        Competence instance = new Competence("test", new FormalParameters(), Collections.<CompetenceElement>emptyList());
        String name = "choice";
        instance.addElement(LapElementsFactory.createCompetenceElement(name));
        instance.addElement(LapElementsFactory.createCompetenceElement(name));
    }

    /**
     * Could happen during drag&drop - what if dragged is not released from original.
     * @throws DuplicateNameException 
     */
    @Test(expected = AssertionError.class)
    public void testAddElementLeakingParent() throws DuplicateNameException {
        System.out.println("addElement - leaking parent");

        Competence instance = new Competence("test", new FormalParameters(), Collections.<CompetenceElement>emptyList());
        instance.addElement(LapElementsFactory.createCompetenceElement("test-element"));
        
        Competence leak = new Competence("leak", new FormalParameters(), Collections.<CompetenceElement>emptyList());
        CompetenceElement leakElement = LapElementsFactory.createCompetenceElement("leak-element");
        leak.addElement(leakElement);
        
        instance.addElement(leakElement);
    }

    @Test
    public void testSetName() throws Exception {
        System.out.println("setName");
        String orgName = "test-set-name";
        String newName = "new-name";

        Competence instance = new Competence(orgName, new FormalParameters(), Collections.<CompetenceElement>emptyList());;
        DummyListener listener = new DummyListener();
        instance.addElementListener(listener);

        instance.setName(newName);

        PropertyChangeEvent evt = listener.lastPropertyChange;
        assertTrue(evt.getPropertyName().equals(Competence.cnName) && evt.getOldValue().equals(orgName) && evt.getNewValue().equals(newName));
        assertEquals(instance.getName(), newName);
    }

    @Test(expected = InvalidNameException.class)
    public void testSetNameInvalidName() throws Exception {
        System.out.println("setName - invalid name");
        String orgName = "test-set-name";
        String newName = "new name";

        Competence instance = new Competence(orgName, new FormalParameters(), Collections.<CompetenceElement>emptyList());;
        DummyListener listener = new DummyListener();
        instance.addElementListener(listener);

        try {
            instance.setName(newName);
        } catch (InvalidNameException ex) {
            assertNull(listener.lastPropertyChange);
            assertEquals(instance.getName(), orgName);
            
            throw ex;
        }
        fail("Name shouldn't be valid");
    }

    public static class DummyListener implements PoshElementListener {

        public PoshElement lastAddedChild;
        public PoshElement lastMovedChild;
        public Integer lastMovedNewPosition;
        public PoshElement lastRemovedChild;
        public PropertyChangeEvent lastPropertyChange;

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            lastPropertyChange = evt;
        }

        @Override
        public void childElementAdded(PoshElement parent, PoshElement child) {
            lastAddedChild = child;
        }

        @Override
        public void childElementMoved(PoshElement parent, PoshElement child, int oldIndex, int newIndex) {
            lastMovedChild = child;
            lastMovedNewPosition = newIndex;
        }

        @Override
        public void childElementRemoved(PoshElement parent, PoshElement child, int removedChildIndex) {
            lastRemovedChild = child;
        }

    }
    List<CompetenceElement> elements;
    List<DummyListener> listeners;
    int num = 10;
    int reps = 100;

    @Before
    public void setUp() {
        elements = new ArrayList<CompetenceElement>();
        listeners = new ArrayList<DummyListener>();

        for (int i = 0; i < num; ++i) {
            CompetenceElement element = LapElementsFactory.createCompetenceElement("e" + i, Arrays.asList(new Sense("t" + i)), "a" + i);
            DummyListener listener = new DummyListener();
            listeners.add(listener);
            element.addElementListener(listener);
            elements.add(element);
        }
    }

    @Test
    public void testRemoveElement() throws Exception {
        System.out.println("removeElement");
        Competence instance = new Competence("testC", new FormalParameters(), elements);

        IElementMethod<Competence, CompetenceElement> removeMethod = new IElementMethod<Competence, CompetenceElement>() {

            @Override
            public void method(Competence element, CompetenceElement child) {
                element.removeElement(child);
            }
        };
        
        RemoveTest<Competence, CompetenceElement> test = new RemoveTest<Competence, CompetenceElement>(instance, elements, removeMethod, 1);
        test.runTest();
    }
}
