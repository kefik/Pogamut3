/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cuni.amis.nb.pogamut.unreal.services;

import java.util.Collection;
import javax.swing.JOptionPane;
import org.openide.util.Lookup;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;

/**
 * Basically lookup for various things that can be selected.
 * @author Honza
 */
public class EnvironmentSelection extends AbstractLookup {

    protected InstanceContent content;

    protected EnvironmentSelection(InstanceContent content) {
        super(content);
        this.content = content;
    }

    /**
     * Add passed object to selected object of the environment
     * @param selected
     */
    public synchronized void addSelected(Object selected) {
        System.out.println("Selected to lookup " + selected);
        this.content.add(selected);
    }

    /**
     * Remove object from the selected objects of the environment
     * @param unselected
     */
    public synchronized void removeSelected(Object unselected) {
        this.content.remove(unselected);
    }

    /**
     * Remove all selected objects of the same class as has the passed paramater
     * and set it as the single selected object for the class.
     *
     * @param singleSelection, can be null
     */
    public synchronized void changeSelected(Object singleSelection) {
        if (singleSelection == null) {
            //JOptionPane.showMessageDialog(null, "Changed to null");
            this.clearSelection();
        } else {
            // remove all with same class as the singleSelection
            Collection allInLookup = this.lookupAll(singleSelection.getClass());
            for (Object o : allInLookup) {
                content.remove(o);
            }
            content.add(singleSelection);
        }
    }

    /**
     * Clear all selections in the environment
     */
    public synchronized void clearSelection() {
        Collection allInLookup = this.lookupAll(Object.class);
        for (Object o : allInLookup) {
            content.remove(o);
        }
    }

}
