package cz.cuni.amis.pogamut.sposh.dbg.view;

import cz.cuni.amis.dash.DashWindow;
import cz.cuni.amis.dash.YaposhEngine;
import cz.cuni.amis.pogamut.sposh.dbg.engine.EngineThread;
import cz.cuni.amis.pogamut.sposh.dbg.engine.EvaluationListener;
import cz.cuni.sposh.debugger.BreakpointManager;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Comparator;
import java.util.Enumeration;
import javax.swing.SwingUtilities;
import javax.swing.tree.*;
import org.netbeans.api.debugger.DebuggerManager;
import org.netbeans.api.debugger.DebuggerManagerAdapter;
import org.netbeans.api.debugger.DebuggerManagerListener;
import org.netbeans.api.debugger.Session;
import org.netbeans.api.debugger.jpda.JPDADebugger;
import org.netbeans.api.debugger.jpda.JPDAThread;
import org.netbeans.api.debugger.jpda.event.JPDABreakpointEvent;
import org.netbeans.api.debugger.jpda.event.JPDABreakpointListener;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;

/**
 * Top component that will show list of all debugged agents in the NetBeans, you
 * can double click on one and it will create a {@link DashWindow} for the engine.
 *
 * @author HonzaH
 */
// You can change icon by including 'iconBase' into TC.Description
@TopComponent.Description(preferredID = "EngineSelectionComponent", persistenceType = TopComponent.PERSISTENCE_ALWAYS)
@TopComponent.Registration(mode = "editor", openAtStartup = false)
@ActionID(category = "Window", id = "cz.cuni.amis.pogamut.sposh.dbg.view.EngineSelectionComponent")
// Change position by setting position attribute of ActionReference
@ActionReference(path = "Menu/Window")
@TopComponent.OpenActionRegistration(displayName = "#CTL_EngineSelectionAction", preferredID = "EngineSelectionComponent")
@NbBundle.Messages({
    "CTL_EngineSelectionAction=EngineSelection",
    "CTL_EngineSelectionComponent=EngineSelection Window",
    "HINT_EngineSelectionComponent=This is a EngineSelection window"
})
public final class EngineSelectionComponent extends TopComponent {

    /**
     * Breakpoint group that will be displayed in the list of breakpoints along
     * with the breakpoint(s) used by this component.
     */
    private final String SELECTION_BREAKPOINT_GROUP = "selection breakpoint group";
    /**
     * Manager of breakpoint(s) of this component used to watch for all engines.
     */
    private final BreakpointManager breakpointManager;
    /**
     * LIstener on breakpoints that will update the tree model each time the
     * breakpoint is tripped.
     */
    private final JPDABreakpointListener engineWatcher;
    /**
     * Model of the tree. Necessary to notify views.
     */
    private final DefaultTreeModel treeModel;
    /**
     * Root node for all sessions in the component.
     */
    private final SessionsRoot sessionsRoot;
    /**
     * SelectionListener
     */
    private final MouseListener treeSelectionListener = new MouseAdapter() {

        @Override
        public void mousePressed(MouseEvent e) {
            int selRow = jEnginesTree.getRowForLocation(e.getX(), e.getY());
            TreePath selPath = jEnginesTree.getPathForLocation(e.getX(), e.getY());
            if (selRow != -1) {
                if (e.getClickCount() == 2) {
                    Object lastComponent = selPath.getLastPathComponent();
                    if (lastComponent instanceof ThreadNode) {
                        ThreadNode threadNode = (ThreadNode) lastComponent;

                        openDebugger(threadNode.getThread());
                    }
                }
            }
        }

        /**
         * TODO: Maybe join with {@link OpenLapDebugger}, TODO: not sure about
         * currentEngine, what if python or cš gets in the way?
         */
        private void openDebugger(EngineThread engineThread) {
            assert SwingUtilities.isEventDispatchThread();

            JPDADebugger debugger = engineThread.getDebugger();
            JPDAThread jpdaThread = engineThread.getThread();
            YaposhEngine engine = new YaposhEngine(new EngineThread(debugger, jpdaThread));
            DashWindow view = new DashWindow(engine, "Lap debugger");
            engine.addListener(view);
            engine.initialize();
            view.open();
            view.requestActive();
        }
    };
    /**
     * Listener that removes nodes from the list of sessions with running
     * engine.
     */
    private final DebuggerManagerListener removeSessionsListener = new DebuggerManagerAdapter() {

        @Override
        public void sessionAdded(Session session) {
            // TODO: Should I add sessions using this method? Probably not, 
            // I have no way to distinguish between engine thread and thread
            // that ends with " logic"
        }

        @Override
        public void sessionRemoved(Session session) {
            SessionNode sessionNode = sessionsRoot.getSessionNode(session);
            if (sessionNode != null) {
                treeModel.removeNodeFromParent(sessionNode);
            }
        }
    };

    public EngineSelectionComponent() {
        initComponents();

        setName(Bundle.CTL_EngineSelectionComponent());
        setToolTipText(Bundle.HINT_EngineSelectionComponent());

        sessionsRoot = new SessionsRoot();
        treeModel = new DefaultTreeModel(sessionsRoot);
        jEnginesTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        jEnginesTree.addMouseListener(treeSelectionListener);
        jEnginesTree.setModel(treeModel);

        breakpointManager = BreakpointManager.createLapEvaluation(SELECTION_BREAKPOINT_GROUP);
        breakpointManager.purge();

        engineWatcher = new EngineWatcher(treeModel, sessionsRoot);
        breakpointManager.addListener(engineWatcher);
    }

    /**
     * This class is intended to listen on evaluation method of the engine and
     * to add any new engine into the tree model. The tree will have following
     * structure: root-&gt;sessions-&gt;engineThreads
     */
    private static class EngineWatcher extends EvaluationListener {

        private final DefaultTreeModel treeModel;
        /**
         * Root node under which will be all sessions
         */
        private final SessionsRoot sessionsRoot;

        /**
         * Create new watcher that will update the model.
         *
         * @param sessionsRoot root of the model, must be childless
         */
        EngineWatcher(DefaultTreeModel treeModel, SessionsRoot sessionsRoot) {
            assert sessionsRoot.getChildCount() == 0;

            this.treeModel = treeModel;
            this.sessionsRoot = sessionsRoot;
        }

        @Override
        public void breakpointReached(JPDABreakpointEvent breakpointEvent) {
            JPDADebugger debugger = breakpointEvent.getDebugger();

            final Session session = getSession(debugger);
            SessionNode sessionNode = sessionsRoot.getSessionNode(session);
            if (sessionNode == null) {
                int sessionPosition = sessionsRoot.getInsertPosition(session);
                sessionNode = new SessionNode(session);
                treeModel.insertNodeInto(sessionNode, sessionsRoot, sessionPosition);
            }

            EngineThread engineThread = new EngineThread(debugger, breakpointEvent.getThread());
            ThreadNode threadNode = sessionNode.getThreadNode(engineThread);
            if (threadNode == null) {
                int threadInsertPosition = sessionNode.getInsertPosition(engineThread);
                threadNode = new ThreadNode(engineThread);
                treeModel.insertNodeInto(threadNode, sessionNode, threadInsertPosition);
            }
        }
    }

    static class AbstractTreeNode<CHILD_OBJECT, CHILD_NODE extends DefaultMutableTreeNode> extends DefaultMutableTreeNode {

        protected AbstractTreeNode() {
        }

        protected AbstractTreeNode(Object userObject, boolean allowsChildren) {
            super(userObject, allowsChildren);
        }

        /**
         * Find child of this node with specified user object and return it.
         *
         * @param userObject Seeked child will have this object
         * @return Found child or null
         */
        public final DefaultMutableTreeNode getChild(CHILD_OBJECT userObject) {
            Enumeration<?> e = children();
            while (e.hasMoreElements()) {
                DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) e.nextElement();
                Object childUserObject = childNode.getUserObject();
                if (childUserObject.equals(userObject)) {
                    return childNode;
                }
            }
            return null;
        }

        /**
         * Get position into which to insert new child. Position is determined
         * as position of first child with greater or equal user object.
         *
         * @param newChildObject
         * @return
         */
        @SuppressWarnings("unchecked")
        public final int getChildPosition(CHILD_OBJECT newChildObject, Comparator<CHILD_OBJECT> comparator) {
            int childNodePosition = 0;
            Enumeration<DefaultMutableTreeNode> e = children();
            while (e.hasMoreElements()) {
                DefaultMutableTreeNode childNode = e.nextElement();
                CHILD_OBJECT childObject = (CHILD_OBJECT) childNode.getUserObject();
                if (comparator.compare(childObject, newChildObject) >= 0) {
                    break;
                }
                childNodePosition++;
            }
            return childNodePosition;
        }
    }

    /**
     * Root node for all sessions in the selection component.
     */
    static final class SessionsRoot extends AbstractTreeNode<Session, SessionNode> {

        /**
         * Get {@link SessionNode} of this root corresponding to the passed {@link Session}.
         *
         * @param session Seeked node has same session
         * @return Found node or null
         */
        public SessionNode getSessionNode(Session session) {
            return (SessionNode) getChild(session);
        }

        public int getInsertPosition(Session session) {
            assert getSessionNode(session) == null;

            Comparator<Session> sessionComparator = new Comparator<Session>() {

                @Override
                public int compare(Session o1, Session o2) {
                    return o1.getName().compareTo(o2.getName());
                }
            };
            int sessionInsertPosition = getChildPosition(session, sessionComparator);
            return sessionInsertPosition;
        }

        @Override
        public String toString() {
            return "Running sessions";
        }
    }

    /**
     * {@link TreeNode} representing one {@link Session debug session} (that can
     * contain several engine threads). The session is stored in the {@link #userObject}.
     * Use {@link #getSession() } to get properly typed user object.
     */
    static final class SessionNode extends AbstractTreeNode<EngineThread, ThreadNode> {

        public SessionNode(Session session) {
            super(session, true);
        }

        /**
         * Get session of this node. Session is stored in the user object.
         *
         * @return Session of this node.
         */
        public Session getSession() {
            return (Session) getUserObject();
        }

        /**
         * Get thread node that is child of this node.
         *
         * @param engineThread thread that is associated with the node
         * @return Found node or null
         */
        public ThreadNode getThreadNode(EngineThread thread) {
            return (ThreadNode) getChild(thread);
        }

        /**
         * Get position at which to insert new {@link EngineThread} into the {@link SessionNode node}.
         * The session can't already contain {@link ThreadNode} with the thread.
         *
         * @param thread Thread for which a {@link ThreadNode} will be created
         * @return Position at which to insert the new node representing the
         * passed thread
         */
        public int getInsertPosition(EngineThread thread) {
            assert getThreadNode(thread) == null;

            int insertPosition = getChildPosition(thread, thread);
            return insertPosition;
        }

        @Override
        public String toString() {
            return getSession().getName();
        }
    }

    /**
     * {@link TreeNode} representing one engine {@link JPDAThread thread}. The
     * thread is stored in the {@link #userObject}. Use {@link #getThread() } to
     * get properly typed user object.
     */
    static final class ThreadNode extends AbstractTreeNode<Object, DefaultMutableTreeNode> {

        public ThreadNode(EngineThread userObject) {
            super(userObject, false);
        }

        /**
         * @return Get thread this node is representing
         */
        public EngineThread getThread() {
            return (EngineThread) getUserObject();
        }

        @Override
        public String toString() {
            return getThread().getName();
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblInfo = new javax.swing.JLabel();
        jScrollPane = new javax.swing.JScrollPane();
        jEnginesTree = new javax.swing.JTree();

        setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.PAGE_AXIS));

        lblInfo.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        org.openide.awt.Mnemonics.setLocalizedText(lblInfo, org.openide.util.NbBundle.getMessage(EngineSelectionComponent.class, "EngineSelectionComponent.lblInfo.text")); // NOI18N
        lblInfo.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        add(lblInfo);

        javax.swing.tree.DefaultMutableTreeNode treeNode1 = new javax.swing.tree.DefaultMutableTreeNode("root");
        jEnginesTree.setModel(new javax.swing.tree.DefaultTreeModel(treeNode1));
        jScrollPane.setViewportView(jEnginesTree);

        add(jScrollPane);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTree jEnginesTree;
    private javax.swing.JScrollPane jScrollPane;
    private javax.swing.JLabel lblInfo;
    // End of variables declaration//GEN-END:variables

    @Override
    public void componentOpened() {
        breakpointManager.addListener(engineWatcher);
        DebuggerManager.getDebuggerManager().addDebuggerListener(removeSessionsListener);
    }

    @Override
    public void componentClosed() {
        DebuggerManager.getDebuggerManager().removeDebuggerListener(removeSessionsListener);
        breakpointManager.removeListener(engineWatcher);
        breakpointManager.purge();
    }
}
