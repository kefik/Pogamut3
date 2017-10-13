package cz.cuni.amis.pogamut.shady;

import cz.cuni.amis.pogamut.sposh.engine.ITestPrimitive;
import cz.cuni.amis.pogamut.sposh.engine.TestWorkExecutor;
import cz.cuni.amis.pogamut.sposh.executor.IWorkExecutor;
import java.util.List;
import java.math.BigDecimal;
import java.util.LinkedList;
import org.junit.Before;
import org.junit.Test;
import org.junit.rules.TestWatchman;
import static org.junit.Assert.*;

class QueryNumber implements IQuery {

    private final BigDecimal number;

    QueryNumber(int number) {
        this.number = new BigDecimal(number);
    }

    @Override
    public BigDecimal execute(IWorkExecutor executor) {
        return number;
    }
}

public class QueryTests {
    private IWorkExecutor executor;

    @Before
    public void setUp() {
        executor = new TestWorkExecutor(new ITestPrimitive[]{});
    }


    @Test
    public void testQueryNot() {
        System.out.println("query not - execute");

        assertEquals(BigDecimal.ZERO, new QueryNot(new QueryNumber(1)).execute(executor));
        assertEquals(BigDecimal.ZERO, new QueryNot(new QueryNumber(-1)).execute(executor));
        assertEquals(BigDecimal.ZERO, new QueryNot(new QueryNumber(42)).execute(executor));
        assertEquals(BigDecimal.ZERO, new QueryNot(new QueryNumber(-95)).execute(executor));
        assertEquals(BigDecimal.ZERO, new QueryNot(new QueryNumber(-123)).execute(executor));
        assertEquals(BigDecimal.ONE, new QueryNot(new QueryNumber(-0)).execute(executor));
        assertEquals(BigDecimal.ONE, new QueryNot(new QueryNumber(+0)).execute(executor));
        assertEquals(BigDecimal.ONE, new QueryNot(new QueryNumber(0)).execute(executor));
    }

    private List<IQuery> createArgs(int ...numbers) {
        List<IQuery> args = new LinkedList<IQuery>();
        for (int number : numbers) {
            args.add(new QueryNumber(number));
        }
        return args;
    }

    @Test
    public void testQueryAnd() {
        System.out.println("query and - execute");

        assertEquals(BigDecimal.ONE, new QueryAnd(createArgs(1,2,-1,42)).execute(executor));
        assertEquals(BigDecimal.ONE, new QueryAnd(createArgs(1)).execute(executor));
        assertEquals(BigDecimal.ONE, new QueryAnd(createArgs(-1,-10,-3)).execute(executor));
        assertEquals(BigDecimal.ZERO, new QueryAnd(createArgs(1,2,3,0,45,0,4)).execute(executor));
        assertEquals(BigDecimal.ZERO, new QueryAnd(createArgs(0,5,0)).execute(executor));
        assertEquals(BigDecimal.ZERO, new QueryAnd(createArgs(99,0,5)).execute(executor));
    }

    @Test
    public void testQueryOr() {
        System.out.println("query or - execute");

        assertEquals(BigDecimal.ONE, new QueryOr(createArgs(1,2,-1,100,666,42)).execute(executor));
        assertEquals(BigDecimal.ONE, new QueryOr(createArgs(5)).execute(executor));
        assertEquals(BigDecimal.ONE, new QueryOr(createArgs(-45, -1)).execute(executor));
        assertEquals(BigDecimal.ONE, new QueryOr(createArgs(0,5)).execute(executor));
        assertEquals(BigDecimal.ONE, new QueryOr(createArgs(65, 48,2,75,8,6,1,7,46,5,32,0,5)).execute(executor));
        assertEquals(BigDecimal.ZERO, new QueryOr(createArgs(0)).execute(executor));
        assertEquals(BigDecimal.ZERO, new QueryOr(createArgs(0,0,0,0)).execute(executor));
        assertEquals(BigDecimal.ZERO, new QueryOr(createArgs(0,0,0,0,0,0,0)).execute(executor));
    }

    @Test
    public void testQueryGt() {
        System.out.println("query gt - execute");
        assertEquals(BigDecimal.ONE, new QueryGt(new QueryNumber(20), new QueryNumber(10)).execute(executor));
        assertEquals(BigDecimal.ONE, new QueryGt(new QueryNumber(-1000), new QueryNumber(-1001)).execute(executor));
        assertEquals(BigDecimal.ONE, new QueryGt(new QueryNumber(0), new QueryNumber(-1)).execute(executor));
        assertEquals(BigDecimal.ONE, new QueryGt(new QueryNumber(1), new QueryNumber(0)).execute(executor));
        assertEquals(BigDecimal.ONE, new QueryGt(new QueryNumber(2), new QueryNumber(1)).execute(executor));
        assertEquals(BigDecimal.ONE, new QueryGt(new QueryNumber(102), new QueryNumber(-101)).execute(executor));

        assertEquals(BigDecimal.ZERO, new QueryGt(new QueryNumber(-1001), new QueryNumber(-1000)).execute(executor));
        assertEquals(BigDecimal.ZERO, new QueryGt(new QueryNumber(-1), new QueryNumber(0)).execute(executor));
        assertEquals(BigDecimal.ZERO, new QueryGt(new QueryNumber(0), new QueryNumber(1)).execute(executor));
        assertEquals(BigDecimal.ZERO, new QueryGt(new QueryNumber(1), new QueryNumber(2)).execute(executor));
        assertEquals(BigDecimal.ZERO, new QueryGt(new QueryNumber(-101), new QueryNumber(102)).execute(executor));
    }

    @Test
    public void testQueryGe() {
        System.out.println("query ge - execute");
        assertEquals(BigDecimal.ONE, new QueryGe(new QueryNumber(20), new QueryNumber(10)).execute(executor));
        assertEquals(BigDecimal.ONE, new QueryGe(new QueryNumber(-1000), new QueryNumber(-1001)).execute(executor));
        assertEquals(BigDecimal.ONE, new QueryGe(new QueryNumber(0), new QueryNumber(-1)).execute(executor));
        assertEquals(BigDecimal.ONE, new QueryGe(new QueryNumber(1), new QueryNumber(0)).execute(executor));
        assertEquals(BigDecimal.ONE, new QueryGe(new QueryNumber(2), new QueryNumber(1)).execute(executor));
        assertEquals(BigDecimal.ONE, new QueryGe(new QueryNumber(102), new QueryNumber(-101)).execute(executor));
        assertEquals(BigDecimal.ONE, new QueryGe(new QueryNumber(0), new QueryNumber(0)).execute(executor));
        assertEquals(BigDecimal.ONE, new QueryGe(new QueryNumber(-10), new QueryNumber(-10)).execute(executor));
        assertEquals(BigDecimal.ONE, new QueryGe(new QueryNumber(11), new QueryNumber(11)).execute(executor));

        assertEquals(BigDecimal.ZERO, new QueryGe(new QueryNumber(-1001), new QueryNumber(-1000)).execute(executor));
        assertEquals(BigDecimal.ZERO, new QueryGe(new QueryNumber(-1), new QueryNumber(0)).execute(executor));
        assertEquals(BigDecimal.ZERO, new QueryGe(new QueryNumber(0), new QueryNumber(1)).execute(executor));
        assertEquals(BigDecimal.ZERO, new QueryGe(new QueryNumber(1), new QueryNumber(2)).execute(executor));
        assertEquals(BigDecimal.ZERO, new QueryGe(new QueryNumber(-101), new QueryNumber(102)).execute(executor));
    }

    @Test
    public void testQueryEq() {
        System.out.println("query eq - execute");
        assertEquals(BigDecimal.ONE, new QueryEq(new QueryNumber(0), new QueryNumber(0)).execute(executor));
        assertEquals(BigDecimal.ONE, new QueryEq(new QueryNumber(1), new QueryNumber(1)).execute(executor));
        assertEquals(BigDecimal.ONE, new QueryEq(new QueryNumber(-42), new QueryNumber(-42)).execute(executor));

        assertEquals(BigDecimal.ZERO, new QueryEq(new QueryNumber(4), new QueryNumber(-4)).execute(executor));
        assertEquals(BigDecimal.ZERO, new QueryEq(new QueryNumber(1), new QueryNumber(0)).execute(executor));
        assertEquals(BigDecimal.ZERO, new QueryEq(new QueryNumber(0), new QueryNumber(-1)).execute(executor));
    }

    @Test
    public void testQueryNe() {
        System.out.println("query ne - execute");
        assertEquals(BigDecimal.ONE, new QueryNe(new QueryNumber(0), new QueryNumber(1)).execute(executor));
        assertEquals(BigDecimal.ONE, new QueryNe(new QueryNumber(-1), new QueryNumber(0)).execute(executor));
        assertEquals(BigDecimal.ONE, new QueryNe(new QueryNumber(65), new QueryNumber(458)).execute(executor));

        assertEquals(BigDecimal.ZERO, new QueryNe(new QueryNumber(0), new QueryNumber(0)).execute(executor));
        assertEquals(BigDecimal.ZERO, new QueryNe(new QueryNumber(11), new QueryNumber(11)).execute(executor));
        assertEquals(BigDecimal.ZERO, new QueryNe(new QueryNumber(-5), new QueryNumber(-5)).execute(executor));
    }

    @Test
    public void testQueryLe() {
        System.out.println("query le - execute");
        assertEquals(BigDecimal.ONE, new QueryLe(new QueryNumber(1), new QueryNumber(12)).execute(executor));
        assertEquals(BigDecimal.ONE, new QueryLe(new QueryNumber(-10), new QueryNumber(5)).execute(executor));
        assertEquals(BigDecimal.ONE, new QueryLe(new QueryNumber(-15), new QueryNumber(-5)).execute(executor));
        assertEquals(BigDecimal.ONE, new QueryLe(new QueryNumber(0), new QueryNumber(0)).execute(executor));
        assertEquals(BigDecimal.ONE, new QueryLe(new QueryNumber(-5), new QueryNumber(-5)).execute(executor));

        assertEquals(BigDecimal.ZERO, new QueryLe(new QueryNumber(-12), new QueryNumber(-13)).execute(executor));
        assertEquals(BigDecimal.ZERO, new QueryLe(new QueryNumber(1), new QueryNumber(0)).execute(executor));
    }

    @Test
    public void testQueryLt() {
        System.out.println("query lt - execute");
        assertEquals(BigDecimal.ONE, new QueryLt(new QueryNumber(-10), new QueryNumber(0)).execute(executor));
        assertEquals(BigDecimal.ONE, new QueryLt(new QueryNumber(0), new QueryNumber(14)).execute(executor));
        assertEquals(BigDecimal.ONE, new QueryLt(new QueryNumber(13), new QueryNumber(14)).execute(executor));
        assertEquals(BigDecimal.ONE, new QueryLt(new QueryNumber(-45), new QueryNumber(-4)).execute(executor));

        assertEquals(BigDecimal.ZERO, new QueryLt(new QueryNumber(-4), new QueryNumber(-4)).execute(executor));
        assertEquals(BigDecimal.ZERO, new QueryLt(new QueryNumber(-4), new QueryNumber(-15)).execute(executor));
        assertEquals(BigDecimal.ZERO, new QueryLt(new QueryNumber(5), new QueryNumber(0)).execute(executor));
    }
}
