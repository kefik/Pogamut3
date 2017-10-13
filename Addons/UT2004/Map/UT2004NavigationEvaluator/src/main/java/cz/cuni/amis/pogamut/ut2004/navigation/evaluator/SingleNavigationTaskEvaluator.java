/*
 * Copyright (C) 2014 AMIS research group, Faculty of Mathematics and Physics, Charles University in Prague, Czech Republic
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

import cz.cuni.amis.pogamut.base.agent.state.level1.IAgentStateDown;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import cz.cuni.amis.pogamut.ut2004.bot.params.UT2004BotParameters;
import cz.cuni.amis.pogamut.ut2004.navigation.evaluator.bot.EvaluatingBot;
import cz.cuni.amis.pogamut.ut2004.navigation.evaluator.bot.ExtendedBotNavigationParameters;
import cz.cuni.amis.pogamut.ut2004.navigation.evaluator.bot.NavigationEvaluatingBot;
import cz.cuni.amis.pogamut.ut2004.navigation.evaluator.bot.PathContainer;
import cz.cuni.amis.pogamut.ut2004.navigation.evaluator.data.EvaluationResult;
import cz.cuni.amis.pogamut.ut2004.navigation.evaluator.data.RecordType;
import cz.cuni.amis.pogamut.ut2004.navigation.evaluator.task.IEvaluationTask;
import cz.cuni.amis.pogamut.ut2004.navigation.evaluator.task.INavigationEvaluationTask;
import cz.cuni.amis.pogamut.ut2004.navigation.evaluator.task.NavigationEvaluationTask;
import cz.cuni.amis.pogamut.ut2004.server.exception.UCCStartException;
import cz.cuni.amis.pogamut.ut2004.utils.UCCWrapper;
import cz.cuni.amis.pogamut.ut2004.utils.UT2004BotRunner;
import cz.cuni.amis.utils.exception.PogamutException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintStream;
import org.zeroturnaround.zip.ZipUtil;

/**
 * Evaluator for single navigation task. Handles required restarts and log files.
 *
 * @author Bogo
 */
public class SingleNavigationTaskEvaluator extends SingleTaskEvaluator {

    private String currentLog = null;

    /**
     * Evaluates given task.
     * 
     * @param task Task to evaluate.
     * @param isResume Whether should try to resume interrupted task.
     * @param label Label for the evaluation.
     * @return Status code of the evaluation.
     */
    public int execute(IEvaluationTask task, boolean isResume, String label) {
        //We can run the task without path record in standard Evaluator... 
        if (!NavigationEvaluationTask.class.isAssignableFrom(task.getClass())) {
            return super.execute(task);
        } else {
            NavigationEvaluationTask nTask = (NavigationEvaluationTask) task;
            if (nTask.getRecordType() != RecordType.PATH && nTask.getRecordType() != RecordType.PATH_FAILED) {
                return super.execute(task);
            }
        }

        //Set base path for result
        task.setResultBasePath(ServerRunner.getStatsBasePath());

        int status = 0;
        UCCWrapper server = null;
        UT2004Bot bot = null;
        int stopTimeout = 1000 * 60 * (360);
        boolean done = false;

        //We will record single paths and restart ucc regularly...
        int iteration = 0;
        UT2004BotParameters params = task.getBotParams();
        
        if(label != null) {
            File file = new File(task.getResultPath(), label + ".label");
            try {
                file.createNewFile();
            } catch (IOException ex) {
                log.info(ex.getMessage());
            }
        }

        if (isResume) {
            ExtendedBotNavigationParameters extParams = tryResume((NavigationEvaluationTask) task);
            if (extParams != null) {
                params = extParams;
                iteration = extParams.getIteration() - 1;
            } else {
                //No task to resume, just end successfuly
                return 0;
            }
        }

        while (!done) {
            try {
                setupLog(task.getLogPath(), iteration);
                ++iteration;
                server = run(task.getMapName());
                System.out.println("Setting control port to: " + server.getControlPort());
                System.setProperty("pogamut.ut2004.server.port", Integer.toString(server.getControlPort()));
                UT2004BotRunner<UT2004Bot, UT2004BotParameters> botRunner = new UT2004BotRunner<UT2004Bot, UT2004BotParameters>(task.getBotClass(), "EvaluatingBot", server.getHost(), server.getBotPort());
                botRunner.setLogLevel(task.getLogLevel());
                log.fine("Starting evaluation bot.");
                System.out.println("Starting evaluation bot from NavigationTaskEvaluator.");
                bot = botRunner.startAgents(params).get(0); //task.getBotParams()).get(0); //
                bot.awaitState(IAgentStateDown.class, stopTimeout);

            } catch (UCCStartException ex) {
                //Failed to launch UCC!
                status = -1;
                log.throwing(SingleTaskEvaluator.class.getSimpleName(), "execute", ex);
            } catch (PogamutException pex) {
                //Bot execution failed!
                if (bot != null && ((EvaluatingBot) bot.getController()).isCompleted()) {
                    status = 0;
                    log.fine("Evaluation completed");
                    System.out.println("Evaluation completed");
                } else {
                    status = -2;
                    log.throwing(SingleTaskEvaluator.class.getSimpleName(), "execute", pex);
                    System.out.println("Unknown Pogamut exception.");
                }
            } finally {
                if (bot != null && bot.notInState(IAgentStateDown.class)) {
                    bot.stop();
                    bot.kill();
                    //throw new RuntimeException("Bot did not stopped in " + stopTimeout + " ms.");
                    status = 0;
                    System.out.println("Bad termination of bot.");
                }
                if (server != null) {
                    server.stop();
                }
                if (bot != null) {
                    System.out.println("Correct repeat finally..");
                    NavigationEvaluatingBot evalBot = (NavigationEvaluatingBot) bot.getController();
                    if (status == 0) {
                        ExtendedBotNavigationParameters paramsExt = evalBot.getNewExtendedParams();
                        System.out.println("Correct status.");
                        System.out.printf("EVALUATION ITERATION COMPLETED - Processed paths: %d, Remaining paths: %d", paramsExt.getEvaluationResult().getProcessedCount(), paramsExt.getPathContainer().size());
                        if (paramsExt.getEvaluationResult().getProcessedCount() >= paramsExt.getLimitForCompare() || paramsExt.getPathContainer().isEmpty()) {
                            done = true;
                        } else {
                            params = new ExtendedBotNavigationParameters((INavigationEvaluationTask) task, paramsExt.getPathContainer(), paramsExt.getEvaluationResult());
                            ((ExtendedBotNavigationParameters) params).setIteration(paramsExt.getIteration() + 1);
                        }
                        exportPathContainer(task, paramsExt.getPathContainer());
                    }
                }
                if (status != 0) {
                    done = true;
                }
                
                bot = null;
                System.gc();
            }
        }

        System.out.close();

        processResult(task);

        return status;
    }

    @Override
    public int execute(IEvaluationTask task) {
        return execute(task, false, null);
    }

    protected void setupLog(String logPath, int iteration) {
        if (iteration != 0) {
            System.out.close();
            if (currentLog != null) {
                File currentLogFile = new File(currentLog);
                if (ServerRunner.doCompress()) {
                    ZipUtil.packEntry(currentLogFile, new File(currentLog + ".zip"));
                    currentLogFile.delete();
                    File[] demos = currentLogFile.getParentFile().listFiles(new FilenameFilter() {

                        public boolean accept(File dir, String name) {
                            return name.endsWith(".demo4");
                        }
                    });
                    if (demos.length > 0) {
                        ZipUtil.packEntries(demos, new File(currentLogFile.getParent() + "/demos-" + (iteration - 1) + ".zip"));
                        for (File demo : demos) {
                            demo.delete();
                        }
                    }

                }
            }
        }
        try {
            if (logPath != null) {
                currentLog = String.format("%s-%d.log", logPath.substring(0, logPath.length() - 4), iteration);
                System.setOut(new PrintStream(currentLog));
            }
        } catch (FileNotFoundException ex) {
        }
    }

    private void exportPathContainer(IEvaluationTask task, PathContainer pathContainer) {
        String path = FileNames.joinPath(task.getResultPath(), FileNames.PATH_CONTAINER_FILE);
        pathContainer.exportToFile(path);
    }

    private ExtendedBotNavigationParameters tryResume(INavigationEvaluationTask task) {

        File newResultDir = new File(task.getResultPath());
        File lastResult = null;
        long lastResultModification = 0;
        for (File oldResult : newResultDir.getParentFile().listFiles()) {
            if (!oldResult.isDirectory()) {
                continue;
            }
            if (oldResult.equals(newResultDir)) {
                continue;
            }
            if (oldResult.lastModified() > lastResultModification) {
                lastResult = oldResult;
                lastResultModification = oldResult.lastModified();
            }
        }
        for(File newFile : newResultDir.listFiles()) {
            newFile.delete();
        }
        newResultDir.delete();
        if (lastResult == null) {
            return null; //No suitable result to resume from was found.
        }
        File pathContainerFile = FileNames.getFile(lastResult, FileNames.PATH_CONTAINER_FILE);
        if (!pathContainerFile.exists()) {
            return null; //No path container file to resume from.
        }
        task.setResultPath(lastResult.getAbsolutePath());

        PathContainer pathContainer = new PathContainer(null);
        pathContainer.buildFromFile(pathContainerFile, false);

        EvaluationResult results = new EvaluationResult(pathContainer.size(), task.getMapName(), null, task.getResultPath());

        File resultFile = FileNames.getFile(lastResult, FileNames.DATA_FILE);
        if (resultFile.exists()) {
            results.loadFromFile(resultFile, pathContainer.size());
        }

        ExtendedBotNavigationParameters params = new ExtendedBotNavigationParameters(task, pathContainer, results);
        params.setIteration(results.getProcessedCount() / ServerRunner.getPathRecordsLimit() + 1);

        return params;
    }

}
