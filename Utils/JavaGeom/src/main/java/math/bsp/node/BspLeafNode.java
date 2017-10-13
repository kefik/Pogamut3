package math.bsp.node;

import math.bsp.BspOccupation;
import math.bsp.BspTree;

/** Leaf node
 * <p>
 * A node that contains only data.
 * 
 * @param <TData> Tree data type
 * @param <TBoundary> Tree boundary type
 */
public class BspLeafNode<TData, TBoundary> 
    extends AbstractBspNode<TData, TBoundary>
    implements
        IBspNode<TData, TBoundary>,
        IConstBspLeafNode<TData, TBoundary> {
	
	private static final long serialVersionUID = 1L;
	
	protected TData data = null;
    
    public BspLeafNode( BspTree<TData, TBoundary> tree ) {
        super(tree);
    }
    
    @Override
    public boolean isLeaf() {
        return true;
    }
    
    @Override
    public TData getData() {
        return data;
    }
    
    /** Set data
     * <p>
     * Low-level API.
     */
    public void setData(TData value) {
        data = value;
    }
    
    @Override
    public TData getSubtreeData(BspOccupation filter) {
        return data;
    }
    
    @Override
    public BspLeafNode<TData, TBoundary> asLeaf() {
        return this;
    }

    @Override
    public IConstBspLeafNode<TData, TBoundary> asConst() {
        return this;
    }
}