
package cz.cuni.amis.nb.pogamut.unreal.timeline.map;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;


/**
 * Interface for fade line of an agent in the map.
 *
 * @author Honza
 */
public interface IFadeLine {
    /**
     * Get position where was fadeline
     * @param time how long in past do we want to know the position (higher number = longer in past)
     * @return Position of fade line at specified time in past
     */
    public Location getPosition(long time);

    /**
     * Fade line is not of infinite length. How big interval does it represents?
     *
     * @return What time duration does fade line represents, in ms
     */
    public long getDuration();
}
