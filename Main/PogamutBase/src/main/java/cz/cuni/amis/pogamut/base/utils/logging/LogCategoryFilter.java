package cz.cuni.amis.pogamut.base.utils.logging;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Filter;
import java.util.logging.LogRecord;

/**
 * Simple filter for LogCategory - it is instantiated with the instance
 * of the LogCategory (or array of them) which it should accept.
 * 
 * @author Jimmy
 */
public class LogCategoryFilter implements Filter {
	
	private interface Filter {
		public boolean isLoggable(LogRecord record);
	}
	
	private class LogSingleCategoryFilter implements Filter {
		
		public boolean isLoggable(LogRecord record) {
			return record.getParameters() != null && record.getParameters().length > 0 && record.getParameters()[0] == category; 
		}
		
	}
	
	private class LogMultipleCategoriesFilter implements Filter {
		
		public boolean isLoggable(LogRecord record) {
			return record.getParameters() != null && record.getParameters().length > 0 && categories.contains(record.getParameters()[0]); 
		}
		
	}
	
	private LogCategory category = null;
	
	private Filter filter;
	
	private Set<LogCategory> categories = null;
	
	public LogCategoryFilter(LogCategory category) {
		this.category = category;
		filter = new LogSingleCategoryFilter();
	}
	
	public LogCategoryFilter(LogCategory[] categories) {
		this.categories = new HashSet<LogCategory>();
		for (LogCategory category : categories) {
			this.categories.add(category);
		}
		filter = new LogMultipleCategoriesFilter();
	}

	@Override
	public boolean isLoggable(LogRecord record) {
		return filter.isLoggable(record); 
	}
	
	/**
	 * Returns cathegories the filter is looking for (taking / allowing the log record to be published).
	 * <p><p>
	 * This array is copy - altering it won't alter the filter.
	 *  
	 * @return
	 */
	public LogCategory[] getFilterCategories() {
		if (category != null) return new LogCategory[]{category};
		return categories.toArray(new LogCategory[categories.size()]);
	}

}
