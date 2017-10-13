package cz.cuni.pogamut.posh.explorer;

import cz.cuni.amis.pogamut.sposh.elements.*;
import cz.cuni.amis.pogamut.sposh.executor.IAction;
import cz.cuni.amis.pogamut.sposh.executor.ISense;
import cz.cuni.pogamut.posh.widget.accept.DataNodeExTransferable;
import cz.cuni.pogamut.posh.widget.kidview.SimpleRoleActionWidget;
import cz.cuni.pogamut.posh.widget.kidview.SimpleSenseWidget;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import org.netbeans.api.java.classpath.GlobalPathRegistry;
import org.openide.cookies.EditorCookie;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.util.Exceptions;
import org.openide.util.datatransfer.ExTransferable;

/**
 * {@link Explorer} for primitives, i.e. actions and senses.
 * @author Honza
 */
abstract class Explorers extends Explorer<PrimitiveData> {

    protected Explorers(Crawler<PrimitiveData> crawler) {
        super(crawler);
    }

    @Override
    public boolean filter(String query, boolean caseSensitive, PrimitiveData item) {
        // TODO: Better query analysis.
        if (item.classFQN.contains(query)) {
            return false;
        }
        if (item.name != null && item.name.contains(query)) {
            return false;
        }
        for (String tag : item.tags) {
            if (tag.contains(query)) {
                return false;
            }
        }
        return true;
    }

    @Override
    protected String getRenderedLabel(PrimitiveData item) {
        return (item.name != null ? item.name : item.classFQN.replaceFirst("^.*\\.", "")) + "(" + item.classFQN + ")";
    }

    @Override
    protected String getItemDescription(PrimitiveData item) {
        StringBuilder info = new StringBuilder();
        info.append("<html>Class: ");
        info.append(item.classFQN);
        if (item.name != null) {
            info.append("<br/>Name: ");
            info.append(item.name);
        }
        if (item.tags.length > 0) {
            info.append("<br/>Tags: ");
            for (int tagIndex = 0; tagIndex < item.tags.length; ++tagIndex) {
                info.append(item.tags[tagIndex]);
                if (tagIndex != item.tags.length - 1) {
                    info.append(',');
                    info.append(' ');
                }
            }
        }
        info.append("</html>");
        return info.toString();
    }

    @Override
    protected void displayItem(PrimitiveData item) {
        String javaFilePath = item.classFQN.replace('.', '/') + ".java";
        for (FileObject curRoot : GlobalPathRegistry.getDefault().getSourceRoots()) {
            FileObject fileObject = curRoot.getFileObject(javaFilePath);
            if (fileObject != null) {
                // do something, e.g. openEditor(fileObject, lineNumber);
                DataObject dobj = null;
                try {
                    dobj = DataObject.find(fileObject);
                } catch (DataObjectNotFoundException ex) {
                    Exceptions.printStackTrace(ex);
                }
                if (dobj != null) {
                    EditorCookie ec = (EditorCookie) dobj.getCookie(EditorCookie.class);
                    if (ec != null) {
                        ec.open();
                    }
                }
            }
        }
    }
}

/**
 * Explorer class for actions. This class is a panel that shows actions
 * (public non-abstract classes implementing {@link IAction} interface).
 * @author Honza
 */
class ActionExplorer extends Explorers {

    ActionExplorer(Crawler<PrimitiveData> crawler) {
        super(crawler);
    }

    @Override
    protected String getNewItemLabel() {
        return "New action (drag and drop)";
    }

    @Override
    protected Transferable createItemTransferable(PrimitiveData data) {
        if (data == null) {
            return null;
        }
        String actionName = data.classFQN;
        return new DataNodeExTransferable(new TriggeredAction(actionName));
    }

    @Override
    protected Transferable createNewItemTransferable() {
        return new ExTransferable.Single(TriggeredAction.dataFlavor) {

            @Override
            protected Object getData() {
                String id = PGSupport.getIdentifierFromDialog("Name of new action");
                if (id == null) {
                    return null;
                }
                return new TriggeredAction(id);
            }
        };
    }
}

/**
 * Explorer class for senses. This class is a panel that shows actions
 * (public non-abstract classes implementing {@link ISense} interface).
 * @author Honza
 */
class SenseExplorer extends Explorers {

    SenseExplorer(Crawler<PrimitiveData> crawler) {
        super(crawler);
    }

    @Override
    protected String getNewItemLabel() {
        return "New sense (drag and drop)";
    }

    @Override
    protected Transferable createItemTransferable(PrimitiveData data) {
        if (data == null) {
            return null;
        }
        String senseName = data.classFQN;
        return new DataNodeExTransferable(new Sense(senseName));
    }

    @Override
    protected Transferable createNewItemTransferable() {
        return new ExTransferable.Single(Sense.dataFlavor) {

            @Override
            protected Object getData() {
                String id = PGSupport.getIdentifierFromDialog("Name of sense");
                if (id == null) {
                    return null;
                }
                return new Sense(id);
            }
        };
    }
}

/**
 * Explorer for {@link PoshElement elementes of posh} in the editor. Provides search function,
 * label of the element and transferable for DnD.
 * @author Honza
 * @param <T> Type of element.
 */
abstract class ElementExplorer<T extends NamedLapElement> extends Explorer<T> {

    protected ElementExplorer(Crawler<T> crawler) {
        super(crawler);
    }

    @Override
    protected boolean filter(String query, boolean caseSensitive, T item) {
        if (caseSensitive) {
            return !item.toString().contains(query);
        }
        return !item.toString().toLowerCase().contains(query.toLowerCase());
    }

    @Override
    protected String getRenderedLabel(T item) {
        return item.getName();
    }

    @Override
    protected String getItemDescription(T item) {
        return null;
    }

    @Override
    protected Transferable createItemTransferable(T data) {
        return new DataNodeExTransferable(data);
    }

    @Override
    protected void displayItem(T item) {
        /** Do nothing */
    }
}

class CompetenceExplorer extends ElementExplorer<Competence> {

    CompetenceExplorer(Crawler<Competence> crawler) {
        super(crawler);
    }

    @Override
    protected String getNewItemLabel() {
        return "New competence (drag and drop)";
    }

    @Override
    protected Transferable createNewItemTransferable() {
        return new ExTransferable.Single(Competence.dataFlavor) {

            @Override
            protected Object getData() throws IOException, UnsupportedFlavorException {
                String competenceName = PGSupport.getIdentifierFromDialog("Name of competence");
                if (competenceName == null) {
                    return null;
                }

                String elementName = PGSupport.getIdentifierFromDialog("Name of competence atom");
                if (elementName == null) {
                    return null;
                }
                Competence c = LapElementsFactory.createCompetence(competenceName, elementName);
                
                // XXX:Hack in 3.3.1 for wrong default action
                for (CompetenceElement cel : c.getChildDataNodes()) {
                    cel.getAction().setActionName(SimpleRoleActionWidget.DEFAULT_ACTION);
                    for (Sense celSense : cel.getTriggerSenses()) {
                        celSense.setSenseName(SimpleSenseWidget.DEFAULT_SUCCEED_SENSE);
                    }
                }
                
                return c;
            }
        };
    }
}

class APExplorer extends ElementExplorer<ActionPattern> {

    public APExplorer(Crawler<ActionPattern> crawler) {
        super(crawler);
    }

    @Override
    protected String getNewItemLabel() {
        return "New action pattern (drag and drop)";
    }

    @Override
    protected Transferable createNewItemTransferable() {
        return new ExTransferable.Single(ActionPattern.dataFlavor) {

            @Override
            protected Object getData() throws IOException, UnsupportedFlavorException {
                String apName = PGSupport.getIdentifierFromDialog("Name of new AP");
                if (apName == null) {
                    return null;
                }

                ActionPattern ap = new ActionPattern(apName);

                // XXX: Ugly hack, remove after 3.3.1. Former constructor has inserted wrong default action
                List<PoshElement> initialChildren = new LinkedList<PoshElement>();
                if (!ap.getChildDataNodes().isEmpty()) {
                    initialChildren.addAll(ap.getChildDataNodes());
                }
                ap.addTriggeredAction(new TriggeredAction(SimpleRoleActionWidget.DEFAULT_ACTION));
                for (PoshElement initialChild : initialChildren) {
                    ap.neutralizeChild(initialChild);
                }
                return ap;
            }
        };
    }
}
