/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cuni.amis.nb.util.flag;

import cz.cuni.amis.utils.collections.ObservableCollection;
import cz.cuni.amis.utils.collections.SimpleListener;
import cz.cuni.amis.utils.flag.Flag;
import cz.cuni.amis.utils.flag.FlagListener;
import java.util.Collection;
import org.openide.nodes.Children.Keys;
import org.openide.nodes.Node;

/**
 *
 * @author ik
 */
public abstract class FlagCollectionChildren<T, S> extends Keys<S> {

    public FlagCollectionChildren(Flag<T> flag) {
        flag.addListener(new FlagListener<T>() {

            SimpleListener<S> listener = null;
            ObservableCollection<? extends S> col = null;

            public void flagChanged(T changedValue) {
                if (col != null) {
                    col.removeCollectionListener(listener);
                }
                col = getCollection(changedValue);
                setKeys(col);
                col.addCollectionListener(listener = new SimpleListener<S>() {

                    @Override
                    protected void changed(Collection<S> collection, Collection<S> added, Collection<S> removed) {
                        setKeys(collection);
                    }
                });
            }
        });
    }

    /**
     * Called when the flag value has been changed.
     * @param val
     */
    protected abstract ObservableCollection<? extends S> getCollection(T val);
}
