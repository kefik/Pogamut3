package math.bsp.node;

import java.io.Serializable;

import math.bsp.BspOccupation;
import math.bsp.IConstBspTree;

/** Constant interface to an internal or leaf node
 * 
 * @param <TData> Tree data type
 * @param <TBoundary> Tree boundary type
 */
public interface IConstBspNode<TData, TBoundary>
	extends Serializable {
    
	/** Get the BSP tree
	 */
    IConstBspTree<TData, TBoundary> getTree();
    
    /** Get parent
     * 
     * @return parent or null if the node is root
     */
    IConstBspInternalNode<TData, TBoundary> getParent();
    
    /** Tell whether node is a leaf node
     */
    boolean isLeaf();
    
    /** Tell whether node is an internal node
     */
    boolean isInternal();
    
    /** Get depth level of the node in the tree
     */
    int getDepth();
    
    /** Cast to leaf
     * <p>
     * Will throw an exception if node the is not actually leaf node. 
     */
    IConstBspLeafNode<TData, TBoundary> asLeaf();
    
    /** Cast to internal
     * <p>
     * Will throw an exception if node the is not actually internal node. 
     */
    IConstBspInternalNode<TData, TBoundary> asInternal();
    
    /** Get subtree data
     * 
     * @param filter parts to be retrieved
     */
    TData getSubtreeData(BspOccupation filter);
    
    /** Get subtree data
     */
    TData getSubtreeData();
}
