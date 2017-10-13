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

import cz.cuni.amis.utils.process.ProcessExecution;
import cz.cuni.amis.utils.process.ProcessExecutionConfig;
import java.io.File;
import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * Handle for {@link SingleTaskEvaluator}. Runs {@link SingleTaskEvaluator} in
 * new JVM.
 *
 * @author Bogo
 */
public class EvaluatorHandle {

    private Status status;
    private ProcessExecution processExecution;
    private File task;
    //Allows debugging of the newly created JVM
    private boolean isDebug = false;

    /**
     * Returns file with XML representation of assigned evaluation task.
     *
     * @return
     */
    public File getTask() {
        return task;
    }

    public EvaluatorHandle() {
        status = Status.NEW;
    }

    /**
     * Creates new JVM and starts {@link SingleTaskEvaluator} in it.
     *
     * @param task
     * @param log
     * @param isResume
     * @param label
     * @return
     */
    public boolean createEvaluator(File task, Logger log, boolean isResume, String label) {
        this.task = task;
        if (status != Status.NEW) {
            return false;
        }

        String separator = System.getProperty("file.separator");
        String classpath = System.getProperty("java.class.path");
        String path = System.getProperty("java.home")
                + separator + "bin" + separator + "java";
        ArrayList<String> command = new ArrayList<String>();
        command.add("-cp");
        command.add(classpath);

        //Debugging options
        if (isDebug) {
            command.add("-Xdebug");
            command.add("-Xrunjdwp:transport=dt_socket,server=y,address=8888,suspend=y");
        }
        
        command.add("-Xms128m");
        command.add("-Xmx1024m");

        command.add(SingleNavigationTaskEvaluator.class.getName());
        command.add(task.getAbsolutePath());
        if(isResume) {
            command.add("--resume");
        }
        if(label != null) {
            command.add(label);
        }

        ProcessExecutionConfig config = new ProcessExecutionConfig();
        config.setPathToProgram(path);
        config.setExecutionDir(".");
        config.setArgs(command);
        config.setRedirectStdErr(false);
        config.setRedirectStdOut(false);
        processExecution = new ProcessExecution(config, log);

        processExecution.start();

        status = Status.CREATED;
        return true;
    }

    /**
     * Gets status of the execution.
     *
     * @return
     */
    public Status getStatus() {
        if (processExecution != null) {
            if (processExecution.isRunning()) {
                return Status.RUNNING;
            }
            switch (processExecution.getExitValue()) {
                case 0:
                    status = Status.COMPLETED;
                    processExecution = null;
                    break;
                case -1:
                case -2:
                case -3:
                    status = Status.FAILED;
                    break;
            }
        }

        return status;
    }

    /**
     * Status of handle.
     */
    public enum Status {

        NEW, CREATED, RUNNING, NOT_RESPONDING, FAILED, DESTROYED, COMPLETED
    }
}
