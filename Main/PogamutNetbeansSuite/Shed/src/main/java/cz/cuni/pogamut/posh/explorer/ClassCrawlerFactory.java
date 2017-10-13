package cz.cuni.pogamut.posh.explorer;

import cz.cuni.amis.pogamut.sposh.executor.IAction;
import java.util.Set;
import org.netbeans.api.project.Project;

/**
 * Factory for creating the {@link ClassCrawler}s for various types we require.
 * @see CrawlerFactory
 * @author Honza
 */
public class ClassCrawlerFactory {
    // XXX: Unify with CrawlerFactory
    
    /**
     * Create crawler for all classes in the @projects that implement {@link IAction}.
     */
    public static ClassCrawler createActionCrawler(Set<Project> projects) {
        return new IActionCrawler(projects);
    }
    /**
     * Create crawler for all classes in the @projects that implement {@link ISense}.
     */
    public static ClassCrawler createSenseCrawler(Set<Project> projects) {
        return new ISenseCrawler(projects);
    }
}
