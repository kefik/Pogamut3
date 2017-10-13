package cz.cuni.amis.nb.pogamut.unreal.timeline.widgets;

import cz.cuni.amis.nb.pogamut.unreal.timeline.records.LogEvent;
import cz.cuni.amis.nb.pogamut.unreal.timeline.records.LogEvents;
import cz.cuni.amis.nb.pogamut.unreal.timeline.records.LogMessage;
import cz.cuni.amis.nb.pogamut.unreal.timeline.records.TLLogRecorder;
import cz.cuni.amis.nb.pogamut.unreal.timeline.view.TLTools;
import java.awt.Color;
import java.util.LinkedList;
import javax.swing.SwingUtilities;
import org.netbeans.api.visual.border.Border;
import org.netbeans.api.visual.border.BorderFactory;

/**
 * This widget represents one of logs from the entity in timeline window.
 *
 * It consists from axis that is marking when was timeline first and last aware of
 * agent (=when was agent active, same length as axis of entity).
 *
 * On the axis are squares that represent log messages (one time event in the log).
 * To see text of message, use tooltip.
 *
 * Under axis are boxwidgets representing log events. 
 *
 * @see LogMessageWidget
 * @see LogEventWidget
 *
 * @author Honza
 */
public class LogCategoryWidget extends AxisWidget implements TLLogRecorder.TLLogRecorderListener {

    private static Border border = BorderFactory.createRoundedBorder(0, 0, Color.LIGHT_GRAY, Color.DARK_GRAY);
    /**
     * The source of our info, the dataobject
     */
    private TLLogRecorder logRecorder;
    private LinkedList<LogMessageWidget> logMessageWidgets = new LinkedList<LogMessageWidget>();
    private LinkedList<LogEventWidget> logEventWidgets = new LinkedList<LogEventWidget>();

    protected LogCategoryWidget(TLScene scene, TLLogRecorder logRecorder) {
        super(scene,
                logRecorder.getEntity(),
                logRecorder.getName(),
                5,
                border);

        // be visible only if containing some message or event
        setVisible(false);

        this.logRecorder = logRecorder;
        this.logRecorder.addLogRecordListener(this);

        LogEvents logEvents = logRecorder.getLogEvents();
        for (LogMessage lm : logEvents.getMessages()) {
            this.onNewLogMessage(lm);
        }

        for (LogEvent le : logEvents.getEvents()) {
            this.onNewLogEvent(le);
        }
    }

    /**
     * Update location of axis, name and log messages and events.
     */
    @Override
    public void updateWidget() {
        super.updateWidget();

        for (LogEventWidget logEventWidget : logEventWidgets) {
            logEventWidget.updateLocation();
        }
    }

    @Override
    public void onNewLogEvent(LogEvent newEvent) {
        assert SwingUtilities.isEventDispatchThread();

        final LogEventWidget lew = new LogEventWidget(this, newEvent);
        logEventWidgets.add(lew);

        addChild(lew);
        setVisible(true);
        getScene().validate();
    }

    @Override
    public void onNewLogMessage(LogMessage newMessage) {
        assert SwingUtilities.isEventDispatchThread();

        final LogMessageWidget lmw = new LogMessageWidget(this, newMessage);
        logMessageWidgets.add(lmw);

        addChild(lmw);
        setVisible(true);
        getScene().validate();
    }
}
