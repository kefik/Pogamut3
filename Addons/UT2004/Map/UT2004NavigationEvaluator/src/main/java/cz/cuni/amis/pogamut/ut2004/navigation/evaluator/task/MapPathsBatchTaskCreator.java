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

import cz.cuni.amis.pogamut.ut2004.navigation.evaluator.task.MapPathsEvaluationTask.PathType;
import java.util.LinkedList;
import java.util.List;

/**
 * Helper class for creating batches of {@link MapPathsEvaluationTask}s.
 *
 * @author Bogo
 */
public class MapPathsBatchTaskCreator {
    
    private int batchNumber;
    private List<String> maps;
    private String navigation;
    private String pathPlanner;
    private String resultPath;
    private List<PathType> pathTypes;
    private boolean relevantOnly;
    
    /**
     * Default constructor.
     * 
     * @param batchNumber
     * @param maps
     * @param navigation
     * @param pathPlanner
     * @param resultPath
     * @param pathTypes 
     */
    public MapPathsBatchTaskCreator(int batchNumber, List<String> maps, String navigation, String pathPlanner, String resultPath, List<PathType> pathTypes, boolean relevantOnly) {
        this.batchNumber = batchNumber;
        this.maps = maps;
        this.navigation = navigation;
        this.pathPlanner = pathPlanner;
        this.resultPath = resultPath;
        this.pathTypes = pathTypes;
        this.relevantOnly = relevantOnly;
    }
    
    /**
     * Create batch of tasks.
     * 
     * @return 
     */
    public List<MapPathsEvaluationTask> createBatch() {
        List<MapPathsEvaluationTask> tasks = new LinkedList<MapPathsEvaluationTask>();
        
        for (String map : maps) {
            for (PathType type : pathTypes) {
                tasks.add(new MapPathsEvaluationTask(map, navigation, pathPlanner, resultPath, type, relevantOnly, batchNumber));
            }
        }
        
        return tasks;
    }    
    
}
