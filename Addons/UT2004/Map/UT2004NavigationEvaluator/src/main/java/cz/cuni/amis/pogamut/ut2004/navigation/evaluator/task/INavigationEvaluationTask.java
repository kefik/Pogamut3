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
package cz.cuni.amis.pogamut.ut2004.navigation.evaluator.task;

import cz.cuni.amis.pogamut.ut2004.navigation.evaluator.bot.BotNavigationParameters;
import cz.cuni.amis.pogamut.ut2004.navigation.evaluator.bot.NavigationEvaluatingBot;
import cz.cuni.amis.pogamut.ut2004.navigation.evaluator.data.RecordType;

/**
 * Interface for navigation evaluation task. Extends base interface with navigation specific parameters.
 *
 * @author Bogo
 */
public interface INavigationEvaluationTask extends IEvaluationTask<BotNavigationParameters, NavigationEvaluatingBot> {

    /**
     * Set limit of evaluated paths.
     * 
     * @param limit 
     */
    public void setLimit(int limit);

    /**
     * Get path planner for evaluation.
     * 
     * @return 
     */
    public String getPathPlanner();

    /**
     * Get limit of evaluated paths.
     * 
     * @return 
     */
    public int getLimit();

    /**
     * Set if evaluate only relevant paths.
     * 
     * @param onlyRelevantPaths 
     */
    public void setOnlyRelevantPaths(boolean onlyRelevantPaths);

    /**
     * Whether to evaluate relevant paths only.
     * 
     * @return 
     */
    public boolean isOnlyRelevantPaths();

    /**
     * Set path for storing results.
     * 
     * @param resultPath 
     */
    public void setResultPath(String resultPath);

    /**
     * Set path planner.
     * 
     * @param pathPlanner 
     */
    public void setPathPlanner(String pathPlanner);

    /**
     * Get navigation for evaluation.
     * 
     * @return 
     */
    public String getNavigation();

    /**
     * Set navigation for evaluation.
     * 
     * @param navigation 
     */
    public void setNavigation(String navigation);
    
    /**
     * Get path where results will be stored.
     * 
     * @return 
     */
    public String getResultPath();
    
    /**
     * Get type of records to create.
     * 
     * @return 
     */
    public RecordType getRecordType();
    
}
