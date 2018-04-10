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

import cz.cuni.amis.pogamut.base.agent.navigation.IPathPlanner;
import cz.cuni.amis.pogamut.base3d.worldview.object.ILocated;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.IUT2004GetBackToNavGraph;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.IUT2004Navigation;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.IUT2004PathExecutor;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.IUT2004RunStraight;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.UT2004GetBackToNavGraph;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.UT2004Navigation;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.UT2004PathExecutor;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.UT2004RunStraight;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.loquenavigator.KefikRunner;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.loquenavigator.LoqueNavigator;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.pathfollowing.NavMeshNavigation;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.pathfollowing.NavMeshNavigator;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.pathfollowing.NavMeshRunner;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.pathfollowing.UT2004AcceleratedPathExecutor;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.stuckdetector.AccUT2004DistanceStuckDetector;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.stuckdetector.AccUT2004PositionStuckDetector;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.stuckdetector.AccUT2004TimeStuckDetector;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.stuckdetector.UT2004DistanceStuckDetector;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.stuckdetector.UT2004PositionStuckDetector;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.stuckdetector.UT2004TimeStuckDetector;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPoint;

import java.io.File;


/**
 * Factory class for creating custom navigations from given parameters.
 * Navigation must be created at bot initialization and this allows for
 * navigation to be specified before execution without modifying bot's source
 * code.
 *
 * @author Bogo
 */
public class NavigationFactory {

    /**
     * Get {@link IPathPlanner} specified in params for given bot .
     *
     * @param bot
     * @param utBot
     * @param pathPlannerType
     * @return
     *
     */
    public static IPathPlanner getPathPlanner(EvaluatingBot bot, UT2004Bot utBot, String pathPlannerType) {
        if (pathPlannerType != null) {
            if (pathPlannerType.equals("fwMap")) {
                return bot.getFwMap();
            } else if (pathPlannerType.equals("navMesh")) {
                return bot.getNavMeshModule().getAStarPathPlanner();
            } else if (pathPlannerType.equals("oldNavMesh")) {
            	throw new RuntimeException("oldNavMesh is deprecated!");
            	//return bot.getOldNavMeshModule().getNavMesh();
            }
        }
        return null;
    }

    /**
     * Get {@link IUT2004Navigation} specified in params for given bot .
     *
     * @param bot
     * @param utBot
     * @param navigationType
     * @return
     *
     */
    public static IUT2004Navigation getNavigation(EvaluatingBot bot, UT2004Bot utBot, String navigationType) {
        if (navigationType.equals("acc")) {
        	return bot.getNMNav();
        } else {
        	return bot.getNavigation();
        }
    }

    /**
     * Initializes the path container.
     *
     * @param pathContainer
     * @param bot
     */
    static void initializePathContainer(PathContainer pathContainer, NavigationEvaluatingBot bot) {
        BotNavigationParameters params = bot.getParams();
        if (params.isRepeatTask()) {
            File file = new File(params.getRepeatFile());
            pathContainer.buildFromFile(file, true);
        } else if (params.isOnlyRelevantPaths()) {
            pathContainer.buildRelevant(bot.getParams().getLimit());
        } else {
            pathContainer.build(bot.getParams().getLimit());
        }

    }
}
