package math.bsp.algorithm;

import math.bsp.BspOccupation;
import math.bsp.IConstBspTree;
import math.bsp.node.IConstBspInternalNode;
import math.bsp.node.IConstBspLeafNode;
import math.bsp.node.IConstBspNode;

/** Data selector
 * <p>
 * Selects data by volume - traverses nodes from root and uses boundaries to skip children not intersecting the volume.
 * 
 * @param <TVolume> Data type describing a volume of space being searched for data.
 * @param <TData> BSP tree data
 * @param <TBoundary> BSP tree boundary
 */
public abstract class BspDataSelector<TVolume, TData, TBoundary> {
	
	protected IConstBspTree<TData, TBoundary> tree;
	
	/** Create a selector linked to a BSP tree
	 */
	public BspDataSelector( IConstBspTree<TData, TBoundary> tree ) {
		this.tree = tree;
	}
	
	/** Select data potentially within the volume
	 */
	public TData select(TVolume volume) {
		return selectAdditionalData( tree.getRoot(), null, volume );
    }
	
	/** Select additional data
	 * 
	 * @param node node to search for data
	 * @param dataFoundSoFar data found so far, will be added to the result
	 * @param volume limiting volume
	 * @return data found so far joined with additionally found data
	 */
	protected TData selectAdditionalData( IConstBspNode<TData, TBoundary> node, TData dataFoundSoFar, TVolume volume ) {
		
		if ( node.isLeaf() ) {
			IConstBspLeafNode<TData, TBoundary> leafNode = node.asLeaf();
			TData filteredData = filterDataByVolume( volume, leafNode.getData() );
			return tree.getStrategy().joinData( dataFoundSoFar, filteredData );
		} else {
			IConstBspInternalNode<TData, TBoundary> parentNode = node.asInternal();
			BspOccupation occupation = determineVolumeOccupation( parentNode.getBoundary(), volume );
		    TData retval = dataFoundSoFar;
			
			if ( occupation.intersectsNegative() ) {
		    	retval = selectAdditionalData( parentNode.getNegativeChild(), retval, volume );
		    }
			
		    if ( occupation.intersectsPositive() ) {
		    	retval = selectAdditionalData( parentNode.getPositiveChild(), retval, volume );
		    }
		    
		    return retval;
		}
	}
	
	/** Determine what partitions a volume occupies
	 */
	public abstract BspOccupation determineVolumeOccupation( TBoundary boundary, TVolume volume );
	
	/** Filter data by volume
	 * 
	 * @return data intersecting the volume
	 */
	protected abstract TData filterDataByVolume( TVolume volume, TData data );
}
