package cz.cuni.amis.pogamut.base.utils.logging;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.logging.Level;

/**
 * Class that wraps the map with log categories. It allows you
 * to simply create new categories or query it's mapping.
 * <p><p>
 * The only constructor LogCategories(Logger) is protected and is instantiated
 * during the construction of AgentLogger.
 *
 * @author Jimmy
 */
public abstract class AbstractLogCategories implements ILogCategories {

	private Map<String, LogCategory> immutableCategories = null;

	private Comparator<String> stringComparator = new Comparator<String>() {

		@Override
		public int compare(String o1, String o2) {
			if (o1 == null) {
				if (o1 == o2) return 0;
				return -1;
			}
			if (o2 == null) return 1;
			return o1.compareTo(o2);
		}

	};

    /**
     * @return Muttable map with categories.
     */
    protected abstract Map<String, LogCategory> getCategoriesInternal();


	/**
	 * Whether some category with specified name exists.
	 * @param name
	 * @return
	 */
    @Override
	public boolean hasCategory(String name) {
		return Arrays.binarySearch(getCategoryNamesSorted(), name, stringComparator) >= 0;
	}

	/**
	 * Returns IMMUTABLE mapping of cathegories names to instances of those log categories.
	 * <p><p>
	 * You have to synchronize on it before iterating through it!
	 * @return
	 */
    @Override
	public Map<String, LogCategory> getCategories() {
        if(immutableCategories == null) {
            immutableCategories = Collections.unmodifiableMap(getCategoriesInternal());
        }
        return immutableCategories;
	}

    /**
     * Used by {@link AbstractAgentLogger} to slip itself into the map.
     * @param name
     * @param category
     */
    @Override
    public void addLogCategory(String name, LogCategory category) {
    	getCategoriesInternal().put(name, category);
    }

	/**
	 * Returns names of all existing log categories.
	 * @return
	 */
    @Override
	public String[] getCategoryNames() {
		return getCategoriesInternal().keySet().toArray(new String[0]);
	}

	/**
	 * Returns names of all existing log categories sorted alphabetically.
	 * @return
	 */
    @Override
	public String[] getCategoryNamesSorted() {
		String[] names = getCategoryNames();
		Arrays.sort(names, stringComparator);
		return names;
	}

	
	/**
	 * Set level for all handlers of all categories.
	 *
	 * @param newLevel
	 */
    @Override
	public void setLevel(Level newLevel) {
    	synchronized(getCategories()) {
    		for (LogCategory category : getCategories().values()) {
    			category.setLevel(newLevel);    		
    		}
    	}
	}

}