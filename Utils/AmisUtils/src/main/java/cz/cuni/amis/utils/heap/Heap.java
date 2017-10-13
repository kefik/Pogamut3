package cz.cuni.amis.utils.heap;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Heap implementation as a {@link Collection} that provides "decreaseKey" operation. In order to do that, we're using
 * a {@link HashMap} that holds references where the node lies within the heap, thus we're requiring that stored NODEs have {@link Object#equals(Object)} and
 * {@link Object#hashCode()} implemented correctly.
 * 
 */
public class Heap<NODE> implements IHeap<NODE> {
	
	/**
	 * First element is always null, nodes are stored beginning with index [1].
	 */
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
	
	public Heap(Comparator<NODE> comp, int capacity){
		initHeap(capacity);	
		cmp = comp;
	}
	
	public Heap(Comparator<NODE> comp){
		initHeap(20);
		cmp = comp;
	}
	
	@Override
	public NODE getMin(){
		return nodes[1]; // capacity is always >= 2
	}
	
	@Override
	public boolean deleteMin(){
		if (items == 0) 
			return false;
		return remove(nodes[1], 1);
	}
	
	@Override
	public boolean decreaseKey(NODE arg0) {
		Integer reference = (Integer)references.get(arg0);
		if (reference == null){
			return add(arg0);
		}
		upNode(reference.intValue());
		return true;
	}
	
	@Override
	public boolean increaseKey(NODE arg0) {
		Integer reference = (Integer)references.get(arg0);
		if (reference == null){
			return add(arg0);
		}
		downNode(reference.intValue());
		return true;
	}
	
	@Override
	public boolean changedKey(NODE arg0) {
		Integer reference = (Integer)references.get(arg0);
		if (reference == null){
			return add(arg0);
		}
		upNode(reference.intValue());
		downNode(reference.intValue());
		return true;
	}

	@Override
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
	
	@Override
	public boolean addAll(NODE[] arg0) {
		boolean ok = true;
		for (int i = 0; i < arg0.length; ++i){
			ok = this.add(arg0[i]) && ok;
		}
		return ok;
	}

	@Override
	public void clear() {
		for (int i = 0; i < count; ++i){
			nodes[i] = null;			
		}
		references.clear();
	}

	@Override
	public boolean contains(Object arg0) {
		return references.containsKey(arg0);		
	}

	@Override
	public boolean containsAll(Collection arg0) {
		for (Object node : arg0){
			if (!this.contains(node)) {
				return false;
			}
		}
		return true;
	}
	
	@Override
	public boolean containsAll(Object[] arg0) {
		for (int i = 0; i < arg0.length; ++i){
			if (!this.contains(arg0[i])) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean isEmpty() {
		return references.isEmpty();
	}

	@Override
	public Iterator<NODE> iterator() {
		return new HeapIterator<NODE>(nodes, items, this);
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

	@Override
	public boolean remove(Object arg0) {
		Integer reference = (Integer)references.get(arg0);
		if (reference == null) 
			return false;
		return remove(reference.intValue());		
	}
	
	@Override
	public boolean removeAll(Collection arg0) {
		Iterator iter = arg0.iterator();
		while (iter.hasNext())
			remove(iter.next());
		return true;
	}

	@Override
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

	@Override
	public int size() {
		return items;
	}
	
	@Override
	public boolean empty() {
		return items == 0;
	}

	@Override
	public Object[] toArray() {
		return references.keySet().toArray();
	}

	@Override
	public Object[] toArray(Object[] arg0) {
		return references.keySet().toArray(arg0);
	}
	
	@Override
	public Set toSet(){
		return new HashSet(references.keySet());
	}

	@Override
	public Comparator<NODE> getComparator() {
		return cmp;
	}
	
}
