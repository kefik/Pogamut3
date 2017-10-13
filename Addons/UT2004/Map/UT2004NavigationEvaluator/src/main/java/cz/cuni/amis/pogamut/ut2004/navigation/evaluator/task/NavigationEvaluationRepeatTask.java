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
import java.util.logging.Level;

/**
 * Special case of {@link NavigationEvaluationTask}. Repeats evaluation with
 * parameters and paths taken from previous evaluation. Initialized by supplied
 * evaluation result.
 *
 * @author Bogo
 */
public class NavigationEvaluationRepeatTask extends NavigationEvaluationTask {

    private String repeatFile;

    public NavigationEvaluationRepeatTask() {
    }

    /**
     * Default constructor.
     *
     * @param repeatFile
     * @param navigation
     * @param pathPlanner
     * @param resultPath
     * @param recordType
     */
    public NavigationEvaluationRepeatTask(String repeatFile, String navigation, String pathPlanner, String resultPath, RecordType recordType) {
        super(navigation, pathPlanner, null, true, -1, resultPath, recordType, Level.ALL);
        this.repeatFile = repeatFile;
        int end = repeatFile.length();
        for (int i = 0; i < 2; i++) {
            end = repeatFile.lastIndexOf('/', end - 1);
        }
        int start = repeatFile.lastIndexOf('/', end - 1);
        setMapName(repeatFile.substring(start + 1, end));
    }

    /**
     * Get file with previous result.
     *
     * @return
     */
    public String getRepeatFile() {
        return repeatFile;
    }
}
