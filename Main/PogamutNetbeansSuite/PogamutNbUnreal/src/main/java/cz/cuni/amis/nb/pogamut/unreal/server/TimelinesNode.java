package cz.cuni.amis.nb.pogamut.unreal.server;

import cz.cuni.amis.nb.pogamut.base.NamedAction;
import cz.cuni.amis.nb.pogamut.unreal.timeline.UTTimelineNode;
import cz.cuni.amis.nb.pogamut.unreal.timeline.dataobject.TLDataObject;
import cz.cuni.amis.nb.util.NodeFactory;
import cz.cuni.amis.nb.util.collections.ObservableCollectionNode;
import cz.cuni.amis.pogamut.unreal.server.IUnrealServer;
import cz.cuni.amis.utils.collections.ObservableCollection;
import cz.cuni.amis.utils.exception.PogamutException;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.ResourceBundle;
import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.cookies.OpenCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;

/**
 * Node representing root for all timelines under an Unreal Server node along with players and agents.
 * This is not a node representinch timeline, its children are representations of some timeline.
 *
 * Nodes under this one are UTTimelineNode and are added when observablecollection is changed.
 *
 * @see UTServerNode
 * @see UTTimelineNode 
 * @see AgentsNode
 * @see PlayersNode
 * @author Honza
 */
public class TimelinesNode extends ObservableCollectionNode<TLDataObject> {

    /**
     * List of experiments for this server
     */
    private ObservableCollection<TLDataObject> experiments;
    private IUnrealServer server;

    /**
     * Create a node representing list of experiments conducted on this server.
     * 
     * @param server Reference to the server, used later to create new TimelineNodes
     */
    public TimelinesNode(IUnrealServer server) {
        this(server, new ObservableCollection<TLDataObject>(new LinkedList<TLDataObject>()));

    }

    /**
     * Create a node representing experiments conducted on this server. Passed collection is list
     * of experiments, that were already conducted and will therefore be children of the node.
     * 
     * @param server Reference to the server, used later to create new TimelineNodes
     * @param experiments
     */
    public TimelinesNode(final IUnrealServer server, ObservableCollection<TLDataObject> experiments) {
        super(experiments, new NodeFactory<TLDataObject>() {

            @Override
            public Node[] create(TLDataObject obj) {
                return new Node[]{new UTTimelineNode(obj, server)};
            }
        });
        setName("Timelines");

        this.experiments = experiments;
        this.server = server;
    }

    @Override
    public Image getIcon(int type) {
        return ImageUtilities.loadImage("cz/cuni/amis/nb/pogamut/ut2004/timeline/dataobject/timeline_icon.png");
    }

    @Override
    public Image getOpenedIcon(int type) {
        return getIcon(type);
    }
    protected static ResourceBundle bundle = NbBundle.getBundle(TimelinesNode.class);

    /**
     * Return "Add new timeline to this node" action.
     * 
     * @param context whether to find actions for context meaning or for the node itself 
     * @return
     */
    @Override
    public Action[] getActions(boolean context) {
        return new Action[]{
                    new NamedAction("ACT_AddTimeline") {

                        /**
                         * See http://blogs.kiyut.com/tonny/2007/09/01/netbeans-platform-and-memory-file-system/
                         * for details.
                         */
                        @Override
                        protected void action(ActionEvent e) throws PogamutException {
                            final JFileChooser fileChooser = new JFileChooser();
                            fileChooser.setFileFilter(new FileFilter() {

                                @Override
                                public boolean accept(File f) {
                                    return true;
                                }

                                @Override
                                public String getDescription() {
                                    return "Pogamut timeline (*.ptl)";
                                }
                            });

                            // Show the dialog and get the file
                            int res = fileChooser.showSaveDialog(null);

                            if (res == JFileChooser.ERROR_OPTION) {
                                NotifyDescriptor.Message errorMessage = new NotifyDescriptor.Message(bundle.getString("MSG_UnableToSaveTimeline"), NotifyDescriptor.ERROR_MESSAGE);
                                DialogDisplayer.getDefault().notify(errorMessage);
                                return;
                            }

                            if (res == JFileChooser.APPROVE_OPTION) {
                                try {
                                    // Append extension if necessary
                                    File file = fileChooser.getSelectedFile();
                                    if (!file.getAbsolutePath().endsWith(".ptl")) {
                                        file = new File(file.getAbsolutePath() + ".ptl");
                                    }

                                    // Ask for possible overwrite
                                    if (file.exists()) {
                                        NotifyDescriptor.Confirmation overwriteDialog = new NotifyDescriptor.Confirmation(
                                                bundle.getString("MSG_OverwriteFileStart") + file.getName() + bundle.getString("MSG_OverwriteFileEnd"),
                                                NotifyDescriptor.OK_CANCEL_OPTION,
                                                NotifyDescriptor.QUESTION_MESSAGE);
                                        
                                        if (DialogDisplayer.getDefault().notify(overwriteDialog) != NotifyDescriptor.OK_OPTION) {
                                            return;
                                        }
                                    }


                                    FileObject fo = FileUtil.createData(file);
                                    TLDataObject dataObject = (TLDataObject) DataObject.find(fo);

                                    dataObject.setSourceServer(server);

                                    OpenCookie oc = dataObject.getLookup().lookup(OpenCookie.class);
                                    if (oc != null) {
                                        oc.open();
                                    }
                                    experiments.add(dataObject);
                                } catch (IOException ex) {
                                    NotifyDescriptor.Message errorMessage = new NotifyDescriptor.Message(bundle.getString("MSG_UnableToSaveTimeline") + "\n"+ex.getMessage(), NotifyDescriptor.ERROR_MESSAGE);
                                    DialogDisplayer.getDefault().notify(errorMessage);
                                    return;
                                }
                            }
                        }
                    }
                };
    }
}
