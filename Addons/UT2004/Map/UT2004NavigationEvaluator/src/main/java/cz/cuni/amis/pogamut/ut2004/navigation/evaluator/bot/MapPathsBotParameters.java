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

import cz.cuni.amis.pogamut.ut2004.bot.params.UT2004BotParameters;
import cz.cuni.amis.pogamut.ut2004.navigation.evaluator.task.MapPathsEvaluationTask;
import cz.cuni.amis.pogamut.ut2004.navigation.evaluator.task.MapPathsEvaluationTask.PathType;

/**
 * Parameters for {@link MapPathsBot}.
 *
 * @author Bogo
 */
public class MapPathsBotParameters extends UT2004BotParameters {

    private MapPathsEvaluationTask task;

    /**
     * Default constructor.
     * 
     * @param task 
     */
    public MapPathsBotParameters(MapPathsEvaluationTask task) {
        this.task = task;
    }

    public String getPathPlanner() {
        return task.getPathPlanner();
    }

    public String getNavigation() {
        return task.getNavigation();
    }

    public PathType getPathType() {
        return task.getPathType();
    }

    public String getMapName() {
        return task.getMapName();
    }

    public MapPathsEvaluationTask getTask() {
        return task;
    }
    
    public boolean getRelevantOnly() {
        return task.getRelevantOnly();
    }
    
}
