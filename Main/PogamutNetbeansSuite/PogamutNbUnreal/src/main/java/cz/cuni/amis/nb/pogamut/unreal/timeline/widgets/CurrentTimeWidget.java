package cz.cuni.amis.nb.pogamut.unreal.timeline.widgets;

import cz.cuni.amis.nb.pogamut.unreal.timeline.records.TLDatabase;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Stroke;
import org.netbeans.api.visual.action.ActionFactory;
import org.netbeans.api.visual.action.MoveProvider;
import org.netbeans.api.visual.action.MoveStrategy;
import org.netbeans.api.visual.widget.Scene;
import org.netbeans.api.visual.widget.Widget;

/**
 * Widget used to show the current  time in the {{@link TLScene}}.
 * Basically line from top to the bottom of the scene that can be dragged
 * and by dragging, it changes current time of the {{@link TLdatabase}}
 * @author Honza
 */
public class CurrentTimeWidget extends Widget {
    private TLDatabase db;

    private static final int width = 6;
    private static final int height = 400;
    private static final Stroke lineStroke = new BasicStroke(width);
    /**
     * Adapter that listens for changes on current time and updates position of this slider.
     */
    private final TLDatabase.Adapter currentTimeListener = new TLDatabase.Adapter() {

        /**
         * When current time is changed, change location of this widget.
         * @param changedValue
         */
        @Override
        public void currentTimeChanged(long previousCurrentTime, long currentTime) {
            setLocationAccordingToTime(db.getDeltaTime());
        }
    };

    public CurrentTimeWidget(Scene scene, TLDatabase database) {
        super(scene);

        db = database;
        setLocationAccordingToTime(db.getDeltaTime());
        db.addDBListener(currentTimeListener);

        getActions().addAction(ActionFactory.createMoveAction(new TimeDragStrategy(), new TimeChangeProvider()));
    }

    @Override
    protected Rectangle calculateClientArea() {
        return new Rectangle(-width / 2 - 1, 0, width + 2, height);
    }

    @Override
    protected void paintWidget() {
        Graphics2D g = getGraphics();

        Stroke formerStroke = g.getStroke();

        g.setColor(Color.RED);
        g.setStroke(lineStroke);
        g.drawLine(0, 0, 0, height);

        g.setStroke(formerStroke);
    }

    /**
     * Set location of the widget in the scene according to the time it is supposed
     * to represent.
     * @param ms Time in ms that should be used as clue for location of widget
     */
    private void setLocationAccordingToTime(long ms) {
        int x = (int) (TLWidget.LEFT_MARGIN + ms / TLWidget.zoomFactor);
        setPreferredLocation(new Point(x, 0));
        // This widget now requires revalidation, but scene validation is automatically
        // done at the end of swing event (like mouse move ect), so ask manually.
        getScene().validate();
    }

    private class TimeDragStrategy implements MoveStrategy {

        /**
         *
         * @param widget
         * @param original point was returned from the MoveProvider getOriginalLocation
         * @param suggested
         * @return
         */
        @Override
        public Point locationSuggested(Widget widget, Point original, Point suggested) {
            // Check against boundaries of db time frame
            int x = suggested.x;
            System.out.print("Move strategy " + original + " (" + widget.convertSceneToLocal(original) + ") " + suggested);
            System.out.println(" -> " + x);
            /*            int max = TLWidget.LEFT_MARGIN + (int)(db.getElapsedTime() / TLWidget.zoomFactor);
            if (x > max) {
            x = max;
            }
             */
            Point res = new Point(suggested.x - original.x, 0);
            System.out.println("MoveStrategy result " + res);
            return res;
        }
    }

    private class TimeChangeProvider implements MoveProvider {

        private Point originalLocation;

        @Override
        public void movementStarted(Widget widget) {
            originalLocation = widget.getLocation();
            System.out.println("Original location " + originalLocation);
        }

        @Override
        public void movementFinished(Widget widget) {
        }

        @Override
        public Point getOriginalLocation(Widget arg0) {
            return originalLocation;
        }

        /**
         *
         * @param widget
         * @param location location returned by MoveStrategy
         */
        @Override
        public void setNewLocation(Widget widget, Point location) {
            System.out.println("Set location " + location);

            widget.setPreferredLocation(new Point(originalLocation.x + location.x, originalLocation.y + location.y));
        }
    }
}
