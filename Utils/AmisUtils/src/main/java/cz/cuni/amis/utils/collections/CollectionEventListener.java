package cz.cuni.amis.utils.collections;

import java.util.Collection;
import java.util.EventListener;

/**
 * Listener on collection change events.
 * @author Ik
 * @param <E>
 */
public interface CollectionEventListener<E> extends EventListener {

    /**
     * Called before the elements will be added to the collection.
     * @param toBeAdded collection of items to be added, in case of one item this contains a collection with one item
     * @param whereToAdd
     */
    void preAddEvent(Collection<E> toBeAdded, Collection<E> whereToAdd);

    /**
     * Called after the elements were added to the collection.
     * @param alreadyAdded
     * @param whereWereAdded
     */
    void postAddEvent(Collection<E> alreadyAdded, Collection<E> whereWereAdded);

    /**
     * Called before the elements will be removed from the collection.
     * @param toBeRemoved
     * @param whereToRemove
     */
    void preRemoveEvent(Collection<E> toBeRemoved, Collection<E> whereToRemove);

    /**
     * Called after the elements were removed from the collection.
     * @param alreadyAdded
     * @param whereWereRemoved
     */
    void postRemoveEvent(Collection<E> alreadyAdded, Collection<E> whereWereRemoved);
}