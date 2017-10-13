package cz.cuni.amis.pogamut.sposh.engine;

import cz.cuni.amis.pogamut.sposh.executor.ActionResult;

/**
 *
 * @author Honza
 */
public interface ITestPrimitive {
    String getName();
    Object work(VariableContext ctx);
    int triggered();
}


class PrintPrimitive implements ITestPrimitive {
    private String primitive;
    private Object returnValue = true;
    private int triggered = 0;

    PrintPrimitive(String primitive) {
        this.primitive = primitive;
    }

    PrintPrimitive(String primitive, boolean b) {
        this.primitive = primitive;
        this.returnValue = b;
    }

    PrintPrimitive(String primitive, String s) {
        this.primitive = primitive;
        this.returnValue = s;
    }
    
    PrintPrimitive(String primitive, ActionResult result) {
        this.primitive = primitive;
        this.returnValue = result;
    }

    @Override
    public Object work(VariableContext ctx) {
        System.out.println("WORKING: " + getName() + "" +  ctx);
        ++triggered;
        return returnValue;
    }

    @Override
    public String getName() {
        return primitive;
    }

    @Override
    public int triggered() {
        return triggered;
    }

    public void setReturnValue(Object b) {
        this.returnValue = b;
    }

}