package math.bsp.node;

import java.util.ArrayList;

import com.google.common.collect.Lists;

import math.bsp.BspOccupation;
import math.bsp.BspTree;

/** Internal node
 * <p>
 * A node with boundary and positive and negative child.
 *
 * @param <TData> Tree data type
 * @param <TBoundary> Tree boundary type
 */
public class BspInternalNode<TData, TBoundary> 
    extends AbstractBspNode<TData, TBoundary>
    implements 
        IBspNode<TData, TBoundary>, 
        IConstBspInternalNode<TData, TBoundary> {
    
	private static final long serialVersionUID = 1L;
	
	protected TBoundary boundary;
	protected IBspNode<TData, TBoundary> positiveChild;
	protected IBspNode<TData, TBoundary> negativeChild;
    
    public BspInternalNode( BspTree<TData, TBoundary> tree ) {
        super(tree);
    }
    
    @Override
    public TBoundary getBoundary() {
        return boundary;
    }
    
    /** Set boundary
     * <p>
     * Low-level API.
     */
    public void setBoundary(TBoundary value) {
    	boundary = value;
    }
    
    @Override
    public IBspNode<TData, TBoundary> getPositiveChild() {
        return positiveChild;
    }

    /** Set positive child
     * <p>
     * Low-level API.
     */
    public void setPositiveChild(IBspNode<TData, TBoundary> value) {
        positiveChild = value;
    }
    
    @Override
    public IBspNode<TData, TBoundary> getNegativeChild() {
        return negativeChild;
    }
    
    /** Set negative child
     * <p>
     * Low-level API.
     */
    public void setNegativeChild(IBspNode<TData, TBoundary> value) {
        negativeChild = value;
    }
    
    @Override
    public boolean isLeaf() {
        return false;
    }
        
    @Override
    public TData getSubtreeData(BspOccupation filter) {
        ArrayList<TData> unjoined = Lists.newArrayList();
        
        if (filter.intersectsPositive()) {
            unjoined.add( positiveChild.getSubtreeData(filter) );
        }
        
        if (filter.intersectsNegative()) {
            unjoined.add( negativeChild.getSubtreeData(filter) );
        }
        
        if (unjoined.isEmpty()) {
            return null;
        }
        
        while (unjoined.size() > 1) {
            TData data1 = unjoined.remove(0);
            TData data2 = unjoined.remove(0);
            unjoined.add( tree.getStrategy().joinData( data1, data2 ) );
        }
        
        return unjoined.get(0);
    }
    
    @Override
    public BspInternalNode<TData, TBoundary> asInternal() {
        return this;
    }
    
    @Override
    public IConstBspInternalNode<TData, TBoundary> asConst() {
        return this;
    }
}
