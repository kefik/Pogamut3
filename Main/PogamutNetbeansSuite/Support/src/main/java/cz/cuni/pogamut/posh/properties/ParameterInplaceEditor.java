package cz.cuni.pogamut.posh.properties;

import cz.cuni.amis.pogamut.sposh.elements.FormalParameters.Parameter;
import java.awt.Component;
import java.awt.event.ActionListener;
import java.beans.PropertyEditor;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import org.openide.explorer.propertysheet.InplaceEditor;
import org.openide.explorer.propertysheet.PropertyEnv;
import org.openide.explorer.propertysheet.PropertyModel;

/**
 * Class that represents (NOT visually) inplace editor for formal parameter of posh plan.
 * In our case, it is basically bridge between custom editor for formal parameters and {@link ParameterEditorPanel visual editor}.
 *
 * Sorry about the bloatware.
 * @author Honza
 */
class ParameterInplaceEditor implements InplaceEditor {
    // model is supposed to do domething, but it is explicitely said I shouldn't touch it
    private PropertyModel model;
    // editor used to change model
    private PropertyEditor propertyEditor;
    // editor to select values ect.
    private ParameterEditorPanel visualEditor;

    @Override
    public void connect(PropertyEditor propertyEditor, PropertyEnv env) {
        this.visualEditor = new ParameterEditorPanel((Parameter) propertyEditor.getValue());
        this.propertyEditor = propertyEditor;
        reset();
    }

    @Override
    public JComponent getComponent() {
        return visualEditor;
    }

    @Override
    public void clear() {
        //avoid memory leaks:
        visualEditor = null;
        propertyEditor = null;
        model = null;
    }

    @Override
    public Object getValue() {
        return visualEditor.getParameter();
    }

    @Override
    public void setValue(Object object) {
        visualEditor.setParameter((Parameter) object);
    }

    @Override
    public boolean supportsTextEntry() {
        return true;
    }

    @Override
    public void reset() {
        Parameter parameter = (Parameter) propertyEditor.getValue();
        if (parameter != null) {
            visualEditor.setParameter(parameter);
        }
    }

    @Override
    public KeyStroke[] getKeyStrokes() {
        return new KeyStroke[0];
    }

    @Override
    public PropertyEditor getPropertyEditor() {
        return propertyEditor;
    }

    @Override
    public PropertyModel getPropertyModel() {
        return model;
    }

    @Override
    public void setPropertyModel(PropertyModel propertyModel) {
        this.model = propertyModel;
    }

    @Override
    public boolean isKnownComponent(Component component) {
        return visualEditor == component || visualEditor.isAncestorOf(component);
    }

    @Override
    public void addActionListener(ActionListener actionListener) {
        //do nothing - not needed for this component
    }

    @Override
    public void removeActionListener(ActionListener actionListener) {
        //do nothing - not needed for this component
    }
}
