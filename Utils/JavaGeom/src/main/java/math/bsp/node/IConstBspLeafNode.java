package math.bsp.node;

/** Constant interface to a leaf node
 * 
 * @param <TData> Tree data type
 * @param <TBoundary> Tree boundary type
 */
public interface IConstBspLeafNode<TData, TBoundary>
    extends IConstBspNode<TData, TBoundary> {
	
	/** Get the data
	 */
	TData getData();
}
