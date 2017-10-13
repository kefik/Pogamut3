package cz.cuni.amis.pogamut.multi.communication.translator.event;

import cz.cuni.amis.pogamut.multi.communication.worldview.object.ISharedProperty;
import cz.cuni.amis.utils.NullCheck;

/**
 * Interface for results returned by ISharedPropertyUpdatedEvent .
 * @author srlok
 */
public interface ISharedPropertyUpdateResult {
	
	public static enum Result {
		
		CREATED,
		UPDATED,
		SAME,
		DESTROYED;
		
	}
	
	public Result getResult();
	
	public ISharedProperty getProperty();

	public static class SharedPropertyUpdateResult implements ISharedPropertyUpdateResult {

		private Result result;
		private ISharedProperty property;

		public SharedPropertyUpdateResult(Result result, ISharedProperty property) {
			this.result = result;
			NullCheck.check(this.result, "result");
			this.property = property;
			if (result != Result.DESTROYED) {
				NullCheck.check(this.property, "object (result != DESTROYED)");
			}
		}
		
		@Override
		public ISharedProperty getProperty() {
			return property;
		}

		@Override
		public Result getResult() {
			return result;
		}

	}
}
