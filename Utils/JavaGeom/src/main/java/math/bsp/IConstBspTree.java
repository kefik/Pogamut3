package math.bsp;

import math.bsp.node.IConstBspNode;

/** Constant interface to {@link BspTree}
 */
public interface IConstBspTree<TData, TBoundary> {

    /** Get root node
     */
    public IConstBspNode<TData, TBoundary> getRoot();

    /** Get strategy
     */
	public IBspStrategy<TData, TBoundary> getStrategy();
}
