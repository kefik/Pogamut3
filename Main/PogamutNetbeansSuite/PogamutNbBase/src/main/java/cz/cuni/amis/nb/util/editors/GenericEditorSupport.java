/*
 * GenericEditorSupport.java
 *
 * Created on 23. cerven 2007, 21:59
 *
 */

package cz.cuni.amis.nb.util.editors;

import java.beans.PropertyEditorSupport;

/**
 *
 * @author ik
 */
public class GenericEditorSupport extends PropertyEditorSupport {
    
    /** Creates a new instance of GenericEditorSupport */
    public GenericEditorSupport() {
    }
    
    public String getAsText() {
        if(getValue() != null)
            return getValue().toString();
        else
            return "null";
    }
    
    public void setAsText(String s) {
        
    }
    
    public boolean supportsCustomEditor() {
        return false;
    }
}
