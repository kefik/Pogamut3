package cz.cuni.amis.nb.pogamut.base;

import cz.cuni.amis.utils.exception.PogamutException;
import java.awt.event.ActionEvent;
import java.util.MissingResourceException;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

/**
 * Action getting the text from resource bundle according to the supplied
 * string key.
 * @author ik
 */
public abstract class NamedAction extends AbstractAction {

    static protected String getStr(String key, Class cls) throws MissingResourceException {
        return NbBundle.getMessage(cls, key);
    }

    public NamedAction(String key) {
        super(getStr(key, NamedAction.class));
        init(key, NamedAction.class);
    }

    /**
     * 
     * @param key
     * @param cls Class used to get the bundle with localized resources
     */
    public NamedAction(String key, Class cls) {
        super(getStr(key, cls));
        init(key, cls);
    }

    protected void init(String key, Class cls) {
        try {
            String hint = getStr(key + "_HINT", cls);
            if (hint != null) {
                putValue(Action.SHORT_DESCRIPTION, hint);
            }
        } catch (MissingResourceException e) {
            // nothing happens
        }
    }

    public void actionPerformed(ActionEvent e) {
        try {
            action(e);
        } catch (PogamutException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    protected abstract void action(ActionEvent e) throws PogamutException;
}
