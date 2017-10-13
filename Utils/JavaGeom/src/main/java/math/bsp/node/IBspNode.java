package math.bsp.node;

import math.bsp.BspTree;

/** Internal or leaf node
 * 
 * @param <TData> Tree data type
 * @param <TBoundary> Tree boundary type
 */
public interface IBspNode<TData, TBoundary>
    extends IConstBspNode<TData, TBoundary> {
    
	@Override
    BspTree<TData, TBoundary> getTree();
    
    @Override
    BspInternalNode<TData, TBoundary> getParent();
    
    /** Set parent
     * <p>
     * Low-level API.
     */
    void setParent(BspInternalNode<TData, TBoundary> parent);
    
    /** Set depth
     * <p>
     * Low-level API.
     */
	void setDepth(int value);
	
	@Override
    BspLeafNode<TData, TBoundary> asLeaf();
	
	@Override
    BspInternalNode<TData, TBoundary> asInternal();
	
	/** Cast to constant node
	 */
    IConstBspNode<TData, TBoundary> asConst();
}
