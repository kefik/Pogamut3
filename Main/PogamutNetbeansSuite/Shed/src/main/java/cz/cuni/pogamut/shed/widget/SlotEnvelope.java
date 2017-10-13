package cz.cuni.pogamut.shed.widget;

import cz.cuni.amis.pogamut.sposh.elements.CompetenceElement;
import cz.cuni.amis.pogamut.sposh.elements.DriveElement;
import java.awt.Point;
import org.netbeans.api.visual.anchor.Anchor;
import org.netbeans.api.visual.layout.LayoutFactory;
import org.netbeans.api.visual.widget.Widget;

/**
 * Envelope for visual representation element (e.g. {@link DriveElement drive}/{@link CompetenceElement choice})
 * with trigger and reference to some other element. The element itself is in
 * the left top corner, the trigger is shown on the right of element and
 * attachment is under the representation of trigger.
 *
 * <b>NOTE: </b> Layout of the right widget has different gap when trigger is
 * empty (gap is 0) and when trigger has some sense in it (gap in {@link ShedWidgetFactory#VERTICAL_GAP}.
 * The layout gap is initialized in the con structor and updated by the {@link TriggerPresenter}.
 *
 * @author Honza
 */
public class SlotEnvelope extends Widget {

    /**
     * Widget representing the slot element, e.g. competence element. It
     * displayes the name ect.
     */
    protected final ShedWidget slotWidget;
    private final ShedTriggerEnvelope triggerEnvelope;
    private ExpandedActionEnvelope expandedActionEnvelope;
    private final Widget right;
    /**
     * Anchor on the left side of the slot envelope. Used for arrows to connect
     * to parent widgets.
     */
    private final Anchor anchor;

    /**
     * Create slot envelope for some element (e.g. choice or drive).
     *
     * @param scene Scene the envelope belongs to.
     * @param slotWidget The widget representing the element (e.g. drive or
     * choice)
     * @param triggerEnvelope Trigger of element.
     * @param expandedActionEnvelope Expanded action of element.
     */
    public SlotEnvelope(ShedScene scene, ShedWidget slotWidget, ShedTriggerEnvelope triggerEnvelope, final ExpandedActionEnvelope expandedActionEnvelope) {
        super(scene);

        setLayout(LayoutFactory.createHorizontalFlowLayout(LayoutFactory.SerialAlignment.LEFT_TOP, ShedWidgetFactory.HORIZONTAL_GAP));

        right = new Widget(scene);
        if (triggerEnvelope.numberOfChildren() == 0) {
            right.setLayout(LayoutFactory.createVerticalFlowLayout(LayoutFactory.SerialAlignment.LEFT_TOP, 0));
        } else {
            right.setLayout(LayoutFactory.createVerticalFlowLayout(LayoutFactory.SerialAlignment.LEFT_TOP, ShedWidgetFactory.VERTICAL_GAP));
        }

        assert slotWidget != null;
        this.slotWidget = slotWidget;
        addChild(slotWidget);

        assert triggerEnvelope != null;
        this.triggerEnvelope = triggerEnvelope;
        right.addChild(triggerEnvelope);


        assert expandedActionEnvelope != null;
        this.expandedActionEnvelope = expandedActionEnvelope;
        right.addChild(expandedActionEnvelope);

        addChild(right);

        this.anchor = new FixedWidgetAnchor(this, new Point(0, ShedWidget.height / 2), Anchor.Direction.LEFT);
    }

    /**
     * Get widget representing the element. In most cases, it is simply widget
     * with name fo element on it.
     */
    public ShedWidget getSlotWidget() {
        return slotWidget;
    }

    /**
     * Get envelope representing the trigger of element.
     */
    public ShedTriggerEnvelope getTriggerEnvelope() {
        return triggerEnvelope;
    }

    /**
     * Get envelope that contains expanded action of element.
     */
    public ExpandedActionEnvelope getExpandedActionEnvelope() {
        return this.expandedActionEnvelope;
    }

    /**
     * Get anchor on the left side of the envelope.
     */
    public Anchor getAnchor() {
        return this.anchor;
    }
}