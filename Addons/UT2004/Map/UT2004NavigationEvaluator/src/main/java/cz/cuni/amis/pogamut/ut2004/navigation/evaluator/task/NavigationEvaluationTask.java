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

import cz.cuni.amis.pogamut.ut2004.navigation.evaluator.FileNames;
import cz.cuni.amis.pogamut.ut2004.navigation.evaluator.bot.BotNavigationParameters;
import cz.cuni.amis.pogamut.ut2004.navigation.evaluator.bot.NavigationEvaluatingBot;
import cz.cuni.amis.pogamut.ut2004.navigation.evaluator.data.RecordType;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;

/**
 * Evaluation task. Consists of map to evaluate and parameters of navigation.
 *
 * @author Bogo
 */
public class NavigationEvaluationTask extends EvaluationTask<BotNavigationParameters, NavigationEvaluatingBot> implements INavigationEvaluationTask {

    private String navigation;
    private String pathPlanner;
    private String mapName;
    private boolean onlyRelevantPaths;
    private String resultPath = null;
    private String resultBasePath;
    private RecordType recordType;
    //Max number of paths to explore
    private int limit;
    private String stringLevel;

    /**
     * Default constructor.
     * 
     * @param navigation
     * @param pathPlanner
     * @param mapName
     * @param onlyRelevantPaths
     * @param limit
     * @param resultBasePath
     * @param recordType 
     * @param level 
     */
    public NavigationEvaluationTask(String navigation, String pathPlanner, String mapName, boolean onlyRelevantPaths, int limit, String resultBasePath, RecordType recordType, Level level) {
        super(BotNavigationParameters.class, NavigationEvaluatingBot.class);
        this.navigation = navigation;
        this.pathPlanner = pathPlanner;
        this.mapName = mapName;
        this.onlyRelevantPaths = onlyRelevantPaths;
        this.limit = limit;
        this.recordType = recordType;
        this.stringLevel = level.getName();

        this.resultBasePath = resultBasePath;
    }

    @Deprecated
    public NavigationEvaluationTask(String navigation, String pathPlanner, String mapName, boolean onlyRelevantPaths, String resultPath) {
        this(navigation, pathPlanner, mapName, onlyRelevantPaths, 30, resultPath, RecordType.FULL, Level.ALL);
    }

    //TODO: Remove
    @Deprecated
    public NavigationEvaluationTask() {
        this("navigation", "fwMap", "DM-TrainingDay", true, 10, "C:/Temp/Pogamut/stats/", RecordType.FULL, Level.ALL);
    }

    /**
     * Creates task from command line arguments.
     *
     * @param args Command line arguments.
     * @return Task built from command line arguments.
     */
    @Deprecated
    public static NavigationEvaluationTask buildFromArgs(String[] args) {
        //TODO: Check validity of args?
        if (args.length == 8) {
            return new NavigationEvaluationTask(args[0], args[1], args[2], Boolean.parseBoolean(args[3]), Integer.parseInt(args[4]), args[5], RecordType.valueOf(args[6]), Level.parse(args[7]));
        }
        return new NavigationEvaluationTask();
    }

    /**
     * Creates {@link BotNavigationParameters} from this task.
     *
     * @return {@link BotNavigationParameters}'s representation of this task.
     */
    public BotNavigationParameters getBotParams() {
        return new BotNavigationParameters(this);
    }

    public String getMapName() {
        return mapName;
    }

    /**
     * Set map for evaluation.
     * 
     * @param mapName 
     */
    public void setMapName(String mapName) {
        this.mapName = mapName;
    }

    public boolean isOnlyRelevantPaths() {
        return onlyRelevantPaths;
    }

    public String getNavigation() {
        return navigation;
    }

    public String getPathPlanner() {
        return pathPlanner;
    }

    public String getResultPath() {
        if (resultPath == null) {
            String basePath = String.format("%s/%s_%s/%s", resultBasePath, navigation, pathPlanner, mapName);
            SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyy_HHmmss");
            String fullPath = String.format("%s/%s/", basePath, dateFormat.format(new Date()));
            File resultFile = new File(fullPath);
            resultFile.mkdirs();
            this.resultPath = fullPath;
        }
        return resultPath;
    }

    public String getResultBasePath() {
        return resultBasePath;
    }

    public int getLimit() {
        return limit;
    }

    public String getLogPath() {
        return FileNames.joinPath(getResultPath(), FileNames.LOG_FILE);
    }

    public void setNavigation(String navigation) {
        this.navigation = navigation;
    }

    public void setPathPlanner(String pathPlanner) {
        this.pathPlanner = pathPlanner;
    }

    public void setOnlyRelevantPaths(boolean onlyRelevantPaths) {
        this.onlyRelevantPaths = onlyRelevantPaths;
    }

    public void setResultPath(String resultPath) {
        this.resultPath = resultPath;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public RecordType getRecordType() {
        return recordType;
    }

    /**
     * Push task as command line arguments.
     *
     * @param command Arguments list to fill.
     */
    @Deprecated
    public void toArgs(List<String> command) {
        command.add(navigation);
        command.add(pathPlanner);
        command.add(mapName);
        command.add(Boolean.toString(onlyRelevantPaths));
        command.add(Integer.toString(limit));
        command.add(resultBasePath);
        command.add(recordType.name());
    }

    public String getFileName() {
        return String.format("NavigationEvaluation_%s_%s_%s", navigation, pathPlanner, mapName);
    }

    public Level getLogLevel() {
        return Level.parse(stringLevel);
    }

    public void setResultBasePath(String basePath) {
        if(resultBasePath.isEmpty()) {
            this.resultBasePath = basePath;
        } else {
            this.resultBasePath = resultBasePath.replace("base:", basePath);
        }
        this.resultPath = null;
    }
}
