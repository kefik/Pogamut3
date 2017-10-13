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

import cz.cuni.amis.pogamut.ut2004.navigation.evaluator.bot.EnvelopeBot;
import cz.cuni.amis.pogamut.ut2004.navigation.evaluator.bot.TaskBotParameters;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * Task for evaluation of types of map paths.
 *
 * @author Bogo
 */
public class MapEnvelopeTask extends EvaluationTask<TaskBotParameters, EnvelopeBot> implements IEvaluationTask<TaskBotParameters, EnvelopeBot> {

    private String mapName;
    private String resultPath;

    private MapEnvelopeTask() {
        super(TaskBotParameters.class, EnvelopeBot.class);
    }

    /**
     * Default constructor.
     *
     * @param mapName
     * @param resultPath
     */
    public MapEnvelopeTask(String mapName, String resultPath) {
        this();
        this.mapName = mapName;
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
        return getResultPath() + "log.log";
    }

    public String getFileName() {
        return String.format("%s.envelope", mapName);
    }

    public void setResultBasePath(String basePath) {
        //Do nothing
    }

    public TaskBotParameters getBotParams() {
        return new TaskBotParameters<MapEnvelopeTask>(this);
    }

    public Level getLogLevel() {
        return Level.OFF;
    }

    public static List<MapEnvelopeTask> createBatch(List<String> mapNames, String resultPath) {
        List<MapEnvelopeTask> list = new ArrayList<MapEnvelopeTask>(mapNames.size());
        for (String map : mapNames) {
            MapEnvelopeTask task = new MapEnvelopeTask(map, resultPath);
            list.add(task);
        }
        return list;
    }
}
