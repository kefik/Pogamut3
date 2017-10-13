package cz.cuni.pogamut.posh.explorer;

import java.util.Collection;

/**
 * Listener for {@link Crawler} and its subclasses.
 * @author Honza
 * @param <T> what kind of data should crawler look for and pass to the listeners.
 */
public interface CrawlerListener<T> {
    /**
     * Crawling has started, the crawler is working.
     * @param crawler crawler that has started executing.
     */
    void started(Crawler<T> crawler);
    /**
     * Crawler has found new data, and they are now passed to the listeners.
     * @param crawler crawler that has crawled new data
     * @param data
     */
    void crawledData(Crawler<T> crawler, Collection<T> data);
    /**
     * Yay, crawler has finished!. No new data will be fed to the listener.
     * @param crawler crawler that has finished.
     * @param error did crawler finsih because of error?
     */
    void finished(Crawler<T> crawler, boolean error);
}
