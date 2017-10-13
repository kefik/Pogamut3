package cz.cuni.amis.pogamut.sposh.executor;

import cz.cuni.amis.pogamut.sposh.engine.VariableContext;
import java.lang.reflect.InvocationTargetException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

class SingleParamMethod {

    String value;

    public void init(@Param("$target") String target) {
        value = target;
    }
}

/**
 * Seeked method is missing the {@link Param} annotation, thus it will be
 * ignored.
 */
class MissingAnnotationMethod {

    String value;

    public void init(String target) {
        value = target;
    }
}

/**
 * This class is missing seeked method.
 */
class MissingMethod {
}

/**
 * Multiple possible methods that fulfill our desired. We don't allow multiple
 * methods
 */
class MultipleMethods {

    public void init(@Param("$target") String target) {
    }

    public void init(@Param("$distance") double distance) {
    }
}

class SimpleTypeMethods {

    public double doubleParam(@Param("$pi") double pi) {
        return Math.PI;
    }

    public int intParam(@Param("$distance") int distance) {
        return 12;
    }
}

class ObjectTypeMethods {

    public Double doubleParam(@Param("$pi") Double pi) {
        return pi;
    }

    public Integer intParam(@Param("$distance") Integer distance) {
        return distance;
    }

    public String stringParam(@Param("$target") String target) {
        return target;
    }
}

class ExceptionMethod {

    public void exception(@Param("$pi") Double pi, @Param("$target") String target) {
        throw new NullPointerException();
    }
}

public class ParamsMethodTest extends Assert {

    private final String target = "Liberty Prime";
    private final Double pi = Math.PI;
    private final Integer distance = 12;
    private VariableContext ctx;

    @Before
    public void setUp() {
        ctx = new VariableContext();
        ctx.put("$target", target);
        ctx.put("$distance", distance);
        ctx.put("$pi", pi);
    }

    @Test
    public void singleParameter() throws InvocationTargetException {
        SingleParamMethod thisClass = new SingleParamMethod();
        ParamsMethod<Void> parameterMethod = new ParamsMethod<Void>(SingleParamMethod.class, "init", Void.TYPE);

        parameterMethod.invoke(thisClass, ctx);
        assertEquals(target, thisClass.value);
    }

    @Test(expected = NoSuchMethodError.class)
    public void missingMethod() {
        MissingMethod missingMethodObject = new MissingMethod();
        ParamsMethod<Void> method = new ParamsMethod<Void>(missingMethodObject.getClass(), "init", Void.TYPE);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void overloadingMethods() {
        MultipleMethods multipleMethodsObject = new MultipleMethods();
        ParamsMethod<Void> method = new ParamsMethod<Void>(multipleMethodsObject.getClass(), "init", Void.TYPE);
    }

    @Test(expected = NoSuchMethodError.class)
    public void missingParamAnnotation() {
        MissingAnnotationMethod missingAnnotationObject = new MissingAnnotationMethod();
        ParamsMethod<Void> method = new ParamsMethod<Void>(missingAnnotationObject.getClass(), "init", Void.TYPE);
    }

    @Test
    public void doubleParameter() throws InvocationTargetException {
        SimpleTypeMethods simpleTypeMethods = new SimpleTypeMethods();
        ParamsMethod<Double> method = new ParamsMethod<Double>(simpleTypeMethods.getClass(), "doubleParam", double.class);
        Double retVal = method.invoke(simpleTypeMethods, ctx);

        assertEquals(pi, retVal);
    }

    @Test
    public void intParameter() throws InvocationTargetException {
        SimpleTypeMethods simpleTypeMethods = new SimpleTypeMethods();
        ParamsMethod<Integer> method = new ParamsMethod<Integer>(simpleTypeMethods.getClass(), "intParam", int.class);
        Integer retVal = method.invoke(simpleTypeMethods, ctx);

        assertEquals(distance, retVal);
    }

    @Test
    public void DoubleParameter() throws InvocationTargetException {
        ObjectTypeMethods objectTypeMethods = new ObjectTypeMethods();
        ParamsMethod<Double> method = new ParamsMethod<Double>(objectTypeMethods.getClass(), "doubleParam", Double.class);
        Double retVal = method.invoke(objectTypeMethods, ctx);

        assertEquals(pi, retVal);
    }

    @Test
    public void IntParameter() throws InvocationTargetException {
        ObjectTypeMethods objectTypeMethods = new ObjectTypeMethods();
        ParamsMethod<Integer> method = new ParamsMethod<Integer>(objectTypeMethods.getClass(), "intParam", Integer.class);
        Integer retVal = method.invoke(objectTypeMethods, ctx);

        assertEquals(distance, retVal);
    }

    @Test
    public void StringParameter() throws InvocationTargetException {
        ObjectTypeMethods objectTypeMethods = new ObjectTypeMethods();
        ParamsMethod<String> method = new ParamsMethod<String>(objectTypeMethods.getClass(), "stringParam", String.class);
        String retVal = method.invoke(objectTypeMethods, ctx);

        assertEquals(target, retVal);
    }

    @Test(expected = InvocationTargetException.class)
    public void exceptionInMethod() throws InvocationTargetException {
        ExceptionMethod exceptionMethodObject = new ExceptionMethod();
        ParamsMethod<Void> method = new ParamsMethod<Void>(exceptionMethodObject.getClass(), "exception", Void.TYPE);
        method.invoke(exceptionMethodObject, ctx);
    }
}
