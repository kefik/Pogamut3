package cz.cuni.pogamut.posh.properties;

import cz.cuni.amis.pogamut.sposh.elements.FormalParameters;
import cz.cuni.amis.pogamut.sposh.elements.FormalParameters.Parameter;
import java.beans.PropertyEditor;
import java.lang.reflect.InvocationTargetException;
import org.openide.nodes.Node.Property;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet.Set;

/**
 * {@link Property Property node} representing the {@link Parameter parameter} of
 * AP,C, and A&S.
 *
 * Property node provides custom editor and sets/gets value of the {@link Parameter parameter}.
 * @author Honza
 */
public class ParameterProperty extends PropertySupport.ReadWrite<FormalParameters.Parameter> {
    private final FormalParameters.Parameter parameter;
    /**
     * Unfortunatelly, we can't change name of a property, so in order to manipulate it, we remove property
     * from the {@link Set sheet set} and insert a new one.
     */
    private Set set;

    public ParameterProperty(FormalParameters.Parameter parameter, Set set) {
        super(parameter.getName(), FormalParameters.Parameter.class, parameter.getName(), "Default value of parameter " + parameter.getName());

        this.parameter = parameter;
        this.set = set;
    }

    ParametersEditor editor;

    @Override
    public synchronized PropertyEditor getPropertyEditor() {
        if (editor == null) {
            editor = new ParametersEditor();
        }
        return editor;
    }

    @Override
    public Parameter getValue() throws IllegalAccessException, InvocationTargetException {
        return parameter;
    }

    @Override
    public void setValue(Parameter value) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        // TODO: copy parameter name
        parameter.setDefaultValue(value.getDefaultValue());
    }
}
