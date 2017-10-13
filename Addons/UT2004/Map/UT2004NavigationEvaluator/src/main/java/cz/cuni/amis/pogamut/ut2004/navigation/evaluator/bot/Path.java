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

import cz.cuni.amis.pogamut.base.agent.navigation.IPathFuture;
import cz.cuni.amis.pogamut.base3d.worldview.object.ILocated;
import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPoint;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPointMessage;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPointNeighbourLink;
import java.util.List;

/**
 * Represents path for evaluation. Contains start and end point.
 *
 * @author Bogo
 */
public class Path {

    private NavPoint start;
    private NavPoint end;
    private double length = 0;
    private int jumps = 0;
    private int lifts = 0;

    /**
     * Creates path with given start and end point.
     *
     * @param start Start point of path.
     * @param end End point of path.
     *
     */
    public Path(NavPoint start, NavPoint end) {
        this.start = start;
        this.end = end;
    }

    public NavPoint getStart() {
        return start;
    }

    public NavPoint getEnd() {
        return end;
    }

    /**
     * Get ID of the path. ID is in format [ID of start]-[ID of end].
     *
     * @return ID of this path.
     *
     */
    public String getId() {
        return String.format("[%s]-[%s]", start.getId().getStringId(), end.getId().getStringId());
    }

    public double getLength() {
        return length;
    }

    public int getJumps() {
        return jumps;
    }

    public int getLifts() {
        return lifts;
    }

    public void computeMetrics(IPathFuture<ILocated> path) {
        List<ILocated> list = path.get();
        length = getPathLength(list);
        jumps = getJumpCount(list);
        lifts = getLiftCount(list);
    }

    private double getPathLength(List<ILocated> list) {
        double sum = 0;

        for (int i = 0; i < list.size() - 1; i++) {
            Location l1 = list.get(i).getLocation();
            Location l2 = list.get(i + 1).getLocation();
            double distance = l1.getDistance(l2);
            sum += distance;
        }
        return sum;
    }

    private int getJumpCount(List<ILocated> list) {
        int jumpCount = 0;

        ILocated fromLocation = list.get(0);
        ILocated toLocation = list.get(1);
        for (int i = 2; i < list.size(); i++) {
            if (fromLocation.getClass() == NavPointMessage.class && toLocation.getClass() == NavPointMessage.class) {
                NavPointMessage from = (NavPointMessage) fromLocation;
                NavPointMessage to = (NavPointMessage) toLocation;

                NavPointNeighbourLink edge = from.getOutgoingEdges().get(to.getId());
                if (edge == null) {
                    continue;
                }
                //from http://wiki.beyondunreal.com/Legacy:ReachSpec
                int jumpFlag = 8;
                boolean isJump = (edge.getFlags() & jumpFlag) == jumpFlag;
                if (isJump) {
                    ++jumpCount;
                }
            }
            fromLocation = toLocation;
            toLocation = list.get(i);
        }
        return jumpCount;
    }

    private int getLiftCount(List<ILocated> list) {
        int liftCount = 0;

        for (int i = 0; i < list.size(); i++) {
            ILocated fromLocation = list.get(i);
            if (fromLocation.getClass() == NavPointMessage.class) {
                NavPointMessage node = (NavPointMessage) fromLocation;
                if (node.isLiftCenter()) {
                    ++liftCount;
                }
            }
        }
        return liftCount;
    }
}
