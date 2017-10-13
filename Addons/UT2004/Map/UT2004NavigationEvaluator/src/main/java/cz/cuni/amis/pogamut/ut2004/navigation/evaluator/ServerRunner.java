/*
 * Copyright (C) 2013 AMIS research group, Faculty of Mathematics and Physics, Charles University in Prague, Czech Republic
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package cz.cuni.amis.pogamut.ut2004.navigation.evaluator;

import cz.cuni.amis.pogamut.ut2004.navigation.evaluator.task.IEvaluationTask;
import cz.cuni.amis.utils.exception.PogamutException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Server runner for evaluation. Capable of multiple concurrent evaluations.
 *
 * @author Bogo
 */
public class ServerRunner {

    private static final Properties properties = new Properties();
    
    private static String label;

    public static final boolean isLab = true;

    /**
     * Indicates whether to compress evaluation results.
     * @return Whether to compress evaluation results.
     */
    public static boolean doCompress() {
        return Boolean.parseBoolean(properties.getProperty("compress"));
    }

    /**
     * Indicates whether to delete logs from evaluation results.
     * @return Whether to delete logs from evaluation results.
     */
    public static boolean doDelete() {
        return Boolean.parseBoolean(properties.getProperty("delete"));
    }

    static {
        try {
            loadProperties();
        } catch (IOException ex) {
            Logger.getLogger(ServerRunner.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Gets current execution directory.
     * 
     * @return the executionDir
     */
    public static String getExecutionDir() {
        return properties.getProperty("execution.dir");
    }

    /**
     * Gets UT2004 home directory.
     * 
     * @return the unrealHome
     */
    public static String getUnrealHome() {
        return properties.getProperty("unreal.home");
    }

    /**
     * Gets the path for records.
     * 
     * @return the recordsPath
     */
    public static String getRecordsPath() {
        return getUnrealHome() + "/Demos";
    }

    /**
     * Gets limit of path records before restart of the server.
     * 
     * @return limit of path records before restart of the server
     */
    public static int getPathRecordsLimit() {
        return Integer.parseInt(properties.getProperty("path.records.limit"));
    }

    /**
     * Gets base directory for evaluation results.
     * 
     * @return base directory for evaluation results
     */
    static String getStatsBasePath() {
        return properties.getProperty("stats.dir");
    }
    
    /**
     * Gets processor multiplier. Indicated how many processes the evaluation should start on each available processor.
     * 
     * @return processor multiplier
     */
    static int getProcessorMultiplier() {
        return Integer.parseInt(properties.getProperty("proc.multiplier"));
    }

    /**
     * Constructor.
     */
    public ServerRunner() {
        log.setLevel(Level.ALL); 
        ConsoleHandler handler = new ConsoleHandler();
        handler.setFormatter(new SimpleFormatter());
        handler.setLevel(Level.ALL);
        log.addHandler(handler);

    }

    private final List<EvaluatorHandle> evaluations = new LinkedList<EvaluatorHandle>();
    private List<File> tasks;
    private boolean isResume = false;
    private static final Logger log = Logger.getLogger("ServerRunner");

    /**
     * Initializes a list of {@link IEvaluationTask} represented in XML by
     * XSTream serialization. Uses passed directory or current of omitted.
     *
     * @param args
     */
    public void initTasks(String[] args) {
        tasks = new ArrayList<File>();

        String evalDir = ".";
        if (args.length >= 1) {
            evalDir = args[0];
        }
        File x = new File(evalDir);
        FilenameFilter filter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(".eval.xml");
            }
        };
        tasks.addAll(Arrays.asList(x.listFiles(filter)));
        if (args.length >= 2) {
            isResume = args[1].equals("--resume");
            if(!isResume) {
                label = args[1];
            } else if(args.length >= 3) {
                label = args[2];
            }
        }
    }

    /**
     * Main method for ServerRunner. Evaluates task from given directory.
     *
     * @param args
     * @throws PogamutException
     */
    public static void main(String args[]) {
        ServerRunner runner = new ServerRunner();

        ServerRunner.initRunner(args);

        runner.initTasks(args);

        //Check if there are enough cores available for multiple concurrent evaluations.
        if (hasCapacityForMultiEvaluation() && runner.getTasks().size() > 1) {
            log.fine("Multi evaluation");
            runner.run(label);
        } else {
            //Run the evaluation directly
            log.fine("Direct evaluation");
            DirectRunner directRunner = new DirectRunner(runner, label);
            directRunner.run(runner.isResume);
        }

        System.exit(0);
    }

    /**
     * Loop of the runner. Every 5 seconds checks for finished tasks and
     * available resources to start new evaluations.
     */
    private void run(String label) {
        log.log(Level.INFO, "Starting multiple evaluation of {0} tasks", tasks.size());
        boolean done = tasks.isEmpty();
        while (!done) {
            boolean hasFreeTasks = true;
            while (hasCapacity() && hasFreeTasks) {
                File task = getFreeTask();
                if (task == null) {
                    hasFreeTasks = false;
                } else {
                    EvaluatorHandle handle = new EvaluatorHandle();
                    if (handle.createEvaluator(task, log, isResume, label)) {
                        log.fine("Created new evaluation handler");
                        evaluations.add(handle);
                    }
                }
            }

            log.log(Level.INFO, "Tasks in progress: {0}, Unfinished tasks: {1}", new Object[]{evaluations.size(), tasks.size()});
            try {
                Thread.sleep(5000);
            } catch (InterruptedException ex) {
                //Bla bla
            }
            checkRunningEvaluations();
            done = tasks.isEmpty();
        }

    }

    /**
     * Checks status of current evaluations.
     */
    private void checkRunningEvaluations() {
        List<EvaluatorHandle> finishedHandles = new LinkedList<EvaluatorHandle>();
        for (EvaluatorHandle handle : evaluations) {
            switch (handle.getStatus()) {
                case NEW:
                    break;
                case CREATED:
                    break;
                case RUNNING:
                    break;
                case NOT_RESPONDING:
                    break;
                case FAILED:
                case DESTROYED:
                case COMPLETED:
                    File task = handle.getTask();
                    log.log(Level.INFO, "Tasks completed. Status: {0}", handle.getStatus());
                    tasks.remove(task);
                    finishedHandles.add(handle);
                    break;
                default:
                    throw new AssertionError(handle.getStatus().name());
            }
        }
        for (EvaluatorHandle handle : finishedHandles) {
            evaluations.remove(handle);
        }
    }

    /**
     * Check if there are enough cores available for multiple concurrent
     * evaluations.
     *
     * @return
     */
    private static boolean hasCapacityForMultiEvaluation() {
        //    return true;
        int threshold = 3;
        int available = Runtime.getRuntime().availableProcessors() * ServerRunner.getProcessorMultiplier();
        return available >= threshold;
    }

    /**
     * Checks for available resources.
     *
     * @return
     */
    private boolean hasCapacity() {
        int used = 0 + evaluations.size() * 2;
        int available = Runtime.getRuntime().availableProcessors() * ServerRunner.getProcessorMultiplier();
        return used < available;
    }

    /**
     * Gets new task to evaluate.
     *
     * @return
     */
    protected File getFreeTask() {
        for (File task : tasks) {
            boolean isFree = true;
            for (EvaluatorHandle handle : evaluations) {
                if (handle.getTask() == task) {
                    isFree = false;
                    break;
                }
            }
            if (isFree) {
                return task;
            }
        }
        return null;
    }

    /**
     * Gets list of tasks.
     *
     * @return
     */
    public List<File> getTasks() {
        return tasks;
    }

    private static void initRunner(String[] args) {
        if (args.length > 1) {
            //Not supported now
            //unrealHome = args[1];
            //executionDir = args[2];
            //recordsPath = args[1] + "/Demos";
            

            log.log(Level.INFO, "Unreal home: {0}", getUnrealHome());
            log.log(Level.INFO, "Exec dir: {0}", getExecutionDir());
        }
    }

    private static void loadProperties() throws FileNotFoundException, IOException {
        File propertiesFile = new File("config.properties");
        if (!propertiesFile.exists()) {
            throw new FileNotFoundException("Required properties file config.properties doesn't exist!");
        }
        properties.load(new FileInputStream(propertiesFile));
    }
}
