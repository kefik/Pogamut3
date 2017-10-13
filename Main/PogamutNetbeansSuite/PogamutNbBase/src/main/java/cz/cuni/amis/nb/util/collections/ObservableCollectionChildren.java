package cz.cuni.amis.nb.util.collections;

import cz.cuni.amis.utils.collections.*;
import java.util.Collection;
import org.openide.nodes.Children;

/**
 * Listens for changes on the observable collection and creates/removes children
 * nodes according to it.
 * @author ik
 */
public abstract class ObservableCollectionChildren<T> extends Children.Keys<T> {

    /**
     * Collection with objects that will be translated into nodes
     */
    protected ObservableCollection<T> col = null;
    protected CollectionEventListener<T> listener = null;

    public ObservableCollectionChildren(ObservableCollection<T> col) {
        this.col = col;
        col.addCollectionListener(listener = new SimpleListener<T>() {

            @Override
            protected void changed(Collection<T> collection, Collection<T> added, Collection<T> removed) {
                setKeys(collection);
            }
        });
        setKeys(col);
    }
}
