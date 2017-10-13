package cz.cuni.amis.utils;

public class AdvancedEquals {
	
	/**
	 * Returns true if
	 * o1.equals(o2) == true
	 * o1 == null or o2 == null
	 * @param o1
	 * @param o2
	 * @return
	 */
	public static boolean equalsOrNull(Object o1, Object o2)
	{
		if ( o1 == null || o2 == null )
		{
			return true;
		}
		else
		{
			return o1.equals(o2);
		}
	}

}
