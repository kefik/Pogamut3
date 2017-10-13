package cz.cuni.amis.pathfinding;

import java.util.ArrayList;
import java.util.List;

public class MazeNode {
	
	public int x;
	public int y;
	
	public List<MazeNode> neighs = new ArrayList<MazeNode>();
	private Maze maze;
	
	public MazeNode(Maze maze, int x, int y) {
		this.x = x;
		this.y = y;
		this.maze = maze;
	}
	
	public boolean isFree() {
		return maze.maze[x][y];
	}
	
	public boolean isWall() {
		return !maze.maze[x][y];
	}
	
	@Override
	public String toString() {
		return "MazeNode[" + x + ", " + y + "]";
	}
	
}
