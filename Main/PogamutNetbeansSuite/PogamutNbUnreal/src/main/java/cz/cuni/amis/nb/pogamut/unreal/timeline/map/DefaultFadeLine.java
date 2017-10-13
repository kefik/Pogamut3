/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cuni.amis.nb.pogamut.unreal.timeline.map;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;

/**
 * FadeLine that is used when there is not fade line.
 * Fadeline is empty and has duration 0.
 * <p>
 * Use this instead of null for non-existent fade line.
 * @author Honza
 */
public class DefaultFadeLine implements IFadeLine {

    @Override
    public Location getPosition(long time) {
        throw new UnsupportedOperationException("No location for time " + time);
    }

    @Override
    public long getDuration() {
        return 0;
    }
}
