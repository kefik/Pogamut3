package cz.cuni.amis.pogamut.base.communication.translator.event;

import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObject;
import cz.cuni.amis.utils.NullCheck;

/**
 * A generic interface for updateResult returned by all IWorldObjectUpdated events
 * (ie. ICompositeWorldObjectUpdatedEvent...) .
 * 
 * @author Jimmy
 * @author srlok
 *
 * @param <OBJECT> Type of World object to be returned (must extend IWorldObject)
 */
public interface IWorldObjectUpdateResult<OBJECT extends IWorldObject> {

	/**
	 * Different result types for the updatedEvent.
	 * {CREATED,UPDATED,SAME,DESTROYED}
	 * 
	 * @author srlok
	 */
	public static enum Result 
	{
		CREATED,
		UPDATED,
		SAME,
		DESTROYED;	
	}

	/**
	 * Returns result type.
	 * @return
	 */
	public Result getResult();
	
	/**
	 * Returns the updated object.
	 * @return
	 */
	public OBJECT getObject();
	
	/**
	 * Implementation of the IGenericObjectUpdateResult interface.
	 * @author srlok
	 *
	 * @param <OBJECT> must extend IWorldObject .
	 */
	public static class WorldObjectUpdateResult<OBJECT extends IWorldObject>
	implements IWorldObjectUpdateResult<OBJECT>
	{

		private Result result;
		private OBJECT object;

		public WorldObjectUpdateResult(Result result, OBJECT object) {
			this.result = result;
			NullCheck.check(this.result, "result");
			this.object = object;
			if (result != Result.DESTROYED) {
				NullCheck.check(this.object, "object (result != DESTROYED)");
			}
		}
		
		@Override
		public OBJECT getObject() {
			return object;
		}

		@Override
		public Result getResult() {
			return result;
		}

	}
}

