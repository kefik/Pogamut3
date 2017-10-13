package cz.cuni.amis.pogamut.sposh.elements;

import cz.cuni.amis.pogamut.sposh.elements.CompetenceTest.DummyListener;
import cz.cuni.amis.pogamut.sposh.exceptions.CycleException;
import cz.cuni.amis.pogamut.sposh.exceptions.DuplicateNameException;
import cz.cuni.amis.pogamut.sposh.exceptions.InvalidNameException;
import java.util.*;
import org.junit.*;
import static org.junit.Assert.*;

/**
 * Interface encapsulating method used for testing add/remove methods of an
 * element.
 *
 * @author HonzaH
 * @param <ELEMENT> element that has children
 * @param <CHILD> children that will be added/removed from ELEMENT .
 */
interface IElementMethod<ELEMENT extends PoshElement, CHILD extends PoshElement> {

    /**
     * Invoke method of element on child.
     */
    abstract void method(ELEMENT element, CHILD child) throws Exception;
}

/**
 * Interface encapsulating setter/getter methods of an element. Used to test
 * wheather property methods of element are working OK(value is changed and
 * listeners are notified).
 *
 * @author HonzaH
 * @param <ELEMENT> element whose property will be tested.
 * @param <T> Type of value of property.
 */
interface IPropertyMethod<ELEMENT extends PoshElement, T> {

    /**
     * Invoke getter method of element.
     */
    abstract T get(ELEMENT element) throws Exception;

    /**
     * Invoke setter method of element using the value as argument.
     */
    abstract void set(ELEMENT element, T value) throws Exception;
}

/**
 * Test some property of {@link PoshElement}.
 *
 * @author HonzaH
 * @param <ELEMENT> element that will be tested.
 * @param <T> Type of property value
 */
class PropertyTest<ELEMENT extends PoshElement, T> {

    private final String name;
    private final ELEMENT instance;
    private final IPropertyMethod<ELEMENT, T> method;
    private final T[] values;

    PropertyTest(String propertyName, ELEMENT instance, IPropertyMethod<ELEMENT, T> method, T... values) {
        this.name = propertyName;
        this.instance = instance;
        this.method = method;
        this.values = values;
    }

    public void runTest() throws Exception {
        T oldValue = method.get(instance);
        for (T value : values) {
            assertEquals(oldValue, method.get(instance));

            DummyListener instanceListener = new DummyListener();
            instance.addElementListener(instanceListener);

            method.set(instance, value);

            assertEquals(value, method.get(instance));
            assertNotNull(instanceListener.lastPropertyChange);
            assertEquals(oldValue, instanceListener.lastPropertyChange.getOldValue());
            assertEquals(value, instanceListener.lastPropertyChange.getNewValue());
            assertEquals(name, instanceListener.lastPropertyChange.getPropertyName());

            instance.removeElementListener(instanceListener);
            oldValue = value;
        }
        assertEquals(oldValue, method.get(instance));
    }
}

/**
 * Class for automatic testing of add method of the ELEMENT. test if listeners
 * are notified, if new child is truly added...
 *
 * @author HonzaH
 * @param <ELEMENT>
 * @param <CHILD>
 */
class AddTest<ELEMENT extends PoshElement, CHILD extends PoshElement> {

    private final ELEMENT instance;
    private final List<CHILD> elements;
    private final IElementMethod<ELEMENT, CHILD> method;
    private final int having;
    private final int end;

    public AddTest(ELEMENT element, List<CHILD> elements, IElementMethod<ELEMENT, CHILD> method, int having, int end) {
        this.instance = element;
        this.elements = new ArrayList<CHILD>(elements);
        this.method = method;
        this.having = having;
        this.end = end;
    }

    public void runTest() throws Exception {
        for (int i = 0; i < elements.size(); ++i) {
            DummyListener instanceListener = new DummyListener();
            instance.addElementListener(instanceListener);

            CHILD element = elements.get(i);
            DummyListener elementListener = new DummyListener();
            element.addElementListener(elementListener);

            assertEquals(i + having, instance.getChildDataNodes().size());
            assertFalse(instance.getChildDataNodes().contains(element));
            assertEquals(null, element.getParent());

            method.method(instance, element);

            assertEquals(i + 1 + having, instance.getChildDataNodes().size());
            assertTrue(instance.getChildDataNodes().contains(element));
            assertEquals(instance, element.getParent());

            assertEquals(null, elementListener.lastRemovedChild);
            assertEquals(null, elementListener.lastAddedChild);
            assertEquals(null, elementListener.lastMovedChild);
            element.removeElementListener(elementListener);

            assertEquals(element, instanceListener.lastAddedChild);
            instance.removeElementListener(instanceListener);
        }
        assertEquals(end, instance.getChildDataNodes().size());
    }
}

/**
 * Class for automatic testing of remove methods of the ELEMENT. Checks if
 * element is removed, if listeners are notified...
 *
 * @author HonzaH
 * @param <ELEMENT> {@link PoshElement} whose remove methods will be tested
 * @param <CHILD> children that wioll be removed.
 */
class RemoveTest<ELEMENT extends PoshElement, CHILD extends PoshElement> {

    private final ELEMENT instance;
    private final List<CHILD> elements;
    private final IElementMethod<ELEMENT, CHILD> method;
    private final int keep;
    private final int extra;

    /**
     * Test removal method of the element. Gradually take away elements and
     * check that they have indeed been removed and that listeners has been
     * notified.
     *
     * @param element Element that already contains all elements
     * @param elements elements that will be removed from element
     * @param method method to actually method child element from the element
     * @param keep how many children should element have after all elements are
     * taken away.
     */
    RemoveTest(ELEMENT element, List<CHILD> elements, IElementMethod<ELEMENT, CHILD> method, int keep) {
        this(element, elements, method, keep, 0);
    }

    /**
     * Same as above
     *
     * @param extra how many other elements are among children of element
     * except all elements.
     */
    RemoveTest(ELEMENT element, List<CHILD> elements, IElementMethod<ELEMENT, CHILD> method, int keep, int extra) {
        this.instance = element;
        this.elements = new ArrayList<CHILD>(elements);
        this.method = method;
        this.keep = keep;
        this.extra = extra;
    }

    public void runTest() throws Exception {
        for (int i = 0; i < elements.size(); ++i) {
            DummyListener instanceListener = new DummyListener();
            instance.addElementListener(instanceListener);

            CHILD element = elements.get(i);
            DummyListener elementListener = new DummyListener();
            element.addElementListener(elementListener);

            assertEquals(instance.getChildDataNodes().size() - extra, elements.size() - i);
            assertTrue(instance.getChildDataNodes().contains(element));
            assertEquals(instance, element.getParent());

            method.method(instance, element);

            assertEquals(null, element.getParent());
            assertFalse(instance.getChildDataNodes().contains(element));
            // Listeners of child weren't notified 
            assertEquals(null, elementListener.lastRemovedChild);
            assertEquals(null, elementListener.lastAddedChild);
            assertEquals(null, elementListener.lastMovedChild);
            element.removeElementListener(elementListener);

            // the parent of removed child was notified
            assertEquals(element, instanceListener.lastRemovedChild);

            instance.removeElementListener(instanceListener);
        }
        assertEquals(keep, instance.getChildDataNodes().size());
    }
}

/**
 *
 * @author HonzaH
 */
public class ActionPatternTest {

    private List<TriggeredAction> actions;
    private ActionPattern instance;
    private int num = 10;
    private String prefix = "action-";
    private String apName = "testAP";

    private void init() throws CycleException {
        actions = new LinkedList<TriggeredAction>();
        for (int i = 0; i < num; ++i) {
            actions.add(LapElementsFactory.createAction(prefix + i));
        }
        instance = LapElementsFactory.createActionPattern(apName, new FormalParameters(), actions);
    }

    @Before
    public void setUp() throws CycleException {
        init();
    }

    @Test
    public void testAddAction() throws Exception {
        System.out.println("addAction");
        TriggeredAction action = LapElementsFactory.createAction("testAction");
        instance.addAction(action);
        assertTrue(instance.getActions().contains(action));
        assertTrue(instance.getActions().size() == num + 1);

        for (int i = 0; i < num; ++i) {
            TriggeredAction sameNameAction = LapElementsFactory.createAction(prefix + i);
            assertFalse(instance.getActions().contains(sameNameAction));
            instance.addAction(sameNameAction);
            assertTrue(instance.getActions().contains(sameNameAction));
            assertTrue(instance.getActions().size() == num + 2 + i);
        }
    }

    @Test
    public void testToString() {
        System.out.println("toString");
        instance = LapElementsFactory.createActionPattern(apName, new FormalParameters(), Arrays.asList(
                LapElementsFactory.createAction("love"),
                LapElementsFactory.createAction("peace"),
                LapElementsFactory.createAction("empathy")));
        String expResult = "(AP testAP (love peace empathy))";
        String result = instance.toString().replaceAll("\\s+", " ").trim();
        assertEquals(expResult, result);
    }

    @Test
    public void testSetName() throws Exception {
        System.out.println("setName");
        String name = "testSetName";
        String oldName = instance.getName();
        CompetenceTest.DummyListener l = new CompetenceTest.DummyListener();
        instance.addElementListener(l);

        instance.setName(name);

        assertEquals(name, instance.getName());
        assertEquals(ActionPattern.apName, l.lastPropertyChange.getPropertyName());
        assertEquals(oldName, l.lastPropertyChange.getOldValue());
        assertEquals(name, l.lastPropertyChange.getNewValue());
    }

    @Test(expected = InvalidNameException.class)
    public void testSetNameInvalid() throws Exception {
        System.out.println("setName - invalid");
        String name = "123zlo";
        instance.setName(name);
    }

    @Test(expected = DuplicateNameException.class)
    public void testSetNameDuplicateAP() throws Exception {
        System.out.println("setName - duplicate AP");
        PoshPlan plan = LapElementsFactory.createPlan();
        plan.addActionPattern(instance);
        ActionPattern o = LapElementsFactory.createActionPattern("otherAP", new FormalParameters());
        plan.addActionPattern(o);
        instance.setName(o.getName());
    }

    @Test(expected = DuplicateNameException.class)
    public void testSetNameDuplicateC() throws Exception {
        System.out.println("setName - duplicate C");
        PoshPlan plan = LapElementsFactory.createPlan();
        plan.addActionPattern(instance);
        Competence o = new Competence("otherAP", new FormalParameters(), Collections.<CompetenceElement>emptyList());
        plan.addCompetence(o);
        instance.setName(o.getName());
    }

    @Test
    public void testRemoveAction() throws Exception {
        System.out.println("removeAction");
        IElementMethod<ActionPattern, TriggeredAction> action = new IElementMethod<ActionPattern, TriggeredAction>() {

            @Override
            public void method(ActionPattern element, TriggeredAction child) {
                element.removeAction(child);
            }
        };

        RemoveTest<ActionPattern, TriggeredAction> test = new RemoveTest<ActionPattern, TriggeredAction>(instance, actions, action, 1);
        test.runTest();
    }

    @Test(expected = AssertionError.class)
    public void testNeutralizeMissingChild() {
        System.out.println("neutralizeChild - missing");
        instance.removeAction(LapElementsFactory.createAction("MissingAction"));
    }

    @Test
    public void testGetActions() {
        System.out.println("getActions");
        List<? extends PoshElement> result = instance.getChildDataNodes();
        assertEquals(actions.size(), result.size());

        for (int i = 0; i < actions.size(); ++i) {
            assertEquals(actions.get(i), result.get(i));
        }
    }
}
