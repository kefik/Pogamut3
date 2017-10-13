package cz.cuni.amis.pogamut.multi.communication.worldview;

import java.util.Map;

import cz.cuni.amis.pogamut.base.communication.worldview.object.WorldObjectId;
import cz.cuni.amis.pogamut.base3d.worldview.object.IViewable;

/**
 * Interface that adds functionality related to visible objects to the worldView.
 * @author srlok
 *
 */
public interface IVisionLocalWorldView extends ILocalWorldView{

	/**
	 * Returns map of all visible objects ({@link IViewable} instances} - those that the agent can currently see.
	 * <p><p>
	 * <b>WARNING:</b> If you will do iteration over the map, you must synchronize on it.
	 */
	@SuppressWarnings("unchecked")
	public Map<Class, Map<WorldObjectId, IViewable>> getAllVisible();
	
	/**
	 * Returns map of all visible objects ({@link IViewable} instances} - those that the agent can currently see.
 	 * <p><p>
	 * <b>WARNING:</b> If you will do iteration over the map, you must synchronize on it.
	 *
	 * @param type
	 * @return
	 */
	public <T extends IViewable> Map<WorldObjectId, T> getAllVisible(Class<T> type);
	
	/**
	 * Returns map of all visible objects ({@link IViewable} instances} organized according to their {@link WorldObjectId} - 
	 * those that the agent can currently see.
	 * <p><p>
	 * <b>WARNING:</b> If you will do iteration over the map, you must synchronize on it.
	 * @return
	 */
	public Map<WorldObjectId, IViewable> getVisible();

	/**
	 * Returns a visible world object of the specific id (if exists inside the world view and is visible).<p>
	 * Otherwise, null is returned.
	 * 
	 * @param id objects's id
	 * @return
	 */
	public IViewable getVisible(WorldObjectId id);
}
