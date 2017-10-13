package cz.cuni.amis.nb.pogamut.base.logging;

import cz.cuni.amis.pogamut.base.utils.logging.LogCategory;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Sheet;

/**
 * Node representing single log category.
 * @author ik
 */
public class LogNode extends AbstractNode {

    LogRecordsSource logRecordsSource = null;

    public LogNode(LogCategory category) {
        super(Children.LEAF);
        setName(category.getCategoryName());
        logRecordsSource = new LogProxy(category);
        // TODO implement log viewer
    }

    public LogRecordsSource getLogRecordsSource() {
        return logRecordsSource;
    }

    /**
     * Prefered action is to open the window with log viewer.
     * @return
     */
    @Override
    public Action getPreferredAction() {
        return new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent evt) {
                openWin();
            }
        };
    }

    /**
     * Open window showing this log.
     */
    public LogViewerTopComponent openWin() {
        LogViewerTopComponent win = LogViewerTopComponent.getWin(LogNode.this);
        win.open();
        win.requestActive();
        return win;
    }

    @Override
    protected Sheet createSheet() {
        Sheet sheet = super.createSheet();
        Sheet.Set props = sheet.get(Sheet.PROPERTIES);
        if (props == null) {
            props = Sheet.createPropertiesSet();
            sheet.put(getLogRecordsSource().getPropSet());
        }
        return sheet;
    }
}
