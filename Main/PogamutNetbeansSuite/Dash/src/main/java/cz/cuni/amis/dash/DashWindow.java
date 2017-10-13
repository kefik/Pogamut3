package cz.cuni.amis.dash;

import cz.cuni.amis.pogamut.sposh.dbg.engine.IDebugEngineListener;
import cz.cuni.amis.pogamut.sposh.dbg.lap.LapBreakpoint;
import cz.cuni.amis.pogamut.sposh.elements.LapPath;
import cz.cuni.amis.pogamut.sposh.elements.PoshPlan;
import cz.cuni.amis.pogamut.sposh.executor.PrimitiveData;
import cz.cuni.amis.pogamut.sposh.executor.PrimitiveInfo;
import cz.cuni.pogamut.posh.explorer.ClassCrawler;
import cz.cuni.pogamut.posh.explorer.ClassCrawlerFactory;
import cz.cuni.pogamut.posh.explorer.CrawlerListener;
import cz.cuni.pogamut.posh.explorer.NameMapCrawler;
import cz.cuni.pogamut.shed.presenter.IPresenterFactory;
import cz.cuni.pogamut.shed.presenter.ShedPresenter;
import cz.cuni.pogamut.shed.widget.LapSceneFactory;
import cz.cuni.pogamut.shed.widget.ShedScene;
import java.awt.BorderLayout;
import java.awt.Container;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ui.OpenProjects;
import org.openide.windows.TopComponent;

/**
 * Window that is informing us about debugged plan. It is working in phases:
 * wait for connection - connected - plan recieved - show debugged scene -
 * disconnect.
 *
 * @author Honza
 */
public class DashWindow extends TopComponent implements IDebugEngineListener {

    private EngineData engineData;
    private final YaposhEngine engine;
    private IPresenterFactory presenter;
    private ShedScene dashScene;
    /**
     * Timestamp when was window last notified about finished evaluation.
     */
    private long lastEvaluationFinished = 0;
    private static int REDRAW_INTERVAL_MS = 50;

    /**
     * Create new window displaying the debugged engine. The content of the
     * window is a text "Waiting to be connected", because in order to display
     * the ending, you need to be connected to engine first (done in {@link #connected()
     * }).
     *
     * @param engine Debugged engine. 
     * @param name Display name of top component
     */
    public DashWindow(YaposhEngine engine, String name) {
        this.engine = engine;

        this.setDisplayName(name);
        this.setLayout(new BorderLayout());
        this.setContent(new JScrollPane(new InfoPanel("Waiting to be connected to the engine")));
    }

    /**
     * Change content of the window to info panel saying that we are connected
     * to the engine and waiting for the plan ({@link #planRecieved(java.lang.String, cz.cuni.amis.pogamut.sposh.elements.PoshPlan)
     * }).
     */
    @Override
    public void connected() {
        this.setContent(new JScrollPane(new InfoPanel("Connected to the engine, waiting for the plan.")));
    }

    /**
     * We got the plan, create the scene using {@link DashPresenterFactory} and
     * set the window to display it. Also start crawlers that will notify
     * widgets about mapping of FQN to name in {@link PrimitiveInfo} through {@link ShedPresenter}.
     *
     * @param name New display name of the window
     * @param plan Plan to display
     */
    @Override
    public void planRecieved(String name, PoshPlan plan) {
        setDisplayName(name);
        ShedScene scene = new ShedScene(plan);
        engineData = new EngineData(plan);
        presenter = new DashPresenterFactory(plan, engine, engineData, scene, scene.getPresenter());
        dashScene = LapSceneFactory.createDashScene(plan, scene, presenter);
        setContent(new JScrollPane(dashScene.createView()));

        Project[] openProjects = OpenProjects.getDefault().getOpenProjects();
        Set<Project> openProjectsSet = new HashSet<Project>(Arrays.asList(openProjects));
        {
            ClassCrawler actionCrawler = ClassCrawlerFactory.createActionCrawler(openProjectsSet);
            CrawlerListener<PrimitiveData> actionNameCrawler = new NameMapCrawler(scene.getPresenter());
            actionCrawler.addListener(actionNameCrawler);
            actionCrawler.crawl();
        }
        {
            ClassCrawler senseCrawler = ClassCrawlerFactory.createSenseCrawler(openProjectsSet);
            CrawlerListener<PrimitiveData> senseNameCrawler = new NameMapCrawler(scene.getPresenter());
            senseCrawler.addListener(senseNameCrawler);
            senseCrawler.crawl();
        }
    }

    /**
     * When engine reaches evaluation, check if time since last repaint is
     * longer than {@link #REDRAW_INTERVAL_MS} and if it is, repaint the window.
     */
    @Override
    public void evaluationReached() {
        // Do nothing, handled by individual presenters
        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - lastEvaluationFinished;
        if (dashScene != null && elapsedTime > REDRAW_INTERVAL_MS) {
            dashScene.validate();
            dashScene.repaint();
            dashScene.getView().repaint();
        }
    }

    @Override
    public void pathReached(LapPath path) {
        // We don't redraw, it would be too much.
    }

    @Override
    public void evaluationFinished() {
        lastEvaluationFinished = System.currentTimeMillis();
    }

    /**
     * Do nothing, handled by individual presenters
     */
    @Override
    public void breakpointAdded(LapBreakpoint breakpoint) {
    }

    /**
     * Do nothing, handled by individual presenters
     */
    @Override
    public void breakpointRemoved(LapBreakpoint breakpoint) {
    }

    /**
     * When disconnected, display info panel at the top of the window with the
     * message.
     */
    @Override
    public void disconnected(String message, boolean error) {
        if (error) {
            this.add(new InfoPanel("<html>Error: " + message + "</html>"), BorderLayout.NORTH);
        } else {
            this.add(new InfoPanel(message), BorderLayout.NORTH);
        }
    }

    /**
     * Make sure to clean up after window will be closed, disconnect the engine.
     */
    @Override
    protected void componentClosed() {
        engine.disconnect("The debugging window is being closed.", false);
    }

    /**
     * Debugger is never persistent
     * @return {@link #PERSISTENCE_NEVER}.
     */
    @Override
    public int getPersistenceType() {
        return PERSISTENCE_NEVER;
    }

    private void setContent(Container container) {
        this.removeAll();
        this.add(container, BorderLayout.CENTER);
        this.revalidate();
    }
}

class InfoPanel extends JPanel {

    private final JLabel label;

    public InfoPanel(String message) {
        label = new JLabel(message, SwingConstants.CENTER);
        label.setToolTipText(message);
        setLayout(new BorderLayout());
        add(label, BorderLayout.CENTER);
    }
}