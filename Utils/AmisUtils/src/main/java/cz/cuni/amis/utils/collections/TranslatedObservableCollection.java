package cz.cuni.amis.utils.collections;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Translates one observable collection into another
 * @author ik
 */
public abstract class TranslatedObservableCollection<T, U> extends ObservableList<T> {

    /**
     * Holds mapping between original objects and the translated one.
     */
    Map<Object, T> map = new HashMap<Object, T>();

    public TranslatedObservableCollection(ObservableCollection<U> col) {
        super(new ArrayList<T>());

        // listen for changes
        col.addCollectionListener(new ElementListener<U>() {

            @Override
            public void elementChanged(U elem, boolean added) {
                if (added) {
                    insert(elem);
                } else {
                    // remove element
                    T val = map.remove(getKeyForObj(elem));
                    remove(val);
                }
            }
        });

        // import existing items
        for (U val : col) {
            insert(val);
        }

    }

    protected synchronized void insert(U elem) {
        if (map.containsKey(getKeyForObj(elem))) return; // will not insert already present item
    	T val = translate(elem);
        if (val != null) {
            map.put(getKeyForObj(elem), val);
            add(val);
        }

    }

    protected Object getKeyForObj(U elem) {
        return elem;
    }

    /**
     * Translates object from wrapped collection into this collection.
     * @param obj object to be translated
     * @return the translated object, null if the object shouldn't be added to the collection
     */
    protected abstract T translate(U obj);
}
