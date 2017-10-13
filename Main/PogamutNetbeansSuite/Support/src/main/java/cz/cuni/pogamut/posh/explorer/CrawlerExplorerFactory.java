package cz.cuni.pogamut.posh.explorer;

import cz.cuni.amis.pogamut.sposh.elements.ActionPattern;
import cz.cuni.amis.pogamut.sposh.elements.Competence;
import cz.cuni.amis.pogamut.sposh.elements.PoshPlan;
import javax.swing.JComponent;
import org.netbeans.api.project.Project;

/**
 * Factory for creating the crawler explorers.
 *
 * @author Honza
 */
public final class CrawlerExplorerFactory {

    public static JComponent createActionsExplorer(Project project, CrawlerListener<PrimitiveData>... listeners) {
        ClassCrawler crawler = new IActionCrawler(project);
        Explorer<PrimitiveData> explorer = new ActionExplorer(crawler);

        crawler.addListener(explorer);
        for (CrawlerListener<PrimitiveData> listener : listeners) {
            crawler.addListener(listener);
        }
        crawler.crawl();
        // Note: once crawling is finished, explorer will remove itself as listener of crawler.
        return explorer;
    }

    public static JComponent createSensesExplorer(Project project, CrawlerListener<PrimitiveData>... listeners) {
        ClassCrawler crawler = new ISenseCrawler(project);
        Explorer<PrimitiveData> explorer = new SenseExplorer(crawler);

        crawler.addListener(explorer);
        for (CrawlerListener<PrimitiveData> listener : listeners) {
            crawler.addListener(listener);
        }
        crawler.crawl();
        // Note: once crawling is finished, explorer will remove itself as listener of crawler.
        return explorer;
    }

    public static JComponent createCompetenceExplorer(PoshPlan plan) {
        Crawler<Competence> crawler = new CompCrawler(plan);
        Explorer<Competence> explorer = new CompetenceExplorer(crawler);

        crawler.addListener(explorer);
        crawler.crawl();
        return explorer;
    }

    public static JComponent createAPExplorer(PoshPlan plan) {
        Crawler<ActionPattern> crawler = new APCrawler(plan);
        Explorer<ActionPattern> explorer = new APExplorer(crawler);

        crawler.addListener(explorer);
        crawler.crawl();
        return explorer;
    }
}
