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

import cz.cuni.amis.pogamut.ut2004.navigation.evaluator.data.RecordType;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;

/**
 * Helper class for fast creation of batch of {@link NavigationEvaluationTask}s.
 *
 * @author Bogo
 */
public class NavigationEvaluationBatchTaskCreator {

    /**
     * Creates batch of tasks. Generates task for every map in {@code mapNames}
     * with given parameters.
     *
     * @param navigation
     * @param pathPlanner
     * @param mapNames
     * @param onlyRelevantPaths
     * @param limit
     * @param resultPath
     * @param recordType
     * @param level
     * @return
     */
    public static List<NavigationEvaluationTask> createBatch(String navigation, String pathPlanner, List<String> mapNames, boolean onlyRelevantPaths, int limit, String resultPath, RecordType recordType, Level level) {
        List<NavigationEvaluationTask> tasks = new LinkedList<NavigationEvaluationTask>();

        for (String map : mapNames) {
            tasks.add(new NavigationEvaluationTask(navigation, pathPlanner, map, onlyRelevantPaths, limit, resultPath, recordType, level));
        }

        return tasks;
    }

    /**
     * Creates batch of tasks. Generates task for every map in {@code mapNames}
     * with given parameters. 
     * 
     * @param navigations
     * @param pathPlanners
     * @param mapName
     * @param onlyRelevantPaths
     * @param limit
     * @param resultPath
     * @param recordType
     * @param level
     * @return 
     */
    public static List<NavigationEvaluationTask> createBatch(List<String> navigations, List<String> pathPlanners, String mapName, boolean onlyRelevantPaths, int limit, String resultPath, RecordType recordType, Level level) {
        List<NavigationEvaluationTask> tasks = new LinkedList<NavigationEvaluationTask>();

        for (String navigation : navigations) {
            for (String pathPlanner : pathPlanners) {
                tasks.add(new NavigationEvaluationTask(navigation, pathPlanner, mapName, onlyRelevantPaths, limit, resultPath, recordType, level));
            }
        }

        return tasks;
    }
}
