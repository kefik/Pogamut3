package cz.cuni.pogamut.posh.properties;

import cz.cuni.amis.pogamut.sposh.elements.FormalParameters.Parameter;
import cz.cuni.amis.pogamut.sposh.elements.Result;
import java.awt.Component;
import java.beans.PropertyEditorSupport;

/**
 * Editor for changing setting and getting default values of a {@link Parameter}.
 * <p/>
 * Default value is given as text or in case of null, the "nil" string is returned.
 * Default value set by parsing given text using  {@link Result#parseValue(java.lang.String) }.
 * 
 * @see Result
 * @author Honza
 */
final class ParametersEditor extends PropertyEditorSupport { // implements ExPropertyEditor, InplaceEditor.Factory { <- for inplace editor

    private ParameterEditorPanel visualEditor;

    @Override
    public String getAsText() {
        Object v = getValue();
        return v == null ? "nil" : v.toString();
    }

    @Override
    public void setAsText(String s) {
        Object defaultValue = Result.parseValue(s);
        setValue(defaultValue);
    }

    @Override
    public boolean supportsCustomEditor() {
        return false;
    }

    @Override
    public Component getCustomEditor() {
//        if (visualEditor == null)
  //          visualEditor = new ParameterEditorPanel(getValue());
        return visualEditor;
    }

/* TODO: When I figure out how to display inplace editor properly (= NOT SHOWN in area for property, but above property sheet),
         use inplace instead of custom

    private InplaceEditor editor;

    @Override
    public void attachEnv(PropertyEnv env) {
        env.registerInplaceEditorFactory(this);
    }

    @Override
    public InplaceEditor getInplaceEditor() {
        if (editor == null) {
            editor = new ParameterInplaceEditor();
        }
        return editor;
    }
 */
};
