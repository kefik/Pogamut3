/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cuni.amis.nb.pogamut.unreal.timeline.map;

import cz.cuni.amis.nb.pogamut.unreal.timeline.records.TLEntity;
import cz.cuni.amis.pogamut.base3d.worldview.object.Location;

/**
 * More or less dummy class representing fade line for entity.
 *
 * @author Honza
 */
public class TLFadeLine implements IFadeLine {
    private TLEntity entity;
    private long origin;

    /**
     * Create a new fade line for entity ending at time time.
     * 
     * @param entity entity that this fade line represents
     * @param time time when fade line ended, in ms
     */
    protected TLFadeLine(TLEntity entity, long time) {
        this.entity = entity;
        this.origin = time;
    }

    @Override
    public Location getPosition(long time) {
        return entity.getLocation(origin - time);
    }

    @Override
    public long getDuration() {
        return 5000;
    }
}
