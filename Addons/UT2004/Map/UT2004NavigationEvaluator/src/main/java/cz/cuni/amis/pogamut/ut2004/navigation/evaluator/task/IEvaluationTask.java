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

import cz.cuni.amis.pogamut.ut2004.bot.params.UT2004BotParameters;
import cz.cuni.amis.pogamut.ut2004.navigation.evaluator.bot.EvaluatingBot;
import java.util.logging.Level;

/**
 * Base interface for evaluation task.
 *
 * @author Bogo
 * @param <T> {@link UT2004BotParameters} parameters class
 * @param <X> {@link EvaluatingBot} bot
 */
public interface IEvaluationTask<T extends UT2004BotParameters, X extends EvaluatingBot> {
    
    /**
     * Map name where the evaluation will take place.
     * @return 
     */
    public String getMapName();
    
    /**
     * Bot parameters.
     * 
     * @return 
     */
    public T getBotParams();
    
    /**
     * Class of bot parameters.
     * 
     * @return 
     */
    public Class<T> getBotParamsClass();
    
    /**
     * Class of bot.
     * 
     * @return 
     */
    public Class<X> getBotClass();

    /**
     * Path where log will be stored.
     * 
     * @return 
     */
    public String getLogPath();

    /**
     * File of the task.
     * 
     * @return 
     */
    public String getFileName();
    
    /**
     * Gets path where jar with bot is located. Or null if bot is contained within evaluator jar.
     * @return 
     * 
     */
    public String getJarPath();

    /**
     * Gets path where result data should be stored.
     * @return 
     */
    public String getResultPath();
    
    /**
     * Get level of log which should be stored in result.
     * @return Level of log to store
     */
    public Level getLogLevel();
    
    public void setResultBasePath(String basePath);
}
