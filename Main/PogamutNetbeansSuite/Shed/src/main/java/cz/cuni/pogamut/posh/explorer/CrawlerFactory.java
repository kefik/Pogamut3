package cz.cuni.pogamut.posh.explorer;

import cz.cuni.amis.pogamut.sposh.elements.ActionPattern;
import cz.cuni.amis.pogamut.sposh.elements.Competence;
import cz.cuni.amis.pogamut.sposh.elements.PoshPlan;

/**
 * Create various crawlers.
 *
 * @see ClassCrawlerFactory
 * @author Honza
 */
public class CrawlerFactory {
    /**
     * Create crawler that will crawl the plan for its competences.
     * @param plan Plan that will be cralwed for its competences.
     * @return Created crawler
     */
    public static Crawler<Competence> createCompetenceCrawler(PoshPlan plan) {
        return new CompCrawler(plan);
    }

    /**
     * Create crawler that will crawl the plan for its {@link ActionPattern}.
     * @param plan Plan that will be cralwed for its {@link ActionPattern}.
     * @return Created crawler
     */
    public static Crawler<ActionPattern> createAPCrawler(PoshPlan plan) {
        return new APCrawler(plan);
    }
}
