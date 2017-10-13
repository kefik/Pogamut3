package cz.cuni.amis.pogamut.base.utils.logging;

import java.util.Map;
import java.util.logging.Level;


import com.google.inject.ImplementedBy;

@ImplementedBy(LogCategories.class)
public interface ILogCategories {
	
	/**
	 * Whether some category with specified name exists.
	 * @param name
	 * @return
	 */
	public boolean hasCategory(String name);
	
	/**
	 * Returns IMMUTABLE mapping of categories names to instances of those log categories.
	 * <p><p>
	 * It does not contain instance of {@link IAgentLogger}.
	 * 
	 * @return
	 */
	public Map<String, LogCategory> getCategories();
	
	/**
	 * Adds log category from outside of the object.
	 * 
	 * @param name
	 * @param category
	 */
	public void addLogCategory(String name, LogCategory category);
	
	/**
	 * Returns names of all existing log categories.
	 * @return
	 */
	public String[] getCategoryNames();
	
	/**
	 * Returns names of all existing log categories sorted alphabetically.
	 * @return
	 */
	public String[] getCategoryNamesSorted();
	
	/**
	 * Returns existing category by the name or adds new one.
	 * <p><p>
	 * Note that new category doesn't have any handler appended,
	 * you have to create at least one for the category to produce something.
	 * <p>
	 * Example:<p>
	 * <code>
         * LogCategory myCategory = categories.getCategory("my log"); // create new category<br/>
	 * myCategory.newHandler(new LogPublisher.ConsolePublisher()); // add new handler with output to the console
	 * </code>
	 * @param name
	 * @return
	 */
	public LogCategory getCategory(String name);
	
	/**
	 * Set level for all handlers of all categories.
	 * 
	 * @param newLevel
	 */
	public void setLevel(Level newLevel);

}
