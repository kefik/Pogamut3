package math.bsp.node;

import math.bsp.BspOccupation;
import math.bsp.BspTree;

@SuppressWarnings("serial") // it's abstract
abstract class AbstractBspNode<TData, TBoundary>
    implements IBspNode<TData, TBoundary> {
    
    protected BspTree<TData, TBoundary> tree;
    protected BspInternalNode<TData, TBoundary> parent = null;
    protected int depth = 0;
    
    public AbstractBspNode( BspTree<TData, TBoundary> tree ) {    
        this.tree = tree;
    }

    @Override
    public BspTree<TData, TBoundary> getTree() {
        return tree;
    }
    
    @Override
    public BspInternalNode<TData, TBoundary> getParent() {
        return parent;
    }
    
    @Override
    public void setParent(BspInternalNode<TData, TBoundary> value) {
        parent = value;
    }
      
    @Override
    public TData getSubtreeData() {
        return getSubtreeData(BspOccupation.BOTH);
    }
            
    @Override
    public boolean isInternal() {
        return !isLeaf();
    }
    
    @Override
    public int getDepth() {
    	return depth;
    }
    
    @Override
    public void setDepth( int value ) {
    	depth = value;
    }
    
    @Override
    public BspLeafNode<TData, TBoundary> asLeaf() {
        return null;
    }
    
    @Override
    public BspInternalNode<TData, TBoundary> asInternal() {
        return null;
    }
}