package cz.cuni.amis.pogamut.sposh.executor;

import cz.cuni.amis.pogamut.sposh.context.Context;
import cz.cuni.amis.pogamut.sposh.elements.EnumValue;
import cz.cuni.amis.pogamut.sposh.engine.VariableContext;
import cz.cuni.amis.pogamut.sposh.exceptions.MethodException;
import java.lang.annotation.RetentionPolicy;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * This class is responsible for testing if executor behaves correctly when
 * {@link ParamsAction}/{@link ParamsSense} gets an enum as a parameter.
 *
 * @author Honza
 */
public class EnumParam extends Assert {

    private StateWorkExecutor executor;

    @Before
    public void setUp() {
        executor = new StateWorkExecutor();
    }

    @Test
    public void enumInAction() {
        VariableContext ctx = new VariableContext();
        ctx.put("$enumVar", new EnumValue("java.lang.annotation.RetentionPolicy.SOURCE"));

        IAction action = new EnumAction();
        executor.addAction("action", action);

        executor.executeAction("action", ctx);
    }

    @Test
    public void enumInSense() {
        VariableContext ctx = new VariableContext();
        ctx.put("$enumVar", new EnumValue("java.lang.annotation.RetentionPolicy.RUNTIME"));

        ISense sense = new EnumSense();
        executor.addSense("sense", sense);

        executor.executeSense("sense", ctx);
    }

    @Test(expected = MethodException.class)
    public void wrongEnumConstantName() {
        VariableContext ctx = new VariableContext();
        ctx.put("$enumVar", new EnumValue("java.lang.annotation.RetentionPolicy.DUMMY"));

        ISense sense = new EnumSense();
        executor.addSense("sense", sense);

        executor.executeSense("sense", ctx);
    }

    @Test(expected=MethodException.class)
    public void wrongEnumClassName() {
        VariableContext ctx = new VariableContext();
        ctx.put("$enumVar", new EnumValue("dummy.lang.annotation.RetentionPolicy.SOURCE"));

        ISense sense = new EnumSense();
        executor.addSense("sense", sense);

        executor.executeSense("sense", ctx);
    }

    /**
     * Value of enum from the plan to the primitive method must be string.
     */
    @Test(expected = MethodException.class)
    public void nonStringToEnum() {
        VariableContext ctx = new VariableContext();
        ctx.put("$enumVar", new Integer(15));

        IAction action = new EnumAction();
        executor.addAction("action", action);

        executor.executeAction("action", ctx);
    }

    /**
     * Executor should ignore methods with non public enum
     */
    @Test(expected = NoSuchMethodError.class)
    public void testNonPublicEnum() {
        executor.addSense(NonPublicEnumSense.class.getSimpleName(), new NonPublicEnumSense());
    }
}

enum NonPublicEnum {

    VALUE_1, VALUE_2
}

class NonPublicEnumSense extends ParamsSense<Context, Boolean> {

    public NonPublicEnumSense() {
        super(null);
    }

    public Boolean query(@Param("$enumVar") NonPublicEnum enumVar) {
        return false;
    }
}

class EnumAction extends ParamsAction<Context> {

    public EnumAction() {
        super(null);
    }

    public void init(@Param("$enumVar") RetentionPolicy enumVariable) {
        Assert.assertEquals(RetentionPolicy.SOURCE, enumVariable);
    }

    public ActionResult run() {
        return ActionResult.FINISHED;
    }

    public void done() {
    }
}

class EnumSense extends ParamsSense<Context, Boolean> {

    public EnumSense() {
        super(null);
    }

    public Boolean query(@Param("$enumVar") RetentionPolicy enumVar) {
        Assert.assertEquals(RetentionPolicy.RUNTIME, enumVar);
        return Boolean.TRUE;
    }
}
