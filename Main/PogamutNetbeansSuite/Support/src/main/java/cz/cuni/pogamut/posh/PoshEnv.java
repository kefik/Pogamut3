/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cuni.pogamut.posh;

import java.io.IOException;
import org.openide.cookies.SaveCookie;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.text.DataEditorSupport;

/**
 * Environment that connects the data object and the CloneableEditorSupport.
 * 
 * @author Honza
 */
class PoshEnv extends DataEditorSupport.Env implements SaveCookie {

    public PoshEnv(PoshDataObject dataObj) {
        super(dataObj);
    }

    @Override
    protected FileObject getFile() {
        return getDataObject().getPrimaryFile();
    }

    @Override
    protected FileLock takeLock() throws IOException {
        return ((PoshDataObject) getDataObject()).getPrimaryEntry().takeLock();
    }

    @Override
    public void save() throws IOException {
        PoshEditorSupport support = (PoshEditorSupport) this.findCloneableOpenSupport();
        support.saveDocument();
    }
}
