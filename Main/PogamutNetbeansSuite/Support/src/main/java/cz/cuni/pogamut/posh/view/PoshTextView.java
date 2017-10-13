package cz.cuni.pogamut.posh.view;

import cz.cuni.pogamut.posh.PoshEditorSupport;
import java.awt.EventQueue;
import java.awt.Image;
import org.netbeans.core.spi.multiview.MultiViewDescription;
import org.netbeans.core.spi.multiview.MultiViewElement;
import org.openide.util.HelpCtx;
import org.openide.windows.TopComponent;

/**
 *
 * @author Honza
 */
final public class PoshTextView implements MultiViewDescription {

    private PoshTextEditor editor;
    private PoshEditorSupport support;

    public PoshTextView(PoshEditorSupport ed) {
        this.support = ed;
    }

    @Override
    public int getPersistenceType() {
        return TopComponent.PERSISTENCE_ONLY_OPENED;
    }

    @Override
    public String getDisplayName() {
        return "Text";
    }

    @Override
    public Image getIcon() {
        return null;
    }

    @Override
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    @Override
    public String preferredID() {
        return "text";
    }

    @Override
    public MultiViewElement createElement() {
        return getEd();
    }

    private PoshTextEditor getEd() {
        assert EventQueue.isDispatchThread();
        if (editor == null) {
            editor = new PoshTextEditor(support);
        }
        return editor;
    }
}