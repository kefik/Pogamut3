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

import cz.cuni.amis.pogamut.ut2004.navigation.evaluator.data.EvaluationResult;
import cz.cuni.amis.pogamut.ut2004.navigation.evaluator.task.INavigationEvaluationTask;

/**
 * Extended bot navigation parameters allowing resume after restart of the
 * server and resume of the interrupted execution.
 *
 * @author Bogo
 */
public class ExtendedBotNavigationParameters extends BotNavigationParameters {

    private PathContainer pathContainer;
    private EvaluationResult result;
    private int iteration;

    /**
     * Construct from bot navigation parameters, current path container and
     * intermediate results.
     *
     * @param params Parameters to extend.
     * @param container Current path container.
     * @param result Intermediate result.
     */
    public ExtendedBotNavigationParameters(BotNavigationParameters params, PathContainer container, EvaluationResult result) {
        this(params.getTask(), container, result);
    }

    /**
     * Construct from task, current path container and intermediate results.
     *
     * @param task Task to resume.
     * @param container Current path container.
     * @param result Intermediate result.
     */
    public ExtendedBotNavigationParameters(INavigationEvaluationTask task, PathContainer container, EvaluationResult result) {
        super(task);
        this.pathContainer = container;
        this.result = result;
        this.iteration = 1;
    }

    /**
     * Gets current path container.
     *
     * @return Path container.
     */
    public PathContainer getPathContainer() {
        return pathContainer;
    }

    /**
     * Sets path container.
     *
     * @param pathContainer Path container.
     */
    public void setPathContainer(PathContainer pathContainer) {
        this.pathContainer = pathContainer;
    }

    /**
     * Gets current evaluation results.
     *
     * @return Evaluation results.
     */
    public EvaluationResult getEvaluationResult() {
        return result;
    }

    /**
     * Sets current evaluation results.
     *
     * @param result Evaluation results.
     */
    public void setEvaluationResult(EvaluationResult result) {
        this.result = result;
    }

    /**
     * Gets current iteration.
     * @return Iteration.
     */
    public int getIteration() {
        return iteration;
    }

    /**
     * Ups iteration number.
     */
    public void upIteration() {
        ++iteration;
    }

    /**
     * Sets iteration.
     * 
     * @param i Iteration.
     */
    public void setIteration(int i) {
        iteration = i;
    }
}
