package cz.cuni.pogamut.posh.view;

import cz.cuni.pogamut.posh.PoshEditorSupport;
import javax.swing.JComponent;
import javax.swing.JToolBar;
import org.netbeans.core.spi.multiview.CloseOperationState;
import org.netbeans.core.spi.multiview.MultiViewElement;
import org.netbeans.core.spi.multiview.MultiViewElementCallback;
import org.openide.text.CloneableEditor;
import org.openide.util.Lookup;

/**
 * Text editor for posh.
 * Provides token highlighting and normal stuff expected in NB text
 * editors (undo, lines, find...).
 * 
 * @author Honza
 */
public class PoshTextEditor extends CloneableEditor implements MultiViewElement{
    private JToolBar toolbar;
    private MultiViewElementCallback callback;

    public PoshTextEditor(PoshEditorSupport editorSupport) {
        super(editorSupport);
    }

    @Override
    public JComponent getVisualRepresentation() {
        return this;
    }

    @Override
    public JComponent getToolbarRepresentation() {
        if (toolbar == null) {
            toolbar = new JToolBar();
        }
        return toolbar;
    }

    @Override
    public Lookup getLookup() {
        return ((PoshEditorSupport)cloneableEditorSupport()).getDataObject().getNodeDelegate().getLookup();
    }

    @Override
    public void componentOpened() {
        super.componentOpened();
    }

    @Override
    public void componentClosed() {
        super.componentClosed();
    }

    @Override
    public void componentShowing() {
        super.componentShowing();
    }

    @Override
    public void componentHidden() {
        super.componentHidden();
    }

    @Override
    public void componentActivated() {
        super.componentActivated();
    }

    @Override
    public void componentDeactivated() {
        super.componentDeactivated();
    }

    @Override
    public void setMultiViewCallback(MultiViewElementCallback mvec) {
        this.callback = mvec;
    }

    @Override
    public CloseOperationState canCloseElement() {
        // TODO: maybe use superclass method?
        return CloseOperationState.STATE_OK;
    }

}
