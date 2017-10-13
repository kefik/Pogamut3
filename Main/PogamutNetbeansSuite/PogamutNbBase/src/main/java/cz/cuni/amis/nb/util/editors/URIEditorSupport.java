package cz.cuni.amis.nb.util.editors;

import java.beans.PropertyEditorSupport;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Editor for URIs.
 * @author Ik
 */
public class URIEditorSupport extends PropertyEditorSupport {

    String protocol;

    /**
     * Creates a new instance of InMessageSetEditor
     */
    public URIEditorSupport() {
        protocol = "ut04";
    }

    public URIEditorSupport(String protocol) {
        this.protocol = protocol;
    }

    @Override
    public String getAsText() {
        URI uri = (URI) getValue();
        if (uri != null) {
            return uri.toString();
        } else {
            return "[not set]";
        }
    }

    @Override
    public void setAsText(String s) {
        try {
            if (s.equals("")) {
                setValue(null);
            } else {
                setValue(translateURI(s));
            }
        } catch (URISyntaxException ex) {
            // be silent

            /*
            IllegalArgumentException iae = new IllegalArgumentException("Invalid URI.");
            ErrorManager.getDefault().annotate(iae,
            ErrorManager.ERROR, null,
            "Invalid URI.", null, null);
            throw iae;
             */
        }
    }

    /** Check for protocol in URI, if missing then add ut:// */
    protected URI translateURI(String uriStr) throws URISyntaxException {
        if (uriStr.isEmpty()) {
            return URI.create("");
        }
        Pattern p = Pattern.compile(".+://.*");
        Matcher m = p.matcher(uriStr);
        if (!m.matches()) {
            uriStr = protocol + "://" + uriStr;
        }
        // add port if not specified
        if (!uriStr.matches(".+://.+:[0-9]+")) {
            uriStr += ":3001";
        }
        return new URI(uriStr);
    }

    @Override
    public boolean supportsCustomEditor() {
        return false;
    }
}
