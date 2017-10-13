package cz.cuni.amis.pogamut.sposh.engine;

import cz.cuni.amis.pogamut.sposh.elements.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Iterator;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import cz.cuni.amis.pogamut.sposh.engine.PoshEngine.EvaluationResult;
import cz.cuni.amis.pogamut.sposh.engine.PoshEngine.EvaluationResultInfo;
import cz.cuni.amis.pogamut.sposh.engine.timer.DebugTimer;
import cz.cuni.amis.pogamut.sposh.executor.ActionResult;
import cz.cuni.amis.pogamut.sposh.executor.IAction;
import cz.cuni.amis.pogamut.sposh.executor.ISense;
import cz.cuni.amis.pogamut.sposh.executor.IWorkExecutor;
import cz.cuni.amis.pogamut.sposh.executor.StateWorkExecutor;
import java.util.*;
import org.junit.Ignore;

/**
 * Tests covering {@link PoshEngine} and its execution.
 *
 * @author Honza
 */
public class PoshEngineTest extends PlanTest {

    private IWorkExecutor createWorkExecutor() {
        return new IWorkExecutor() {

            @Override
            public ActionResult executeAction(String actionName, VariableContext ctx) {
                return null;
            }

            @Override
            public Object executeSense(String senseName, VariableContext ctx) {
                return null;
            }
        };
    }

    @Test
    public void testGoalFulfilled() throws IOException, ParseException {
        System.out.println("\n === Test: " + getMethodName() + " ===");

        PoshPlan parsePlan = parsePlan("testplans/TestGoalFulfilled.lap");
        PoshEngine poshEngine = new PoshEngine(parsePlan);
        IWorkExecutor workExecuter = new TestWorkExecutor(
                new ITestPrimitive[]{
                    new PrintPrimitive("succeed", true),
                    new PrintPrimitive("fail", false)
                });

        EvaluationResult result = poshEngine.evaluatePlan(workExecuter).result;

        assertEquals(EvaluationResult.GOAL_SATISFIED, result);
    }

    /**
     * Test if primitive "dummyPrimitive" is evaluated and if has no variables
     * in passed context.
     *
     * @throws IOException
     * @throws ParseException
     */
    @Test
    public void testParameterlessPrimitiveEvaluation() throws IOException, ParseException {
        System.out.println("\n === Test: " + getMethodName() + " ===");

        PoshPlan parsePlan = parsePlan("testplans/TestPrimitiveEvaluation.lap");
        PoshEngine poshEngine = new PoshEngine(parsePlan);

        ITestPrimitive dummyPrimitive = new PrintPrimitive("dummyPrimitive", ActionResult.FAILED) {

            @Override
            public Object work(VariableContext ctx) {
                // test if context of primitive is empty as it should
                assertEquals(ctx.size(), 0);

                return super.work(ctx);
            }
        };
        IWorkExecutor workExecuter = new TestWorkExecutor(
                new ITestPrimitive[]{
                    new PrintPrimitive("succeed", true),
                    new PrintPrimitive("fail", false),
                    dummyPrimitive
                });

        assertEquals(poshEngine.evaluatePlan(workExecuter).result, EvaluationResult.ELEMENT_FIRED);
        if (dummyPrimitive.triggered() != 0) {
            fail("Primitive was triggered, shouldn't be, expected only to add primitive to stack.");
        }
        assertEquals(poshEngine.evaluatePlan(workExecuter).result, EvaluationResult.ELEMENT_FIRED);
        if (dummyPrimitive.triggered() != 1) {
            fail("Primitive wasn't triggered.");
        }
    }

    @Test
    public void testDEFrequencyFail() throws IOException, ParseException {
        System.out.println("\n === Test: " + getMethodName() + " ===");

        DebugTimer timer = new DebugTimer();
        PoshEngine poshEngine = new PoshEngine(
                parsePlan("testplans/TestDEFrequencyFail.lap"),
                timer);

        ITestPrimitive doNothingPrimitive = new PrintPrimitive("doNothing", ActionResult.FAILED);
        IWorkExecutor workExecuter = new TestWorkExecutor(
                new ITestPrimitive[]{
                    new PrintPrimitive("fail", false),
                    doNothingPrimitive
                });

        // First one is to create the root "doNothing" element on the call stack ...
        assertEquals(EvaluationResult.ELEMENT_FIRED, poshEngine.evaluatePlan(workExecuter).result);
        // .. and the second for actully firing it.
        assertEquals(EvaluationResult.ELEMENT_FIRED, poshEngine.evaluatePlan(workExecuter).result);
        for (int i = 0; i < 3; ++i) {
            assertEquals(EvaluationResult.NO_ELEMENT_FIRED, poshEngine.evaluatePlan(workExecuter).result);
            assertEquals(EvaluationResult.NO_ELEMENT_FIRED, poshEngine.evaluatePlan(workExecuter).result);
            timer.addTime(250);
            if (doNothingPrimitive.triggered() != 1) {
                fail("DE wasn't triggered just once");
            }
        }
        System.out.println("Adding 300 so it is over 1000ms");
        timer.addTime(300);
        assertEquals(EvaluationResult.ELEMENT_FIRED, poshEngine.evaluatePlan(workExecuter).result);
        assertEquals(EvaluationResult.ELEMENT_FIRED, poshEngine.evaluatePlan(workExecuter).result);

        if (doNothingPrimitive.triggered() != 2) {
            fail("DE wasn't triggered after freq limiter timeout");
        }
    }

    /**
     * Test what happens if goal wasn't specified in the DC. Expected: consider
     * missing goal as always failing.
     */
    @Test
    public void testNoGoal() throws IOException, ParseException {
        System.out.println("\n === Test: " + getMethodName() + " ===");

        PoshPlan parsePlan = parsePlan("testplans/TestNoGoal.lap");
        PoshEngine poshEngine = new PoshEngine(parsePlan);

        ITestPrimitive dummyPrimitive = new PrintPrimitive("dummyPrimitive", ActionResult.FAILED);
        IWorkExecutor workExecuter = new TestWorkExecutor(
                new ITestPrimitive[]{
                    dummyPrimitive
                });

        assertEquals(EvaluationResult.ELEMENT_FIRED, poshEngine.evaluatePlan(workExecuter).result);
        if (dummyPrimitive.triggered() != 0) {
            fail("Primitive was triggered, should only be added to stack.");
        }
        assertEquals(EvaluationResult.ELEMENT_FIRED, poshEngine.evaluatePlan(workExecuter).result);

        if (dummyPrimitive.triggered() != 1) {
            fail("Primitive wasn't triggered.");
        }
    }

    /**
     * Test if de will execute the ap
     */
    @Test
    public void testAPExecutor() throws IOException, ParseException {
        System.out.println("\n === Test: " + getMethodName() + " ===");

        PoshPlan parsePlan = parsePlan("testplans/TestAPExecutor.lap");
        PoshEngine poshEngine = new PoshEngine(parsePlan);

        ITestPrimitive[] actions = new ITestPrimitive[3];
        int[] res = new int[]{0, 0, 0};

        for (int i = 0; i < actions.length; ++i) {
            actions[i] = new PrintPrimitive("action" + i);
        }

        IWorkExecutor workExecuter = new TestWorkExecutor(actions);

        // this evaluation will add testAP to callStack
        assertEquals(EvaluationResult.ELEMENT_FIRED, poshEngine.evaluatePlan(workExecuter).result);
        checkPrimitives(actions, res);
        // Enter testAP
//        assertEquals(EvaluationResult.ELEMENT_FIRED, poshEngine.evaluatePlan(workExecuter));
        checkPrimitives(actions, res);

        for (int i = 0; i < actions.length; ++i) {
            System.out.println(" * Firing action " + actions[i].getName());
            // add action to call stack
            assertEquals(EvaluationResult.ELEMENT_FIRED, poshEngine.evaluatePlan(workExecuter).result);
            checkPrimitives(actions, res);

            // execute the action
            assertEquals(EvaluationResult.ELEMENT_FIRED, poshEngine.evaluatePlan(workExecuter).result);
            res[i] = 1;
            checkPrimitives(actions, res);
        }
    }

    private void checkPrimitives(ITestPrimitive[] primitives, int[] results) {
        for (int i = 0; i < primitives.length; ++i) {
            if (primitives[i].triggered() != results[i]) {
                throw new IllegalStateException("Primitive " + primitives[i].getName() + " wasn't triggered " + results[i] + " times, but " + primitives[i].triggered());
            }
        }
    }

    /**
     * Test if constants passed from posh plan to primitives are what they are
     * supposed to be.
     *
     * @throws IOException
     * @throws ParseException
     */
    @Test
    public void testPassedConstantsInPrimitive() throws IOException, ParseException {
        System.out.println("\n === Test: " + getMethodName() + " ===");

        PoshPlan parsePlan = parsePlan("testplans/TestPrimitiveVariables.lap");
        PoshEngine poshEngine = new PoshEngine(parsePlan);

        ITestPrimitive testVariable = new ITestPrimitive() {

            @Override
            public String getName() {
                return "testVariable";
            }

            @Override
            public Object work(VariableContext ctx) {
                if (!"Snowhite".equals(ctx.getValue("$name"))) {
                    throw new IllegalStateException("Variable $name is not snowhite, but " + ctx.getValue("$name"));
                }
                assertEquals(ctx.getValue("0"), 12.4);
                if (ctx.size() != 2) {
                    throw new IllegalStateException("More than two variables passed (\"" + ctx.size() + "\").");
                }
                return null;
            }

            @Override
            public int triggered() {
                return 0;
            }
        };
        IWorkExecutor workExecuter = new TestWorkExecutor(
                new ITestPrimitive[]{testVariable});

        assertEquals(EvaluationResult.ELEMENT_FIRED, poshEngine.evaluatePlan(workExecuter).result);
        assertEquals(EvaluationResult.ELEMENT_FIRED, poshEngine.evaluatePlan(workExecuter).result);
    }

    /**
     * Test if order of actions will be OK in nested APs
     */
    @Test
    public void testNestedAP() throws IOException, ParseException {
        System.out.println("\n === Test: " + getMethodName() + " ===");

        PoshPlan parsePlan = parsePlan("testplans/TestNestedAP.lap");
        PoshEngine poshEngine = new PoshEngine(parsePlan);

        int[] res = new int[]{0, 0, 0, 0};
        ITestPrimitive[] actions = new ITestPrimitive[4];
        for (int i = 0; i < actions.length; ++i) {
            actions[i] = new PrintPrimitive("action" + i, ActionResult.FINISHED);
        }

        IWorkExecutor workExecuter = new TestWorkExecutor(actions);

        // add outerAP to stack
        assertEquals(EvaluationResult.ELEMENT_FIRED, poshEngine.evaluatePlan(workExecuter).result);
        checkPrimitives(actions, res);
        // add action0 to stack
        assertEquals(EvaluationResult.ELEMENT_FIRED, poshEngine.evaluatePlan(workExecuter).result);
        checkPrimitives(actions, res);
        // fire action0
        assertEquals(EvaluationResult.ELEMENT_FIRED, poshEngine.evaluatePlan(workExecuter).result);
        res[0]++;
        checkPrimitives(actions, res);

        // add innerAP to stack
        assertEquals(EvaluationResult.ELEMENT_FIRED, poshEngine.evaluatePlan(workExecuter).result);
        checkPrimitives(actions, res);
        // add action1 to stack
        assertEquals(EvaluationResult.ELEMENT_FIRED, poshEngine.evaluatePlan(workExecuter).result);
        checkPrimitives(actions, res);
        // fire action1
        assertEquals(EvaluationResult.ELEMENT_FIRED, poshEngine.evaluatePlan(workExecuter).result);
        res[1]++;
        checkPrimitives(actions, res);

        // add action2 to stack
        assertEquals(EvaluationResult.ELEMENT_FIRED, poshEngine.evaluatePlan(workExecuter).result);
        checkPrimitives(actions, res);
        // fire action2
        assertEquals(EvaluationResult.ELEMENT_FIRED, poshEngine.evaluatePlan(workExecuter).result);
        res[2]++;
        checkPrimitives(actions, res);

        // return to outerAP, add action3 to stack
        assertEquals(EvaluationResult.ELEMENT_FIRED, poshEngine.evaluatePlan(workExecuter).result);
        checkPrimitives(actions, res);


        // add action3 to stack
        assertEquals(EvaluationResult.ELEMENT_FIRED, poshEngine.evaluatePlan(workExecuter).result);
        checkPrimitives(actions, res);
        // fire action3
        assertEquals(EvaluationResult.ELEMENT_FIRED, poshEngine.evaluatePlan(workExecuter).result);
        res[3]++;
        checkPrimitives(actions, res);
    }

    /**
     * Test what happens if action in inner ap of two nested APs fails. action2
     * will fail
     */
    @Test
    public void testNestedAPFailure() throws IOException, ParseException {
        System.out.println("\n === Test: " + getMethodName() + " ===");

        PoshPlan parsePlan = parsePlan("testplans/TestNestedAPFailure.lap");
        PoshEngine poshEngine = new PoshEngine(parsePlan);

        int[] res = new int[]{0, 0, 0, 0};
        ITestPrimitive[] actions = new ITestPrimitive[4];
        for (int i = 0; i < actions.length; ++i) {
            if (i == 2) {
                actions[i] = new PrintPrimitive("action" + i, ActionResult.FAILED);
            } else {
                actions[i] = new PrintPrimitive("action" + i, ActionResult.FINISHED);
            }
        }

        IWorkExecutor workExecuter = new TestWorkExecutor(actions);

        for (int loop = 0; loop < 4; loop++) {
            // add outerAP to stack
            assertEquals(EvaluationResult.ELEMENT_FIRED, poshEngine.evaluatePlan(workExecuter).result);
            checkPrimitives(actions, res);
            // add action0 to stack
            assertEquals(EvaluationResult.ELEMENT_FIRED, poshEngine.evaluatePlan(workExecuter).result);
            checkPrimitives(actions, res);
            // fire action0
            assertEquals(EvaluationResult.ELEMENT_FIRED, poshEngine.evaluatePlan(workExecuter).result);
            res[0]++;
            checkPrimitives(actions, res);

            // add innerAP to stack
            assertEquals(EvaluationResult.ELEMENT_FIRED, poshEngine.evaluatePlan(workExecuter).result);
            checkPrimitives(actions, res);
            // add action1 to stack
            assertEquals(EvaluationResult.ELEMENT_FIRED, poshEngine.evaluatePlan(workExecuter).result);
            checkPrimitives(actions, res);
            // fire action1
            assertEquals(EvaluationResult.ELEMENT_FIRED, poshEngine.evaluatePlan(workExecuter).result);
            res[1]++;
            checkPrimitives(actions, res);

            // add action2 to stack
            assertEquals(EvaluationResult.ELEMENT_FIRED, poshEngine.evaluatePlan(workExecuter).result);
            checkPrimitives(actions, res);
            // fire action2
            System.out.println("Action2 will fail, reset");
            assertEquals(EvaluationResult.ELEMENT_FIRED, poshEngine.evaluatePlan(workExecuter).result);
            res[2]++; // it failed, but it was still fired
            checkPrimitives(actions, res);

            // the stack of DE is now reseted
            System.out.println("Stack has been reseted.");
        }
    }

    @Test
    public void testSimpleC() throws IOException, ParseException {
        System.out.println("\n === Test: " + getMethodName() + " ===");

        PoshPlan parsePlan = parsePlan("testplans/TestSimpleC.lap");
        PoshEngine poshEngine = new PoshEngine(parsePlan);

        ITestPrimitive action = new PrintPrimitive("action");
        IWorkExecutor workExecuter = new TestWorkExecutor(
                new ITestPrimitive[]{action});
        
        String[] paths = new String[]{
            "/P:0/DC:0/DE:0",
            "/P:0/DC:0/DE:0", "/P:0/DC:0/DE:0/A:0/C:0",
            "/P:0/DC:0/DE:0", "/P:0/DC:0/DE:0/A:0/C:0/CE:0",
            "/P:0/DC:0/DE:0", "/P:0/DC:0/DE:0/A:0/C:0/CE:0/A:0",
            "/P:0/DC:0/DE:0", "/P:0/DC:0/DE:0/A:0/C:0/CE:0",
            "/P:0/DC:0/DE:0", "/P:0/DC:0/DE:0/A:0/C:0",
            "/P:0/DC:0/DE:0",
            "/P:0/DC:0/DE:0", "/P:0/DC:0/DE:0/A:0/C:0",
            "/P:0/DC:0/DE:0", "/P:0/DC:0/DE:0/A:0/C:0/CE:0",
            "/P:0/DC:0/DE:0", "/P:0/DC:0/DE:0/A:0/C:0/CE:0/A:0",
            "/P:0/DC:0/DE:0", "/P:0/DC:0/DE:0/A:0/C:0/CE:0",
            "/P:0/DC:0/DE:0", "/P:0/DC:0/DE:0/A:0/C:0",
        };
        assertEngineProgress(poshEngine, workExecuter, paths);
    }
    
    /**
     * Is engine evaluation same as the supplied paths?
     */
    private void assertEngineProgress(PoshEngine engine, IWorkExecutor executor, String[] paths) {
        Iterator<String> expectedIt = Arrays.asList(paths).iterator();
        Iterator<LapPath> engineIt = Collections.<LapPath>emptyList().iterator();
        while (expectedIt.hasNext()) {
            String curPath = expectedIt.next();
            if (!engineIt.hasNext()) {
                EvaluationResultInfo evaluationResult = engine.evaluatePlan(executor);
                assertEquals(EvaluationResult.ELEMENT_FIRED, evaluationResult.result);
                engineIt = engine.getEvaluatedPaths().iterator();
            }
            LapPath enginePath = engineIt.next();
            assertEquals("Mismatched elements at path index " + Arrays.asList(paths).indexOf(curPath) + ", expected " + curPath + " but got " + enginePath.toString(), curPath, enginePath.toString());
        }
        assertFalse("Engine has extra paths", engineIt.hasNext());
    }

    /**
     * Print notice "Evaluated paths:" and after that print passed paths.
     * @param paths Paths that are supposed to be printed.
     */
    private void printPaths(List<LapPath> paths) {
        System.out.println("Evaluated paths:");
        for (LapPath path : paths) {
            System.out.println(path.toString());
        }
        System.out.println();
    }

    // TODO: Retries in CEExecutor don't work because the executor is always unwinded. Remove them?
    @Test
    @Ignore
    public void testSimpleCRetry() throws IOException, ParseException {
        System.out.println("\n === Test: " + getMethodName() + " ===");

        PoshPlan parsePlan = parsePlan("testplans/TestSimpleCRetry.lap");
        PoshEngine poshEngine = new PoshEngine(parsePlan);

        ITestPrimitive action = new PrintPrimitive("action");
        IWorkExecutor workExecuter = new TestWorkExecutor(
                new ITestPrimitive[]{action});
        
        // fire DC, add testC to stack
        assertEquals(EvaluationResult.ELEMENT_FIRED, poshEngine.evaluatePlan(workExecuter).result);
        if (action.triggered() != 0) {
            fail("Action was fired.");
        }
        final int retries = 6;
        for (int i = 0; i < retries; i++) {
            System.out.println("i" + i);
            // now in testC, add testCE to stack
            assertEquals(EvaluationResult.ELEMENT_FIRED, poshEngine.evaluatePlan(workExecuter).result);
            if (action.triggered() != 0 + i) {
                fail("Action was fired.");
            }
            // enter testCE, add action to stack
            assertEquals(EvaluationResult.ELEMENT_FIRED, poshEngine.evaluatePlan(workExecuter).result);
            if (action.triggered() != 0 + i) {
                fail("Action was fired.");
            }
            // fire action
            assertEquals(EvaluationResult.ELEMENT_FIRED, poshEngine.evaluatePlan(workExecuter).result);
            if (action.triggered() != 1 + i) {
                fail("Action wasn't fired.");
            }
            // now in testCE, go up one level
            assertEquals(EvaluationResult.ELEMENT_FIRED, poshEngine.evaluatePlan(workExecuter).result);
            if (action.triggered() != 1 + i) {
                fail("Action wasn't fired.");
            }
        }
        // next execution of testC should fail.
        // now in testC, being fired, but retry limit reached so the competence has failed
        assertEquals(EvaluationResult.ELEMENT_FIRED, poshEngine.evaluatePlan(workExecuter).result);

        // therefore the stack is reduced to zero
        assertEquals(0, poshEngine.getStackForDE("testDE").size());
    }

    /**
     * Test if primitive correctly recieves mix of variables and constants
     * passed from posh plan.
     */
    @Test
    public void test011VarConstPassed2Primitive() throws IOException, ParseException {
        System.out.println("\n === Test: " + getMethodName() + " ===");

        PoshPlan parsePlan = parsePlan("testplans/011VarConstPassed2Primitive.lap");
        PoshEngine poshEngine = new PoshEngine(parsePlan);

        ITestPrimitive murder = new PrintPrimitive("murder") {

            /**
             * Check that variable is contained in context
             *
             * @param contextName name of variable in context
             */
            private boolean variableInContext(VariableContext ctx, String contextName, String value) {
                Object contextValue = ctx.getValue(contextName);
                return value == null ? contextValue == null : value.equals(contextValue);
            }

            private void assertVariableInContext(VariableContext ctx, String contextName, String value) {
                assertTrue("Variable \"" + contextName + "\" is not \"" + value + "\", but " + ctx.getValue(contextName),
                        variableInContext(ctx, contextName, value));
            }

            @Override
            public Object work(VariableContext ctx) {
                if (ctx.size() != 5) {
                    fail("Expected 5 variables, is " + ctx.size());
                }

                assertVariableInContext(ctx, "0", "killAllHumans");
                assertEquals(ctx.getValue("1"), 12.6);
                assertVariableInContext(ctx, "2", "Tomorrow");
                assertVariableInContext(ctx, "$method", "brutal");
                assertVariableInContext(ctx, "$baka", "Sekal");

                return super.work(ctx);
            }
        };
        IWorkExecutor workExecuter = new TestWorkExecutor(
                new ITestPrimitive[]{
                    new PrintPrimitive("succeed", true),
                    new PrintPrimitive("fail", false),
                    murder});

        // put testAP to stack
        assertEquals(EvaluationResult.ELEMENT_FIRED, poshEngine.evaluatePlan(workExecuter).result);
        // put murder primitive
        assertEquals(EvaluationResult.ELEMENT_FIRED, poshEngine.evaluatePlan(workExecuter).result);
        Iterator<StackElement> enumerator = poshEngine.getStackForDE("stay").iterator();

        while (enumerator.hasNext()) {
            StackElement stackElement = enumerator.next();
            VariableContext ctx = ((AbstractExecutor) stackElement.getExecutor()).ctx;

            System.out.println("Keys for " + stackElement.toString());
            for (String key : ctx.getKeys()) {
                System.out.println(" - \"" + key + "\" " + ctx.getValue(key));
            }
        }

        // fire murder primitive
        assertEquals(EvaluationResult.ELEMENT_FIRED, poshEngine.evaluatePlan(workExecuter).result);

        assertTrue(murder.triggered() == 1);
    }

    /**
     * Test that actions in AP are called in sequenmce and when one fails,
     * process is restarted
     *
     * @throws IOException
     * @throws ParseException
     */
    @Test
    public void test012TestAPProcessing() throws IOException, ParseException {
        System.out.println("\n === Test: " + getMethodName() + " ===");

        PoshPlan parsePlan = parsePlan("testplans/012TestAPProcessing.lap");
        PoshEngine poshEngine = new PoshEngine(parsePlan);

        ITestPrimitive succeed = new PrintPrimitive("succeed", true);
        ITestPrimitive fail = new PrintPrimitive("fail", false);
        ITestPrimitive[] ok = new ITestPrimitive[]{
            new PrintPrimitive("ok1", ActionResult.FINISHED),
            new PrintPrimitive("ok2", ActionResult.FINISHED),
            new PrintPrimitive("ok3", ActionResult.FINISHED)
        };
        ITestPrimitive fail1 = new PrintPrimitive("fail1", ActionResult.FAILED);
        ITestPrimitive last = new PrintPrimitive("last", ActionResult.FINISHED);

        IWorkExecutor executor = new TestWorkExecutor(new ITestPrimitive[]{
                    succeed,
                    fail,
                    ok[0],
                    ok[1],
                    ok[2],
                    fail1,
                    last
                });

        // put testAP to stack
        assertEquals(EvaluationResult.ELEMENT_FIRED, poshEngine.evaluatePlan(executor).result);

        for (int i = 0; i < 3; ++i) {
            // put first ok[i] to stack
            assertEquals(EvaluationResult.ELEMENT_FIRED, poshEngine.evaluatePlan(executor).result);
            assertTrue(ok[i].triggered() == 0);
            // fire first ok
            assertEquals(EvaluationResult.ELEMENT_FIRED, poshEngine.evaluatePlan(executor).result);
            assertTrue(ok[i].triggered() == 1);

            assertTrue(last.triggered() == 0);
        }

        // put fail1 to stack
        assertEquals(EvaluationResult.ELEMENT_FIRED, poshEngine.evaluatePlan(executor).result);
        assertTrue(fail1.triggered() == 0);
        assertTrue(last.triggered() == 0);
        // fire fail1, should fail
        assertEquals(EvaluationResult.ELEMENT_FIRED, poshEngine.evaluatePlan(executor).result);
        assertTrue(fail1.triggered() == 1);
        assertTrue(last.triggered() == 0);

        // ok, back to DC
        // put testAP to stack
        assertEquals(EvaluationResult.ELEMENT_FIRED, poshEngine.evaluatePlan(executor).result);

        // and now it should  restart back to start
        for (int i = 0; i < 3; ++i) {
            // put first ok[i] to stack
            assertEquals(EvaluationResult.ELEMENT_FIRED, poshEngine.evaluatePlan(executor).result);
            assertTrue("ok" + i + " was triggered " + ok[i].triggered() + " != 1", ok[i].triggered() == 1);
            // fire first ok
            assertEquals(EvaluationResult.ELEMENT_FIRED, poshEngine.evaluatePlan(executor).result);
            assertTrue("ok" + i + " was triggered " + ok[i].triggered() + " != 2", ok[i].triggered() == 2);

            assertTrue(last.triggered() == 0);
        }

    }

    /**
     * Test that primitives are triggered exactly how they should.
     *
     * @param engine engine that will be used for testing.
     * @param executor executor from which list of primitives and how many times
     * was primitive actually triggered.
     * @param order two dimensional array, in each subarray is list of
     * primitives that are supposed to be triggered in that loop.
     */
    private void testPrimitiveExecutionOrder(PoshEngine engine, TestWorkExecutor executor, String[][] order) {
        // at the start, put map of primitive-# of fired into fired map.
        HashMap<String, Integer> fired = new HashMap<String, Integer>();
        for (String key : executor.getPrimitives().keySet()) {
            fired.put(key, executor.getPrimitives().get(key).triggered());
        }


        for (int executionLoop = 0; executionLoop < order.length; ++executionLoop) {
            System.out.print(">>> Loop " + executionLoop + " [");
            for (String primitive : order[executionLoop]) {
                System.out.print(primitive + ", ");
            }
            System.out.println("]");
            // evalue
            assertEquals(EvaluationResult.ELEMENT_FIRED, engine.evaluatePlan(executor).result);

            // increase expected triggered count
            for (String triggeredPrimitive : order[executionLoop]) {
                if (!fired.containsKey(triggeredPrimitive)) {
                    fail("Primitive \"" + triggeredPrimitive + "\" is not in executor list of primitives.");
                }
                fired.put(triggeredPrimitive, fired.get(triggeredPrimitive) + 1);
            }

            // check that everything is triggered according to expectations.
            for (String key : executor.getPrimitives().keySet()) {
                int wasTriggeredCount = executor.getPrimitives().get(key).triggered();
                int supposedTriggerCount = fired.get(key);
                if (wasTriggeredCount != supposedTriggerCount) {
                    fail("Primtives \"" + key + "\" is supposed to be triggered " + supposedTriggerCount + " times, but was " + wasTriggeredCount + " in loop " + executionLoop + "\n"
                            + order[executionLoop]);
                }
            }

        }
    }

    /**
     * We need better tools for evaluation. Repeat test 012 with new approach.
     */
    @Test
    public void test012New() throws IOException, ParseException {
        System.out.println("\n === Test: " + getMethodName() + " ===");

        PoshPlan parsePlan = parsePlan("testplans/012TestAPProcessing.lap");
        PoshEngine poshEngine = new PoshEngine(parsePlan);


        TestWorkExecutor executor = new TestWorkExecutor(new ITestPrimitive[]{
                    new PrintPrimitive("succeed", true),
                    new PrintPrimitive("fail", false),
                    new PrintPrimitive("ok1", ActionResult.FINISHED),
                    new PrintPrimitive("ok2", ActionResult.FINISHED),
                    new PrintPrimitive("ok3", ActionResult.FINISHED),
                    new PrintPrimitive("fail1", ActionResult.FAILED),
                    new PrintPrimitive("last", ActionResult.FINISHED)
                });
        // test order
        String[][] primitivesOrder = {
            // put testAP to stack
            new String[]{"fail", "succeed"},
            // put ok1 to top of stack
            new String[]{"fail", "succeed"},
            // ok1 will be fired
            new String[]{"fail", "succeed", "ok1"},
            // put ok2 to top of stack
            new String[]{"fail", "succeed"},
            // ok2 will be fired
            new String[]{"fail", "succeed", "ok2"},
            // ok3 to top of stack
            new String[]{"fail", "succeed"},
            // fire ok3
            new String[]{"fail", "succeed", "ok3"},
            // put fail1 to top of stack
            new String[]{"fail", "succeed"},
            // fire fail1 = reset stack, because testAP failed
            new String[]{"fail", "succeed", "fail1"}
        };

        // because stack is reseted after last fail, you can repeat multiple times
        for (int i = 0; i < 10; i++) {
            testPrimitiveExecutionOrder(poshEngine, executor, primitivesOrder);
        }
    }

    /**
     * Test that DC will correctly switch between three DE
     */
    @Test
    public void test013TestDC() throws IOException, ParseException {
        System.out.println("\n === Test: " + getMethodName() + " ===");

        PoshPlan parsePlan = parsePlan("testplans/013DCSwitch.lap");
        PoshEngine poshEngine = new PoshEngine(parsePlan);

        // ok are enabled according to phase, ok125 means ok in phase 1,2 and 5, otherwise fail.
        PrintPrimitive ok125 = new PrintPrimitive("ok125", true);
        PrintPrimitive ok3 = new PrintPrimitive("ok3", true);
        PrintPrimitive ok1234 = new PrintPrimitive("ok1234", true);
        TestWorkExecutor executor = new TestWorkExecutor(new ITestPrimitive[]{
                    new PrintPrimitive("fail", false),
                    ok125, ok3, ok1234,
                    new PrintPrimitive("action1", ActionResult.FAILED),
                    new PrintPrimitive("action2", ActionResult.FINISHED),
                    new PrintPrimitive("action3", ActionResult.FINISHED)
                });

        String[][] primitivesOrder1 = { // phase 1
            // put action1 to the stack
            new String[]{"fail", "ok125"},
            new String[]{"fail", "ok125", "action1"}
        };
        String[][] primitivesOrder2 = { // phase 2
            new String[]{"fail", "ok125"},
            new String[]{"fail", "ok125", "action1"}
        };
        String[][] primitivesOrder3 = { // phase 3
            // put action2 to top of stack
            new String[]{"fail", "ok125", "ok3"},
            new String[]{"fail", "ok125", "ok3", "action2"}
        };
        String[][] primitivesOrder4 = { // phase 4
            new String[]{"fail", "ok125", "ok3", "ok1234"},
            new String[]{"fail", "ok125", "ok3", "ok1234", "action3"}
        };
        String[][] primitivesOrder5 = { // phase 5
            new String[]{"fail", "ok125"},
            new String[]{"fail", "ok125", "action1"}
        };

        // phase 1
        ok125.setReturnValue(true);
        ok3.setReturnValue(false);
        ok1234.setReturnValue(true);
        testPrimitiveExecutionOrder(poshEngine, executor, primitivesOrder1);
        // phase 2
        ok125.setReturnValue(true);
        ok3.setReturnValue(false);
        ok1234.setReturnValue(true);
        testPrimitiveExecutionOrder(poshEngine, executor, primitivesOrder2);
        // phase 3
        ok125.setReturnValue(false);
        ok3.setReturnValue(true);
        ok1234.setReturnValue(true);
        testPrimitiveExecutionOrder(poshEngine, executor, primitivesOrder3);
        // phase 4
        ok125.setReturnValue(false);
        ok3.setReturnValue(false);
        ok1234.setReturnValue(true);
        testPrimitiveExecutionOrder(poshEngine, executor, primitivesOrder4);
        // phase 5
        ok125.setReturnValue(true);
        ok3.setReturnValue(false);
        ok1234.setReturnValue(false);
        testPrimitiveExecutionOrder(poshEngine, executor, primitivesOrder5);
    }

    private class ValuePrimitive<T> implements ITestPrimitive {

        private String name;
        private T value;
        private int triggered = 0;

        public ValuePrimitive(String name, T initialValue) {
            this.name = name;
            this.value = initialValue;
        }

        public void setValue(T value) {
            this.value = value;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public Object work(VariableContext ctx) {
            ++triggered;
            return value;
        }

        @Override
        public int triggered() {
            return triggered;
        }
    }

    @Test
    public void test014Comparison() throws IOException, ParseException {
        System.out.println("\n === Test: " + getMethodName() + " ===");

        PoshPlan parsePlan = parsePlan("testplans/014Comparison.lap");
        PoshEngine poshEngine = new PoshEngine(parsePlan);

        ValuePrimitive<Integer> value = new ValuePrimitive<Integer>("value", 0);

        TestWorkExecutor executor = new TestWorkExecutor(new ITestPrimitive[]{
                    value, new PrintPrimitive("action", ActionResult.FAILED)
                });

        for (int i = 0; i < 14; i++) {
            value.setValue(i);
            assertTrue(poshEngine.evaluatePlan(executor).result != EvaluationResult.GOAL_SATISFIED);
        }

        for (int i = 14; i < 100; i++) {
            value.setValue(i);
            assertTrue(" Loop " + i, poshEngine.evaluatePlan(executor).result == EvaluationResult.GOAL_SATISFIED);
        }
    }

    /**
     * Test if AP in another AP is working as expected. In this case, every
     * action is OK (=doesn't fail)
     */
    @Test
    public void test015APDouble() throws IOException, ParseException {
        System.out.println("\n === Test: " + getMethodName() + " ===");

        PoshPlan parsePlan = parsePlan("testplans/015APdouble.lap");
        PoshEngine poshEngine = new PoshEngine(parsePlan);


        TestWorkExecutor executor = new TestWorkExecutor(new ITestPrimitive[]{
                    new PrintPrimitive("action0", ActionResult.FINISHED),
                    new PrintPrimitive("action1", ActionResult.FINISHED),
                    new PrintPrimitive("action2", ActionResult.FINISHED),
                    new PrintPrimitive("action3", ActionResult.FINISHED),
                    new PrintPrimitive("action4", ActionResult.FINISHED)
                });
        // test order
        String[][] primitivesOrder = {
            // put testAP1 to stack
            new String[]{},
            // put action0 to top of stack
            new String[]{},
            // ok1 will be fired
            new String[]{"action0"},
            // put testAP2 to top of stack
            new String[]{},
            // action1 TOS
            new String[]{},
            // action1 fire
            new String[]{"action1"},
            // action2 TOS
            new String[]{},
            // action2 fire
            new String[]{"action2"},
            // action3 TOS
            new String[]{},
            // action3 fire
            new String[]{"action3"},
            // surface from testAP2
            new String[]{},
            // action4 TOS
            new String[]{},
            // action4 fire
            new String[]{"action4"},
            // surface from testAP1
            new String[]{},};

        // because stack is reseted after last fail, you can repeat multiple times
        for (int i = 0; i < 10; i++) {
            testPrimitiveExecutionOrder(poshEngine, executor, primitivesOrder);
        }
    }

    /**
     * Ap in AP, one action in deeper AP fails. Does engine behave correctly?
     *
     * @throws IOException
     * @throws ParseException
     */
    @Test
    public void test016APDoubleFail() throws IOException, ParseException {
        System.out.println("\n === Test: " + getMethodName() + " ===");

        PoshPlan parsePlan = parsePlan("testplans/016APdoubleFail.lap");
        PoshEngine poshEngine = new PoshEngine(parsePlan);


        TestWorkExecutor executor = new TestWorkExecutor(new ITestPrimitive[]{
                    new PrintPrimitive("action0", ActionResult.FINISHED),
                    new PrintPrimitive("action1", ActionResult.FINISHED),
                    new PrintPrimitive("action2", ActionResult.FAILED),
                    new PrintPrimitive("action3", ActionResult.FINISHED),
                    new PrintPrimitive("action4", ActionResult.FINISHED)
                });
        // test order
        String[][] primitivesOrder = {
            // put testAP1 to stack
            new String[]{},
            // put action0 to top of stack
            new String[]{},
            // ok1 will be fired
            new String[]{"action0"},
            // put testAP2 to top of stack
            new String[]{},
            // action1 TOS
            new String[]{},
            // action1 fire
            new String[]{"action1"},
            // action2 TOS
            new String[]{},
            // action2 fire and fails! By failing, the whole stack is unrolled
            new String[]{"action2"},};

        // because stack is reseted after last fail, you can repeat multiple times
        for (int i = 0; i < 10; i++) {
            testPrimitiveExecutionOrder(poshEngine, executor, primitivesOrder);
        }
    }

    /**
     * Test if multiple CE in C are correctly evaluated. I should choose ce2 (=
     * highest ce with valid trigger), go into testAP and back to testC. Repeat
     * 20x
     */
    @Test
    public void test017MultiCE() throws IOException, ParseException {
        System.out.println("\n === Test: " + getMethodName() + " ===");

        PoshPlan parsePlan = parsePlan("testplans/017MultiCE.lap");
        PoshEngine poshEngine = new PoshEngine(parsePlan);

        TestWorkExecutor executor = new TestWorkExecutor(new ITestPrimitive[]{
                    new PrintPrimitive("tr1", false),
                    new PrintPrimitive("tr2", true),
                    new PrintPrimitive("tr3", false),
                    new PrintPrimitive("action", ActionResult.FINISHED),
                    new PrintPrimitive("action0", ActionResult.FINISHED),
                    new PrintPrimitive("action1", ActionResult.FINISHED)
                });
        
        String[] traverseChoices = new String[]{
            "/P:0/DC:0/DE:0",

            "/P:0/DC:0/DE:0",
            "/P:0/DC:0/DE:0/A:0/C:0",
            "/P:0/DC:0/DE:0/A:0/C:0/CE:0/S:0",
            "/P:0/DC:0/DE:0/A:0/C:0/CE:1/S:0",

            "/P:0/DC:0/DE:0",
            "/P:0/DC:0/DE:0/A:0/C:0/CE:1",

            "/P:0/DC:0/DE:0",
            "/P:0/DC:0/DE:0/A:0/C:0/CE:1/A:0/AP:0",

            "/P:0/DC:0/DE:0",
            "/P:0/DC:0/DE:0/A:0/C:0/CE:1/A:0/AP:0/A:0",

            "/P:0/DC:0/DE:0",
            "/P:0/DC:0/DE:0/A:0/C:0/CE:1/A:0/AP:0",

            "/P:0/DC:0/DE:0",
            "/P:0/DC:0/DE:0/A:0/C:0/CE:1/A:0/AP:0/A:1",

            "/P:0/DC:0/DE:0",
            "/P:0/DC:0/DE:0/A:0/C:0/CE:1/A:0/AP:0",

            "/P:0/DC:0/DE:0",
            "/P:0/DC:0/DE:0/A:0/C:0/CE:1",

            "/P:0/DC:0/DE:0",
            "/P:0/DC:0/DE:0/A:0/C:0",
        };
        for (int i=0; i<20; i++) {
            assertEngineProgress(poshEngine, executor, traverseChoices);
        }
    }

    /**
     * When everything is OK, I should get stuck in testC2 (no another DC to
     * change)
     */
    @Test
    public void test018MultiC() throws IOException, ParseException {
        System.out.println("\n === Test: " + getMethodName() + " ===");

        PoshPlan parsePlan = parsePlan("testplans/018MultiC.lap");
        PoshEngine poshEngine = new PoshEngine(parsePlan);

        ValuePrimitive<Boolean> tr1 = new ValuePrimitive<Boolean>("tr1", true);
        ValuePrimitive<Boolean> tr2 = new ValuePrimitive<Boolean>("tr2", true);
        ValuePrimitive<Boolean> action = new ValuePrimitive<Boolean>("action", true); // true is auto-evaluated to ActionResult.FINISHED

        TestWorkExecutor executor = new TestWorkExecutor(new ITestPrimitive[]{
                    tr1, tr2, action
                });

        String[] traverseTwoCompetences = new String[]{
            "/P:0/DC:0/DE:0",
            "/P:0/DC:0/DE:0", "/P:0/DC:0/DE:0/A:0/C:0",
            "/P:0/DC:0/DE:0", "/P:0/DC:0/DE:0/A:0/C:0/CE:0",
            "/P:0/DC:0/DE:0", "/P:0/DC:0/DE:0/A:0/C:0/CE:0/A:0/C:1",
            "/P:0/DC:0/DE:0", "/P:0/DC:0/DE:0/A:0/C:0/CE:0/A:0/C:1/CE:0",
            "/P:0/DC:0/DE:0", "/P:0/DC:0/DE:0/A:0/C:0/CE:0/A:0/C:1/CE:0/A:0",
            "/P:0/DC:0/DE:0", "/P:0/DC:0/DE:0/A:0/C:0/CE:0/A:0/C:1/CE:0",
            "/P:0/DC:0/DE:0", "/P:0/DC:0/DE:0/A:0/C:0/CE:0/A:0/C:1",
            "/P:0/DC:0/DE:0", "/P:0/DC:0/DE:0/A:0/C:0/CE:0",
            "/P:0/DC:0/DE:0", "/P:0/DC:0/DE:0/A:0/C:0",
        };
        for (int i=0; i<10; i++) {
            assertEngineProgress(poshEngine, executor, traverseTwoCompetences);
        }
    }

    /**
     * Test that variable context is properly passed into sense
     */
    @Test
    public void test020SenseCtx() throws IOException, ParseException {
        System.out.println("\n === Test: " + getMethodName() + " ===");

        PoshPlan parsePlan = parsePlan("testplans/020SenseCtx.lap");
        PoshEngine poshEngine = new PoshEngine(parsePlan);


        StateWorkExecutor stateWorkExecutor = new StateWorkExecutor();

        stateWorkExecutor.addAction("doNothing", new IAction() {

            @Override
            public void init(VariableContext ctx) {
            }

            @Override
            public ActionResult run(VariableContext ctx) {
                return ActionResult.FINISHED;
            }

            @Override
            public void done(VariableContext ctx) {
            }
        });
        stateWorkExecutor.addSense("playerClose", new ISense() {

            @Override
            public Object query(VariableContext ctx) {
                if (ctx.size() != 3) {
                    fail("Size is not 3, but " + ctx.size());
                }

                assertEquals(ctx.getValue("0"), 12.4);
                assertEquals(ctx.getValue("$second"), "brutal");
                assertEquals(ctx.getValue("$third"), "lala");

                return true;
            }
        });

        // put competence to the stack
        poshEngine.evaluatePlan(stateWorkExecutor);
        // evaluate
        poshEngine.evaluatePlan(stateWorkExecutor);
    }

    @Test
    public void test021TestEqualTrue() throws IOException, ParseException {
        System.out.println("\n === Test: " + getMethodName() + " ===");

        PoshPlan parsePlan = parsePlan("testplans/021TestEqualTrue.lap");
        PoshEngine poshEngine = new PoshEngine(parsePlan);

        PrintPrimitive something = new PrintPrimitive("something", ActionResult.FINISHED);
        PrintPrimitive nothing = new PrintPrimitive("doNothing", ActionResult.FINISHED);

        TestWorkExecutor executor = new TestWorkExecutor(new ITestPrimitive[]{
                    new PrintPrimitive("succeed", true),
                    new PrintPrimitive("fail", false),
                    something, nothing
                });
        // test order
        String[][] primitivesOrder = {
            // put 'something' TOS
            new String[]{"fail", "succeed"},
            new String[]{"fail", "succeed", "something"},};

        // because stack is reseted after last fail, you can repeat multiple times
        for (int i = 0; i < 20; i++) {
            testPrimitiveExecutionOrder(poshEngine, executor, primitivesOrder);
        }
    }

    @Test
    public void test022TestAdopt() throws IOException, ParseException {
        System.out.println("\n === Test: " + getMethodName() + " ===");

        PoshPlan parsePlan = parsePlan("testplans/022TestAdopt.lap");
        PoshEngine poshEngine = new PoshEngine(parsePlan);

        PrintPrimitive action0 = new PrintPrimitive("action0", ActionResult.FINISHED);
        PrintPrimitive action1 = new PrintPrimitive("action1", ActionResult.FINISHED);
        PrintPrimitive action2 = new PrintPrimitive("action2", ActionResult.FINISHED);

        PrintPrimitive succeed0 = new PrintPrimitive("succeed0", true);
        PrintPrimitive succeed1 = new PrintPrimitive("succeed1", true);
        PrintPrimitive succeed2 = new PrintPrimitive("succeed2", true);

        PrintPrimitive fail = new PrintPrimitive("fail", false);

        PrintPrimitive sometime = new PrintPrimitive("sometime", false);


        TestWorkExecutor executor = new TestWorkExecutor(new ITestPrimitive[]{
                    succeed0, succeed1, succeed2, fail, sometime, action0, action1, action2
                });

        System.out.println("--- CYCLE ---");
        System.out.println("put testC1 on stack");
        assertEquals(EvaluationResult.ELEMENT_FIRED, poshEngine.evaluatePlan(executor).result);
        printDrives(poshEngine);


        System.out.println("--- CYCLE ---");
        System.out.println("put ce2 on stack");
        assertEquals(EvaluationResult.ELEMENT_FIRED, poshEngine.evaluatePlan(executor).result);
        printDrives(poshEngine);

        System.out.println("--- CYCLE ---");
        System.out.println("put adoptTestC2 on stack");
        assertEquals(EvaluationResult.ELEMENT_FIRED, poshEngine.evaluatePlan(executor).result);
        printDrives(poshEngine);

        System.out.println("--- CYCLE ---");
        System.out.println("put testC2 on stack");
        assertEquals(EvaluationResult.ELEMENT_FIRED, poshEngine.evaluatePlan(executor).result);
        printDrives(poshEngine);

        System.out.println("--- CYCLE ---");
        System.out.println("put ce3 on stack");
        assertEquals(EvaluationResult.ELEMENT_FIRED, poshEngine.evaluatePlan(executor).result);
        printDrives(poshEngine);

        System.out.println("--- CYCLE ---");
        System.out.println("put action2 on stack");
        assertEquals(EvaluationResult.ELEMENT_FIRED, poshEngine.evaluatePlan(executor).result);
        printDrives(poshEngine);

        System.out.println("--- CYCLE ---");
        System.out.println("put execute action2, delete it from stack");
        assertEquals(EvaluationResult.ELEMENT_FIRED, poshEngine.evaluatePlan(executor).result);
        printDrives(poshEngine);
        assertTrue(action2.triggered() == 1);

        System.out.println("--- CYCLE ---");
        System.out.println("let 'morePrior' drive to interrupt previous drive");
        sometime.setReturnValue(true);
        System.out.println("interrupt 'default' drive, put action0 on stack");
        assertEquals(EvaluationResult.ELEMENT_FIRED, poshEngine.evaluatePlan(executor).result);
        printDrives(poshEngine);
        assertTrue(action2.triggered() == 1);

        System.out.println("--- CYCLE ---");
        System.out.println("execute action0, remove it from stack");
        assertEquals(EvaluationResult.ELEMENT_FIRED, poshEngine.evaluatePlan(executor).result);
        printDrives(poshEngine);
        assertTrue(action0.triggered() == 1);
        assertTrue(action2.triggered() == 1);

        System.out.println("--- CYCLE ---");
        System.out.println("return to 'default' drive");
        sometime.setReturnValue(false);
        System.out.println("drive's stack should already be cut back to 'adoptTestC2', execute 'adoptTestC2' put testC2 on stack");
        assertEquals(EvaluationResult.ELEMENT_FIRED, poshEngine.evaluatePlan(executor).result);
        printDrives(poshEngine);
        assertTrue(action0.triggered() == 1);
        assertTrue(action2.triggered() == 1);

        System.out.println("--- CYCLE ---");
        System.out.println("put ce3 on stack");
        assertEquals(EvaluationResult.ELEMENT_FIRED, poshEngine.evaluatePlan(executor).result);
        printDrives(poshEngine);
        assertTrue(action0.triggered() == 1);
        assertTrue(action2.triggered() == 1);

        System.out.println("--- CYCLE ---");
        System.out.println("put action2 on stack");
        assertEquals(EvaluationResult.ELEMENT_FIRED, poshEngine.evaluatePlan(executor).result);
        printDrives(poshEngine);
        assertTrue(action0.triggered() == 1);
        assertTrue(action2.triggered() == 1);

        System.out.println("--- CYCLE ---");
        System.out.println("execute action2, remove it from stack");
        assertEquals(EvaluationResult.ELEMENT_FIRED, poshEngine.evaluatePlan(executor).result);
        printDrives(poshEngine);
        assertTrue(action0.triggered() == 1);
        assertTrue(action2.triggered() == 2);
    }

    private void printDrives(PoshEngine poshEngine) {
        for (int i = 0; i < poshEngine.getDECount(); ++i) {
            System.out.println("Stack(" + poshEngine.getDEName(i) + "): " + poshEngine.getStackForDE(i));
        }
    }
}
