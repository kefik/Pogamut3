package cz.cuni.amis.nb.pogamut.unreal.timeline.widgets;

import cz.cuni.amis.nb.pogamut.unreal.timeline.records.LogMessage;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.util.logging.LogRecord;
import org.netbeans.api.visual.border.BorderFactory;

/**
 *
 * @author Honza
 */
public class LogMessageWidget extends TLWidget {

    private int width = 6;
    private int height = 7;
    private LogMessage logMessage;
    LogCategoryWidget logAxisWidget;

    LogMessageWidget(LogCategoryWidget logAxisWidget, LogMessage logMessage) {
        super(logAxisWidget.getScene(), logAxisWidget.entity);

        this.logAxisWidget = logAxisWidget;
        this.logMessage = logMessage;

        this.setToolTipText(logMessage.getMessage());
        this.setBorder(BorderFactory.createRoundedBorder(0, 0, Color.black, Color.black));
        this.setPreferredSize(new Dimension(width, height));
        this.updateLocation();
    }

    public void updateLocation() {
        Point position = new Point(getStartOffset() + getOffsetFromStart(logMessage.getTime()), logAxisWidget.TOP_MARGIN);
        this.setPosition(position);
    }

    private void setPosition(Point location) {
        int stripHeight = logAxisWidget.getStripHeight();
        Point newLocation = new Point(location.x - width / 2, location.y - (height - stripHeight) / 2);
        this.setPreferredLocation(newLocation);
    }
}
