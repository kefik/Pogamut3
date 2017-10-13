package cz.cuni.pogamut.posh.view;

import cz.cuni.amis.pogamut.sposh.elements.*;
import cz.cuni.amis.pogamut.sposh.exceptions.CycleException;
import cz.cuni.pogamut.posh.PoshDataObject;
import cz.cuni.pogamut.posh.PoshEditorSupport;
import cz.cuni.pogamut.posh.explorer.Crawler;
import cz.cuni.pogamut.posh.explorer.CrawlerExplorerFactory;
import cz.cuni.pogamut.posh.explorer.CrawlerListener;
import cz.cuni.pogamut.posh.explorer.PrimitiveData;
import java.io.IOException;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.text.BadLocationException;
import org.netbeans.core.spi.multiview.CloseOperationState;
import org.netbeans.core.spi.multiview.MultiViewElementCallback;
import cz.cuni.pogamut.posh.widget.PoshScene;
import cz.cuni.pogamut.posh.widget.PoshWidget;
import cz.cuni.pogamut.posh.widget.kidview.SimpleDriveCollectionWidget;
import java.awt.event.ActionEvent;
import java.io.InputStream;
import java.util.Collection;
import javax.swing.*;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.core.spi.multiview.MultiViewElement;
import org.netbeans.core.spi.multiview.MultiViewFactory;
import org.openide.awt.UndoRedo;
import org.openide.loaders.DataObject;
import org.openide.text.NbDocument;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;
import org.openide.util.lookup.ProxyLookup;

/**
 * Show POSH plan as tree-like structure, that is fully expanded (POSH plan
 * starts at drive collection, actions are connected to competences or APs is
 * necessary)
 *
 * @author Honza Havlicek
 */
public class KidViewElement implements MultiViewElement {

    /**
     * This is a root node of posh tree that is shown in the component
     */
    private final PoshPlan plan;
    private PoshEditorSupport support;
    /**
     * When you switch from text to source and back, while you change something
     * in the source, this will try to restore original collapse state of tree
     * even when the tree itsdelf is not the same.
     */
    private TreeCollapseImprint collapseImprint;
    /**
     * Scene we are placing the widgets into
     */
    private PoshScene scene;
    private JComponent panel;
    /**
     * The JComponent that is representing this MVE
     */
    private JScrollPane scrollPane;
    /**
     * Toolbar of this MVE, the one bext to buttons for switch between elements
     */
    private JToolBar toolbar;
    /**
     * Lookup, we only want to show palette for this element, not for source
     * stuff
     */
    private final Lookup lookup;
    private InstanceContent ic;

    public KidViewElement(PoshEditorSupport support) {
        this.support = support;

        plan = new PoshPlan();
        scene = new PoshScene();

        scene.setSourceUpdater(new SourceUpdater(plan, support));

        // Create a palette and add it to lookup
        ic = new InstanceContent();
        lookup = new ProxyLookup(support.getDataObject().getNodeDelegate().getLookup(), new AbstractLookup(ic));

        panel = createVisualRepresentation();
    }

    /**
     * Get project the posh plan belongs to.
     *
     * @return project the lap file belongs to or null if it doesn't belong to
     * any project.
     */
    private Project getProject() {
        return FileOwnerQuery.getOwner(support.getDataObject().getPrimaryFile());
    }
    CrawlerListener<PrimitiveData> actionsListener = new CrawlerListener<PrimitiveData>() {

        @Override
        public void started(Crawler<PrimitiveData> crawler) {
        }

        private void updateActionName(PoshWidget<? extends PoshElement> widget, String name, String newName) {
            if (newName == null) {
                scene.getActionsFQNMapping().remove(name);
            } else {
                scene.getActionsFQNMapping().put(name, newName);
            }
        }

        @Override
        public void crawledData(Crawler<PrimitiveData> crawler, Collection<PrimitiveData> data) {
            for (PrimitiveData info : data) {
                updateActionName(scene.getRootWidget(), info.classFQN, info.name);
            }
        }

        @Override
        public void finished(Crawler<PrimitiveData> crawler, boolean error) {
            assert SwingUtilities.isEventDispatchThread();
            scene.repaint();
        }
    };
    CrawlerListener<PrimitiveData> sensesListener = new CrawlerListener<PrimitiveData>() {

        @Override
        public void started(Crawler<PrimitiveData> crawler) {
        }

        private void updateSenseName(PoshWidget<? extends PoshElement> widget, String name, String newName) {
            if (newName == null) {
                scene.getSensesFQNMapping().remove(name);
            } else {
                scene.getSensesFQNMapping().put(name, newName);
            }
        }

        @Override
        public void crawledData(Crawler<PrimitiveData> crawler, Collection<PrimitiveData> data) {
            for (PrimitiveData info : data) {
                updateSenseName(scene.getRootWidget(), info.classFQN, info.name);
            }
        }

        @Override
        public void finished(Crawler<PrimitiveData> crawler, boolean error) {
            assert SwingUtilities.isEventDispatchThread();
            scene.repaint();
        }
    };
    JTabbedPane explorer;

    /**
     * Create visual editor for posh. If there is a project the plan belongs to,
     * show also explorer for primitives and such.
     *
     * @return
     */
    private JComponent createVisualRepresentation() {
        scrollPane = new JScrollPane();
        scrollPane.setViewportView(scene.createView());

        Project project = getProject();
        if (project == null) {
            return scrollPane;
        }

        explorer = new JTabbedPane();
        explorer.setPreferredSize(explorer.getMinimumSize());

        JSplitPane rootPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollPane, explorer);
        rootPanel.resetToPreferredSizes();
        //rootPanel.setDividerLocation(0.8);
        //rootPanel.setOneTouchExpandable(true);

        return rootPanel;
    }

    @Override
    public synchronized JComponent getVisualRepresentation() {
        return panel;
    }

    private void refresh(InputStream stream) throws ParseException, CycleException {
        plan.synchronizePlan(stream);

        scene.clearPoshWidgets();
        scene.setRootWidget(new SimpleDriveCollectionWidget(scene, plan.getDriveCollection(), null));

        plan.emitTree();

        scene.setSourceUpdater(new SourceUpdater(plan, support));
    }

    @Override
    public JComponent getToolbarRepresentation() {
        if (toolbar == null) {
            toolbar = new JToolBar();
        }
        return toolbar;
    }

    @Override
    public Action[] getActions() {
        return support.getDataObject().getNodeDelegate().getActions(false);
    }

    @Override
    public Lookup getLookup() {
        return lookup;
    }

    @Override
    public void componentOpened() {
    }

    @Override
    public void componentClosed() {
    }
    boolean explorerCreated = false;

    @Override
    public void componentShowing() {
        support.notifyModified();

        regenerateTreeFromSource();
        Project project = getProject();
        if (project != null && !explorerCreated) {
            explorer.addTab("Competences", CrawlerExplorerFactory.createCompetenceExplorer(plan));
            explorer.addTab("Action patterns", CrawlerExplorerFactory.createAPExplorer(plan));
            explorer.addTab("Actions", CrawlerExplorerFactory.createActionsExplorer(project, actionsListener));
            explorer.addTab("Senses", CrawlerExplorerFactory.createSensesExplorer(project, sensesListener));
            explorerCreated = true;
        }
    }

    @Override
    public void componentHidden() {
//        if (scene.getRootWidget() != null) {
//            collapseImprint = new TreeCollapseImprint(scene.getRootWidget());
//        }
    }

    @Override
    public void componentActivated() {
    }

    @Override
    public void componentDeactivated() {
    }

    @Override
    public UndoRedo getUndoRedo() {
        return null;
    }

    @Override
    public void setMultiViewCallback(MultiViewElementCallback mvec) {
        mvec.requestVisible();
        mvec.requestActive();
    }

    @Override
    public CloseOperationState canCloseElement() {
        DataObject dObj = support.getDataObject();
        if (dObj.isModified()) {
            AbstractAction saveAction = new AbstractAction() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        support.saveDocument();
                    } catch (IOException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }
            };
            AbstractAction discardAction = new AbstractAction() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    support.onCloseDiscard();
                }
            };
            return MultiViewFactory.createUnsafeCloseState(support.messageSave(), saveAction, discardAction);
        }
        return CloseOperationState.STATE_OK;
    }

    /**
     * Regenrate the graph in the PoshScene according to posh plan in document.
     * If there is an syntax error, show error pane.
     */
    private void regenerateTreeFromSource() {
        try {
            scrollPane.setViewportView(scene.getView());
            refresh(support.getInputStream());

            if (collapseImprint != null) {
                collapseImprint.restore(scene.getRootWidget());
            }
            collapseImprint = null;
        } catch (ParseException ex) {
            scrollPane.setViewportView(new ParseErrorPane(ex));
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    /**
     * Updater of source. KidViewElement registers global listener to this class
     * and when tree changes, this updates document with posh plan.
     */
    public final static class SourceUpdater implements Runnable {

        private PoshEditorSupport support;
        private PoshPlan plan;

        private SourceUpdater(PoshPlan plan, PoshEditorSupport support) {
            this.plan = plan;
            this.support = support;
        }

        public void update() {
            NbDocument.runAtomic(support.getDocument(), this);
        }

        @Override
        public void run() {
            try {
                String planString = plan.toString();
                support.getDocument().remove(0, support.getDocument().getLength());
                support.getDocument().insertString(0, planString, null);
                support.notifyModified();
                support.setModified();
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            } catch (BadLocationException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

    /**
     * Pane that is shown in case of syntax error during creation of syntax
     * tree.
     */
    private static class ParseErrorPane extends JEditorPane {

        private ParseErrorPane(ParseException exception) {
            super("text/html",
                    "<html><head><style>"
                    + ".errorbox {"
                    + //"   width: 400px;" +
                    "}"
                    + ".boxtitle {"
                    + "	background-color: rgb(157, 173, 198);"
                    + "	text-align: center;"
                    + "}"
                    + ".boxtitle h2 {"
                    + "	margin: 0;"
                    + "	padding: 15px 30px 5px;"
                    + "	color: white;"
                    + "	font-weight: bold;"
                    + "	font-size: 1.5em;"
                    + "}"
                    + ".boxtext {"
                    + "	background-color: rgb(230, 230, 230);"
                    + "	padding: 5px 50px 31px;"
                    + "}"
                    + "</style>"
                    + "</head>"
                    + "<body>"
                    + "<div class=\"errorbox\">"
                    + "  <div class=\"boxtitle\"><h2>Syntax error</h2></div>"
                    + "  <div class=\"boxtext\">"
                    + "    <p>There is a problem in syntax of supplied posh plan. To remedy this situation, switch to source view and correct syntax error.</p>"
                    + "    <p>The syntax error will be marked by red exclamation mark at the line of error. Description of error will be in tooltip of the exclamation mark.</p>"
                    + "    <p><b>Error:</b> " + exception.getMessage().replace("<", "&lt;").replace(">", "&gt;") + "</p>"
                    + "  </div>"
                    + "</div>"
                    + "</body>"
                    + "</html>");
        }
    }
}
