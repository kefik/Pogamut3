package cz.cuni.amis.nb.pogamut.unreal.timeline.widgets;

import cz.cuni.amis.nb.pogamut.unreal.timeline.records.TLEntity;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import org.netbeans.api.visual.border.Border;
import org.netbeans.api.visual.layout.LayoutFactory;
import org.netbeans.api.visual.widget.LabelWidget;
import org.netbeans.api.visual.widget.Scene;

/**
 * Ancestor of axis. It provides label and strip on the right side of label.
 * @author Honza
 */
public abstract class AxisWidget extends TLWidget {

    protected int zoom = 100;
    protected final int TEXT_HEIGHT = 15;
    public final int TOP_MARGIN = 0;
    /**
     * Widget representing entity name
     */
    protected LabelWidget nameWidget;
    protected StripWidget stripWidget;
    private int stripHeight;

    AxisWidget(Scene scene, TLEntity entity, String text, int stripHeight, Border border) {
        super(scene, entity);

        this.setLayout(LayoutFactory.createAbsoluteLayout());

        this.stripHeight = stripHeight;

        this.nameWidget = new LabelWidget(scene, text);
        this.addChild(nameWidget);
        this.revalidate();
        this.updateNameLocation();

        this.stripWidget = new StripWidget(scene, entity, border);
        this.addChild(stripWidget);
        this.revalidate();
        this.updateStripRange();
    }

    public int getStripHeight() {
        return stripHeight;
    }

    protected void updateStripRange() {
        Point location = new Point(getStartOffset(), TOP_MARGIN);
        Dimension dimension = new Dimension(getTimeframeLength(getEntityTimeframe()), getStripHeight());
        this.stripWidget.setPreferredLocation(location);
        this.stripWidget.setPreferredBounds(new Rectangle(dimension));
//		this.axisWidget.setPreferredBounds(new Rectangle(new Point(0,0), dimension));
    }

    public void updateWidget() {
        this.updateStripRange();
        this.updateNameLocation();
    }

    /**
     * todo: make it properly, there is some problem with calculateClientArea
     * Default: location is at baseline
     * Alignment baseline: location is at baseline
     *
     */
    private void updateNameLocation() {

        //this.entityNameWidget.setAlignment(LabelWidget.Alignment.CENTER);
        this.nameWidget.setVerticalAlignment(LabelWidget.VerticalAlignment.CENTER);

        Dimension bounds = new Dimension(80, TEXT_HEIGHT);
        Point p = new Point(
                getStartOffset() - LEFT_MARGIN,// - bounds.width,
                TOP_MARGIN - (TEXT_HEIGHT - getStripHeight()) / 2);

        /*		System.out.println("UpdateNameLocation: " + getEntity().getName() + " " + getEntity());
        System.out.println("point: " + p);
        System.out.println("bounds: " + bounds);
        System.out.println("getStartOffset " + getStartOffset());
         */
        this.nameWidget.setPreferredLocation(p);
        this.nameWidget.setPreferredBounds(new Rectangle(bounds));
    }

    public TLEntity getEntity() {
        return entity;
    }
}
