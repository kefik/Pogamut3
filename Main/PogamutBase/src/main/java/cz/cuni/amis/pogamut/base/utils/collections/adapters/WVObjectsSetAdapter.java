package cz.cuni.amis.pogamut.base.utils.collections.adapters;

import java.util.HashSet;

import cz.cuni.amis.pogamut.base.communication.worldview.IWorldView;
import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObjectEventListener;
import cz.cuni.amis.pogamut.base.communication.worldview.object.event.WorldObjectDestroyedEvent;
import cz.cuni.amis.pogamut.base.communication.worldview.object.event.WorldObjectFirstEncounteredEvent;
import cz.cuni.amis.pogamut.base3d.worldview.object.IViewable;
import cz.cuni.amis.pogamut.base3d.worldview.object.event.WorldObjectAppearedEvent;
import cz.cuni.amis.pogamut.base3d.worldview.object.event.WorldObjectDisappearedEvent;
import cz.cuni.amis.utils.collections.ObservableSet;

/**
 * Provides set of all existing world objects extending some class as observable set.
 * @author ik
 */
public class WVObjectsSetAdapter<T extends IViewable> extends ObservableSet<T> {

    IWorldObjectEventListener<T, WorldObjectFirstEncounteredEvent<T>> addListener;
    IWorldObjectEventListener<T, WorldObjectDestroyedEvent<T>> remListener;

	public WVObjectsSetAdapter(Class<T> objectClass, IWorldView worldView) {
        super(new HashSet<T>());
        // register listener
        worldView.addObjectListener(
        	objectClass, 
        	WorldObjectFirstEncounteredEvent.class,
        	addListener = new IWorldObjectEventListener<T, WorldObjectFirstEncounteredEvent<T>>() {

            @Override
            public void notify(WorldObjectFirstEncounteredEvent<T> event) {
                add(event.getObject());
            }
        });

        worldView.addObjectListener(
        	objectClass,
        	WorldObjectDestroyedEvent.class,
        	remListener = new IWorldObjectEventListener<T, WorldObjectDestroyedEvent<T>>() {
	            @Override
	            public void notify(WorldObjectDestroyedEvent<T> event) {
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
