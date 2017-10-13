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
package cz.cuni.amis.pogamut.ut2004.navigation.evaluator.bot;

import cz.cuni.amis.pogamut.base.agent.navigation.IPathPlanner;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.IUT2004Navigation;
import cz.cuni.amis.pogamut.ut2004.bot.params.UT2004BotParameters;
import cz.cuni.amis.pogamut.ut2004.navigation.evaluator.task.NavigationEvaluationRepeatTask;
import cz.cuni.amis.pogamut.ut2004.navigation.evaluator.task.INavigationEvaluationTask;
import cz.cuni.amis.pogamut.ut2004.navigation.evaluator.data.RecordType;
import java.util.logging.Level;

/**
 * Navigation parameters for creating custom navigation in
 * {@link NavigationFactory}.
 *
 * @author Bogo
 */
public class BotNavigationParameters extends UT2004BotParameters {

    private INavigationEvaluationTask task;

    public BotNavigationParameters(INavigationEvaluationTask task) {
        this.task = task;
    }

    /**
     * Gets path where the evaluation results should be saved.
     *
     * @return
     *
     */
    public String getResultPath() {
        return task.getResultPath();
    }

    /**
     * Whether to evaluate only relevant paths.
     * 
     * @return 
     */
    public boolean isOnlyRelevantPaths() {
        return task.isOnlyRelevantPaths();
    }

    /**
     * Max number of paths to evaluate. Unlimited represented by -1.
     * 
     * @return 
     */
    public int getLimit() {
        return task.getLimit();
    }

    /**
     * Gets string representation of {@link IPathPlanner} which will be used for evaluation.
     * 
     * @return 
     */
    public String getPathPlanner() {
        return task.getPathPlanner();
    }

    /**
     * Gets string representation of {@link IUT2004Navigation} which will be used for evaluation.
     * 
     * @return 
     */
    public String getNavigation() {
        return task.getNavigation();
    }

    /**
     * Whether this is repeat task.
     * 
     * @return 
     */
    public boolean isRepeatTask() {
        return task instanceof NavigationEvaluationRepeatTask;
    }

    /**
     * Returns path to repeat file if repeat task, null otherwise.
     * 
     * @return 
     * 
     */
    public String getRepeatFile() {
        if (!isRepeatTask()) {
            return null;
        } else {
            return ((NavigationEvaluationRepeatTask) task).getRepeatFile();
        }

    }

    /**
     * Limit for comparison. Replaces -1 with {@code Integer.MAX_VALUE}.
     * 
     * @return 
     */
    public int getLimitForCompare() {
        return task.getLimit() < 0 ? Integer.MAX_VALUE : task.getLimit();
    }

    /**
     * TODO: Unused?
     * @return 
     */
    public String getRecordPath() {
        return String.format("%s_%s_record", getNavigation(), getPathPlanner());
    }

    /**
     * Gets type of record which should be recorded and stored in result of evaluation.
     * 
     * @return 
     */
    public RecordType getRecordType() {
        return task.getRecordType();
    }

    /**
     * Gets task for this evaluation.
     * 
     * @return 
     */
    public INavigationEvaluationTask getTask() {
        return task;
    }

    /**
     * Whether path recording should be used.
     * 
     * @return 
     */
    boolean isPathRecord() {
        return task.getRecordType() == RecordType.PATH || task.getRecordType() == RecordType.PATH_FAILED;
    }

    /**
     * Whether full evaluation recording should be used.
     * 
     * @return 
     */
    boolean isFullRecord() {
        return task.getRecordType() == RecordType.FULL || task.getRecordType() == RecordType.FULL_FAILED;
    }

    /**
     * Whether to keep only records of failed navigation.
     * 
     * @return 
     */
    boolean keepOnlyFailedRecords() {
        return task.getRecordType() == RecordType.PATH_FAILED || task.getRecordType() == RecordType.FULL_FAILED;
    }

    Level getLogLevel() {
        return task.getLogLevel();
    }
}
