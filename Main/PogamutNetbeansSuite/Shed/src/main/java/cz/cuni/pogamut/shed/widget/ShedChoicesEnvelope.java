package cz.cuni.pogamut.shed.widget;

import org.netbeans.api.visual.anchor.Anchor;
import org.netbeans.api.visual.layout.LayoutFactory;

/**
 * This is an envelope for {@link ShedChoiceEnvelope}s, similar to the {@link ShedDrivesEnvelope}
 * and {@link ShedTriggerEnvelope}.
 *
 * @author Honza H
 */
public final class ShedChoicesEnvelope extends AbstractShedEnvelope<SlotEnvelope> implements IAnchorProvider {
    private final Anchor anchor;

    ShedChoicesEnvelope(ShedScene scene, Anchor anchor) {
        super(scene);
        setLayout(LayoutFactory.createVerticalFlowLayout(LayoutFactory.SerialAlignment.LEFT_TOP, ShedWidgetFactory.VERTICAL_GAP));
        this.anchor = anchor;
    }

    @Override
    protected void updateChildrenPositions() {
        // Do nothing, layout engine will take care of that
    }

    @Override
    public Anchor getCommonAnchor() {
        return anchor;
    }
}
