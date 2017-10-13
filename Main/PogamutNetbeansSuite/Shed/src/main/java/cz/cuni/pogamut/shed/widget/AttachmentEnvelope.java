package cz.cuni.pogamut.shed.widget;

import org.netbeans.api.visual.widget.Widget;

/**
 * This is only general container for other widgets. It doesn't have presenter.
 * This widget is used when some node is being referenced, e.g. AP/C/Action.
 *
 * {@link AttachmentEnvelope} is a widget containing expanded action below
 * choice, below drive or the expande action in AP.
 *
 * @author HonzaH
 */
public final class AttachmentEnvelope extends Widget {

    public AttachmentEnvelope(ShedScene scene) {
        super(scene);
    }
}
