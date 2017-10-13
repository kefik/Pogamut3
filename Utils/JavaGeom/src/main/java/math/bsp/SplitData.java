package math.bsp;

/** Data spit into positive and negative parts
 * <p>
 * Immutable. Data should not be modified even if TData type allows for modification.
 * @param <TData> Data type
 */
public class SplitData<TData> {
	
	/** Negative part
	 */
    public final TData negativeData;
    
    /** Positive part 
     */
    public final TData positiveData;
    
	public SplitData(TData negativeData, TData positiveData) {
		this.negativeData = negativeData;
		this.positiveData = positiveData;
	}
}