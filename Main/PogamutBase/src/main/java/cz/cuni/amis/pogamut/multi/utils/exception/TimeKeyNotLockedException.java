package cz.cuni.amis.pogamut.multi.utils.exception;

/**
 * This exception is thrown when an object is requested from any WorldView under an unlockedTimeKey == (!worldView.isLocked(timeKey)).
 * Using an object returned by this call may result in nondeterministic behavior. Use at your own risk.
 * @author srlok
 *
 */
public class TimeKeyNotLockedException extends Exception {

	private String msg;
	
	@Override
	public String toString()
	{
		return "TimeKeyNotLockedException [ " + msg + " ] ";
	}
	
	public TimeKeyNotLockedException(String message) {
		msg = message;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
