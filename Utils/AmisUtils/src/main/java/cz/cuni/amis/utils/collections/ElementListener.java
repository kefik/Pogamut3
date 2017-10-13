package cz.cuni.amis.utils.collections;

import java.util.Collection;

/**
 * Simple listener called for each element that has changed even in batch updates.
 * @author ik
 */
public abstract class ElementListener<T> extends SimpleListener<T> {

    /**
     * Called each time an element changes.
     * @param elem
     * @param added
     */
    public abstract void elementChanged(T elem, boolean added);

    @Override
    protected void changed(Collection<T> collection, Collection<T> added, Collection<T> removed) {
        Collection<T> changed = null;
        boolean addedFlag = true;

        if(added != null) {
            changed = added;
        } else {
            changed = removed;
            addedFlag = false;
        }

        for (T elem : changed) {
            elementChanged(elem, addedFlag);
        }
    }
}
