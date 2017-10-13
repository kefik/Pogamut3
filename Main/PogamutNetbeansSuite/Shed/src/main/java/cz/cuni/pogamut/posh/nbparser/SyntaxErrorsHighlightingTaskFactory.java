package cz.cuni.pogamut.posh.nbparser;

import java.util.Collection;
import java.util.Collections;
import org.netbeans.modules.parsing.api.Snapshot;
import org.netbeans.modules.parsing.spi.SchedulerTask;
import org.netbeans.modules.parsing.spi.TaskFactory;

/**
 * Factory used by <code>layer.xml</code> to create syntax highlighting task for 
 * @author Honza
 */
public class SyntaxErrorsHighlightingTaskFactory extends TaskFactory {

    /**
     * Create task that will highlight the syntax errors.
     */
    @Override
    public Collection<? extends SchedulerTask> create (Snapshot snapshot) {
        return Collections.singleton (new SyntaxErrorsHighlightingTask ());
    }
}
