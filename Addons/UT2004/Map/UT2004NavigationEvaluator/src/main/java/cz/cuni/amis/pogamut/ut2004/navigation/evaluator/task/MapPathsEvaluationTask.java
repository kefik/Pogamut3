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

import cz.cuni.amis.pogamut.ut2004.navigation.evaluator.bot.MapPathsBotParameters;
import cz.cuni.amis.pogamut.ut2004.navigation.evaluator.bot.MapPathsBot;
import java.io.File;
import java.util.List;
import java.util.logging.Level;

/**
 * Task for evaluation of types of map paths.
 *
 * @author Bogo
 */
public class MapPathsEvaluationTask extends EvaluationTask<MapPathsBotParameters, MapPathsBot> implements IEvaluationTask<MapPathsBotParameters, MapPathsBot> {

    private String navigation;
    private String pathPlanner;
    private String mapName;
    private String resultPath;
    private PathType pathType;
    private int batchNumber = 0;
    private boolean relevantOnly = false;
    private String level = Level.ALL.getName();

    private MapPathsEvaluationTask() {
        super(MapPathsBotParameters.class, MapPathsBot.class);
    }
    
    /**
     * Default constructor.
     * 
     * @param mapName
     * @param navigation
     * @param pathPlanner
     * @param resultPath
     * @param pathType 
     * @param relevantOnly 
     */
    public MapPathsEvaluationTask(String mapName, String navigation, String pathPlanner, String resultPath, PathType pathType, boolean relevantOnly) {
        this();
        this.navigation = navigation;
        this.mapName = mapName;
        this.pathPlanner = pathPlanner;
        this.resultPath = resultPath;
        this.pathType = pathType;
        this.relevantOnly = relevantOnly;
    }

    /**
     * Constructor with explicit batch number. Could be used to differentiate the results for further processing.
     * 
     * @param mapName
     * @param navigation
     * @param pathPlanner
     * @param resultPath
     * @param pathType
     * @param relevantOnly
     * @param batchNumber 
     */
    public MapPathsEvaluationTask(String mapName, String navigation, String pathPlanner, String resultPath, PathType pathType, boolean relevantOnly, int batchNumber) {
        this(mapName, navigation, pathPlanner, resultPath, pathType, relevantOnly);
        this.batchNumber = batchNumber;
    }

    public String getMapName() {
        return mapName;
    }

    public MapPathsBotParameters getBotParams() {
        return new MapPathsBotParameters(this);
    }

    public String getNavigation() {
        return navigation;
    }

    public String getPathPlanner() {
        return pathPlanner;
    }

    public String getResultPath() {
        String fullPath = String.format("%s/%s/", resultPath, getMapName());
        File resultFile = new File(fullPath);
        resultFile.mkdirs();

        return fullPath;
    }

    public PathType getPathType() {
        return pathType;
    }

    /**
     * Gets batch number.
     * 
     * @return 
     */
    public int getBatchNumber() {
        return batchNumber;
    }

    /**
     * Whether is part of explicitly numbered batch.
     * 
     * @return 
     */
    public boolean isBatchTask() {
        return batchNumber > 0;
    }

    /**
     * Creates task from command line arguments.
     *
     * @param args Command line arguments.
     * @return Task built from command line arguments.
     */
    @Deprecated
    public static MapPathsEvaluationTask buildFromArgs(String[] args) {
        //TODO: Check validity of args?
        if (args.length == 6) {
//            String[] pathTypeStrings = args[4].split(";");
//            List<PathType> types = new LinkedList<PathType>();
//            for (String type : pathTypeStrings) {
//                types.add(PathType.valueOf(type));
//            }
            return new MapPathsEvaluationTask(args[0], args[1], args[2], args[3], PathType.valueOf(args[4]), Boolean.parseBoolean(args[5]));
        }
        return null;
    }

    /**
     * Push task as command line arguments.
     *
     * @param command Arguments list to fill.
     */
    @Deprecated
    public void toArgs(List<String> command) {
        command.add(mapName);
        command.add(navigation);
        command.add(pathPlanner);
        command.add(resultPath);

//        String typeString = "";
//        for (PathType type : pathTypes) {
//            typeString += type.name() + ";";
//        }
//        if (!typeString.isEmpty()) {
//            typeString = typeString.substring(typeString.length() - 1);
//        }

        command.add(pathType.name());
    }

    public String getLogPath() {
        return getResultPath() + "log.log";
    }

    public String getFileName() {
        return String.format("MapPaths_%s_%s_%s_%d", navigation, mapName, pathType.name(), batchNumber);
    }

    public boolean getRelevantOnly() {
        return relevantOnly;
    }

    public Level getLogLevel() {
        return Level.parse(level);
    }

    public void setResultBasePath(String basePath) {
        //Do nothing
    }

    /**
     * Path types to count.
     * 
     */
    public enum PathType {
        ALL, NO_JUMPS, NO_LIFTS, NO_JUMP_NO_LIFTS
    }
}
