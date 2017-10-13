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

import cz.cuni.amis.pogamut.ut2004.navigation.evaluator.task.EvaluationTaskFactory;
import cz.cuni.amis.pogamut.ut2004.navigation.evaluator.task.IEvaluationTask;
import cz.cuni.amis.pogamut.ut2004.utils.UCCWrapper;
import cz.cuni.amis.pogamut.ut2004.utils.UCCWrapperConf;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.zeroturnaround.zip.ZipUtil;

/**
 * Base class for task evaluators.
 *
 * @author Bogo
 */
public abstract class SingleTaskEvaluatorBase {

    protected static final Logger log = Logger.getLogger("TaskEvaluator");
    private static final boolean REDIRECT_LOG = true;

    /**
     * Main method. Accepts path to task file in args.
     *
     * @param args Path to file, resume, label.
     */
    public static void main(String[] args) {
        log.setLevel(Level.ALL);
        log.fine("Running SingleTaskEvaluator");
        IEvaluationTask task = EvaluationTaskFactory.build(args);
        boolean isResume = args.length >= 2 && args[1].equals("--resume");

        String label = null;
        if (args.length > 2) {
            label = args[2];
        } else if (args.length == 2 && !isResume) {
            label = args[1];
        }

        task.setResultBasePath(ServerRunner.getStatsBasePath());
        File taskFile = new File(args[0]);
        File taskDefFile = new File(task.getResultPath(), taskFile.getName());
        try {
            //Files.copy(taskFile.toPath(), taskDefFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        	taskDefFile.delete();
        	com.google.common.io.Files.copy(taskFile, taskDefFile);
        } catch (IOException ex) {
            log.log(Level.SEVERE, null, ex);
        }

        log.fine("Task built from args");
        SingleNavigationTaskEvaluator evaluator = new SingleNavigationTaskEvaluator();
        int result = evaluator.execute(task, isResume, label);
        System.exit(result);
    }

    /**
     * Starts UCC server.
     *
     * @param mapName Map which start on server.
     * @return Server wrapper.
     */
    public static UCCWrapper run(String mapName) {
        log.fine("UCC server starting...");
        UCCWrapperConf conf = new UCCWrapperConf();
        conf.setUnrealHome(ServerRunner.getUnrealHome());
        conf.setStartOnUnusedPort(true);
        String gameType = MapInfo.getGameType(mapName);
        conf.setGameType(gameType);
        conf.setMapName(mapName);
        UCCWrapper server = new UCCWrapper(conf);
        server.getLogger().setLevel(Level.WARNING);
        log.fine("UCC server started.");
        return server;
    }

    /**
     * Redirects out to save log in file.
     *
     * @param logPath
     */
    protected void setupLog(String logPath) {
        if (logPath == null) {
            return;
        }
        try {
            if (REDIRECT_LOG) {
                System.setOut(new PrintStream(logPath));
            }
        } catch (FileNotFoundException ex) {
            log.log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Executes given {@link IEvaluationTask}.
     *
     * @param task Task to execute.
     * @return Execution result.
     */
    public abstract int execute(IEvaluationTask task);

    protected void processResult(IEvaluationTask task) {
        String resultPath = task.getResultPath();
        if (ServerRunner.doCompress()) {
            String zipName = resultPath.substring(0, resultPath.length() - 2) + ".zip";
            ZipUtil.pack(new File(resultPath), new File(zipName));
            if (ServerRunner.doDelete()) {
                try {
                    FileUtils.deleteDirectory(new File(resultPath));
                } catch (IOException ex) {
                    Logger.getLogger(SingleTaskEvaluatorBase.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

}
