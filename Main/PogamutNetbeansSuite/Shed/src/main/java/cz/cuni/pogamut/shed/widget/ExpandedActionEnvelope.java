package cz.cuni.pogamut.shed.widget;

import cz.cuni.amis.pogamut.sposh.elements.TriggeredAction;
import cz.cuni.pogamut.shed.presenter.IPresenter;
import java.awt.Point;
import java.util.List;
import org.netbeans.api.visual.anchor.Anchor;
import org.netbeans.api.visual.border.BorderFactory;
import org.netbeans.api.visual.widget.Widget;

/**
 * This class is used as a container for expanded {@link TriggeredAction}.
 * Basically it has exactly one child at all times ({@link AttachmentEnvelope})
 * that contains expanded action widgets. This widget is presentable and when
 * the action reference changes, so must the {@link AttachmentEnvelope}.
 *
 * Why not use {@link AttachmentEnvelope} directly? Because widget that switches
 * its content must be presentable and there could be some nasties when
 * replacing old presentable attachment with a new one.
 *
 * @see SlotEnvelope used for CE, drives
 * @see ShedActionsEnvelope used for AP
 *
 * @author Honza Havlicek
 */
public class ExpandedActionEnvelope extends Widget implements IPresentedWidget {

    private final ShedScene scene;
    private IPresenter presenter;
    private AttachmentEnvelope attachmentEnvelope;
    private Anchor anchor;
    
    ExpandedActionEnvelope(ShedScene scene, AttachmentEnvelope attachmentEnvelope) {
        super(scene);
        this.scene = scene;
        assert attachmentEnvelope != null;
        addChild(attachmentEnvelope);
        this.attachmentEnvelope = attachmentEnvelope;
    }

    /**
     * Take the current attachment envelope, remove its branch ({@link ShedScene#removeBranch(org.netbeans.api.visual.widget.Widget) )
     * and add newly passed attachment as content of this envelope.
     * @param newAttachmentEnvelope Replacement for old attachment envelope.
     */
    public void changeAttachmentWidget(AttachmentEnvelope newAttachmentEnvelope) {
        assert getChildren().size() == 1;
        assert getChildren().get(0) instanceof AttachmentEnvelope;
        assert getChildren().get(0) == attachmentEnvelope;
        
        scene.removeBranch(attachmentEnvelope);
        this.attachmentEnvelope = null;
        addChild(newAttachmentEnvelope);
        this.attachmentEnvelope = newAttachmentEnvelope;
    }
    
    @Override
    public IPresenter getPresenter() {
        return this.presenter;
    }

    public void setPresenter(IPresenter newPresenter) {
        this.presenter = newPresenter;
    }

    /**
     * Anchor is fixed at left side, coords [0,{@link ShedWidget#height}].
     */
    public Anchor getAnchor() {
        if (anchor == null) {
            anchor = new FixedWidgetAnchor(this, new Point(0, ShedWidget.height / 2), Anchor.Direction.LEFT);
        }
        return anchor;
    }

}
