package cz.cuni.amis.pathfinding.alg.floydwarshall;

import java.util.List;
import java.util.Random;

import org.junit.BeforeClass;
import org.junit.Test;

import cz.cuni.amis.pathfinding.Maze;
import cz.cuni.amis.pathfinding.MazeNode;
import cz.cuni.amis.tests.BaseTest;

public class Test01_FloydWarshall extends BaseTest {

	@BeforeClass
	public static void before() {
		String mazeImage = "/cz/cuni/amis/pathfinding/maze-small.bmp";
		log.info("Loading image " + mazeImage);
		maze = new Maze(mazeImage);
		log.info("Maze loaded...");
		fw = new FloydWarshall<MazeNode>(maze, log);
	}

	static Maze maze;
	
	static FloydWarshall<MazeNode> fw;
	
	private void test(int num, int startX, int startY, int endX, int endY, boolean expectedSuccess) {
		log.info("TEST " + num + " / 42");
		log.info("" + (expectedSuccess ? "POSITIVE TEST" : "NEGATIVE TEST"));
		log.info("Start: " + startX + "," + startY);
		log.info("End: " + endX + ", " + endY);
		
		MazeNode start = maze.nodes[startX][startY];
		MazeNode end = maze.nodes[endX][endY];
		
		if (expectedSuccess && !start.isFree()) {
			log.info("Start[" + startX + "," + startY + "] is not a free point!");
			throw new RuntimeException("Start[" + startX + "," + startY + "] is not a free point!");
		}
		if (expectedSuccess && !end.isFree()) {
			log.info("Start[" + endX + ", " + endY + "] is not a free point!");
			throw new RuntimeException("Start[" + endX + ", " + endY + "] is not a free point!");
		}
		
		List<MazeNode> path = fw.getPath(start, end);
		
		if (expectedSuccess && path == null) {
			testFailed("Path not found! Can't be! Either someone passed wrong maze.png or AStar has failed!");
		}
		if (!expectedSuccess && path != null) {
			testFailed("Path found! Should not exist!");
		}
		if (path != null) {
			log.info("Path found!");
			log.info("Path length: " + path.size());
			maze.output(path, "FW", num);
		} else {
			log.info("Path does not exist! (Expected == correct)");
		}
		
		testOk();
	}
	
	Random random = new Random(System.currentTimeMillis());
	
	private MazeNode getRandomNode(boolean free) {
		int x = random.nextInt(maze.width);
		int y = random.nextInt(maze.height);
		while (free != maze.maze[x][y]) {
			x = random.nextInt(maze.width);
			y = random.nextInt(maze.height);
		}
		return maze.nodes[x][y];
	}
	
	
	private void testPositiveRandom(int num) {
		MazeNode start = getRandomNode(true);
		MazeNode goal = getRandomNode(true);
		while (start == goal) {
			goal = getRandomNode(true);
		}
		test(num, start.x, start.y, goal.x, goal.y, true);
	}
	
	private void testNegativeRandom(int num) {
		MazeNode start = getRandomNode(true);
		MazeNode goal = getRandomNode(false);
		while (start == goal) {
			goal = getRandomNode(false);
		}
		test(num, start.x, start.y, goal.x, goal.y, false);
	}
		
	@Test
	public void test1() {
		test(1, 0, 0, 1, maze.height-2, true);
	}

	@Test
	public void test2() {
		test(2, 0, 2, 1, maze.height-2, true);
	}
	
	@Test
	public void test2PositiveRandom() {		
		for (int i = 0; i < 20; ++i) {
			testPositiveRandom(5 + i);
		}
	}
	
	@Test
	public void test3NegativeRandom() {		
		for (int i = 0; i < 20; ++i) {
			testNegativeRandom(25 + i);
		}
	}

	
}
