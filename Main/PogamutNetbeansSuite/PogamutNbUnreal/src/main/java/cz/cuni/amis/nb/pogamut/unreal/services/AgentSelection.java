/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cuni.amis.nb.pogamut.unreal.services;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This is a selection set for one environment(server, timeline).
 * <p>
 * It is possible to listen for changes in selection and change selected entities.
 * Nothing more for now.
 *
 * @author Honza
 */
public class AgentSelection<E> {
    private E selected = null;
    private Set<ISelectionListener<E>> listeners = new HashSet<ISelectionListener<E>>();

    public synchronized void changeSelected(E newSelection) {
        E oldSelected = selected;
        selected = newSelection;

        fireSelectionPropertyChange(oldSelected, newSelection);
    }
    
    public synchronized void addSelectionListener(ISelectionListener<E> listener) {
        this.listeners.add(listener);
    }
    
    public synchronized void removeSelectionListener(ISelectionListener<E> listener) {
        this.listeners.remove(listener);
    }

    private synchronized void fireSelectionPropertyChange(E oldSelection, E newSelection) {
        List<ISelectionListener<E>> listenersArray = new ArrayList<ISelectionListener<E>>(listeners);

        for (ISelectionListener<E> listener : listenersArray) {
            listener.selectionChanged(oldSelection, newSelection);
        }
    }

}
