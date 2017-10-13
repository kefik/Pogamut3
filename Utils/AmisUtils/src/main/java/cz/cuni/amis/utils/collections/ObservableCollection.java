package cz.cuni.amis.utils.collections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;

import cz.cuni.amis.utils.listener.Listeners;

/**
 * Wrapper for collections. It raises events when elements are added/removed 
 * from the collection.
 * @author Ik
 * @param <E>
 */
public class ObservableCollection<E> implements Collection<E> {

    protected Collection<E> col = null;
    protected Listeners<CollectionEventListener> eventListeners = new Listeners<CollectionEventListener>();
    
    private abstract class ListenerNotifier implements Listeners.ListenerNotifier<CollectionEventListener> {

    	public Collection what;
    	public Collection where;
    	
    	public ListenerNotifier() {    		
    	}
    	
		@Override
		public Object getEvent() {
			return what;
		}

		@Override
		public abstract void notify(CollectionEventListener listener);
    	
    }
    
    private ListenerNotifier preAddNotifier = new ListenerNotifier() {
		
		@Override
		public void notify(CollectionEventListener listener) {
			listener.preAddEvent(what, where);
		}
		
	};
	
	private ListenerNotifier postAddNotifier = new ListenerNotifier() {
		
		@Override
		public void notify(CollectionEventListener listener) {
			listener.postAddEvent(what, where);
		}
		
	};
	
	 private ListenerNotifier preRemoveNotifier = new ListenerNotifier() {
			
		@Override
		public void notify(CollectionEventListener listener) {
			listener.preRemoveEvent(what, where);
		}
		
	};
	
	private ListenerNotifier postRemoveNotifier = new ListenerNotifier() {
		
		@Override
		public void notify(CollectionEventListener listener) {
			listener.postRemoveEvent(what, where);
		}
		
	};

    public void addCollectionListener(CollectionEventListener listener) {
        eventListeners.addStrongListener(listener);
    }

    public boolean removeCollectionListener(CollectionEventListener listener) {
        return eventListeners.removeListener(listener) > 0;
    }

    public ObservableCollection(Collection c) {
        col = c;
    }

    @Override
    public int size() {
        return col.size();
    }

    @Override
    public boolean isEmpty() {
        return col.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return col.contains(o);
    }

    @Override
    public Iterator<E> iterator() {
        return col.iterator();
    }

    @Override
    public Object[] toArray() {
        return col.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return col.toArray(a);
    }

    @Override
    public boolean add(E e) {
        Collection add = Collections.singletonList(e);
        notifyPreAdd(add);
        boolean ret = col.add(e);
        notifyPostAdd(add);
        return ret;
    }

    @Override
    public boolean remove(Object o) {
        Collection rem = Collections.singletonList(o);
        notifyPreRemove(rem);
        boolean ret = col.remove(o);
        notifyPostRemove(rem);
        return ret;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return col.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        notifyPreAdd(c);
        boolean ret = col.addAll(c);
        notifyPostAdd(c);
        return ret;

    }

    @Override
    public boolean removeAll(Collection<?> c) {
        notifyPreRemove(c);
        boolean ret = col.removeAll(c);
        notifyPostRemove(c);
        return ret;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
    	// Compute difference
    	Collection<Object> toRemove = new LinkedList<Object>();
    	for (Object o : col) {
    		if (!c.contains(o)) {
    			toRemove.add(o);
    		}
    	}
    	notifyPreRemove(toRemove);
    	boolean ret = col.retainAll(c);
    	notifyPostRemove(toRemove);
    	 
    	return ret;
    }

    @Override
    public void clear() {
    	Collection<Object> copy = new ArrayList<Object>(col);
    	notifyPreRemove(copy);
    	col.clear();
    	notifyPostRemove(copy);
    }

    protected synchronized void notifyPreAdd(Collection<? extends E> add) {
    	preAddNotifier.what = add;
    	preAddNotifier.where = this;
    	eventListeners.notify(preAddNotifier);
    }

    protected synchronized void notifyPostAdd(Collection<? extends E> add) {
    	postAddNotifier.what = add;
    	postAddNotifier.where = this;
    	eventListeners.notify(postAddNotifier);
    }

    protected synchronized void notifyPreRemove(Collection<?> remove) {
    	preRemoveNotifier.what = remove;
    	preRemoveNotifier.where = this;
    	eventListeners.notify(preRemoveNotifier);
    }

    protected synchronized void notifyPostRemove(Collection<?> remove) {
    	postRemoveNotifier.what = remove;
    	postRemoveNotifier.where = this;
    	eventListeners.notify(postRemoveNotifier);
    }
}