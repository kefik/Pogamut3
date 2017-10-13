package cz.cuni.amis.pogamut.base.utils.collections.adapters;

import java.util.HashSet;

import cz.cuni.amis.pogamut.base.communication.worldview.IWorldView;
import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObjectEventListener;
import cz.cuni.amis.pogamut.base3d.worldview.object.IViewable;
import cz.cuni.amis.pogamut.base3d.worldview.object.event.WorldObjectAppearedEvent;
import cz.cuni.amis.pogamut.base3d.worldview.object.event.WorldObjectDisappearedEvent;
import cz.cuni.amis.utils.collections.ObservableSet;

/**
 * Provides set of all visible world objects extending some class as observable set.
 * @author ik
 */
public class WVVisibleObjectsSetAdapter<T extends IViewable> extends ObservableSet<T> {

    IWorldObjectEventListener<T, WorldObjectAppearedEvent<T>> addListener;
    IWorldObjectEventListener<T, WorldObjectDisappearedEvent<T>> remListener;

	public WVVisibleObjectsSetAdapter(Class<T> objectClass, IWorldView worldView) {
        super(new HashSet<T>());
        // register listener
        worldView.addObjectListener(
        	objectClass, 
        	WorldObjectAppearedEvent.class,
        	addListener = new IWorldObjectEventListener<T, WorldObjectAppearedEvent<T>>() {

            @Override
            public void notify(WorldObjectAppearedEvent<T> event) {
                add(event.getObject());
            }
        });

        worldView.addObjectListener(
        	objectClass,
        	WorldObjectDisappearedEvent.class,
        	remListener = new IWorldObjectEventListener<T, WorldObjectDisappearedEvent<T>>() {
	            @Override
	            public void notify(WorldObjectDisappearedEvent<T> event) {
	                remove(event.getObject());
	            }
        	}
        );

        // add the data
        synchronized(worldView.getAll(objectClass)) {
        	addAll(worldView.getAll(objectClass).values());
        }
    }
}
