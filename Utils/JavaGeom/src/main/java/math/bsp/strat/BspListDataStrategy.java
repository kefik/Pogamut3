package math.bsp.strat;

import java.util.ArrayList;
import java.util.HashSet;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import math.bsp.BspOccupation;
import math.bsp.IBspStrategy;
import math.bsp.SplitData;

/** Abstract BSP strategy for list data
 * 
 * @param <TElement> Type of elements stored in data lists.
 * @param <TBoundary> BSP boundary type.
 */
public abstract class BspListDataStrategy<TElement, TBoundary> { 
		
	/** Split data
	 * 
	 * See {@link IBspStrategy#splitData(Object, Object)}
	 */
    public SplitData<ArrayList<TElement>> splitData(TBoundary boundary, ArrayList<TElement> data) {
        ArrayList<TElement> positiveData = Lists.newArrayList();
        ArrayList<TElement> negativeData = Lists.newArrayList();
        
        for (TElement element : data) {
            BspOccupation occupation = determineElementOccupation(boundary, element);
            
            if (occupation.intersectsPositive()) {
                positiveData.add(element);
            }
            
            if (occupation.intersectsNegative()) {
                negativeData.add(element);
            }
        }
        
        if ( positiveData.isEmpty() ) {
        	positiveData = null;
        }
        
        if ( negativeData.isEmpty() ) {
        	negativeData = null;
        } 
        
        return new SplitData<ArrayList<TElement>>( negativeData, positiveData );
    }
    
	/** Join data
	 * 
	 * See {@link IBspStrategy#joinData(Object, Object)}
	 */
    public static <TElement> ArrayList<TElement> joinData_static( ArrayList<TElement> data1, ArrayList<TElement> data2 ) {
    	if ( data1 == null && data2 == null ) {
    		return null;
    	}
    	
    	HashSet<TElement> joined = Sets.newHashSet();
        
        if ( data1 != null ) {
        	joined.addAll(data1);
        }
        
        if ( data2 != null ) {
        	joined.addAll(data2);
        }
        
        return new ArrayList<TElement>( joined );
    }
    
	/** Join data
	 * 
	 * See {@link IBspStrategy#joinData(Object, Object)}
	 */
    public ArrayList<TElement> joinData( ArrayList<TElement> data1, ArrayList<TElement> data2 ) {
    	return joinData_static( data1, data2 );
    }
    
	/** Remove data
	 * 
	 * See {@link IBspStrategy#removeData(Object, Object)}
	 */
    public static <TElement> ArrayList<TElement> removeData_static( ArrayList<TElement> data, ArrayList<TElement> dataToRemove ) {
    	if ( data == null ) {
    		return null;
    	}
    	
    	ArrayList<TElement> pruned = Lists.newArrayList( data );
    	
    	if ( dataToRemove != null ) {
    		pruned.removeAll( dataToRemove );
    	}
    	
    	return pruned;
    }
    
    /** Remove data
	 * 
	 * See {@link IBspStrategy#removeData(Object, Object)}
	 */
    public ArrayList<TElement> removeData( ArrayList<TElement> data, ArrayList<TElement> dataToRemove ) {
    	return removeData_static( data, dataToRemove );
    }
    
    /** Determine occupation of an element
     */
    public abstract BspOccupation determineElementOccupation(TBoundary boundary, TElement element);
}
