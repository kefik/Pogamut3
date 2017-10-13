package cz.cuni.pogamut.shed.widget;

import cz.cuni.amis.pogamut.sposh.elements.ActionPattern;
import cz.cuni.amis.pogamut.sposh.elements.TriggeredAction;
import org.netbeans.api.visual.anchor.Anchor;
import org.netbeans.api.visual.layout.LayoutFactory;

/**
 * {@link IShedEnvelope Envelope} for expanded actions of the {@link ActionPattern}.
 * This envelope will contain either attachment of {@link TriggeredAction} or
 * expanded {@link ActionPattern}.
 *
 * XXX: Maybe unify with {@link ShedChoicesEnvelope} and {@link ShedDrivesEnvelope}
 *
 * @author Honza H
 */
public final class ShedActionsEnvelope extends AbstractShedEnvelope<ExpandedActionEnvelope> implements IPresentedWidget {

    private final Anchor anchor;

    /**
     * Create new envelope for expanded actions of AP.
     *
     * @param scene Scene of the envelope
     * @param anchor Anchor of the envelope.
     */
    ShedActionsEnvelope(ShedScene scene, Anchor anchor) {
        super(scene);
        this.anchor = anchor;
        setLayout(LayoutFactory.createVerticalFlowLayout(LayoutFactory.SerialAlignment.LEFT_TOP, ShedWidgetFactory.VERTICAL_GAP));
    }

    /**
     * Do nothing, layout will take care of it.
     */
    @Override
    protected void updateChildrenPositions() {
        // Do nothing, layout engine will take care of that
    }

    /**
     * @return Get anchor to which should all actions in the envelope be
     * connected to.
     */
    public Anchor getSourceAnchor() {
        return anchor;
    }
}
