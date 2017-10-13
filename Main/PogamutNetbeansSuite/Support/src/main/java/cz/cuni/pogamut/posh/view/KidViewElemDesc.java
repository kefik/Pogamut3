package cz.cuni.pogamut.posh.view;

import cz.cuni.pogamut.posh.PoshEditorSupport;
import java.awt.EventQueue;
import java.awt.Image;
import org.netbeans.core.spi.multiview.MultiViewDescription;
import org.netbeans.core.spi.multiview.MultiViewElement;
import org.openide.util.HelpCtx;
import org.openide.windows.TopComponent;

/**
 * Description of MVE for simple representation and editation of POSH plan.
 * This description specifies that switching button will have "Simple view" on it
 * and <tt>KidViewElement</tt> will be view.
 *
 * @author Honza Havlicek
 */
public class KidViewElemDesc implements MultiViewDescription {
    private KidViewElement editor;
    private PoshEditorSupport support;

    public KidViewElemDesc(PoshEditorSupport support) {
        this.support = support;
    }

    @Override
    public String getDisplayName() {
        return "Simple";
    }

    @Override
    public String preferredID() {
        return "KidView";
    }

    @Override
    public int getPersistenceType() {
            return TopComponent.PERSISTENCE_ONLY_OPENED;
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
    public MultiViewElement createElement() {
        return getEditor();
    }

    private KidViewElement getEditor() {
        // So only AWT thread can get it
        assert EventQueue.isDispatchThread();
        if (editor == null) {
            editor = new KidViewElement(support);
        }
        return editor;
    }

}
