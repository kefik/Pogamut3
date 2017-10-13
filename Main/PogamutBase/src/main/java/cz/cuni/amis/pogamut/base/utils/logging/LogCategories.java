package cz.cuni.amis.pogamut.base.utils.logging;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.google.inject.Inject;

import cz.cuni.amis.pogamut.base.utils.guice.AgentScoped;

/**
 * Class that wraps the map with log categories. It allows you
 * to simply create new categories or query it's mapping.
 * <p><p>
 * The only constructor LogCategories(Logger) is protected and is instantiated
 * during the construction of {@link AgentLogger}.
 * 
 * @author Jimmy
 */
@AgentScoped
public class LogCategories extends AbstractLogCategories {

    private Map<String, LogCategory> categories = Collections.synchronizedMap(new HashMap<String, LogCategory>());

    @Inject
    public LogCategories() {    	
    }
    
    /**
     * Returns existing category by the name or adds new one.
     * <p><p>
     * Note that new category doesn't have any handler appended,
     * you have to create at least one for the category to produce something.
     * <p>
     * Example:<p>
     * LogCategory myCategory = categories.getCategory("my log");  // creates new category
     * myCategory.newHandler(new LogPublisher.ConsolePublisher()); // adds new handler with output to the console
     *
     * @param name
     * @return
     */
    @Override
    public LogCategory getCategory(String name) {
        LogCategory category = getCategoriesInternal().get(name);
        if (category != null) {
            return category;
        }
        category = new LogCategory(name);
        getCategoriesInternal().put(name, category);
        return category;
    }

    @Override
    protected Map<String, LogCategory> getCategoriesInternal() {
        return categories;
    }
}