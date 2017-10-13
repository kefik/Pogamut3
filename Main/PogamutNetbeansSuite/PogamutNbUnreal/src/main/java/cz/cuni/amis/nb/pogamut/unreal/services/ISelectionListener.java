/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cuni.amis.nb.pogamut.unreal.services;

/**
 * Listener for changes in selection of some set. At most one item can be selected.
 * @author Honza
 */
public interface ISelectionListener<E> {
    /**
     * Notify method that selected item has changed.
     * When this is called, source already has newSelection as the selection
     * @param oldSelection what was  originally selected
     * @param newSelection what is selected now.
     */
    public void selectionChanged(E oldSelection, E newSelection);
}
