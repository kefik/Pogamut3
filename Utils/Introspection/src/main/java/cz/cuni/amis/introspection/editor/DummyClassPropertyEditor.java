package cz.cuni.amis.introspection.editor;
/*
 * DummyClassPropertyEditor.java
 *
 * Created on 27. duben 2007, 20:39
 *
 */

import java.beans.PropertyEditorManager;
import java.beans.PropertyEditorSupport;

/**
 *
 * @author ik
 */
public class DummyClassPropertyEditor extends PropertyEditorSupport {
    
    {
        // register this editor
        //PropertyEditorManager.registerEditor(DummyClass.class, DummyClassPropertyEditor.class);
    }
    
    /** Creates a new instance of DummyClassPropertyEditor */
    public DummyClassPropertyEditor() {
    }
    
    public String getAsText() {
        return "[property type changed]";
    }
    
}
