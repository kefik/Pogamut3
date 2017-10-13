package cz.cuni.amis.utils.collections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

/**
 * Wrapper for the Lists adding events notification.
 * @author Ik
 * @param <E>
 */
public class ObservableList<E> extends ObservableCollection<E> implements List<E> {

    protected List<E> l = null;

    public ObservableList(List<E> list) {
        super(list);
        l = list;
    }

    /**
     * Returns the wrapped List instance.
     * @return
     */
    public List<E> getList() {
        return l;
    }


    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        notifyPreAdd(c);
        boolean ret = l.addAll(index, c);
        notifyPostAdd(c);
        return ret;
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        return addAll(l.size(), c);
    }

    @Override
    public void clear() {
    	List<Object> copy = new ArrayList<Object>(col);
    	notifyPreRemove(copy);
        l.clear();
        notifyPostRemove(copy);
    }



    @Override
    public E get(int index) {
        return l.get(index);
    }

    @Override
    public E set(int index, E element) {
        return l.set(index, element);
    }

    @Override
    public void add(int index, E element) {
        Set<E> add = Collections.singleton(element);
        notifyPreAdd(add);
        l.add(index, element);
        notifyPostAdd(add);
    }

    @Override
    public E remove(int index) {
        Set<E> toRem = Collections.singleton(l.get(index));
        notifyPreRemove(toRem);
        E rem = l.remove(index);
        notifyPostRemove(Collections.singleton(rem));
        return rem;
    }

    @Override
    public int indexOf(Object o) {
        return l.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return lastIndexOf(o);
    }

    @Override
    public ListIterator<E> listIterator() {
        return l.listIterator();
    }

    @Override
    public ListIterator<E> listIterator(int index) {
        return l.listIterator(index);
    }

    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        return l.subList(fromIndex, toIndex);
    }
}