package math.bsp;

/** Binary space partition occupation
 * <p>
 * Enumeration that tells what part of space divided by a boundary an entity occupies 
 */
public enum BspOccupation {
    
    NEGATIVE( true, false ), 
    POSITIVE( false, true ),
    BOTH( true, true );
    
    private final boolean intersectsNegative;
    private final boolean intersectsPositive;
    
    private BspOccupation( boolean intersectsNegative, boolean intersectsPositive ) {
        this.intersectsNegative = intersectsNegative;
        this.intersectsPositive = intersectsPositive;
    }
    
    /** Get occupation matching the parameters
     * 
     * @return occupation
     */
    public static BspOccupation get( boolean intersectsNegative, boolean intersectsPositive ) {
        for ( BspOccupation occupation : values() ) {
            boolean isMatching = (
                occupation.intersectsNegative() == intersectsNegative
                &&
                occupation.intersectsPositive() == intersectsPositive 
            );
            
            if ( isMatching ) {
                return occupation;
            }
        }
        
        // were at least one argument true, occupation would be matched above
        throw new IllegalArgumentException("Cannot represent non-occupation.");
    }
    
    /** Tell whether entity intersect negative part 
     */
    public boolean intersectsNegative() {
        return intersectsNegative;
    }

    /** Tell whether entity intersect positive part 
     */
    public boolean intersectsPositive() {
        return intersectsPositive;
    }
}
