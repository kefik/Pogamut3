package cz.cuni.amis.nb.pogamut.unreal.timeline.widgets;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;
import java.util.logging.Logger;
import org.netbeans.api.visual.widget.Scene;
import org.netbeans.api.visual.widget.Widget;

/**
 * This widget shows long axis with time tics in the scene so user has better
 * idea when did something happen.
 * <p>
 * It has space on the left {@link TLWidget.LEFT_MARGIN} pixels long.
 * @author Honza
 */
final public class TimeTicsWidget extends Widget {

    /**
     * Total time this widget should show, in ms
     */
    protected long totalTime = 0;
    /**
     * Height of one tick, in pixels
     */
    private static int majorTickHeight = 10;
    /**
     * Stroke used to draw ticks and line
     */
    private Stroke lineStroke = new BasicStroke(3);

    public TimeTicsWidget(Scene scene) {
        super(scene);
    }

    @Override
    protected Rectangle calculateClientArea() {
        Graphics2D g = getGraphics();

        Rectangle2D r = g.getFontMetrics().getStringBounds("0", g);
        double height = majorTickHeight + 1 + r.getHeight();

        
        return new Rectangle(-TLWidget.LEFT_MARGIN, -5, (int) (totalTime / TLWidget.zoomFactor) + 1,(int) height);
    }

    @Override
    protected void
            paintWidget() {
        int secondsPerTick = 10;
        int secTickHeight = 6;

        Graphics2D g = getGraphics();
        g.setColor(getForeground());

        int ticksNum = (int) (totalTime / (1000 * secondsPerTick));

        Stroke formerStroke = g.getStroke();
        g.setStroke(lineStroke);

        // draw main line
        g.drawLine(0, 0, (int) (totalTime / TLWidget.zoomFactor), 0);

        // draw ticks
        for (int tick = 0; tick <= ticksNum; tick++) {
            int x = tick * secondsPerTick * 1000 / TLWidget.zoomFactor;
            g.drawLine(x, 0, x, majorTickHeight);

            // draw label
            String time = ((tick * secondsPerTick) / 60) + ":" + ((tick * secondsPerTick) % 60);
            //g.drawString(time, x, majorTickHeight);

            Rectangle2D r = g.getFontMetrics().getStringBounds(time, g);
            g.drawString(time, x - (float)r.getWidth() * 0.5f, majorTickHeight + (float)(r.getHeight()));

            // draw minor ticks, per seconds
            int lastSecTick = (int) (totalTime / 1000 < (tick + 1) * secondsPerTick - 1 ? totalTime / 1000 : (tick+1) * secondsPerTick - 1);
            for (int secTick = tick * secondsPerTick + 1; secTick <= lastSecTick; secTick++) {
                int secX = secTick * 1000/TLWidget.zoomFactor;
                g.drawLine(secX, 0, secX, secTickHeight);
            }
        }

        g.setStroke(formerStroke);
    }

    /**
     * Change length of the widget according to time interval.
     * @param newTime total time that this widget will show, in ms
     */
    public void setTime(long newTime) {
        Logger.getLogger("TL").fine("Tick widget changed to " + newTime);
        this.totalTime = newTime;
        this.revalidate();
    }
}
