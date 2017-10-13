package cz.cuni.amis.pogamut.sposh.ut2004;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;

import cz.cuni.amis.pogamut.base.utils.guice.AgentScoped;
import cz.cuni.amis.pogamut.sposh.elements.ParseException;
import cz.cuni.amis.pogamut.sposh.elements.PoshParser;
import cz.cuni.amis.pogamut.sposh.elements.PoshPlan;
import cz.cuni.amis.pogamut.sposh.engine.FireResult;
import cz.cuni.amis.pogamut.sposh.engine.PoshEngine;
import cz.cuni.amis.pogamut.sposh.engine.PoshEngine.EvaluationResultInfo;
import cz.cuni.amis.pogamut.sposh.engine.timer.ITimer;
import cz.cuni.amis.pogamut.sposh.engine.timer.SystemClockTimer;
import cz.cuni.amis.pogamut.sposh.executor.ILogicWorkExecutor;
import cz.cuni.amis.pogamut.sposh.executor.IWorkExecutor;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotLogicController;

/**
 * Logic controller that utilizes sposh engine for decision making of bot in UT2004
 * environment.
 * <p/>
 * Sposh requires two things: plan and primitives. The plan is provided by 
 * {@link SposhLogicController#getPlan() } method and are supplied by {@link IWorkExecutor}.
 * {@link IWorkExecutor} is instantiated during first call of logic, so if the varioud
 * modules are already initialized.
 * <p/>
 * If needed, override {@link SposhLogicController#createTimer() }, but it
 * shouldn't be needed.
 * @author Honza H.
 */
@AgentScoped
public abstract class SposhLogicController<BOT extends UT2004Bot, WORK_EXECUTOR extends IWorkExecutor> extends UT2004BotLogicController<BOT> {

    public static final String SPOSH_LOG_CATEGORY = "SPOSH";
    /**
     * Posh engine that is evaluating the plan.
     */
    private List<PoshEngine> engines = new LinkedList<PoshEngine>();
    /**
     * Primitive executor that is executing the primitves when engine requests it,
     * passes the variables and returns the value of executed primitive.
     */
    private WORK_EXECUTOR workExecutor;
    /**
     * Timer for posh engine, for things like timeouts and so on.
     */
    private ITimer timer;

    /**
     * {@inheritDoc}.
     *
     * Yaposh egnines that will be used by this bot are created here (so {@link #getEngines()
     * } doesn't return empty list after this).
     *
     * @param bot
     */
    @Override
    public void initializeController(BOT bot) {
        super.initializeController(bot);
        createEngines();
    }

    private void createEngines() {
        try {
            int planId = 0;
            for (String planSrc : getPlans()) {
                engines.add(createEngine(planId++, planSrc));
            }
        } catch (IOException ex) {
            bot.getLogger().getCategory(SPOSH_LOG_CATEGORY).log(Level.SEVERE, "IOException {0} - Stacktrace:\n{1}", new Object[]{ex.getMessage(), getStackTrace(ex)});
            throw new IllegalStateException(ex);
        }
    }

    private String getStackTrace(Exception ex) {
        StringWriter sw = new StringWriter();
        ex.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }
    
    /**
     * Create {@link IWorkExecutor} that will execute primitives contained in the plan.
     * This method will be called only once.
     * @return executor to execute primitives.
     */
    protected abstract WORK_EXECUTOR createWorkExecutor();

    /**
     * Get work executor. If work executor is not yet created, create one using
     * {@link SposhLogicController#createWorkExecutor() }.
     * @return work executor for this bot controller.
     */
    protected final WORK_EXECUTOR getWorkExecutor() {
        if (workExecutor == null) {
            workExecutor = createWorkExecutor();
        }
        return workExecutor;
    }

    /**
     * Logic method evaluates all Yaposh plans in same order as they were
     * specified. 
     */
    @Override
    public final void logic() {
        if (!logicBeforePlan()) {
        	getLog().log(Level.INFO, "Bot LOGIC not ready to run; logicBeforePlan returned false.");
        	return;
        }
        for (int engineId = 0; engineId < engines.size(); engineId++) {
            iterateEngine(engines.get(engineId), engineId);
        }
        logicAfterPlan();
    }

    private void iterateEngine(PoshEngine engine, int engineId) {
        engine.getLog().log(Level.INFO, "Invoking Yaposh engine " + engineId + " for plan: " + engine.getName());
        while (true) {
            EvaluationResultInfo result = engine.evaluatePlan(getWorkExecutor());
            engine.getLog().fine("Element result: " + result.result + " / " + result.type);
            if (result.result == PoshEngine.EvaluationResult.ELEMENT_FIRED) {
                if (result.type != null && (result.type == FireResult.Type.CONTINUE || result.type == FireResult.Type.FOLLOW || result.type == FireResult.Type.FULFILLED
                		                    || result.type == FireResult.Type.SURFACE_CONTINUE || result.type == FireResult.Type.FAILED)) {
                    engine.getLog().info("Plan evaluation continues...");
                    continue;
                }
            }
            break;
        }
        engine.getLog().info("Plan evaluation end.");
    }
    
    /**
     * Method that is triggered every time before yaPOSH plans are evaluated.
     * It is triggered right before the plan evaluation.
     * Currently, it checks if {@link SposhLogicController#workExecutor} is a
     * {@link ILogicWorkExecutor} and if it is, it executes {@link ILogicWorkExecutor#logicBeforePlan() }.
     * 
     * @return whether yaPOSH plans may be evaluated (false == skip yaPOSH)
     */
    protected boolean logicBeforePlan() {
    	return true;
    }

    /**
     * Method that is triggered every time the plan for executor is evaluated.
     * It is triggered right after the plan evaluation.
     * Currently, it checks if {@link SposhLogicController#workExecutor} is a
     * {@link ILogicWorkExecutor} and if it is, it executes {@link ILogicWorkExecutor#logicBeforePlan() }.
     */
    protected void logicAfterPlan() {
    }

    /**
     * Create timer for posh engine. By default, use {@link SystemClockTimer}.
     * @see SposhLogicController#getTimer() 
     * @return
     */
    protected ITimer createTimer() {
        return new SystemClockTimer();
    }

    /**
     * Get timer that is used by posh engine to make sure timeouts and other stuff
     * that requires time are working properly.
     * @return
     */
    protected final ITimer getTimer() {
        if (timer == null) {
            timer = createTimer();
        }
        return timer;
    }

    /**
     * Parse the supplied plan.
     * @param planSource plan source to be parsed
     * @return parsed plan
     * @throws ParseException if there is an syntax error in the plan.
     */
    private PoshPlan parsePlan(String planSource) throws ParseException {
        StringReader planReader = new StringReader(planSource);
        PoshParser parser = new PoshParser(planReader);
        return parser.parsePlan();
    }

    /**
     * Create one engine for passed @planSrc.
     *
     * @param engineId Id of created engine
     * @param planSrc Source of the plan for the engine.
     * @throws IllegalArgumentException If @planSrc can't be parsed.
     */
    private PoshEngine createEngine(int engineId, String planSrc) {
        try {
            PoshPlan plan = parsePlan(planSrc);
            return new PoshEngine(engineId, plan, getTimer(), bot.getLogger().getCategory(SPOSH_LOG_CATEGORY));
        } catch (ParseException ex) {
            bot.getLogger().getCategory(SPOSH_LOG_CATEGORY).log(Level.SEVERE, "Parse exceptions during parsing plan:\n{0}\nStacktrace:\n{1}", new Object[]{planSrc, getStackTrace(ex)});
            throw new IllegalArgumentException(ex);
        }
    }

    /**
     * Get engines used by this bot.
     *
     * @return Empty list if engine wasn't yet created(they are created in {@link SposhLogicController#initializeController(UT2004Bot)
     * } ) or the engines.
     */
    protected final List<PoshEngine> getEngines() {
        return engines;
    }

    /**
     * Get all Yaposh plans this bot is supposed to execute. The plans will be
     * executed in same order as in the returned list. Easiest way is to use
     * {@link #getPlanFromResource(java.lang.String) }, {@link #getPlanFromFile(java.lang.String)},
     * {@link #getPlanFromStream(java.io.InputStream) or {@link #getPlansFromDirectory(String)}.
     * }.
     *
     * @return List of sources of the plans
     */
    protected abstract List<String> getPlans() throws IOException;

    /**
     * Read POSH plan from the stream and return it. Close the stream.
     * @param in Input stream from which the plan is going to be read
     * @return Text of the plan, basically content of the stream
     * @throws IOException If there is some error while reading the stream
     */
    protected final String getPlanFromStream(InputStream in) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(in));

        StringBuilder plan = new StringBuilder();
        String line;
        try {
            while ((line = br.readLine()) != null) {
                plan.append(line);
            }
        } finally {
            br.close();
        }

        return plan.toString();
    }
    
    /**
     * Reads all '.lap' file from specified directory. They are alphabetically sorted (ascending order).
     * @param directoryPath
     * @return
     * @throws IOException
     */
    protected final List<String> getPlansFromDirectory(String directoryPath) throws IOException {
    	List<File> files = new ArrayList<File>();
    	for (File file : new File(directoryPath).listFiles()) {
    		if (".lap".equals(file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf(".")))) {
    			files.add(file);
    		}
    	}
    	Collections.sort(files, new Comparator<File>() {
			@Override
			public int compare(File o1, File o2) {
				return o1.getAbsolutePath().compareTo(o2.getAbsolutePath());
			}
    		
    	});
    	List<String> result = new ArrayList<String>();
    	for (File file : files) {
    		result.add(getPlanFromFile(file.getAbsolutePath()));
    	}
    	return result;
    }

    /**
     * Read POSH plan from the file and return it.
     * @param filename Path to the file that contains the POSH plan.
     * @return Text of the plan, basically content of the file
     * @throws IOException If there is some error while reading the stream
     */
    protected final String getPlanFromFile(String filename) throws IOException {
        FileInputStream f = new FileInputStream(filename);
        return getPlanFromStream(f);
    }

    /**
     * Get POSh plan from resource int the same jar as the class.
     * <p>
     * <pre>
     *  // Plan is stored in package cz.cuni.amis.pogamut.testbot under name poshPlan.lap
     *  // This can get the file from .jar or package structure
     *  getPlanFromResource("cz/cuni/amis/pogamut/testbot/poshPlan.lap");
     * </pre>
     * @param resourcePath Path to the plan in some package
     * @return Content of the plan.
     * @throws IOException if something goes wrong, like file is missing, hardisk has blown up ect.
     */
    protected final String getPlanFromResource(String resourcePath) throws IOException {
        ClassLoader cl = this.getClass().getClassLoader();
        return getPlanFromStream(cl.getResourceAsStream(resourcePath));
    }
}
