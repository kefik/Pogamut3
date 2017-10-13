/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cuni.pogamut.posh;

import cz.cuni.pogamut.posh.view.KidViewElemDesc;
import cz.cuni.pogamut.posh.view.PoshTextView;
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
public class PoshEditorSupport extends DataEditorSupport
        implements OpenCookie, EditorCookie, EditCookie, EditorCookie.Observable {

    CloneableTopComponent tc;

    public PoshEditorSupport(PoshDataObject dataObj) {
        super(dataObj, new PoshEnv(dataObj));
    }

    @Override
    protected CloneableTopComponent createCloneableTopComponent() {
        MultiViewDescription[] descriptions = new MultiViewDescription[]{
            new KidViewElemDesc(this),
            new PoshTextView(this)
        };

        // create TC with MVs
        tc = MultiViewFactory.createCloneableMultiView(descriptions, descriptions[0]);

        tc.setDisplayName(getDataObject().getPrimaryFile().getNameExt());

        return tc;
    }

    private void updateName() {
        
        // TODO: this is weired, somehow NB is creating PoshEditorSupport object twice.
        //       1. is created and closed upon first switch between Graphical -> Text representation of .lap file
        //       2. will remain in memory (won't be closed and will be used from now on)
        //       Somehow during 1. closed operation, we get here when 'tc' is still null :(
        //       NB lookup probably fails :(
        if (tc == null) return;
                
        String htmlName = messageHtmlName();
        tc.setHtmlDisplayName(htmlName);
        String name = messageName();
        tc.setDisplayName(name);
        tc.setName(name);
    }

    @Override
    public boolean notifyModified() {
        // super will basically call env.notifyModified and it will update titles
        boolean retValue = super.notifyModified();

        if (retValue) {
            PoshDataObject obj = (PoshDataObject) getDataObject();
            obj.ic.add(env);

            updateName();
        }

        return retValue;
    }

    public void setModified() throws IOException {
        env.markModified();
    }

    @Override
    protected void notifyUnmodified() {
        super.notifyUnmodified();

        PoshDataObject obj = (PoshDataObject) getDataObject();
        obj.ic.remove(env);
        updateName();
    }

    /**
     * Get message asking if user wants to save data object. Only wrapper to
     * weaken priviledges.
     *
     * @return Message to display to the user, asking if she want to save the
     * data
     */
    @Override
    public String messageSave() {
        return super.messageSave();
    }

    public void onCloseDiscard() {
        reloadDocument().waitFinished();
        notifyClosed();
    }
}
