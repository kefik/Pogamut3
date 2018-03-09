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
package ut2004.exercises.e02;

import cz.cuni.amis.pogamut.base3d.worldview.object.ILocated;
import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.utils.Tuple2;

/**
 * Describes wall boundaries within 'DM-TagMap'.
 * <p><p>
 * There is a 'ut2004/Maps' directory containing 'DM-TagMap.ut2' that should be placed into 'UT2004/Maps' directory.
 * The map is simple "rectangle" with no internal walls / obstacles. Simple "rectangle" arena.
 * 
 * @author Jakub Gemrot aka Jimmy
 */
public class TagMap {
	
	public static final double TAG_NORTH_WALL_Y = 100;
	public static final double TAG_EAST_WALL_X = 5000;
	public static final double TAG_SOUTH_WALL_Y = 2700;
	public static final double TAG_WEST_WALL_X = 100;
	
	public static enum Direction {
		NORTH(new Location(0,-1,0)),
		EAST(new Location(1,0,0)),
		SOUTH(new Location(0,1,0)),
		WEST(new Location(-1,0,0));
		
		private Location dir;

		private Direction(Location dir) {
			this.dir = dir;
		}
		
		public Location direction() {
			return dir;
		}
		
		public Location direction(double scale) {
			return dir.scale(scale);
		}
		
		public Direction opposite() {
			switch(this) {
			case NORTH: return SOUTH;
			case EAST:  return WEST;
			case SOUTH: return NORTH;
			case WEST:  return EAST;
			default:
				throw new RuntimeException("Invalid direction: " + this);
			}			
		}
		
		public Direction clockwise() {
			switch(this) {
			case NORTH: return EAST;
			case EAST:  return SOUTH;
			case SOUTH: return WEST;
			case WEST:  return NORTH;
			default:
				throw new RuntimeException("Invalid direction: " + this);
			}			
		}
		
		public Direction counterClockwise() {
			switch(this) {
			case NORTH: return WEST;
			case EAST:  return NORTH;
			case SOUTH: return EAST;
			case WEST:  return SOUTH;
			default:
				throw new RuntimeException("Invalid direction: " + this);
			}			
		}
		
	}
	
	public static double getWallDistance(ILocated located, Direction dir) {
		switch (dir) {
		case NORTH: return getNorthWallDistance(located);
		case EAST:  return getEastWallDistance(located);
		case SOUTH: return getSouthWallDistance(located);
		case WEST:  return getWestWallDistance(located);
		}
		throw new RuntimeException("Should not reach here!");
	}
	
	public static double getNorthWallDistance(ILocated located) {
		if (located == null)  return Double.POSITIVE_INFINITY;
		Location location = located.getLocation();
		if (location == null) return Double.POSITIVE_INFINITY;
		
		return Math.abs(location.y - TAG_NORTH_WALL_Y);
	}
	
	public static double getEastWallDistance(ILocated located) {
		if (located == null)  return Double.POSITIVE_INFINITY;
		Location location = located.getLocation();
		if (location == null) return Double.POSITIVE_INFINITY;
		
		return Math.abs(location.x - TAG_EAST_WALL_X);
	}
	
	public static double getSouthWallDistance(ILocated located) {
		if (located == null)  return Double.POSITIVE_INFINITY;
		Location location = located.getLocation();
		if (location == null) return Double.POSITIVE_INFINITY;
		
		return Math.abs(location.y - TAG_SOUTH_WALL_Y);
	}
	
	public static double getWestWallDistance(ILocated located) {
		if (located == null)  return Double.POSITIVE_INFINITY;
		Location location = located.getLocation();
		if (location == null) return Double.POSITIVE_INFINITY;
		
		return Math.abs(location.x - TAG_WEST_WALL_X);
	}
	
	public static double getWallDistance(Direction direction, ILocated located) {
		switch (direction) {
		case NORTH: return getNorthWallDistance(located);
		case EAST: return getEastWallDistance(located);
		case SOUTH: return getSouthWallDistance(located);
		case WEST:  return getWestWallDistance(located);
		default:
			throw new RuntimeException("Invalid direction: " + direction);
		}
	}
	
	public static Tuple2<Direction, Double> getNearestWall(ILocated located) {
		double distance = Double.POSITIVE_INFINITY;
		Direction direction = null;
		for (Direction newDirection : Direction.values()) {
			double newDistance = getWallDistance(newDirection, located);
			if (newDistance < distance) {
				distance = newDistance;
				direction = newDirection;
			}
		}
		return new Tuple2<Direction, Double>(direction, distance);		
	}
	
	public static double getNearestWallDistance(ILocated located) {
		return getNearestWall(located).getSecond();			
	}
	
	public static Direction getNearestWallDirection(ILocated located) {
		return getNearestWall(located).getFirst();
	}

}
