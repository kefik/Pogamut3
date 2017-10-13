package cz.cuni.pogamut.posh.explorer;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Abstract crawler supporting listeners.
 *
 * @author Honza
 * @param <T> what is result of crawling
 */
public abstract class Crawler<T> {

    /**
     * Set of listeners for crawler. Please use {@link #listenersUm unmodifiable version}.
     */
    protected final Set<CrawlerListener<T>> listeners = new HashSet<CrawlerListener<T>>();
    /**
     * Unmodifiable wrapper of listeners.
     */
    protected final Set<CrawlerListener<T>> listenersUm = Collections.unmodifiableSet(listeners);
    /**
     * Set of all values found by this crawler.
     */
    protected final Set<T> cache = new HashSet<T>();
    /**
     * Unmodifiable wrapper of cache.
     */
    protected final Set<T> cacheUm = Collections.unmodifiableSet(cache);

    /**
     * Get name of things the crawler is crawling.
     * @return What is being crawled?
     * @deprecated XXX: Not used, remove
     */
    @Deprecated
    public abstract String getName();

    /**
     * Get description of what is being crawled.
     * @return description of primitive
     * @deprecated XXX: Not used, remove
     */
    @Deprecated
    public abstract String getDescription();

    /**
     * Start crawling for the primitives. All you need to do is to call this method,
     * it will notify listeners about progress and in due time it terminates itself
     * (no need for programmer to take care about that).
     * <br/>
     *  <b>DO NOT CALL TWICE, create new crawler in you need to.</b>
     * <br/>
     * Once crawling starts, it will notify listeners about progress(using {@link CrawlerListener).
     * <br/>
     * If you have to stop the crawling prematurely, use {@link Crawler#die() }.
     * @see CrawlerListener
     */
    public abstract void crawl();

    /**
     * Terminate the crawler, once this is called, crawler should die and liseners shouldn't recieve
     * any further messages.
     * This will notify all listeners with {@link CrawlerListener#finished(boolean) }.
     */
    public abstract void die();

    /**
     * Add listener for crawler.
     * @param listener listener to add
     * @return true if listener wans't already in the set of all listeners
     */
    public final boolean addListener(CrawlerListener<T> listener) {
        synchronized (listeners) {
            return listeners.add(listener);
        }
    }

    /**
     * Remove crawler listener from set of listeners.
     * @param listener listener to remove
     * @return true if listener was in set of crawler listeners.
     */
    public final boolean removeListener(CrawlerListener<T> listener) {
        synchronized (listeners) {
            return listeners.remove(listener);
        }
    }

    /**
     * Notify all listeners that crawling has started.
     */
    protected final void notifyStarted() {
        synchronized (listeners) {
            Set<CrawlerListener<T>> listenersCopy = new HashSet<CrawlerListener<T>>(listeners);
            for (CrawlerListener<T> listener : listenersCopy) {
                listener.started(this);
            }
        }
    }

    /**
     * Notify all listeners that we have new data as result of diligent crawling.
     */
    protected final void notifyCrawledData(Collection<T> data) {
        cache.addAll(data);
        synchronized (listeners) {
            // make a copy so listener can remove itself or add a new one
            Set<CrawlerListener<T>> listenersCopy = new HashSet<CrawlerListener<T>>(listeners);
            for (CrawlerListener<T> listener : listenersCopy) {
                listener.crawledData(this, data);
            }
        }
    }

    /**
     * Notify all listeners that crawling has been finished.
     * @param error am I ending due to an error?
     */
    protected final void notifyFinished(boolean error) {
        synchronized (listeners) {
            Set<CrawlerListener<T>> listenersCopy = new HashSet<CrawlerListener<T>>(listeners);
            for (CrawlerListener<T> listener : listenersCopy) {
                listener.finished(this, error);
            }
        }
    }
}
