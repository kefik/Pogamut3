package cz.cuni.amis.pathfinding.alg.astar;

import java.util.Random;
import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Test;

import cz.cuni.amis.pathfinding.Maze;
import cz.cuni.amis.pathfinding.MazeNode;
import cz.cuni.amis.pathfinding.map.IPFGoal;
import cz.cuni.amis.tests.BaseTest;
import cz.cuni.amis.utils.StopWatch;
import cz.cuni.amis.utils.heap.IHeap;

public class Test02_AStar extends BaseTest {

	@BeforeClass
	public static void before() {
		String mazeImage = "/cz/cuni/amis/pathfinding/maze.bmp";
		log.info("Loading image " + mazeImage);
		maze = new Maze(mazeImage);
		log.info("Maze loaded...");		
	}

	static Maze maze;
	
	public class MazeGoal implements IPFGoal<MazeNode> {

		private MazeNode goal;
		private MazeNode start;

		public MazeGoal(MazeNode start, MazeNode goal) {
			this.start = start;
			this.goal = goal;
		}
		
		@Override
		public int getEstimatedCostToGoal(MazeNode node) {
			int dX = goal.x - node.x;
			int dY = goal.y - node.y;
			return (int)Math.sqrt(dX*dX+dY*dY);
		}

		@Override
		public MazeNode getStart() {
			return start;
		}

		@Override
		public boolean isGoalReached(MazeNode actualNode) {
			return this.goal.equals(actualNode);
		}

		@Override
		public void setCloseList(Set<MazeNode> closedList) {
		}

		@Override
		public void setOpenList(IHeap<MazeNode> openList) {
		}
		
	}
	
	private void test(int num, int startX, int startY, int endX, int endY, boolean expectedSuccess) {
		log.info("TEST " + num + " / 44");
		log.info("" + (expectedSuccess ? "POSITIVE TEST" : "NEGATIVE TEST"));
		log.info("Start: " + startX + "," + startY);
		log.info("End: " + endX + ", " + endY);
		
		MazeNode start = maze.nodes[startX][startY];
		MazeNode end = maze.nodes[endX][endY];
		
		if (expectedSuccess && !start.isFree()) {
			testFailed("Start[" + startX + "," + startY + "] is not a free point!");
			throw new RuntimeException("[ERROR] Start[" + startX + "," + startY + "] is not a free point!");
		}
		if (expectedSuccess && !end.isFree()) {
			testFailed("Start[" + endX + ", " + endY + "] is not a free point!");
			throw new RuntimeException("[ERROR] Start[" + endX + ", " + endY + "] is not a free point!");
		}
		
		log.info("Invoking AStar!");
		StopWatch watch = new StopWatch();
		AStar aStar = new AStar(maze);
		IAStarResult<MazeNode> result = aStar.findPath(new MazeGoal(start, end), 0);
		log.info("AStar time:  " + watch.stopStr() + " ms");
		
		if (expectedSuccess != result.isSuccess()) {
			if (!result.isSuccess()) {
				testFailed("Path not found! Can't be! Either someone passed wrong maze.png or AStar has failed!");
				throw new RuntimeException("Path not found! Can't be! Either someone passed wrong maze.png or AStar has failed!");
			} else {
				testFailed("Path found! Should not exist!");
				throw new RuntimeException("Path found! Should not exist!");
			}
		}
		if (result.isSuccess()) {
			log.info("Path found!");
			log.info("Path length: " + result.getPath().size());
			maze.output(result.getPath(), "AStar", num);
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
			goal = getRandomNode(true);
		}
		test(num, start.x, start.y, goal.x, goal.y, false);
	}
	
	
	@Test
	public void test1() {
		test(1, 1, 1, maze.width-1, maze.height-2, true);
	}

	@Test
	public void test2() {
		test(2, 1, maze.height-2, maze.width-1, maze.height-2, true);
	}
	
	@Test
	public void test3() {
		test(3, 1, maze.height-2, maze.width-2, 1, true);
	}
	
	@Test
	public void test4Same() {
		test(4, 1, 1, 1, 1, true);
	}
	
	@Test
	public void test5PositiveRandom() {		
		for (int i = 0; i < 20; ++i) {
			testPositiveRandom(5 + i);
		}
	}
	
	@Test
	public void test6NegativeRandom() {		
		for (int i = 0; i < 20; ++i) {
			testNegativeRandom(25 + i);
		}
	}

	
}
