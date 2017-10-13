package cz.cuni.amis.pogamut.sposh.elements;

import cz.cuni.amis.pogamut.sposh.exceptions.MissingParameterException;
import java.beans.PropertyChangeEvent;
import java.util.Collections;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test if parameter related methods of {@link Adopt} work correctly.
 *
 * @author Honza H
 */
public class AdoptParams extends Assert {

    private FormalParameters params;
    private Adopt adopt;

    @Before
    public void before() {
        params = new FormalParameters();
        params.add(new FormalParameters.Parameter("$param", "$param value"));
        Arguments args = new Arguments();
        args.addFormal(new Arguments.ValueArgument("$valueArg", "$valueArg value"), params);
        args.addFormal(new Arguments.VariableArgument("$variableArg", "$param"), params);
        PrimitiveCall call = new PrimitiveCall("primitive", args);
        adopt = new Adopt("adopt-element", params, Collections.<Sense>emptyList(), call);
    }

    @Test(expected = MissingParameterException.class)
    public void missingParam() throws MissingParameterException {
        FormalParameters empty = new FormalParameters();
        adopt.setParameters(empty);
    }

    @Test
    public void changeParams() throws MissingParameterException {
        FormalParameters newParams = new FormalParameters();
        newParams.add(new FormalParameters.Parameter("$headParam", "$headParam value"));
        newParams.add(new FormalParameters.Parameter("$param", "$param new value"));
        newParams.add(new FormalParameters.Parameter("$tailParam", "$tailParam value"));

        adopt.setParameters(newParams);
    }

    public void listenerNotified() throws MissingParameterException {
        FormalParameters newParams = new FormalParameters();
        newParams.add(new FormalParameters.Parameter("$param", "$param new value"));

        PropertyTestListener<Adopt> listener = new PropertyTestListener<Adopt>("$param", params, newParams);
        adopt.addElementListener(listener);

        adopt.setParameters(newParams);
        assertTrue("Listener wasn't notified", listener.wasCalled());
        adopt.removeElementListener(listener);
    }

    class PropertyTestListener<PARENT extends PoshElement> implements PoshElementListener<PARENT> {

        private final String propertyName;
        private final Object oldValue;
        private final Object newValue;
        private boolean changed = false;

        public PropertyTestListener(String propertyName, Object oldValue, Object newValue) {
            this.propertyName = propertyName;
            this.oldValue = oldValue;
            this.newValue = newValue;
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            Assert.assertEquals("Property name not equal", this.propertyName, evt.getPropertyName());
            Assert.assertEquals("Old value not equal", this.oldValue, evt.getOldValue());
            Assert.assertEquals("New value not equal", this.newValue, evt.getNewValue());
        }

        public boolean wasCalled() {
            return changed;
        }

        @Override
        public void childElementAdded(PARENT parent, PoshElement child) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void childElementMoved(PARENT parent, PoshElement child, int oldIndex, int newIndex) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void childElementRemoved(PARENT parent, PoshElement child, int removedChildPosition) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }
}
