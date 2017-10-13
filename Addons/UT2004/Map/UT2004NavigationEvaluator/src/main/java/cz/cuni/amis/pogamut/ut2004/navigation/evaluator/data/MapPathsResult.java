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
package cz.cuni.amis.pogamut.ut2004.navigation.evaluator.data;

import cz.cuni.amis.pogamut.ut2004.navigation.evaluator.task.MapPathsEvaluationTask;
import cz.cuni.amis.pogamut.base.utils.logging.LogCategory;
import cz.cuni.amis.pogamut.ut2004.navigation.evaluator.task.MapPathsEvaluationTask.PathType;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Result of map path evaluation.
 *
 * @author Bogo
 */
public class MapPathsResult {

    private LogCategory log;
    private String mapName;
    private PathType pathType;
    private int totalCount;
    private int builtCount;
    private MapPathsEvaluationTask task = null;

    public MapPathsResult(MapPathsEvaluationTask task, LogCategory log) {
        this.task = task;
        this.mapName = task.getMapName();
        this.pathType = task.getPathType();
        this.log = log;
    }

    /**
     * Add successfully built path.
     * 
     */
    public void addSuccessful() {
        ++builtCount;
        ++totalCount;
    }

    /**
     * Add failed to build path.
     * 
     */
    public void addFailed() {
        ++totalCount;
    }

    /**
     * Export complete statistics about evaluation.
     */
    public void export() {
        FileWriter fstream = null;
        try {
            String fileName;
            if (task.isBatchTask()) {
                fileName = String.format("data_%d_%s.csv", task.getBatchNumber(), pathType.name());
            } else {
                fileName = String.format("data_%s.csv", pathType.name());
                File file = new File(task.getResultPath() + fileName);
                int i = 0;
                while (file.exists()) {
                    fileName = String.format("data_%s_%d.csv", pathType.name(), ++i);
                    file = new File(task.getResultPath() + fileName);
                }
            }

            String fullFilePath = task.getResultPath() + fileName;
            File resultFile = new File(fullFilePath);
            resultFile.getParentFile().mkdirs();
            fstream = new FileWriter(resultFile);
            BufferedWriter out = new BufferedWriter(fstream);
            out.write("PathType;Total;Successful");
            out.newLine();
            out.write(String.format("%s;%s;%d;%d\n", mapName, pathType.name(), totalCount, builtCount));
            out.close();
        } catch (IOException ex) {
            log.warning(ex.getMessage());
        } finally {
            try {
                if (fstream != null) {
                    fstream.close();
                }
            } catch (IOException ex) {
                log.warning(ex.getMessage());
            }
        }
    }
}
