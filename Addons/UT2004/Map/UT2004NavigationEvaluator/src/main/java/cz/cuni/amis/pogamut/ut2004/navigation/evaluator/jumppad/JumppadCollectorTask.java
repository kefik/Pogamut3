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
package cz.cuni.amis.pogamut.ut2004.navigation.evaluator.jumppad;

import cz.cuni.amis.pogamut.ut2004.navigation.evaluator.bot.TaskBotParameters;
import cz.cuni.amis.pogamut.ut2004.navigation.evaluator.task.EvaluationTask;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * Task for collecting data about jump pads for their highlighting in the map
 * geometry before creating mesh.
 *
 * @author Bogo
 */
public class JumppadCollectorTask extends EvaluationTask<TaskBotParameters, JumppadCollectorBot> {

    private String mapName;
    private String resultPath;

    public JumppadCollectorTask() {
        super(TaskBotParameters.class, JumppadCollectorBot.class);
    }

    private JumppadCollectorTask(String map, String resultPath) {
        this();
        this.mapName = map;
        this.resultPath = resultPath;
    }

    public String getMapName() {
        return mapName;
    }

    public String getResultPath() {
        String fullPath = String.format("%s/", resultPath, getMapName());
        File resultFile = new File(fullPath);
        resultFile.mkdirs();

        return fullPath;
    }

    public String getLogPath() {
        return null;
    }

    public String getFileName() {
        return String.format("%s.jumppads", mapName);
    }

    public void setResultBasePath(String basePath) {
        //Do nothing
    }

    public TaskBotParameters getBotParams() {
        return new TaskBotParameters<JumppadCollectorTask>(this);
    }

    public Level getLogLevel() {
        return Level.OFF;
    }

    public static List<JumppadCollectorTask> createBatch(List<String> mapNames, String resultPath) {
        List<JumppadCollectorTask> list = new ArrayList<JumppadCollectorTask>(mapNames.size());
        for (String map : mapNames) {
            JumppadCollectorTask task = new JumppadCollectorTask(map, resultPath);
            list.add(task);
        }
        return list;
    }

}
