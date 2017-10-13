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
import cz.cuni.amis.pogamut.ut2004.utils.UT2004BotRunner;

/**
 * Abstract class for evaluation task. Contains information required to start bot via {@link UT2004BotRunner}.
 *
 * @author Bogo
 * @param <T> Class of the bot parameters.
 * @param <X> Class of the evaluating bot.
 */
public abstract class EvaluationTask<T extends UT2004BotParameters, X extends EvaluatingBot> implements IEvaluationTask<T, X> {

    private Class<T> paramsClass;
    private Class<X> botClass;

    public EvaluationTask(Class<T> paramsClass, Class<X> botClass) {
        this.paramsClass = paramsClass;
        this.botClass = botClass;
    }

    public Class<T> getBotParamsClass() {
        return paramsClass;
    }

    public Class<X> getBotClass() {
        return botClass;
    }
    
    public String getJarPath() {
        return null;
    }
}
