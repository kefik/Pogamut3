package cz.cuni.pogamut.shed.widget;

import java.awt.Point;
import java.awt.Rectangle;
import org.netbeans.api.visual.anchor.Anchor;
import org.netbeans.api.visual.anchor.AnchorShape;
import org.netbeans.api.visual.router.RouterFactory;
import org.netbeans.api.visual.widget.ConnectionWidget;
import org.netbeans.api.visual.widget.Widget;

/**
 * Arrow widget between two elements in the {@link ShedScene}. Use as arrow
 * between two senses, between competence and choice, between AP and its actions
 * and so on.
 *
 * This widget can never have client area with negative location (in the {@link ConnectionWidget},
 * it has greater client area than just the line thanks to anchor shape + small
 * padding).
 *
 * @author Honza Havlicek
 */
public class ArrowWidget extends ConnectionWidget {

    /**
     * Create an arrow between @sourceAnchor and @targetAnchor. Use orthogonal
     * router.
     *
     * @param scene Scene into which the arrow will be added.
     * @param sourceAnchor Starting point of the arrow
     * @param targetAnchor Ending point of arrow
     */
    public ArrowWidget(ShedScene scene, Anchor sourceAnchor, Anchor targetAnchor) {
        super(scene);

        setSourceAnchor(sourceAnchor);
        setTargetAnchor(targetAnchor);
        setRouter(RouterFactory.createOrthogonalSearchRouter());
        setTargetAnchorShape(AnchorShape.TRIANGLE_FILLED);
    }

    @Override
    protected Rectangle calculateClientArea() {
        Rectangle clientArea = super.calculateClientArea();
        if (true) {
            return clientArea;
        }
        Rectangle sceneClientArea = convertLocalToScene(clientArea);
        if (sceneClientArea.x < 0) {
            clientArea.x = convertSceneToLocal(new Point()).x;
        }
        if (sceneClientArea.y < 0) {
            clientArea.y = convertSceneToLocal(new Point()).y;
        }

        return clientArea;
    }

    
    @Override
    public String toString() {
        Widget sourceAnchorWidget = getSourceAnchor().getRelatedWidget();
        Point sourceAnchorSceneLocation = getSourceAnchor().getRelatedSceneLocation();

        Widget targetAnchorWidget = getTargetAnchor().getRelatedWidget();
        Point targetAnchorSceneLocation = getTargetAnchor().getRelatedSceneLocation();

        String res = "Arrow: " + sourceAnchorWidget + " -> " + targetAnchorWidget + 
                "; " + sourceAnchorSceneLocation + " -> " + targetAnchorSceneLocation;
        return res;
    }

}

interface IAnchorProvider {

    /**
     * Get anchor of the widget that is used as a common source anchor for the {@link ArrowWidget},
     * the target anchors will be provided by its children.
     *
     * Related widget must be the widget of the anchor.
     *
     * @return Common source anchor
     */
    Anchor getCommonAnchor();
}

final class FixedWidgetAnchor extends Anchor {

    private final Point localLocation;
    private final Direction direction;

    public FixedWidgetAnchor(Widget widget, Point localLocation, Direction direction) {
        super(widget);
        assert widget != null;
        this.localLocation = new Point(localLocation);
        this.direction = direction;
    }

    @Override
    protected void notifyRevalidate() {
        super.notifyRevalidate();
    }
    
    
    
    @Override
    public Result compute(Entry entry) {
        Widget widget = getRelatedWidget();
        Point sceneLocation = widget.convertLocalToScene(localLocation);

        return new Anchor.Result(new Point(sceneLocation), direction);
    }
}


final class RightWidgetAnchor extends Anchor {

    public RightWidgetAnchor(Widget widget) {
        super(widget);
        assert widget != null;
    }

    
    @Override
    public Result compute(Entry entry) {
        Widget widget = getRelatedWidget();
        int widgetWidth = widget.getClientArea().width;
        Point rightAnchorPoint = new Point(widgetWidth, ShedWidget.height / 2);
        Point sceneLocation = widget.convertLocalToScene(rightAnchorPoint);

        return new Anchor.Result(new Point(sceneLocation), Anchor.Direction.RIGHT);
    }
}