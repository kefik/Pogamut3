package cz.cuni.pogamut.shed.view;

import cz.cuni.amis.pogamut.sposh.elements.*;
import cz.cuni.amis.pogamut.sposh.exceptions.CycleException;
import cz.cuni.amis.pogamut.sposh.executor.IAction;
import cz.cuni.amis.pogamut.sposh.executor.ISense;
import cz.cuni.pogamut.posh.PoshDataObject;
import cz.cuni.pogamut.posh.PoshEditorSupport;
import cz.cuni.pogamut.posh.explorer.*;
import cz.cuni.pogamut.shed.presenter.ShedPresenter;
import cz.cuni.pogamut.shed.view.LapTreeMVElement.ShedUndoRedo;
import cz.cuni.pogamut.shed.widget.LapSceneFactory;
import cz.cuni.pogamut.shed.widget.ShedScene;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.io.IOException;
import java.io.StringReader;
import java.text.MessageFormat;
import java.util.*;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.core.spi.multiview.CloseOperationState;
import org.netbeans.core.spi.multiview.MultiViewElement;
import org.netbeans.core.spi.multiview.MultiViewElementCallback;
import org.netbeans.core.spi.multiview.MultiViewFactory;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.UndoRedo;
import org.openide.loaders.DataObject;
import org.openide.util.ChangeSupport;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;

/**
 * MV component showing the lap plan as tree. Basically visual mode of modifying
 * the plan.
 *
 * @author HonzaH
 */
final class LapTreeMVElement extends JSplitPane implements MultiViewElement {

    /**
     * Toolbar of this MVE.
     */
    private final JToolBar toolbar;
    /**
     * MV callback.
     */
    private MultiViewElementCallback callback;
    /**
     * Data object that will be edited in this element.
     */
    private final PoshDataObject dObj;
    /**
     * Tree form of lap plan.
     */
    private final PoshPlan lapTree;
    /**
     * Sceen that will be showing the lap tree as widget tree.
     */
    private final ShedScene scene;
    /**
     * Default name of the drive collection that is displayed
     */
    private static final String DEFAULT_DC_NAME = "life";
    /**
     * Global listener that registers all elements of the tree and if anything
     * changes, it marks data object as modified.
     */
    private final TreeModificationListener saveListener;
    private final ShedUndoRedo undoRedo;
    /**
     * Scroll pane that contains the {@link ShedScene#createView() view of the scene},
     * the left side of this element.
     */
    private final JScrollPane sceneScrollPane;
    /**
     * Tabbed panel with explorers for APs, Cs, actions and senses({@link ExplorerFactory}).
     * The right side of the element.
     */
    private final PalettePane explorer;

    /**
     * Create new view for lap tree. This one only creates defualtlap tree
     * element for passed data object.
     *
     * @param dObj object that will be edited using this element.
     */
    LapTreeMVElement(PoshDataObject dObj) {
        super(JSplitPane.HORIZONTAL_SPLIT);

        this.dObj = dObj;
        this.lapTree = LapElementsFactory.createPlan(DEFAULT_DC_NAME);
        this.undoRedo = new ShedUndoRedo(50);
        this.saveListener = new TreeModificationListener(lapTree, dObj.getEditorSupport().getDocument(), undoRedo);

        this.scene = LapSceneFactory.createShedScene(this.lapTree);

        this.toolbar = createToolbarRepresentation();

        this.sceneScrollPane = new JScrollPane();
        this.sceneScrollPane.setViewportView(scene.createView());
        // focus grabber necessary for keys actions in the scene to work.
        scene.getView().addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                JComponent view = scene.getView();
                if (!view.isFocusOwner()) {
                    view.requestFocusInWindow();
                }
            }
        });

        this.setLeftComponent(this.sceneScrollPane);

        this.explorer = new PalettePane(lapTree, getProject(), scene.getPresenter());
        this.explorer.setPreferredSize(new Dimension(300, 1024));
        this.setRightComponent(explorer);

        this.scene.setPaletteActions(explorer);

        // all extra space is allocated to the scene
        this.setResizeWeight(1.0);
    }

    private Project getProject() {
        return FileOwnerQuery.getOwner(dObj.getPrimaryFile());
    }

    @Override
    public JComponent getVisualRepresentation() {
        // called every time the view is activated
        return this;
    }

    @Override
    public JComponent getToolbarRepresentation() {
        return toolbar;
    }

    @Override
    public Action[] getActions() {
        if (callback != null) {
            // default actions of TC
            return callback.createDefaultActions();
        }
        return new Action[0];
    }

    @Override
    public Lookup getLookup() {
        return dObj.getNodeDelegate().getLookup();
    }

    /**
     * Called only when enclosing multi view top component was closed before and
     * now is opened again for the first time.
     */
    @Override
    public void componentOpened() {
        //  Subclasses will usually perform initializing tasks here. 
        saveListener.register();
    }

    /**
     * Called when this MultiViewElement is about to be shown. That can happen
     * when switching the current perspective/view or when the topcomonent
     * itself is shown for the first time.
     */
    @Override
    public void componentShowing() {
        PoshPlan documentLapTree;
        try {
            documentLapTree = dObj.parseLapPlan();
        } catch (ParseException ex) {
            sceneScrollPane.setViewportView(new JTextField(ex.getMessage()));
            return;
        }

        sceneScrollPane.setViewportView(scene.getView());

        // Synchronization changes three - thus notifying the saveListener that 
        // generates the plan from the tree. Unless user has modified the plan 
        // in the scene, don't regenerate the plan. This allows to open the 
        // plan for viewing only without unnecessary save dialog and for 
        // modification of the plan purely in text mode while displaying (and 
        // ONLY displaying) the plan in the graphic view.
        if (!saveListener.isModified()) {
            saveListener.setEnabled(false);
            lapTree.synchronize(documentLapTree);
            saveListener.setEnabled(true);
        } else {
            lapTree.synchronize(documentLapTree);
        }
        
        scene.update();

        // update competences and AP
        explorer.refresh();
    }

    /**
     * Called only when multi view top component was closed.
     */
    @Override
    public void componentClosed() {
        // Do the cleanup
        saveListener.unregister();
    }

    @Override
    public void componentHidden() {
        // Switching to the text mode or to another window. 
    }

    @Override
    public void componentActivated() {
        scene.getView().requestFocusInWindow();
    }

    @Override
    public void componentDeactivated() {
    }

    @Override
    public UndoRedo getUndoRedo() {
        return undoRedo;
    }

    @Override
    public void setMultiViewCallback(MultiViewElementCallback mvec) {
        this.callback = mvec;
    }

    @Override
    public CloseOperationState canCloseElement() {
        // MVTC will be closed when all MVE return OK, so we let text editor handle closing.
        if (dObj.isModified()) {
            AbstractAction saveAction = new AbstractAction() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        dObj.getEditorSupport().saveDocument();
                    } catch (IOException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }
            };
            AbstractAction discardAction = new AbstractAction() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    dObj.getEditorSupport().onCloseDiscard();
                }
            };
            String messageSave = dObj.getEditorSupport().messageSave();

            return MultiViewFactory.createUnsafeCloseState(messageSave, saveAction, discardAction);
        }
        return CloseOperationState.STATE_OK;

    }

    private JToolBar createToolbarRepresentation() {
        JToolBar tb = new JToolBar();
        tb.add(new AbstractAction("Create sense") {

            @Override
            public void actionPerformed(ActionEvent e) {
                NotifyDescriptor.InputLine desc = new NotifyDescriptor.InputLine("Get name of sense", "Add new sense to goal od DC");
                DialogDisplayer.getDefault().notify(desc);
                if (desc.getValue() != NotifyDescriptor.OK_OPTION) {
                    return;
                }
                lapTree.getDriveCollection().getGoal().add(LapElementsFactory.createSense(desc.getInputText()));
            }
        });
        return tb;
    }

    class ShedUndoRedo implements UndoRedo {

        private final ChangeSupport cs = new ChangeSupport(this);
        private final int historySize;
        private final List<String> history;
        private int position;
        /**
         * Should we ignore new added texts from tree modification? We are
         * ignoring during undo/redo because of synchronization.
         */
        private boolean ignore = false;

        /**
         *
         * @param steps How many steps store
         */
        ShedUndoRedo(int steps) {
            this.historySize = steps;
            this.history = new ArrayList<String>(steps);
            this.position = -1;
        }

        public void add(String plan) {
            if (ignore) {
                return;
            }

            boolean isPositionAtLastElement = (position == history.size() - 1);
            if (!isPositionAtLastElement) {
                history.subList(position + 1, history.size()).clear();
            }

            history.add(plan);

            while (history.size() > historySize) {
                history.remove(0);
            }
            position = history.size() - 1;
            cs.fireChange();
        }

        @Override
        public boolean canUndo() {
            return !history.isEmpty() && position > 0;
        }

        @Override
        public boolean canRedo() {
            return !history.isEmpty() && position < history.size() - 1;
        }

        @Override
        public void undo() throws CannotUndoException {
            try {
                ignore = true;
                
                // take text from position - 1
                String planText = history.get(position - 1);
                // parse it
                PoshParser parser = new PoshParser(new StringReader(planText));
                PoshPlan plan = parser.parsePlan();
                // synchronize with the editor tree
                lapTree.synchronize(plan);

                if (position > 0) {
                    --position;
                }
            } catch (ParseException ex) {
                throw new CannotUndoException();
            } finally {
                ignore = false;
                cs.fireChange();
            }
        }

        @Override
        public void redo() throws CannotRedoException {
            try {
                ignore = true;
                // take text from position + 1
                String planText = history.get(position + 1);
                // parse it
                PoshParser parser = new PoshParser(new StringReader(planText));
                PoshPlan plan = parser.parsePlan();
                // synchronize with the editor tree
                lapTree.synchronize(plan);

                if (position < history.size() - 1) {
                    ++position;
                }
            } catch (ParseException ex) {
                throw new CannotRedoException();
            } finally {
                ignore = false;
                cs.fireChange();
            }
        }

        @Override
        public void addChangeListener(ChangeListener l) {
            cs.addChangeListener(l);
        }

        @Override
        public void removeChangeListener(ChangeListener l) {
            cs.removeChangeListener(l);
        }

        @Override
        public String getUndoPresentationName() {
            return "";
        }

        @Override
        public String getRedoPresentationName() {
            return "";
        }
    };
}

class TreeModificationListener implements PoshElementListener {

    private final PoshPlan lapTree;
    private final StyledDocument document;
    private int balance = 0;
    private boolean modified = false;
    private boolean enabled = true;
    private final ShedUndoRedo undoRedo;

    public TreeModificationListener(PoshPlan lapTree, StyledDocument document, ShedUndoRedo undoRedo) {
        this.lapTree = lapTree;
        this.document = document;
        this.undoRedo = undoRedo;
    }

    public boolean isModified() {
        return modified;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    public void register() {
        registerBranch(lapTree, true);
    }

    public void unregister() {
        registerBranch(lapTree, false);
        if (balance != 0) {
            String message = MessageFormat.format("Balance of register/unregister pairs for global listener is {0}, should be 0. If you can reproduce, report.", balance);
            NotifyDescriptor.Message desc = new NotifyDescriptor.Message(message);
            DialogDisplayer.getDefault().notify(desc);
        }
    }

    private void registerElement(PoshElement element, boolean register) {
        if (register) {
            element.addElementListener(this);
            ++balance;
        } else {
            element.removeElementListener(this);
            --balance;
        }
    }

    private void registerBranch(PoshElement<?, ?> branchRoot, boolean register) {
        registerElement(branchRoot, register);
        for (PoshElement child : branchRoot.getChildDataNodes()) {
            registerBranch(child, register);
        }
    }

    @Override
    public void childElementAdded(PoshElement parent, PoshElement child) {
        registerBranch(child, true);
        notifyTreeModified();
    }

    @Override
    public void childElementMoved(PoshElement parent, PoshElement child, int oldIndex, int newIndex) {
        notifyTreeModified();
    }

    @Override
    public void childElementRemoved(PoshElement parent, PoshElement child, int removedChildPosition) {
        registerBranch(child, false);
        notifyTreeModified();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        notifyTreeModified();
    }

    private void notifyTreeModified() {
        if (enabled) {
            modified = true;
            updateDocument();
            undoRedo.add(lapTree.toString());
        }
    }

    private void updateDocument() {
        try {
            document.remove(0, document.getLength());
            document.insertString(0, lapTree.toString(), null);
        } catch (BadLocationException ex) {
            Exceptions.printStackTrace(ex);
        }
    }
}
