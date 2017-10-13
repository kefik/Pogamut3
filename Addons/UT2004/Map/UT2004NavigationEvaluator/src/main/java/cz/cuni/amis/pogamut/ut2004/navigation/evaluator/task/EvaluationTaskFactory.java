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

import com.thoughtworks.xstream.XStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Factory for de/serializing of the evaluation tasks. Wraps XStream usage.
 *
 * @author Bogo
 */
public class EvaluationTaskFactory {

    /**
     * De-serializes task from file passed in arguments. Deprecated option of passing parameters directly in arguments.
     * @param args
     * @return 
     */
    public static IEvaluationTask build(String[] args) {
        if (args.length >= 1) {

            File file = new File(args[0]);
            return build(file);

        } else {


            return NavigationEvaluationTask.buildFromArgs(args);
        }
    }

    @Deprecated
    public static void toArgs(IEvaluationTask task, ArrayList<String> command) {
        ((NavigationEvaluationTask) task).toArgs(command);
    }

    /**
     * Serializes task to XML file.
     * 
     * @param task
     * @param directory 
     */
    public static void toXml(IEvaluationTask task, String directory) {
        FileWriter writer = null;
        try {
            String fileName = task.getFileName() + ".eval.xml";
            if(directory != null) {
                fileName = directory + fileName;
            }
            XStream xstream = new XStream();
            File file = new File(fileName);
            file.getParentFile().mkdirs();
            writer = new FileWriter(fileName);
            xstream.toXML(task, writer);
        } catch (IOException ex) {
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException ex) {
            }
        }
    }

    /**
     * De-serializes task from given {@link File}.
     * 
     * @param freeTask
     * @return 
     */
    public static IEvaluationTask build(File freeTask) {
        XStream xstream = new XStream();
        FileReader reader;
        try {
            reader = new FileReader(freeTask);
        } catch (FileNotFoundException ex) {
            return null;
        }
        return (IEvaluationTask) xstream.fromXML(reader);
    }
}
