package cz.cuni.amis.pogamut.sposh.engine;

import cz.cuni.amis.pogamut.sposh.executor.ActionResult;
import cz.cuni.amis.pogamut.sposh.executor.IWorkExecutor;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Honza
 */
public class TestWorkExecutor implements IWorkExecutor {

    private Map<String, ITestPrimitive> primitivesMap = new HashMap<String, ITestPrimitive>();
    private Map<String, ITestPrimitive> primitivesMapUm = Collections.unmodifiableMap(primitivesMap);

    public TestWorkExecutor(ITestPrimitive[] primitives) {
        for (ITestPrimitive primitive : primitives) {
            if (primitivesMap.put(primitive.getName(), primitive) != null) {
                throw new IllegalArgumentException("Primitive with name \"" + primitive.getName() + "\" was specified twice.");
            }
        }
    }

    /**
     * Get all primitives in this test executor.
     *
     * @return Unmodifiable map of all primitives.
     */
    public Map<String, ITestPrimitive> getPrimitives() {
        return primitivesMapUm;
    }

    private Object executePrimitive(String name, VariableContext ctx) {
        ITestPrimitive primitive = primitivesMap.get(name);
        if (primitive == null) {
            throw new IllegalArgumentException("Primitive \"" + name + "\" is not defined.");
        }
        return primitive.work(ctx);
    }

	@Override
	public ActionResult executeAction(String actionName, VariableContext ctx) {
		Object result = executePrimitive(actionName, ctx);
		if (result instanceof ActionResult) return (ActionResult)result;
		return ActionResult.FINISHED;
	}

	@Override
	public Object executeSense(String senseName, VariableContext ctx) {
		return executePrimitive(senseName, ctx);
	}
}
