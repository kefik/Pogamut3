package cz.cuni.pogamut.posh.explorer;

import cz.cuni.amis.pogamut.sposh.executor.PrimitiveData;
import cz.cuni.pogamut.shed.presenter.ShedPresenter;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import javax.swing.SwingUtilities;

/**
 * Special cralwer listener that will listen for all collected {@link PrimitiveData}
 * and pass mapping of names to {@link ShedPresenter}.
 *
 * @author Honza
 */
public class NameMapCrawler implements CrawlerListener<PrimitiveData> {

    private final ShedPresenter presenter;
    private Set<PrimitiveData> crawledNames = new HashSet<PrimitiveData>();

    public NameMapCrawler(ShedPresenter presenter) {
        this.presenter = presenter;
    }

    /**
     * When crawling starts, clear previsou name mappings ({@link ShedPresenter#setNameMapping(java.lang.String, cz.cuni.pogamut.posh.explorer.PrimitiveData) ShedPresenter#setNameMapping(fqn, null)})
     */
    @Override
    public void started(Crawler<PrimitiveData> crawler) {
        assert SwingUtilities.isEventDispatchThread();

        // Clear the mapping
        for (PrimitiveData record : crawledNames) {
            String key = record.classFQN;
            presenter.setNameMapping(key, null);
        }
        crawledNames.clear();
    }

    /**
     * When receiving new {@link PrimitiveData}, set name mapping in {@link ShedPresenter},
     * fqn to {@link PrimitiveData}.
     *
     * @see ShedPresenter#setNameMapping(java.lang.String,
     * cz.cuni.pogamut.posh.explorer.PrimitiveData)
     * @param data
     */
    @Override
    public void crawledData(Crawler<PrimitiveData> crawler, Collection<PrimitiveData> data) {
        assert SwingUtilities.isEventDispatchThread();

        crawledNames.addAll(data);

        for (PrimitiveData record : data) {
            String key = record.classFQN;

            presenter.setNameMapping(key, record);
        }
    }

    /**
     * Do nothing.
     */
    @Override
    public void finished(Crawler<PrimitiveData> crawler, boolean error) {
        assert SwingUtilities.isEventDispatchThread();
    }
}
