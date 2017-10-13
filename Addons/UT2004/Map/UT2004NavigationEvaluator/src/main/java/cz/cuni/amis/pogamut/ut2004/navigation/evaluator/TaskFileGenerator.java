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
package cz.cuni.amis.pogamut.ut2004.navigation.evaluator;

import cz.cuni.amis.pogamut.ut2004.navigation.evaluator.data.RecordType;
import cz.cuni.amis.pogamut.ut2004.navigation.evaluator.jumppad.JumppadCollectorTask;
import cz.cuni.amis.pogamut.ut2004.navigation.evaluator.task.EvaluationTaskFactory;
import cz.cuni.amis.pogamut.ut2004.navigation.evaluator.task.IEvaluationTask;
import cz.cuni.amis.pogamut.ut2004.navigation.evaluator.task.JumpInspectingTask;
import cz.cuni.amis.pogamut.ut2004.navigation.evaluator.task.MapEnvelopeTask;
import cz.cuni.amis.pogamut.ut2004.navigation.evaluator.task.MapPathsBatchTaskCreator;
import cz.cuni.amis.pogamut.ut2004.navigation.evaluator.task.MapPathsEvaluationTask.PathType;
import cz.cuni.amis.pogamut.ut2004.navigation.evaluator.task.NavigationEvaluationBatchTaskCreator;
import cz.cuni.amis.pogamut.ut2004.navigation.evaluator.task.NavigationEvaluationRepeatTask;
import cz.cuni.amis.pogamut.ut2004.navigation.evaluator.task.NavigationEvaluationTask;
import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import org.zeroturnaround.zip.ZipUtil;

/**
 * Helper class for generating task files from code.
 *
 * @author Bogo
 */
public class TaskFileGenerator {

    public static final boolean isLab = true;

    //public static String statsPath = "base:";
    public static String statsPath = "C:/Temp/Pogamut/stats/jumppads";

    /**
     * Generates specified tasks and saves them to specified place.
     *
     * @param args
     */
    public static void main(String args[]) {

        List<IEvaluationTask> myTasks = new LinkedList<IEvaluationTask>();

        List<String> maps = Arrays.asList(
                "DM-DE-Osiris2",
                "DM-Junkyard",
                "DM-Goliath"
        //           "DM-1on1-Albatross",
        //          "DM-1on1-Idoma",
        //         "DM-1on1-Irondust",
        //          "DM-1on1-Mixer",
        //           "DM-1on1-Roughinery",
        //          "DM-1on1-Desolation"
        //           ,"DM-1on1-Crash"
        //            ,"DM-TrainingDay"
        );

//        MapPathsBatchTaskCreator mapPathsBatchTask = new MapPathsBatchTaskCreator(6, MapInfo.getAllMaps(), "navigation", "fwMap", "C:/Temp/Pogamut/all_maps_stats", Arrays.asList(PathType.values()), false);
        //       myTasks.addAll(mapPathsBatchTask.createBatch());
 //       MapPathsBatchTaskCreator mapPathsBatchTask2 = new MapPathsBatchTaskCreator(7, MapInfo.getAllMaps(), "navigation", "fwMap", "C:/Temp/Pogamut/all_maps_stats", Arrays.asList(PathType.values()), true);
        //       myTasks.addAll(mapPathsBatchTask2.createBatch());
//        
//        MapPathsBatchTaskCreator mapPathsBatchTask2 = new MapPathsBatchTaskCreator(5, Arrays.asList("DM-1on1-Serpentine"), "navigation", "fwMap", statsPath + "/maps2502", Arrays.asList(PathType.values()), true);
//        myTasks.addAll(mapPathsBatchTask2.createBatch());
//        MapPathsEvaluationTask taskMapEval = new MapPathsEvaluationTask("DM-1on1-Albatross", "navigation", "fwMap", "C:/Temp/Pogamut/mapStats", PathType.NO_JUMPS);
//        myTasks.add(taskMapEval);
//
        //DM-TrainingDay task
//        NavigationEvaluationTask taskDMTrainingDay = new NavigationEvaluationTask("navigation", "fwMap", "DM-TrainingDay", true, -1, statsPath, RecordType.FULL);
//        myTasks.add(taskDMTrainingDay);
//
////        //DM-Crash task
//        NavigationEvaluationTask taskDMCrash = new NavigationEvaluationTask("navigation", "fwMap", "DM-1on1-Crash", true, 10, statsPath, true, RecordType.NONE);
//        myTasks.add(taskDMCrash);
        //myTasks.addAll(NavigationEvaluationBatchTaskCreator.createBatch("navigation", "fwMap", maps, true, -1, statsPath, RecordType.PATH_FAILED, Level.WARNING));
        // myTasks.addAll(NavigationEvaluationBatchTaskCreator.createBatch("navigation", "fwMap", MapInfo.getAllBRMaps(), true, -1, statsPath, RecordType.PATH_FAILED, Level.WARNING));
        //myTasks.addAll(NavigationEvaluationBatchTaskCreator.createBatch("acc", "navMesh", MapInfo.getAllMaps(), true, -1, statsPath, RecordType.PATH_FAILED, Level.OFF));
        //myTasks.addAll(MapEnvelopeTask.createBatch(MapInfo.getAllMaps(), statsPath));
        myTasks.addAll(JumppadCollectorTask.createBatch(MapInfo.getAllMaps(), statsPath));

        //myTasks.addAll(NavigationEvaluationBatchTaskCreator.createBatch(Arrays.asList("navigation", "acc"), Arrays.asList("fwMap", "navMesh"), "CTF-1on1-Joust", true, -1, statsPath, RecordType.PATH_FAILED));
//        XStream xstream = new XStream();
//        xstream.from
        //DM-Crash task
//        NavigationEvaluationTask taskRepeat = new NavigationEvaluationRepeatTask("C:/Temp/Pogamut/stats/navigation_fwMap/DM-1on1-Crash/160114_112055/data.csv", "acc", "navMesh", "C:/Temp/Pogamut/stats/", RecordType.PATH_FAILED);
//        myTasks.add(taskRepeat);
//        JumpInspectingTask jumpTask = new JumpInspectingTask();
//        jumpTask.setResultPath(statsPath);
//        myTasks.add(jumpTask);
        for (IEvaluationTask task : myTasks) {
            EvaluationTaskFactory.toXml(task, "C:/Temp/Pogamut/jumppad/");
        }
    }
}
