package math.bsp;

import java.io.Serializable;

import math.bsp.node.BspLeafNode;
import math.bsp.node.BspInternalNode;
import math.bsp.node.IBspNode;

/** Binary space partitioning tree
 * <p>
 * This is a generic implementation supporting both 2D and 3D spaces (or any other space, even non-euclidean).
 * The TBoundary type parameter and the provided strategy define the space and how it is partitioned.
 * BspTreeND<?, Point2D, StraightLine2D>( new BspLinePartitiongStrategy(6), ? ) would define a 2D BSP tree with up to 6 elements per leaf.
 * BspTreeND<?, Point3D, Plane3D>( new BspPlanePartitiongStrategy(6), ? ) would define a 3D BSP tree with up to 6 elements per leaf.
 * 
 * @param <TData> Data stored in each leaf node
 * @param <TBoundary> An object that splits the space into two distinct subsets
 */
public class BspTree<TData, TBoundary>
    implements IConstBspTree<TData, TBoundary>, Serializable {
    
    private static final long serialVersionUID = 1L;
    
    protected IBspNode<TData, TBoundary> root;
    protected IBspStrategy<TData, TBoundary> strategy;
    
    /** Make tree with data
     * <p>
     * Shortcut to avoid having to specify type parameters.
     * 
     * @param strategy strategy to use
     * @param data data to store in the tree, 
     */
    public static <TData, TBoundary> BspTree<TData, TBoundary> make(
    	IBspStrategy<TData, TBoundary> strategy,
    	TData data 
    ) {
    	BspTree<TData, TBoundary> tree = new BspTree<TData, TBoundary>( strategy );
    	tree.addData( data );
    	return tree;
    }
    
    /** Make empty tree
     * <p>
     * Shortcut to avoid having to specify type parameters.
     * @param strategy splitting strategy to use
     */
    public static <TData, TBoundary> BspTree<TData, TBoundary> make(
    	IBspStrategy<TData, TBoundary> strategy 
    ) {
    	return make( strategy, null );
    }
    
    /** Constructor
     * <p>
     * Creates an empty leaf as root.
     * 
     * @param strategy partitioning strategy to use
     */
    public BspTree(IBspStrategy<TData, TBoundary> strategy) {
        this.strategy = strategy;
        this.root = makeLeafNode();
    }
    
    /** Get constant interface
     */
    public IConstBspTree<TData, TBoundary> asConst() {
        return this;
    }
    
    @Override
    public IBspNode<TData, TBoundary> getRoot() {
        return root;
    }
    
    /** Set the root node
     * <p>
     * Low-level API.
     */
    public void setRoot(IBspNode<TData, TBoundary> value) {
    	assert( value.getTree() == this );
        root = value;
        value.setParent( null );
        setDepthRecursively( root, 0 );
    }
    
    @Override
    public IBspStrategy<TData, TBoundary> getStrategy() {
        return strategy;
    }
    
    /** Add data to the tree
     * <p>
     * May cause tree optimization - splitting of nodes.
     * 
     * @param data data to add, may be null, which would result in no op
     */
    public void addData(TData data) {
    	addData( root, data );
    }
    
    /** Add data to a particular node
     * <p>
     * Low-level API.
     * <p>
     * Caution: If the data spans multiple nodes, it must be added to all such nodes otherwise tree will become corrupted.
     * May cause tree optimization - splitting of nodes.
     * 
     * @param node node to which data should be added
     * @param data data to add, may be null, which would result in no op
     */
    public void addData( IBspNode<TData, TBoundary> node, TData data ) {
    	if ( data == null ) {
    		return;
    	}
    	
    	if ( node.isLeaf() ) {
    		BspLeafNode<TData, TBoundary> leafNode = node.asLeaf();
    		TData joinedData = strategy.joinData( leafNode.getData(), data );
    		leafNode.setData( joinedData );
    		optimize(leafNode);
    	} else {
    		BspInternalNode<TData, TBoundary> internalNode = node.asInternal();
    		SplitData<TData> splitData = strategy.splitData( internalNode.getBoundary(), data );
   			addData( internalNode.getPositiveChild(), splitData.positiveData );
   			addData( internalNode.getNegativeChild(), splitData.negativeData );
    	}
    }
    
    /** Remove data from the tree
     * <p>
     * @param dataToRemove data to remove
     */
    public void removeData( TData dataToRemove ) {
    	removeData( root, dataToRemove );
    }
    
    /** Remove data from a particular node
     * <p>
     * Low-level API.
     * <p>
     * Caution: If the tree contains a portion of the data in multiple nodes,
     * the data must be removed from all such nodes otherwise the tree will become corrupted.
     * 
     * @param node node to remove data from
     * @param dataToRemove data to remove
     */
    public void removeData( IBspNode<TData, TBoundary> node, TData dataToRemove ) {
    	if ( dataToRemove == null ) {
    		return;
    	}
    	
    	if ( node.isLeaf() ) {
    		BspLeafNode<TData, TBoundary> leafNode = node.asLeaf();
    		TData prunedData = strategy.removeData( leafNode.getData(), dataToRemove );
    		leafNode.setData( prunedData );
    	} else {
    		BspInternalNode<TData, TBoundary> internalNode = node.asInternal();
    		SplitData<TData> splitDataToRemove = strategy.splitData( internalNode.getBoundary(), dataToRemove );
   			removeData( internalNode.getPositiveChild(), splitDataToRemove.positiveData );
   			removeData( internalNode.getNegativeChild(), splitDataToRemove.negativeData );
    	}
    }
    
    /** Optimize leaf node
     * <p>
     * Recursively splits data as determined by partitioning strategy to optimize tree performance.
     * 
     * @param nodeToOptimize optimized node
     */
    public void optimize(BspLeafNode<TData, TBoundary> nodeToOptimize) {
        
    	TBoundary boundary = null;
        
        if (strategy.shouldSplit(nodeToOptimize)) {
            boundary = strategy.findBoundary(nodeToOptimize);
        }
        
        if (boundary == null) {
            return; // don't want to or can't find suitable boundary, we are done
        }
        
        SplitData<TData> splitData = strategy.splitData( boundary, nodeToOptimize.getData() );
        
        BspInternalNode<TData, TBoundary> splitNode = makeInternalNode();
        splitNode.setBoundary( boundary );
                        
        BspLeafNode<TData, TBoundary> negativeChild = makeLeafNode();
        splitNode.setNegativeChild( negativeChild );
        negativeChild.setParent( splitNode );
        negativeChild.setData( splitData.negativeData );
        negativeChild.setDepth( splitNode.getDepth()+1 );
        
        BspLeafNode<TData, TBoundary> positiveChild = makeLeafNode();
        splitNode.setPositiveChild( positiveChild );
        positiveChild.setParent( splitNode );
        positiveChild.setData( splitData.positiveData );
        positiveChild.setDepth( splitNode.getDepth()+1 );
        
        replace( nodeToOptimize, splitNode );
        
        optimize( positiveChild );
        optimize( negativeChild );    
    }
    
    /** Make leaf node
     * <p>
     * Shortcut to avoid having to specify type parameters.
     */
    public BspLeafNode<TData, TBoundary> makeLeafNode() {
        return new BspLeafNode<TData, TBoundary>(this);
    }
    
    /** Make internal node
     * <p>
     * Shortcut to avoid having to specify type parameters.
     */
    public BspInternalNode<TData, TBoundary> makeInternalNode() {
        return new BspInternalNode<TData, TBoundary>(this);
    }
    
    /** Replace old node in the tree by a new node
     * <p>
     * Low-level API.
     * @param oldNode node to replace, supports both root and internal nodes and both parent and leaf nodes
     * @param newNode new node to take place of the old node
     */
    public void replace( IBspNode<TData, TBoundary> oldNode, IBspNode<TData, TBoundary> newNode ) {
    	setDepthRecursively( newNode, oldNode.getDepth() );
    	
        BspInternalNode<TData, TBoundary> parent = oldNode.getParent();
        newNode.setParent( parent );
        
        // update reference from parent/root
        if ( parent != null ) {      	
        	if ( parent.getPositiveChild() == oldNode ) {
        		parent.setPositiveChild( newNode );
        	} else {
        		parent.setNegativeChild( newNode );
        	}
        } else {
        	assert( root == oldNode );
        	root = newNode;
        }
    }

    /** Clear tree
     * <p>
     * Remove all data and nodes and create a new empty leaf root.
     */
	public void clear() {
		setRoot( makeLeafNode() );		
	}
	
	protected void setDepthRecursively( IBspNode<TData, TBoundary> node, int depth ) {
		if ( node == null ) {
			return;
		}
		
        node.setDepth( depth );
        if ( node.isInternal() ) {
        	setDepthRecursively( node.asInternal().getNegativeChild(), depth+1 );
        	setDepthRecursively( node.asInternal().getPositiveChild(), depth+1 );
        }
	}
}
