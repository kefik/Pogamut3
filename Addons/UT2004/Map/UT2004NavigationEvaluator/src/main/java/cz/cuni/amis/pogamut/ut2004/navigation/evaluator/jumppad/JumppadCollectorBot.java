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

import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPoint;
import cz.cuni.amis.pogamut.ut2004.navigation.evaluator.bot.EvaluatingBot;
import cz.cuni.amis.pogamut.ut2004.navigation.evaluator.bot.TaskBotParameters;
import java.util.Map;

/**
 * Bot collecting data about jump pads for their highlighting in the map geometry before creating mesh.
 *
 * @author Bogo
 */
public class JumppadCollectorBot extends EvaluatingBot {
    
    public TaskBotParameters<JumppadCollectorTask> getParams() {
        return (TaskBotParameters<JumppadCollectorTask>) bot.getParams();
    }
    
    /**
     * This method is called only once right before actual logic() method is
     * called for the first time.
     */
    @Override
    public void beforeFirstLogic() {
        
        JumppadResult jumppads = new JumppadResult(getParams().getTask());

        //Preprocessing navigation graph - removing disabled links
        for (Map.Entry<UnrealId, NavPoint> entry : navPoints.getNavPoints().entrySet()) {
            NavPoint navPoint = entry.getValue();
            
            if(navPoint.isJumpPad()) {
                jumppads.add(navPoint);
            }     
            
        }

        //Wrap up and end
        jumppads.export();

        isCompleted = true;

        bot.stop();

    }
    
}
