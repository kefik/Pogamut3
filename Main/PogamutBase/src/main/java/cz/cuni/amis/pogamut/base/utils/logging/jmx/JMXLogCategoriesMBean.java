package cz.cuni.amis.pogamut.base.utils.logging.jmx;

import java.util.logging.Level;
import javax.management.ObjectName;

/**
 * MBean for the JMXLogCategories.
 * 
 * @author Jimmy
 */
public interface JMXLogCategoriesMBean {
		
	/**
	 * Returns names of all logging categories.
	 * @return
	 */
	public String[] getCategoryNames();
	
	/**
	 * Returns names of all logging categories alphabetically sorted.
	 * @return
	 */
	public String[] getCategoryNamesSorted();
	
	/**
	 * Sets logging level for all categories.
	 * @param newLevel
	 */
	public void setLevel(Level newLevel);

	/**
	 * Returns jmx name for the specified category name (obtained from getCategoryNames()).
	 * @param categoryName
	 * @return
	 */
	public ObjectName getJMXLogCategoryName(String categoryName);

	/**
	 * Returns actual JMX object name for this object. 
	 * @return
	 */
	public ObjectName getJMXLogCategoriesName();

}
