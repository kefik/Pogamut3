/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cuni.amis.utils.collections;

import java.util.Collection;

/**
 * Collection listener with only one method changed() called on each change.
 * @author ik
 */
public abstract class SimpleListener<T> implements CollectionEventListener<T> {

    @Override
    public void preAddEvent(Collection<T> toBeAdded, Collection<T> whereToAdd) {
    }

    @Override
    public void postAddEvent(Collection<T> alreadyAdded, Collection<T> whereWereAdded) {
        changed(whereWereAdded, alreadyAdded, null);
    }

    @Override
    public void preRemoveEvent(Collection<T> toBeRemoved, Collection<T> whereToRemove) {
    }

    @Override
    public void postRemoveEvent(Collection<T> alreadyRemoved, Collection<T> whereWereRemoved) {
        changed(whereWereRemoved, null, alreadyRemoved);
    }

    /**
     * Called when a change occures on the collection.
     * @param collection The changed collection.
     */
    protected abstract void changed(Collection<T> collection, Collection<T> added, Collection<T> removed);

}
