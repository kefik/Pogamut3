package cz.cuni.amis.pogamut.sposh.elements;

import cz.cuni.amis.pogamut.sposh.exceptions.CycleException;
import cz.cuni.amis.pogamut.sposh.exceptions.DuplicateNameException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author HonzaH
 */
public class PoshPlanTest {

    private String name = "life";
    private PoshPlan instance;
    private int numAPs = 10;
    private String apPrefix = "ap-";
    private List<ActionPattern> aps;
    private int numCs = 12;
    private String cPrefix = "c-";
    private List<Competence> cs;

    @Before
    public void setUp() {
        instance = LapElementsFactory.createPlan(name);
    }

    @After
    public void tearDown() {
        aps = null;
        cs = null;
        instance = null;
    }

    private void createAPs() {
        aps = new ArrayList<ActionPattern>(numAPs);
        for (int i = 0; i < numAPs; ++i) {
            List<TriggeredAction> actions = Arrays.asList(LapElementsFactory.createAction(apPrefix + i + "action"));
            ActionPattern ap = new ActionPattern(apPrefix + i, new FormalParameters(), actions, "");
            aps.add(ap);
        }
    }

    private void fillAPs() throws DuplicateNameException, CycleException {
        if (aps == null) {
            createAPs();
        }

        for (ActionPattern ap : aps) {
            instance.addActionPattern(ap);
        }
    }

    private void createCs() throws DuplicateNameException {
        cs = new ArrayList<Competence>(numCs);
        for (int i = 0; i < numCs; ++i) {
            Competence c = LapElementsFactory.createCompetence(cPrefix + i, cPrefix + i + "action");
            cs.add(c);
        }
    }

    private void fillCs() throws DuplicateNameException, CycleException {
        if (cs == null) {
            createCs();
        }
        for (Competence c : cs) {
            instance.addCompetence(c);
        }
    }

    @Test
    @Ignore
    public void testIsC() {
        System.out.println("isC");
        String name = "";
        PoshPlan instance = null;
        boolean expResult = false;
        boolean result = instance.isC(name);
        assertEquals(expResult, result);
        fail("The test case is a prototype.");
    }

    @Test
    @Ignore
    public void testGetC() {
        System.out.println("getC");
        String name = "";
        PoshPlan instance = null;
        Competence expResult = null;
        Competence result = instance.getC(name);
        assertEquals(expResult, result);
        fail("The test case is a prototype.");
    }

    @Test
    @Ignore
    public void testIsAP() {
        System.out.println("isAP");
        String name = "";
        PoshPlan instance = null;
        boolean expResult = false;
        boolean result = instance.isAP(name);
        assertEquals(expResult, result);
        fail("The test case is a prototype.");
    }

    @Test
    @Ignore
    public void testGetAP() {
        System.out.println("getAP");
        String name = "";
        PoshPlan instance = null;
        ActionPattern expResult = null;
        ActionPattern result = instance.getAP(name);
        assertEquals(expResult, result);
        fail("The test case is a prototype.");
    }

    @Test
    @Ignore
    public void testIsUniqueAPorComp() {
        System.out.println("isUniqueAPorComp");
        String testedName = "";
        PoshPlan instance = null;
        boolean expResult = false;
        boolean result = instance.isUniqueNodeName(testedName);
        assertEquals(expResult, result);
        fail("The test case is a prototype.");
    }

    @Test
    public void testAddCompetence() throws Exception {
        System.out.println("addCompetence");
        fillAPs();
        createCs();
        IElementMethod<PoshPlan, Competence> method = new IElementMethod<PoshPlan, Competence>() {

            @Override
            public void method(PoshPlan element, Competence child) throws DuplicateNameException, CycleException {
                element.addCompetence(child);
            }
        };
        AddTest<PoshPlan, Competence> test = new AddTest<PoshPlan, Competence>(instance, cs, method, numAPs + 1, numAPs + numCs + 1);
        test.runTest();
    }

    @Test
    public void testSetName() throws Exception {
        System.out.println("setName");
        IPropertyMethod<PoshPlan, String> propertyMethod = new IPropertyMethod<PoshPlan, String>() {

            @Override
            public String get(PoshPlan element) throws Exception {
                return element.getName();
            }

            @Override
            public void set(PoshPlan element, String value) throws Exception {
                element.setName(value);
            }
        };
        String[] testValues = new String[]{"Hello", "World"};
        new PropertyTest<PoshPlan, String>(PoshPlan.PROP_NAME, instance, propertyMethod, testValues).runTest();
    }

    @Test
    public void testSetAuthor() throws Exception {
        System.out.println("setAuthor");
        IPropertyMethod<PoshPlan, String> propertyMethod = new IPropertyMethod<PoshPlan, String>() {

            @Override
            public String get(PoshPlan element) throws Exception {
                return element.getAuthor();
            }

            @Override
            public void set(PoshPlan element, String value) throws Exception {
                element.setAuthor(value);
            }
        };
        String[] testValues = new String[]{"Joseph Heller", "Charler Babbage", "Ada Lovelace"};
        new PropertyTest<PoshPlan, String>(PoshPlan.PROP_AUTHOR, instance, propertyMethod, testValues).runTest();
    }

    @Test
    public void testSetInfo() throws Exception {
        System.out.println("setInfo");
        IPropertyMethod<PoshPlan, String> propertyMethod = new IPropertyMethod<PoshPlan, String>() {

            @Override
            public String get(PoshPlan element) throws Exception {
                return element.getInfo();
            }

            @Override
            public void set(PoshPlan element, String value) throws Exception {
                element.setInfo(value);
            }
        };
        String[] testValues = new String[]{"Overall failure", "The weak will perish"};
        new PropertyTest<PoshPlan, String>(PoshPlan.PROP_INFO, instance, propertyMethod, testValues).runTest();
    }

    @Test
    public void testAddActionPattern() throws Exception {
        System.out.println("addActionPattern");

        fillCs();
        createAPs();
        IElementMethod<PoshPlan, ActionPattern> addMethod = new IElementMethod<PoshPlan, ActionPattern>() {

            @Override
            public void method(PoshPlan plan, ActionPattern ap) throws Exception {
                plan.addActionPattern(ap);
            }
        };
        AddTest<PoshPlan, ActionPattern> test = new AddTest<PoshPlan, ActionPattern>(instance, aps, addMethod, numCs + 1, numCs + numAPs + 1);// extra element is DC
        test.runTest();
    }

    @Test
    @Ignore
    public void testIsCycled() {
        System.out.println("isCycled");
        boolean expResult = false;
        boolean result = instance.isCycled();
        assertEquals(expResult, result);
        fail("The test case is a prototype.");
    }

    @Test
    @Ignore
    public void testToString() {
        System.out.println("toString");
        String expResult = "";
        String result = instance.toString();
        assertEquals(expResult, result);
        fail("The test case is a prototype.");
    }

    @Test
    public void testGetChildDataNodes() throws DuplicateNameException, CycleException {
        System.out.println("getChildDataNodes");
        fillAPs();
        fillCs();
        List result = instance.getChildDataNodes();
        assertEquals(aps.size() + cs.size() + 1, result.size());
        for (ActionPattern ap : aps) {
            assertTrue(result.contains(ap));
        }
        for (Competence c : cs) {
            assertTrue(result.contains(c));
        }
        assertTrue(result.contains(instance.getDriveCollection()));
    }

    @Test
    @Ignore
    public void testMoveChild() {
        System.out.println("moveChild");
        PoshElement child = null;
        int newIndex = 0;
        boolean expResult = false;
        boolean result = instance.moveChild(newIndex, child);
        assertEquals(expResult, result);
        fail("The test case is a prototype.");
    }

    @Test
    public void testRemoveCompetence() throws Exception {
        System.out.println("removeCompetence");
        fillCs();
        IElementMethod<PoshPlan, Competence> removeMethod = new IElementMethod<PoshPlan, Competence>() {

            @Override
            public void method(PoshPlan element, Competence child) {
                element.removeCompetence(child);
            }
        };
        RemoveTest<PoshPlan, Competence> test = new RemoveTest<PoshPlan, Competence>(instance, cs, removeMethod, 1, 1); // extra child is DC
        test.runTest();
    }

    @Test
    public void testRemoveActionPattern() throws Exception {
        System.out.println("removeActionPattern");
        fillAPs();
        IElementMethod<PoshPlan, ActionPattern> removeMethod = new IElementMethod<PoshPlan, ActionPattern>() {

            @Override
            public void method(PoshPlan element, ActionPattern child) {
                element.removeActionPattern(child);
            }
        };
        RemoveTest<PoshPlan, ActionPattern> test = new RemoveTest<PoshPlan, ActionPattern>(instance, aps, removeMethod, 1, 1); // extra is DC
        test.runTest();
    }

    @Test
    @Ignore
    public void testSynchronize() throws Exception {
        System.out.println("synchronize");
        PoshPlan other = null;
        instance.synchronize(other);
        fail("The test case is a prototype.");
    }
    
    @Test
    @Ignore
    public void testGetActionsNames() throws Exception {
        System.out.println("getActionsNames");
        fail("The test case is a prototype.");
    }

    @Test
    @Ignore
    public void testGetSensesNames() throws Exception {
        System.out.println("getSensesNames");
        fail("The test case is a prototype.");
    }
}
