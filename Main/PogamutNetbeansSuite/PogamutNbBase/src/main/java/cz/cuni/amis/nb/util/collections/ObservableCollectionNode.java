package cz.cuni.amis.nb.util.collections;

import cz.cuni.amis.nb.util.NodeFactory;
import cz.cuni.amis.utils.collections.ObservableCollection;
import cz.cuni.amis.utils.collections.ObservableList;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Node;

/**
 * Node whose children are updated when the observable collection changes.
 * @author ik
 */
public class ObservableCollectionNode<T> extends AbstractNode {

    private ObservableCollection<T> enclosedCollection = null;

    public ObservableCollectionNode(final ObservableCollection<T> col, final NodeFactory<T> translator) {
        super(new ObservableCollectionChildren<T>(col) {

            @Override
            protected Node[] createNodes(T obj) {
                return translator.create(obj);
            }
        });
        enclosedCollection = col;
    }

    public ObservableCollectionNode(final ObservableCollection<Node> col) {
        super(new ObservableCollectionChildren<Node>(col) {

            @Override
            protected Node[] createNodes(Node obj) {
                return new Node[]{obj};
            }
        });
        enclosedCollection = (ObservableCollection<T>) col;
    }

    protected ObservableCollection<T> getChildrenCollection() {
        return enclosedCollection;
    }
}
