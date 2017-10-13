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
package cz.cuni.amis.pogamut.ut2004.navigation.evaluator.task;

import cz.cuni.amis.pogamut.ut2004.bot.params.UT2004BotParameters;
import cz.cuni.amis.pogamut.ut2004.navigation.evaluator.bot.JumpInspectingBot;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;

/**
 * Task for jump inspection. Gathers data about jumps.
 *
 * @author Bogo
 */
public class JumpInspectingTask extends EvaluationTask<UT2004BotParameters, JumpInspectingBot> implements IEvaluationTask<UT2004BotParameters, JumpInspectingBot> {

    private String resultBasePath;
    private String resultPath;
    
    public JumpInspectingTask() {
        super(UT2004BotParameters.class, JumpInspectingBot.class);
    }
    
    public String getMapName() {
        return "DM-1on1-Idoma";
    }

    public UT2004BotParameters getBotParams() {
        return new UT2004BotParameters();
    }

    public String getLogPath() {
        return getResultPath() + "log.log";
    }

    public String getFileName() {
        return String.format("JumpInspecting_%s_", getMapName());
    }
    
    public void setResultPath(String path) {
        resultBasePath = path;
    }
    
    public String getResultPath() {
        if (resultPath == null) {
            String basePath = String.format("%s/jump/%s", resultBasePath, getMapName());
            SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyy_HHmmss");
            String fullPath = String.format("%s/%s/", basePath, dateFormat.format(new Date()));
            File resultFile = new File(fullPath);
            resultFile.mkdirs();
            this.resultPath = fullPath;
        }
        return resultPath;
    }

    public Level getLogLevel() {
        return Level.ALL;
    }

    public void setResultBasePath(String basePath) {
        //Do nothing
    }
    
}
