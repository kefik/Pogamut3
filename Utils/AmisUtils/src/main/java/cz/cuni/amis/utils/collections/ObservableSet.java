package cz.cuni.amis.utils.collections;

import java.util.Set;

public class ObservableSet<E> extends ObservableCollection<E> implements Set<E> {

    protected Set<E> s = null;

    public ObservableSet(Set<E> set) {
        super(set);
        s = set;
    }
}