package cz.cuni.pogamut.posh;

import cz.cuni.pogamut.posh.view.PoshTextView;
import cz.cuni.pogamut.shed.view.LapTreeViewDesc;
import java.io.IOException;
import org.netbeans.core.spi.multiview.MultiViewDescription;
import org.netbeans.core.spi.multiview.MultiViewFactory;
import org.openide.cookies.EditCookie;
import org.openide.cookies.EditorCookie;
import org.openide.cookies.OpenCookie;
import org.openide.text.DataEditorSupport;
import org.openide.windows.CloneableTopComponent;

/**
 * Support for associating an editor and a Swing Document to a data object.
 * 
 * @author Honza
 */
public final class PoshEditorSupport extends DataEditorSupport
        implements OpenCookie, EditorCookie, EditCookie, EditorCookie.Observable {

    public PoshEditorSupport(PoshDataObject dataObj) {
        super(dataObj, new PoshEnv(dataObj));
    }

    @Override
    protected CloneableTopComponent createCloneableTopComponent() {
        MultiViewDescription[] descriptions = new MultiViewDescription[] {
            new LapTreeViewDesc((PoshDataObject)this.getDataObject()),
            new PoshTextView(this)
        };

        // create TC with MVs
        CloneableTopComponent tc = MultiViewFactory.createCloneableMultiView(descriptions, descriptions[0]);

        tc.setDisplayName(getDataObject().getPrimaryFile().getNameExt());

        return tc;
    }

    @Override
    public boolean notifyModified() {
        boolean retValue = super.notifyModified();
        
        if (retValue) {
            PoshDataObject obj = (PoshDataObject)getDataObject();
            obj.ic.add(env);
        }
        return retValue;
    }

    public void setModified() throws IOException {
        env.markModified();
        this.updateTitles();
    }

    @Override
    protected void notifyUnmodified() {
        super.notifyUnmodified();

        PoshDataObject obj = (PoshDataObject)getDataObject();
        obj.ic.remove(env);
    }

    public void onCloseDiscard() {
        // Reload in case some other program has modified it outside of this editor
        reloadDocument().waitFinished();
        // Method that is called when all components of the support are closed. The default implementation closes the document. 
        notifyClosed();
    }
    
    @Override
    public String messageSave() {
        return super.messageSave();
    }
}
