package cz.cuni.amis.nb.pogamut.unreal.timeline.widgets;

import cz.cuni.amis.nb.pogamut.unreal.timeline.records.LogEvent;
import cz.cuni.amis.nb.pogamut.unreal.timeline.records.TLEntity;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import org.netbeans.api.visual.border.BorderFactory;
import org.netbeans.api.visual.widget.LabelWidget;

/**
 * Widget representing Log event. Shape of this widget is box and color is same as
 * is color of entity.
 * 
 * LogEvent is a event that took some time from the start to end. There is also
 * LogMessage for instant events.
 *
 * This widget is part of LogCategoryWidget shown in as slot under the category axis.
 *
 * @see LogCategoryWidget
 * @author Honza
 */
public class LogEventWidget extends LabelWidget {

    private LogCategoryWidget logAxisWidget;
    private LogEvent logEvent;
    public static final int height = 14;
    private TLEntity entity;
    private LabelWidget.VerticalAlignment valign = LabelWidget.VerticalAlignment.CENTER;

    public LogEventWidget(LogCategoryWidget logAxisWidget, LogEvent logEvent) {
        //super(logAxisWidget.getScene(), logAxisWidget.getEntity());
        super(logAxisWidget.getScene());

        this.setLabel(logEvent.getMessage());
        this.setFont(getScene().getDefaultFont().deriveFont(height));

        this.logAxisWidget = logAxisWidget;
        this.logEvent = logEvent;

        this.entity = logAxisWidget.getEntity();
        this.setVerticalAlignment(valign);

        this.setToolTipText(logEvent.getMessage());
        this.setBorder(BorderFactory.createRoundedBorder(0, 0, this.entity.getColor(), Color.BLACK));

        this.updateLocation();
    }

    /**
     * Update location and range
     */
    public void updateLocation() {
        // figure out location
        long endTS = Math.min(entity.getEndTime(), logEvent.getEndTS());

        int startOffset = this.getStartOffset() + this.getOffsetFromStart(logEvent.getStartTS());
        int ypos = logEvent.getSlot() * LogEventWidget.height + logAxisWidget.getStripHeight();

        int length = this.getTimeframeLength(logEvent.getStartTS(), endTS);

        Point location = new Point(startOffset, ypos);
        Rectangle rect = new Rectangle(new Dimension(length, height));

//        Point oldLocation = this.getPreferredLocation();
//        Rectangle oldRect = this.getPreferredBounds();

//        if (!location.equals(oldLocation) || ! rect.equals(oldRect))
        {
            this.setPreferredLocation(location);
            this.setPreferredBounds(rect);
        }
    }
    protected final int zoomFactor = 100;
    protected final int LEFT_MARGIN = 80;

    final protected int getStartOffset() {
        long dbStartMilis = entity.getDatabase().getStartTime();
        int delta = (int) (entity.getStartTime() - dbStartMilis) / zoomFactor;

        return delta + LEFT_MARGIN;
    }

    /**
     * Get offset from the start of the entity this widget represents to
     * the passed ms.
     * @param ms
     * @return
     */
    final protected int getOffsetFromStart(long ms) {
        long delta = ms - entity.getStartTime();
        int offset = (int) (delta / zoomFactor);

        return offset;
    }

    final protected int getTimeframeLength(long startTS, long endTS) {
        return getTimeframeLength(endTS - startTS);
    }

    final protected int getTimeframeLength(long delta) {
        return (int) (delta / zoomFactor);
    }
}

