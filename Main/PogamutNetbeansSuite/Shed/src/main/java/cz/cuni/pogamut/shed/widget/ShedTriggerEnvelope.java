package cz.cuni.pogamut.shed.widget;

import cz.cuni.amis.pogamut.sposh.elements.Sense;
import cz.cuni.amis.pogamut.sposh.elements.Trigger;
import java.awt.Point;
import org.netbeans.api.visual.anchor.Anchor;

/**
 * This envelope is containing the {@link Trigger trigger} {@link Sense senses}.
 * It can add/move/delete {@link ShedSenseWidget}.
 * <p/>
 * The origin of the envelope ([0,0]) is at left top corner, its height is {@link ShedWidget#height}
 * and its width is width of its children + {@link ShedWidgetFactory#HORIZONTAL_GAP}
 * between each widget.
 *
 * @author HonzaH
 */
public class ShedTriggerEnvelope extends AbstractShedEnvelope<ShedSenseWidget> implements IAnchorProvider {

    /**
     * Anchor used as source for connections between the widgets.
     */
    private final Anchor anchor;

    /**
     * Create envelope for the trigger senses. The @anchor is can used as source
     * anchor for {@link ArrowWidget}s, through {@link #getCommonAnchor() }.
     *
     * @param scene
     * @param anchor
     */
    public ShedTriggerEnvelope(ShedScene scene, Anchor anchor) {
        super(scene);
        this.anchor = anchor;
    }

    @Override
    public void updateChildrenPositions() {
        int currentX = 0;
        int currentY = 0;

        for (int childIndex = 0; childIndex < numberOfChildren(); ++childIndex) {
            ShedSenseWidget child = getChild(childIndex);
            child.setPreferredLocation(new Point(currentX, currentY));
            currentX += ShedWidget.width + ShedWidgetFactory.HORIZONTAL_GAP;
        }
    }

    @Override
    public Anchor getCommonAnchor() {
        return anchor;
    }
}
