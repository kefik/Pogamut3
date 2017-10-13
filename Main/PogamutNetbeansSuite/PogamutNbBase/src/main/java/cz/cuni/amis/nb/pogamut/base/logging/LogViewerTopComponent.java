package cz.cuni.amis.nb.pogamut.base.logging;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import org.openide.ErrorManager;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 * Top component which displays table with log records.
 */
public final class LogViewerTopComponent extends TopComponent {
    
    private static LogViewerTopComponent instance;
    /** path to the icon used by the component and its open action */
//    static final String ICON_PATH = "SET/PATH/TO/ICON/HERE";
    
    private static final String PREFERRED_ID = "LogViewerTopComponent";
    
    /**
     * Node associated with this window
     */
    protected LogNode logNode = null;
    
    protected LogViewerTopComponent(LogNode logNode) {
        initComponents();
        setToolTipText(NbBundle.getMessage(LogViewerTopComponent.class, "HINT_LogViewerTopComponent"));
        setNodeToView(logNode);
        this.logNode = logNode;
    }
    
    
    /**
     * Returns log node associated with this window.
     */
    public LogNode getLogNode() {
        return logNode;
    }

    /* TODO
    public static void closeAllLogsOwnedBy(Object owner) {
        Collection toClose = new ArrayList();
        
        String logBeh = (String)Settings.get(Settings.Setting.LOG_WIN_BEHAVIOR);
        if (logBeh.equals("NEW")) {
            for(LogNode node : logToWin.keySet()) {
                if(node.getOwner().get() == owner) {
                    toClose.add(node);
                    boolean b = logToWin.get(node).close();
             System.out.println(b);       
                }
            }
            logToWin.entrySet().removeAll(toClose);
        } else {
            assert(logBeh.equals("TYPE"));
            LogViewerTopComponent logTc = null;
            for(LogType type : typeToWin.keySet()) {
                logTc = typeToWin.get(type);
                if(logTc.getLogNode().getOwner().get() == owner) {
                    logTc.close();
                    toClose.add(type);
                }
            }
            typeToWin.entrySet().removeAll(toClose);
        }
        
        
        // TODO close log windows dedicated for specific type of logs
        // TODO .... maybe don't close it, just clear the messages and change name
    }
    */
    protected Node currentNode = null;
    
    /**
     * Sets a log node that will be viewed by this component.
     */
    protected void setNodeToView(LogNode logNode) {
        setName(logNode.getName());
        /*getLogTableModel().removeAllDataSources();
        getLogTableModel().addDataSource(
                logNode.getLogRecordsSource());
        */
        logViewerPane1.setLogNode(logNode);
        currentNode = logNode;
        lkp = logNode.getLookup();
    }
    
    /**
     * Show properties of node representing this log.
     */
    public void componentActivated() {
        setActivatedNodes(new Node[] {currentNode});
        super.componentActivated();
    }
    
    protected Lookup lkp = null;
    
    public Lookup getLookup() {
        return lkp;
    }
    
    /*
     * Map between logNodes and theirs log windows. Applicable only to mode where
     * for each logNode has its own log window.
     */
    protected static Map<LogNode, LogViewerTopComponent> logToWin = new HashMap<LogNode, LogViewerTopComponent>();
    
    /*
     * Map between types of logs and theirs log windows. Applicable only to mode where
     * all logNodes of the same type share only one window.
     */
    // TODO protected static Map<LogType, LogViewerTopComponent> typeToWin = new HashMap<LogType, LogViewerTopComponent>();
    
    
    /**
     * Creates new win or returns existing window. Behavior depends on preference
     * entry "LogWinBehavior" in node cz.cuni.pogamut.Client.Agent.class
     */
    public static LogViewerTopComponent getWin(LogNode logNode) {
/* TODO        String logBeh = (String)Settings.get(Settings.Setting.LOG_WIN_BEHAVIOR);
        if (logBeh.equals("NEW")) {
       */     // each log will be opened in new window
            if (!logToWin.containsKey(logNode)) {
                LogViewerTopComponent logWin = new LogViewerTopComponent(logNode);
                logToWin.put(logNode, logWin);
                return logWin;
            }
            return logToWin.get(logNode);
      /*  } else {
            // all logNodes of the same type share only one window
            if (!typeToWin.containsKey(logNode.getType())) {
                LogViewerTopComponent logWin = new LogViewerTopComponent(logNode);
                typeToWin.put(logNode.getType(), logWin);
                return logWin;
            }
            
            LogViewerTopComponent win = typeToWin.get(logNode.getType());
            win.setNodeToView(logNode);
            return win;
        }
       */
    }
    
    public LogViewerTopComponent() {
        initComponents();
        setName(NbBundle.getMessage(LogViewerTopComponent.class, "CTL_LogViewerTopComponent"));
        setToolTipText(NbBundle.getMessage(LogViewerTopComponent.class, "HINT_LogViewerTopComponent"));
    }
    
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        logViewerPane1 = new LogViewerPane();

        setLayout(new java.awt.BorderLayout());

        add(logViewerPane1, java.awt.BorderLayout.CENTER);

    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private LogViewerPane logViewerPane1;
    // End of variables declaration//GEN-END:variables
    
    /**
     * Gets default instance. Do not use directly: reserved for *.settings files only,
     * i.e. deserialization routines; otherwise you could get a non-deserialized instance.
     * To obtain the singleton instance, use {@link findInstance}.
     */
    
    public static synchronized LogViewerTopComponent getDefault() {
        if (instance == null) {
            instance = new LogViewerTopComponent();
        }
        return instance;
    }
    
    /**
     * Obtain the LogViewerTopComponent instance. Never call {@link #getDefault} directly!
     */
    
    public static synchronized LogViewerTopComponent findInstance() {
        TopComponent win = WindowManager.getDefault().findTopComponent(PREFERRED_ID);
        if (win == null) {
            ErrorManager.getDefault().log(ErrorManager.WARNING, "Cannot find LogViewer component. It will not be located properly in the window system.");
            return getDefault();
        }
        if (win instanceof LogViewerTopComponent) {
            return (LogViewerTopComponent)win;
        }
        ErrorManager.getDefault().log(ErrorManager.WARNING, "There seem to be multiple components with the '" + PREFERRED_ID + "' ID. That is a potential source of errors and unexpected behavior.");
        return getDefault();
    }
    
    
    public LogTableModel getLogTableModel() {
        return logViewerPane1.getLogTableModel();
    }
    
    public int getPersistenceType() {
        return TopComponent.PERSISTENCE_NEVER;
    }
    
    public void componentOpened() {
        // TODO add custom code on component opening
    }
    
    public void componentClosed() {
        // TODO add custom code on component closing
    }
    
    /** replaces this in object stream */
    public Object writeReplace() {
        return new ResolvableHelper();
    }
    
    protected String preferredID() {
        return PREFERRED_ID;
    }
    
    final static class ResolvableHelper implements Serializable {
        private static final long serialVersionUID = 1L;
        public Object readResolve() {
            //TODO fix this
            return LogViewerTopComponent.getDefault();
        }
    }
    
}
