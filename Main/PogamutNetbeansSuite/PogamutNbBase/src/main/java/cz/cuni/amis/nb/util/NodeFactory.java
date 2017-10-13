package cz.cuni.amis.nb.util;

import org.openide.nodes.Node;

/**
 *
 * @author ik
 */
public interface NodeFactory<T> {
    public class IdentityFactory implements NodeFactory<Node> {

        @Override
        public Node[] create(Node obj) {
            return new Node[] {obj};
        }
        
    }
    Node[] create(T obj);
}
