package cz.cuni.amis.nb.pogamut.base.introspection;

import org.openide.nodes.Node.Property;

/**
 * Fires property change event on given property.
 * This interface was created because firePropertyChange() is protected in Node.
 * @author ik
 */
public interface PropertyUpdater {
    /** Updates given property. */
    void updateProp(Property prop);
}
