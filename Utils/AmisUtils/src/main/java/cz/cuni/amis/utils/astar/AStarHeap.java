package cz.cuni.amis.utils.astar;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * This is Heap used by AStar algorithm.
 * 
 * Note that we assume that inserted Object has correctly implemented hashCode()
 * and equals() function!
 * 
 * <p><p>
 * Use amis-path-finding library instead, see svn://artemis.ms.mff.cuni.cz/pogamut/trunk/project/Utils/AmisPathFinding
 */
@Deprecated
public class AStarHeap<NODE> implements Collection<NODE> {
	
	private NODE[] nodes;
	private int count;
	private int items;
	private HashMap<NODE, Integer> references;
	private Comparator<NODE> cmp;
	
	private void grow(){
		NODE[] tempNodes = (NODE[]) new Object[count*2];
		for (int i = 0; i <= items; ++i){
			tempNodes[i] = nodes[i];
		}
		nodes = tempNodes;
		count = count * 2;
	}
	
	private int left(int reference){
		return 2*reference;
	}
	
	private int right(int reference){
		return 2*reference+1;
	}
	
	private int upRef(int reference){
		return reference / 2;
	}
	
	private NODE getNode(int reference){
		while (reference >= count) 
			grow();		
		return nodes[reference];
	}
	
	private int downNode(int reference){
		NODE currentNode = getNode(reference);
		if (currentNode == null) {
			return reference;
		}
		NODE leftNode = null;
		NODE rightNode = null;
		int way = 0;
	
		while (true){
			way = 0;
			leftNode = getNode(left(reference));
			rightNode = getNode(right(reference));
		
			if ((leftNode == null) && (rightNode == null)){
				references.put(currentNode, reference);
				return reference;
			}
			if (rightNode == null) way = -1;
			else
				if (leftNode  == null) way = 1;
			if (way == 0) way = cmp.compare(leftNode, rightNode);
			
			if (way < 0){
				// we've got to ascend to the left
				if (cmp.compare(currentNode, leftNode) > 0){
					nodes[reference] = leftNode;
					references.put(leftNode, new Integer(reference));
					reference = left(reference);
					nodes[reference] = currentNode;					
				} else {
					references.put(currentNode, reference);				
					return reference;
				}				 
			} else {
				// we've got to ascend to the right
				if (cmp.compare(currentNode, rightNode) > 0){
					nodes[reference] = rightNode;
					references.put(rightNode, new Integer(reference));
					reference = right(reference);
					nodes[reference] = currentNode;					
					
				} else {
					references.put(currentNode, reference);				
					return reference;
				}				 
			}			
		}		
	}
	
	private int upNode(int reference){
		if (reference == 1) 
			return reference;
		NODE currentNode = getNode(reference);
		if (currentNode == null) 
			return reference;
		NODE upNode = null;
		while (reference > 1){
			upNode = getNode(upRef(reference));
			if (cmp.compare(currentNode, upNode) < 0){
				nodes[reference] = upNode;
				references.put(upNode, reference);				
				reference = upRef(reference);
			} else {
				break;
			}
		}
		nodes[reference] = currentNode;
		references.put(currentNode, reference);
		return reference;
	}
	
	private void initHeap(int capacity){
		if (capacity < 2) capacity = 2;
		nodes = (NODE[]) new Object[capacity];
		count = capacity;
		items = 0;
		references = new HashMap(capacity);
	}
	
	public AStarHeap(Comparator<NODE> comp, int capacity){
		initHeap(capacity);	
		cmp = comp;
	}
	
	public AStarHeap(Comparator<NODE> comp){
		initHeap(20);
		cmp = comp;
	}
	
	public NODE getMin(){
		return nodes[1]; // capacity is always >= 1
	}
	
	public boolean deleteMin(){
		if (items == 0) 
			return false;
		return remove(nodes[1], 1);
	}
	
	public boolean decreaseKey(NODE arg0){
		Integer reference = (Integer)references.get(arg0);
		if (reference == null){
			return add(arg0);
		}
		upNode(reference.intValue());
		return true;
	}

	public boolean add(NODE arg0) {
		Integer reference = (Integer)references.get(arg0);
		if (reference == null){
			getNode(items+1); // ensure that capacity is sufficient
			nodes[items+1] = arg0;
			upNode(items+1);
			items = items + 1;
			return true;
		} else {
			int tempRef = upNode(reference.intValue());
			if (tempRef == reference.intValue()) 
				downNode(reference.intValue());
			return true;
		}		
	}

	@Override
	public boolean addAll(Collection arg0) {
		Iterator<NODE> iter = arg0.iterator();
		while (iter.hasNext())
			add(iter.next());		
		return true;
	}
	
	public boolean addAll(NODE[] arg0){
		boolean ok = true;
		for (int i = 0; i < arg0.length; ++i){
			ok = this.add(arg0[i]) && ok;
		}
		return ok;
	}

	public void clear() {
		for (int i = 0; i < count; ++i){
			nodes[i] = null;
			references.clear();
		}
	}

	public boolean contains(Object arg0) {
		return references.containsKey(arg0);		
	}

	public boolean containsAll(Collection arg0) {
		return references.containsKey(arg0);
	}
	
	public boolean containsAll(Object[] arg0) {
		for (int i = 0; i < arg0.length; ++i){
			if (!this.contains(arg0[i])) {
				return false;
			}
		}
		return true;
	}

	public boolean isEmpty() {
		return references.isEmpty();
	}

	public Iterator<NODE> iterator() {
		return new AStarHeapIterator<NODE>(nodes, items, this);
	}
	
	private boolean remove(NODE arg0, int reference){
		references.remove(arg0);
		if (items == 1){
			nodes[1] = null;
			items = 0;
			return true;
		}
		nodes[reference] = nodes[items];
		nodes[items] = null;
		items = items - 1;
		downNode(reference);	
		return true;
	}

	public boolean remove(Object arg0) {
		Integer reference = (Integer)references.get(arg0);
		if (reference == null) 
			return false;
		return remove(reference.intValue());		
	}

	public boolean removeAll(Collection arg0) {
		Iterator iter = arg0.iterator();
		while (iter.hasNext())
			remove(iter.next());
		return true;
	}

	public boolean retainAll(Collection arg0) {
		Iterator iter = references.keySet().iterator();
		Object item; Integer reference;
		while (iter.hasNext()){
			item = iter.next();
			reference = (Integer)references.get(item);
			if (!arg0.contains(item)) remove((NODE) item, reference.intValue());
		}
		return true;
	}

	public int size() {
		return items;
	}
	
	public boolean empty() {
		return items == 0;
	}

	public Object[] toArray() {
		return references.keySet().toArray();
	}

	public Object[] toArray(Object[] arg0) {
		return references.keySet().toArray(arg0);
	}
	
	public Set toSet(){
		return references.keySet();
	}

	
// =======================
// MAIN METHOD - TEST ONLY
// =======================
	
	public static String mainToStr(Integer[] nums){
		if (nums.length == 0) return "";
		String str = nums[0].toString();
		for (int i = 1; i < nums.length; ++i){
			str += ", " + nums[i].toString();
		}
		return str;
	}
	
	public static boolean mainCheck(AStarHeap heap, Integer[] nums){
		System.out.println("Removing and checking " + mainToStr(nums));
		List heapInts = new ArrayList();
		List desiredInts = new ArrayList();
		for (int i = 0; i < nums.length; ++i){
			desiredInts.add(nums[i]);
			heapInts.add(heap.getMin());
			heap.deleteMin();
		}
		if ( heapInts.containsAll(desiredInts) ){
			System.out.println("OK");
			return true;
		} else {
			System.out.println("KO!");
			System.exit(1);
		}
		return false;
	}
	
	public static void mainAdd(AStarHeap heap, Integer[] nums){
		System.out.println("Adding: " + mainToStr(nums));
		for (int i = 0; i < nums.length; ++i){
			heap.add(nums[i]);
		}
	}
	
	public static void main(String[] args){
		/*
		AStarHeap heap = new AStarHeap(new Comparator(){
                                                            public int compare(Object arg0, Object arg1) {
                                                                    return (Integer)arg0 - (Integer)arg1;
                                                            }
                                                       },
                                                       20
                                                      );
		
		mainAdd(  heap, new Integer[]{10, 100, 1, 50, 5});
		mainCheck(heap, new Integer[]{1,5});
		mainCheck(heap, new Integer[]{10,50});
		mainAdd(  heap, new Integer[]{80, 60, 70});
		mainCheck(heap, new Integer[]{60, 70, 80, 100});
		mainAdd(  heap, new Integer[]{5,8,3,7,4,1,9});
		mainCheck(heap, new Integer[]{5,8,3,7,4,1,9});
		mainAdd(  heap, new Integer[]{2,7,3,5,6,4,9,1});
		mainCheck(heap, new Integer[]{1,2,3,4});
		mainAdd(  heap, new Integer[]{20,70,30,50,60,2,3,1,4});
		mainCheck(heap, new Integer[]{20,70,30,50,60,2,3,1,4,5,6,7,9});
		*/
	}
	
}
