package cz.cuni.amis.pogamut.sposh.dbg.lap;

import cz.cuni.amis.pogamut.sposh.elements.INamedElement;
import cz.cuni.amis.pogamut.sposh.elements.LapPath;
import cz.cuni.amis.pogamut.sposh.elements.LapPath.Link;
import cz.cuni.amis.pogamut.sposh.elements.LapType;
import cz.cuni.amis.pogamut.sposh.elements.PoshElement;
import cz.cuni.amis.pogamut.sposh.elements.PoshPlan;

/**
 * Representation of yaposh node breakpoint. There are two types of them, single
 * and permanent.
 *
 * What are lap breakpoints? Basically a specification of path along with some
 * extra info. When engine reaches the breakpoint path, it finds out the method
 * of the action there (only leaves can be breakpoints) and a java breakpoint
 * there. After that, depending on its type, it can remove itself from the list
 * of breakpoints (singular) or stay there (permanent).
 *
 * Remember, Lap breakpoints are there to insert java breakpoints at the
 * primitives.
 *
 * NOTE: equals checks only path, not single field.
 *
 * @author Honza
 */
public class LapBreakpoint {

    private final LapPath path;
    private final boolean single;

    public LapBreakpoint(LapPath path, boolean single) {
        this.path = path;
        this.single = single;
    }

    /**
     * Get breakpoint path
     *
     * @return the path
     */
    public LapPath getPath() {
        return path;
    }

    /**
     * Is this a single breakpoint? If not, it is permanent.
     *
     * @return is this a single breakpoint?
     */
    public boolean isSingle() {
        return single;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final LapBreakpoint other = (LapBreakpoint) obj;
        if (this.path != other.path && (this.path == null || !this.path.equals(other.path))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + (this.path != null ? this.path.hashCode() : 0);
        hash = 37 * hash + (this.single ? 1 : 0);
        return hash;
    }

    /**
     * Get name of primitive. This requires last link of breakpoint path to be {@link LapType#ACTION}
     * or {@link LapType#SENSE}.
     */
    public String getPrimitiveName(PoshPlan plan) {
        // due to some Java Generic troubles, we have to get primitive as PoshElement first
        PoshElement primitive = path.traversePath(plan);
        // and HARD-CAST it later to INamedElement
        return ((INamedElement)primitive).getName();
    }

    /**
     * Get type of element where the breakpoint is.
     *
     * @return {@link LapType} of last link in the breakpoint path.
     */
    public LapType getType(PoshPlan plan) {
        PoshElement primitive = path.traversePath(plan);
        return primitive.getType();
    }
}
