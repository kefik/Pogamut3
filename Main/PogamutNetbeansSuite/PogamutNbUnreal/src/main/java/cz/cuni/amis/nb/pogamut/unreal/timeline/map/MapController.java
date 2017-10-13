package cz.cuni.amis.nb.pogamut.unreal.timeline.map;

import cz.cuni.amis.nb.pogamut.unreal.map.MapGLPanel;
import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import java.awt.AWTException;
import java.awt.Point;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import javax.swing.event.MouseInputAdapter;
import javax.vecmath.Vector3d;
import org.openide.util.Exceptions;

/**
 * Class for controling viewport in map.
 * <p>
 * The view of map is specified in {@link MapViewpoint}, it consists from
 * <ul>
 *  <li>center point - the point in worldspace at which the eye is looking at</li>
 *  <li>eye point - the worldspace point from which is player looking at the center point</li>
 *  <li>up vector - determines which direction is up, in worldspace</li>
 * </ul>
 * These three components are always orthogonal (the angle between them is 90 degrees).
 * <p>
 * The user is controlling the view by dragging the mouse while pressing the buttons:
 * <ul>
 *  <li>Left button - deltaX will move your view angle to left or right,
 *      deltaY will move you forward or backward (no change in height)</li>
 *  <li>Right button - freely change the direction at which you are looking. </li>
 *  <li>Left and right button - change position while maintaining the view vector.
 *      deltaX - left or right, deltaY up or down.</li>
 *  <li>Triple click anywhere will turn your view to the origin (0,0,0), where map usually is</li>
 * </ul>
 * X axis is green,
 * Y axis is red
 * Z axis is up/down (up direction is negative)
 *
 * @author Honza
 */
public class MapController extends MouseInputAdapter implements MouseListener, MouseMotionListener {

    final private MapGLPanel panel;
    final private MapViewpoint viewpoint;
    private Point last;

    private Location center;

    /**
     * Robot for keeping cursor at one place during dragging (otherwise drag is limited by screen space)
     */
    java.awt.Robot robot;

    public MapController(MapGLPanel panel, MapViewpoint mapViewpoint, Location center) {
        this.panel = panel;
        this.viewpoint = mapViewpoint;
        this.center = new Location(center);
    }

    public void registerListeners() {
        panel.addMouseListener(this);
        panel.addMouseMotionListener(this);
        panel.addMouseWheelListener(this);
    }

    public void unregisterListeners() {
        panel.removeMouseListener(this);
        panel.removeMouseMotionListener(this);
        panel.removeMouseWheelListener(this);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 3) {
            viewpoint.lookAt(center, 2, false);
        }
    }

    

    @Override
    public void mousePressed(MouseEvent e) {
        updateMode(e);
        last = e.getLocationOnScreen();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        updateMode(e);
        last = e.getLocationOnScreen();
    }

    private ModificationStatus updateMode(MouseEvent e) {
        boolean leftDown = (e.getModifiersEx() & InputEvent.BUTTON1_DOWN_MASK) == InputEvent.BUTTON1_DOWN_MASK;
        boolean rightDown = (e.getModifiersEx() & InputEvent.BUTTON3_DOWN_MASK) == InputEvent.BUTTON3_DOWN_MASK;

        if (leftDown && rightDown) {
            operationMode = ModificationStatus.MOVE_CENTER;
        } else if (leftDown && !rightDown) {
            operationMode = ModificationStatus.MOVE_AHEAD;
        } else if (!leftDown && rightDown) {
            operationMode = ModificationStatus.ROTATE_EYE;
        } else {
            operationMode = ModificationStatus.NONE;
        }
        System.out.println("ModificationMode: " + operationMode);
        return operationMode;
    }

    private void changeViewpoint(double deltaX, double deltaY) {
        // how many pixels are necessary to move one degree
        double pixelsPerDegree = 5;
        // how many ut units is one pixel
        double unitsPerPixel = 10;
        switch (operationMode) {
            case ROTATE_EYE:
                rotateEye((deltaX) / pixelsPerDegree, (deltaY) / pixelsPerDegree);
                break;
            case MOVE_CENTER:
                moveCenter(deltaX * unitsPerPixel, (deltaY) * unitsPerPixel);
                break;
            case MOVE_AHEAD:
                moveAhead(deltaY * unitsPerPixel, deltaX / pixelsPerDegree);
                break;
        }
    }

    private void moveAhead(double distance, double angle) {
        Location forward = viewpoint.getEye2Center().getNormalized().scale(-distance).setZ(0);
        viewpoint.move(forward);
        viewpoint.rotateCenter(new Location(0, 0, 1), angle);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        Point current = e.getLocationOnScreen();

        int deltaX = current.x - last.x;
        int deltaY = current.y - last.y;

        changeViewpoint(deltaX, deltaY);

        unregisterListeners();
        //robot.mouseMove(last.x, last.y);
        registerListeners();

        last = current;
    }

    /**
     * Rotate eye according to the delta move by mouse or other pointer.
     * <p>
     * @param leftRightAngle how many degrees should I turn to left or right
     * @param upDownAngle how many degrees should I move my "viewangle" up or down
     */
    private void rotateEye(double leftRightAngle, double upDownAngle) {
        // rotate up/down
        viewpoint.rotateCenter(viewpoint.getRightVector(), upDownAngle);

        // rotate left/right
        viewpoint.rotateCenter(new Location(0, 0, 1), leftRightAngle);
    }

    private void moveCenter(double deltaX, double deltaY) {
        // horizontal movement
        Location right = viewpoint.getRightVector().getNormalized().scale(-deltaX);
        viewpoint.move(right);

        // vertical movement
        Location up = new Location(0, 0, deltaY);
        System.out.println("MoveCenter up " + up);
        viewpoint.move(up);
    }

    private enum ModificationStatus {

        NONE,
        /** look around, only 180 degrees */
        ROTATE_EYE,
        /** move center up/down and left/right     */
        MOVE_CENTER,
        /** move ahead and turn */
        MOVE_AHEAD,
    }
    private ModificationStatus operationMode = ModificationStatus.NONE;
}
