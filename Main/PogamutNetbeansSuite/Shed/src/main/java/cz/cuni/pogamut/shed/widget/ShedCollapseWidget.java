package cz.cuni.pogamut.shed.widget;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;

/**
 * {@link ShedWidget} that allows user to display little triangle on the right
 * side. The triangle indicates if it is collapsed or not. It used for drive and
 * choice.
 *
 * @see CollapseAction in {@link ShedWidgetFactory}, when you change the
 * shape/size of collapse widget, collapse action must reflect that.
 *
 * @author Honza
 */
public class ShedCollapseWidget extends ShedWidget {

    public static final int SQUARE_SIZE = 20;
    /**
     * Does triangle point up (expanded) or down (collapsed)
     */
    private boolean collapsed = false;

    public ShedCollapseWidget(ShedScene scene, String displayName, Color color) {
        super(scene, displayName, color);
    }

    private void paintPlus(Graphics2D g, Point center, int thickness) {
        g.fillRect(
                center.x - SQUARE_SIZE / 2,
                center.y - thickness / 2,
                SQUARE_SIZE,
                thickness);
        g.fillRect(
                center.x - thickness / 2,
                center.y - SQUARE_SIZE / 2,
                thickness,
                SQUARE_SIZE);
    }

    private void paintMinus(Graphics2D g, Point center, int thickness) {
        g.fillRect(
                center.x - SQUARE_SIZE / 2,
                center.y - thickness / 2,
                SQUARE_SIZE,
                thickness);
    }

    @Override
    protected void paintWidget() {
        super.paintWidget();
        Graphics2D g = getGraphics();
        Rectangle clientArea = getClientArea();

        Point center = new Point(
                clientArea.x + clientArea.width - ShedWidget.BREAKPOINT_STRIP_WIDTH - SQUARE_SIZE / 2,
                clientArea.y + clientArea.height / 2);

        g.setColor(color.darker());
        if (collapsed) {
            paintPlus(g, center, 6);
        } else {
            paintMinus(g, center, 6);
        }
    }

    public void setCollapsed(boolean newCollapsed) {
        this.collapsed = newCollapsed;
        // request revalidation for repainting only
        revalidate(true);
    }

    public boolean isCollapseArea(Point localPoint) {
        Rectangle clientArea = getClientArea();
        Rectangle collapseArea = new Rectangle(
                clientArea.x + clientArea.width - ShedWidget.BREAKPOINT_STRIP_WIDTH - ShedCollapseWidget.SQUARE_SIZE,
                clientArea.y + (clientArea.height - ShedCollapseWidget.SQUARE_SIZE) / 2,
                ShedCollapseWidget.SQUARE_SIZE,
                ShedCollapseWidget.SQUARE_SIZE);
        return collapseArea.contains(localPoint);
    }
}
