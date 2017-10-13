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
package cz.cuni.amis.pogamut.ut2004.agent.navigation;

import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPoint;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPointNeighbourLink;
import cz.cuni.amis.pogamut.ut2004.utils.LinkFlag;
import java.util.Collection;

/**
 *
 * @author Bogo
 */
public class UT2004EdgeChecker {
    
    private static final double NEAR = 50.0;
    private static final double FLOOR_DIFF = 150.0;

    /**
     * Flag mask representing unusable edge.
     */
    public static final int BAD_EDGE_FLAG = LinkFlag.FLY.get()
            | LinkFlag.LADDER.get() | LinkFlag.PROSCRIBED.get()
            | LinkFlag.SWIM.get() | LinkFlag.PLAYERONLY.get();

    /**
     * Checks whether the edge is usable.
     *
     * @param edge NeighNav object representing the edge.
     * @return boolean
     */
    public static boolean checkLink(NavPointNeighbourLink edge) {
        // Bad flags (prohibited edges, swimming, flying...).
        if ((edge.getFlags() & BAD_EDGE_FLAG) != 0) {
            return false;
        }

        // Lift flags.
        if ((edge.getFlags() & LinkFlag.SPECIAL.get()) != 0) {
            //This is a navpoint that requires lift jump - our bots can't do this - banning the link!
            if (edge.getToNavPoint().isLiftJumpExit()) {
                return false;
            }
            
            //Could be the case of LiftJump
            NavPoint liftCenter = edge.getFromNavPoint();
            if(liftCenter.isLiftCenter()) {
                Collection<NavPointNeighbourLink> liftEdges = liftCenter.getOutgoingEdges().values();
                double bottom = Double.NEGATIVE_INFINITY;
                double top = Double.NEGATIVE_INFINITY;
                
                
                for(NavPointNeighbourLink liftEdge : liftEdges) {
                    double toZ = liftEdge.getToNavPoint().getLocation().z;
                    
                    if(bottom == Double.NEGATIVE_INFINITY) {
                        bottom = toZ;
                    } else if(toZ < bottom - NEAR) {
                        top = bottom;
                        bottom = toZ;
                    } else if(toZ > bottom + FLOOR_DIFF && (top == Double.NEGATIVE_INFINITY || toZ < top - NEAR)) {
                        top = toZ;
                    }
                }
                
                if(edge.getToNavPoint().getLocation().z > top + FLOOR_DIFF) {
                    //3rd level of NavPoints from lift -> we assume it's LiftJump
                    return false;
                }
            }

            //Classic lift edge.
            return true;
        }

        // Check whether the climbing angle is not so steep.
//		if ((edge.getFromNavPoint().getLocation().getPoint3d().distance(
//				edge.getToNavPoint().getLocation().getPoint3d()) < (edge
//				.getToNavPoint().getLocation().z - edge.getFromNavPoint()
//				.getLocation().z))
//				&& (edge.getFromNavPoint().getLocation().getPoint3d().distance(
//						edge.getToNavPoint().getLocation().getPoint3d()) > 100)) {
//			return false;
//		}
        // Check whether the jump is not so high.
//		if (((edge.getFlags() & LinkFlag.JUMP.get()) != 0)
//				&& (edge.getToNavPoint().getLocation().z
//						- edge.getFromNavPoint().getLocation().z > 80)) {
//			return false;
//		}
        //Check whether there is NeededJump attribute set - this means the bot has to 
        //provide the jump himself - if the Z of the jump is too high it means he
        //needs to rocket jump or ShieldGun jump - we will erase those links
        //as our bots are not capable of this
        if (edge.getNeededJump() != null && edge.getNeededJump().z > 680) {
            return false;
        }

        //This is a navpoint that requires lift jump - our bots can't do this - banning the link!
        if (edge.getToNavPoint().isLiftJumpExit()) {
            return false;
        }

        return true;
    }

}
