package cz.cuni.pogamut.shed.widget;

import org.netbeans.api.visual.layout.LayoutFactory;

/**
 * Envelope containing all view representatin of drives. Since drives are all
 * simple envelopes, just inherit everything and let the {@link LayoutFactory#createVerticalFlowLayout(org.netbeans.api.visual.layout.LayoutFactory.SerialAlignment, int) layout engine}
 * do its work.
 *
 * @author HonzaH
 */
public class ShedDrivesEnvelope extends AbstractShedEnvelope<SlotEnvelope> {

    ShedDrivesEnvelope(ShedScene scene) {
        super(scene);
        setLayout(LayoutFactory.createVerticalFlowLayout(LayoutFactory.SerialAlignment.LEFT_TOP, ShedWidgetFactory.VERTICAL_GAP));
    }

    @Override
    protected void updateChildrenPositions() {
        // Do nothing, layout engine will take care of that
    }
}
