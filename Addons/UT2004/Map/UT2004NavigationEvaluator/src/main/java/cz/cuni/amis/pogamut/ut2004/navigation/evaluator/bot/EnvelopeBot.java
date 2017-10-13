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
package cz.cuni.amis.pogamut.ut2004.navigation.evaluator.bot;

import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.Initialize;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPoint;
import cz.cuni.amis.pogamut.ut2004.navigation.evaluator.data.EnvelopeResult;
import cz.cuni.amis.pogamut.ut2004.navigation.evaluator.task.MapEnvelopeTask;
import java.util.Map.Entry;

/**
 * Bot for computation of the map envelope.
 *
 * @author Bogo
 */
public class EnvelopeBot extends EvaluatingBot {
    
    public TaskBotParameters<MapEnvelopeTask> getParams() {
        return (TaskBotParameters<MapEnvelopeTask>) bot.getParams();
    }

    @Override
    public Initialize getInitializeCommand() {
        return new Initialize().setName("EnvelopeBot");
    }

    /**
     * This method is called only once right before actual logic() method is
     * called for the first time.
     */
    @Override
    public void beforeFirstLogic() {
        
        EnvelopeResult envelope = new EnvelopeResult(getParams().getTask());

        //Preprocessing navigation graph - removing disabled links
        for (Entry<UnrealId, NavPoint> entry : navPoints.getNavPoints().entrySet()) {
            NavPoint navPoint = entry.getValue();
            
            envelope.checkNavPoint(navPoint);
            
        }

        //Wrap up and end
        envelope.export();

        isCompleted = true;

        bot.stop();

    }
}
