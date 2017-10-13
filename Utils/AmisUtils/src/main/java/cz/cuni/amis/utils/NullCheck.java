package cz.cuni.amis.utils;

public class NullCheck {
	
	/**
	 * Throws {@link IllegalArgumentException} if obj == null. Used during the construction of the objects.
	 * @param obj
	 * @param name
	 */
	public static void check(Object obj, String name) {
		if (obj == null) throw new IllegalArgumentException("'" + name + "' can't be null");
	}

}
