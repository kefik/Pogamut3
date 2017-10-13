package math.bsp.node;

/** Constant interface to an internal node
 * 
 * @param <TData> Tree data type
 * @param <TBoundary> Tree boundary type
 */
public interface IConstBspInternalNode<TData, TBoundary>
    extends IConstBspNode<TData, TBoundary> {
    
	/** Get the boundary between positive and negative child
	 */
    TBoundary getBoundary();
    
    /** Get the positive child
     */
    IConstBspNode<TData, TBoundary> getPositiveChild();
    
    /** Get the negative child
     */
    IConstBspNode<TData, TBoundary> getNegativeChild();
}
