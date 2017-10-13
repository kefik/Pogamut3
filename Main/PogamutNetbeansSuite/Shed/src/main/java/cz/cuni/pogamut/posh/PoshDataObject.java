package cz.cuni.pogamut.posh;

import cz.cuni.amis.pogamut.sposh.elements.ParseException;
import cz.cuni.amis.pogamut.sposh.elements.PoshParser;
import cz.cuni.amis.pogamut.sposh.elements.PoshPlan;
import cz.cuni.amis.pogamut.sposh.exceptions.FubarException;
import java.io.IOException;
import java.io.StringReader;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;
import org.openide.cookies.SaveCookie;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataNode;
import org.openide.loaders.DataObjectExistsException;
import org.openide.loaders.MultiDataObject;
import org.openide.loaders.MultiFileLoader;
import org.openide.nodes.Node;
import org.openide.nodes.Children;
import org.openide.text.DataEditorSupport;
import org.openide.text.NbDocument;
import org.openide.util.Lookup;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;

/**
 * Representation of Yaposh data object. It provides various stuff, like icon
 * and actions that can be performed on the data object (save).
 *
 * @author Honza
 */
public class PoshDataObject extends MultiDataObject implements Lookup.Provider {

    final InstanceContent ic;
    private AbstractLookup lookup;
    private final PoshEditorSupport support;
    private final SaveCookie saveCookie = new SaveCookie() {

        @Override
        public void save() throws java.io.IOException {
            getEditorSupport().saveDocument();
        }
    };

    public PoshDataObject(FileObject pf, MultiFileLoader loader) throws DataObjectExistsException, IOException {
        super(pf, loader);

        ic = new InstanceContent();
        lookup = new AbstractLookup(ic);
        ic.add(support = new PoshEditorSupport(this));
        ic.add(this);

    }

    @Override
    protected Node createNodeDelegate() {
        DataNode dn = new DataNode(this, Children.LEAF, getLookup());
        dn.setIconBaseWithExtension("cz/cuni/pogamut/posh/POSH_icon.png");
        return dn;
    }

    @Override
    public <T extends Node.Cookie> T getCookie(Class<T> type) {
        return lookup.lookup(type);
    }

    @Override
    public Lookup getLookup() {
        return lookup;
    }

    /**
     * Get editor support for this data object
     *
     * @return
     */
    public PoshEditorSupport getEditorSupport() {
        return support;
    }

    /**
     * Adds/removes data object from registry of modified data objects and
     * adds/removes save cookie.
     *
     * @param modif true if data object is modified, false is data object is no
     * longer modified.
     */
    @Override
    public void setModified(boolean modif) {
        super.setModified(modif);
        if (modif) {
            // Add save cookie
            if (getCookie(SaveCookie.class) == null) {
                getCookieSet().add(saveCookie);
            }
        } else {
            // Remove save cookie
            if (saveCookie.equals(getCookie(SaveCookie.class))) {
                getCookieSet().remove(saveCookie);
            }
        }
    }

    /**
     * Take the {@link DataEditorSupport#getDocument() document} holding the lap
     * plan, parse it and return resulting lap tree.
     *
     * @return Current lap tree.
     */
    public PoshPlan parseLapPlan() throws ParseException {
        StyledDocument doc = getEditorSupport().getDocument();
        GetDocumentText getText = new GetDocumentText(doc);
        NbDocument.runAtomic(doc, getText);
        String lapPlan = getText.text;
        PoshParser parser = new PoshParser(new StringReader(lapPlan));
        return parser.parsePlan();
    }

    /**
     * Get text of the document using {@link NbDocument#runAtomic(javax.swing.text.StyledDocument, java.lang.Runnable)
     * }. Why not just doc.getText(0, doc.getLength())? Someone else could
     * change the document between {@link StyledDocument#getLength() } and {@link StyledDocument#getText(int, int)
     * }.
     */
    private static class GetDocumentText implements Runnable {

        /**
         * Document to get the text from
         */
        private StyledDocument doc;
        /**
         * Text of the document or null if unable to get it.
         */
        private String text;

        public GetDocumentText(StyledDocument doc) {
            this.doc = doc;
        }

        @Override
        public void run() {
            try {
                text = doc.getText(0, doc.getLength());
            } catch (BadLocationException ex) {
                throw new FubarException("I should have an exclusive access to doc.", ex);
            }
        }
    }
}
