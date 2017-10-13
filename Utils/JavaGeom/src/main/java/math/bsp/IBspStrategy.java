package math.bsp;

import math.bsp.node.IConstBspLeafNode;

/** Binary space partitioning strategy
 * <p>
 * Strategy tells {@link BspTree} how to partition space/data. For example a 3D tree using planes to partition the space needs to know 
 * when to split a leaf node ({@link #shouldSplit(IConstBspLeafNode)}), what planes should be used ({@link #findBoundary(IConstBspLeafNode)}) and
 * then how to split the data ({@link #splitData(Object, Object)}). 
 *
 * @param <TData>
 * @param <TPoint>
 * @param <TBoundary>
 */
public interface IBspStrategy<TData, TBoundary> {

	/** Whether a leaf node should be split
	 * 
	 * @param leafNode tested leaf node
	 */
    boolean shouldSplit( IConstBspLeafNode<TData, TBoundary> leafNode);
    
    /** Find a boundary to split a leaf node
     * 
     * @param leafNode leaf node to split
     * @return a boundary suitable for splitting the node and its data, or null
     */
    TBoundary findBoundary( IConstBspLeafNode<TData, TBoundary> leafNode );
    
    /** Split data according to a boundary
     * <p>
     * Depending on the nature of the data this can be done multiple ways.
     * For all purposes the sum of partitioned data must be equivalent to the original data and
     * for best results each half should contain only those bits that are relevant to that half.
     * <p> 
     * For example, if the data is composed of elements then elements that span both across the boundary
     * should be included in both the positive and the negative part. Elements that belong to only one side should
     * be added only to the respective side.
     * 
     * @param boundary boundary used to split the data
     * @param data data to split
     * @return split data
     */
    SplitData<TData> splitData( TBoundary boundary, TData data );
    
    /** Join data
     * 
     * @param data1 data, ignored if null
     * @param data2 data, ignored if null
     * @return data comprised of both data1 and data2 joined
     */
    TData joinData( TData data1, TData data2 );
    
    /** Remove data
     * <p>
     * Required for {@link BspTree#removeData(Object)}, may throw {@link UnsupportedOperationException} if removal is not required.
     * 
     * @param data data to remove from
     * @param dataToRemove what to remove
     * @return data without dataToRemove
     */
    TData removeData( TData data, TData dataToRemove );
}