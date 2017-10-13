package cz.cuni.amis.utils.astar;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;

import org.junit.BeforeClass;
import org.junit.Test;

import cz.cuni.amis.utils.StopWatch;

public class Test04_AStar {

	@BeforeClass
	public static void before() {
		String mazeImage = "/cz/cuni/amis/utils/astar/maze.bmp";
		System.out.println("[INFO] Loading image " + mazeImage);
		maze = new Maze(mazeImage);
		System.out.println("Maze loaded...");		
	}

	static Maze maze;
	
	public static class MazeNode {
		
		public int x;
		public int y;
		
		public List<MazeNode> neighs = new ArrayList<MazeNode>();
		
		public MazeNode(int x, int y) {
			this.x = x;
			this.y = y;
		}
		
		public boolean isFree() {
			return maze.maze[x][y];
		}
		
		public boolean isWall() {
			return !maze.maze[x][y];
		}
		
	}
	
	public static class Maze implements AStarMap<MazeNode> {
		
		public boolean[][] maze = null; // false == wall, true == empty space
		
		public MazeNode[][] nodes = null;

		private int width;

		private int height;

		private BufferedImage image;
		
		private File imageFile;
		
		public Maze(String pathToResource) {
			Class cls = this.getClass();
			URL url = this.getClass().getResource(pathToResource);
			URI uri;
			try {
				uri = url.toURI();
			} catch (URISyntaxException e) {
				throw new RuntimeException("Could not obrain URI from URL: " + url.toString());
			}
			this.imageFile = new File(uri);
			if (!imageFile.exists()) {
				throw new RuntimeException("File as resource (" + pathToResource + ") does not exist at: " + imageFile.getAbsolutePath());
			}
						
			try {
				this.image = ImageIO.read(imageFile);
			} catch (IOException e) {
				throw new RuntimeException("Could not read image from: " + imageFile.getAbsolutePath());
			}
		
			this.width = image.getWidth();
			this.height = image.getHeight();
			
			maze = new boolean[width][];
			nodes = new MazeNode[width][];
						
			for (int i = 0; i < width; ++i) {
				maze[i] = new boolean[image.getHeight()];
				nodes[i] = new MazeNode[image.getHeight()];
				for (int j = 0; j < height; ++j) {
					int pixel = image.getRGB(i,j);
					int alpha = (pixel >> 24) & 0xff;
				    int red = (pixel >> 16) & 0xff;
				    int green = (pixel >> 8) & 0xff;
				    int blue = (pixel) & 0xff;
					maze[i][j] = red != 0;
					nodes[i][j] = new MazeNode(i, j);
				}
			}	
			
			for (int i = 0; i < width; ++i) {
				for (int j = 0; j < height; ++j) {
					if (i > 0) {
						if (maze[i-1][j]) {
							nodes[i][j].neighs.add(nodes[i-1][j]);
						}
					}
					if (i < width-1) {
						if (maze[i+1][j]) {
							nodes[i][j].neighs.add(nodes[i+1][j]);
						}
					}
					if (j > 0) {
						if (maze[i][j-1]) {
							nodes[i][j].neighs.add(nodes[i][j-1]);
						}
					}
					if (j < height-1) {
						if (maze[i][j+1]) {
							nodes[i][j].neighs.add(nodes[i][j+1]);
						}
					}
				}
			}
		}

		@Override
		public int getEdgeCost(MazeNode nodeFrom, MazeNode nodeTo) {			
			return Math.abs(nodeFrom.x - nodeTo.x) + Math.abs(nodeFrom.y - nodeTo.y) ;
		}

		@Override
		public Collection<MazeNode> getNodeNeighbours(MazeNode node) {
			return node.neighs;
		}
		
		public int getRGB(int r, int g, int b) {
			 int pixel = 0;
			 pixel = 255 & 0xff;
			 pixel = pixel << 8 | r;
			 pixel = pixel << 8 | g;
			 pixel = pixel << 8 | b;
			 
			 return pixel;
		}
		
		int black = getRGB(0,0,0);
		int red = getRGB(255,0,0);
		int green = getRGB(0,255,0);
		int blue = getRGB(100,100,255);
		int white = getRGB(255,255,255);
		
		private void setPixel(int x, int y, int rgb) {
			if (x >= 0 && x < width && y >= 0 && y < height) {
				image.setRGB(x,y,rgb);
			}
		}
		
		private void restorePixel(int x, int y) {
			if (x >= 0 && x < width && y >= 0 && y < height) {
				if (maze[x][y]) image.setRGB(x,y,white);
				else image.setRGB(x,y,black);
			}
		}
		
		private void rectangle(int x, int y, int rgb) {
			for (int i = x-4; i < x+4; ++i) {
				for (int j = y-4; j < y+4; ++j) {
					setPixel(i,j,rgb);
				}
			}
		}
		
		private void restoreRectangle(int x, int y) {
			for (int i = x-4; i < x+4; ++i) {
				for (int j = y-4; j < y+4; ++j) {
					restorePixel(i,j);
				}
			}
		}
		
		public void output(AStarResult<MazeNode> result, int number) {
			MazeNode start = result.getPath().get(0);
			MazeNode end = result.getPath().get(result.getPath().size()-1);
			int r = 100;
			int g = 100;
			int b = 100;
			int i = 0;
			for (MazeNode node : result.getPath()) {
				++i;
				if (i % 10 == 0) {
					++r;
					if (r == 256) {
						g += 10;
						r = 100;
					}
					if (g >= 256) {
						b += 10;
						g = 100;
					}
					if (b >= 256) {
						b = 100;
					}
				}
				image.setRGB(node.x, node.y, getRGB(r,g,b));
			}
			rectangle(start.x, start.y, red);	
			rectangle(end.x, end.y, green);			
			String separ = System.getProperty("file.separator");
			String imagePath = imageFile.getAbsolutePath();
			File out = new File(imagePath.substring(0, imagePath.lastIndexOf(separ)) + separ + "maze-result-" + (number > 9 ? number : "0" + number) + ".bmp");
			try {
				ImageIO.write(image, "bmp", out);
			} catch (IOException e) {
				throw new RuntimeException("Could not write PNG output with maze-result into " + out.getAbsolutePath(), e);
			}
			System.out.println("[INFO] result saved into " + out.getAbsolutePath());
			
			restoreRectangle(start.x, start.y);	
			restoreRectangle(end.x, end.y);
			for (MazeNode node : result.getPath()) {
				image.setRGB(node.x, node.y, white);
			}
		}

		@Override
		public int getNodeCost(MazeNode node) {
			// TODO Auto-generated method stub
			return 0;
		}
		
	}
	
	public class MazeHeuristic implements AStarHeuristic<MazeNode> {

		private MazeNode goal;

		public MazeHeuristic(MazeNode goal) {
			this.goal = goal;
		}
		
		@Override
		public int getEstimatedDistanceToGoal(MazeNode node) {
			int dX = goal.x - node.x;
			int dY = goal.y - node.y;
			return (int)Math.sqrt(dX*dX+dY*dY);
		}
		
	}
	
	private void test(int num, int startX, int startY, int endX, int endY, boolean expectedSuccess) {
		System.out.println("TEST " + num + " / 44");
		System.out.println("[INFO] " + (expectedSuccess ? "POSITIVE TEST" : "NEGATIVE TEST"));
		System.out.println("[INFO] Start: " + startX + "," + startY);
		System.out.println("[INFO] End: " + endX + ", " + endY);
		
		MazeNode start = maze.nodes[startX][startY];
		MazeNode end = maze.nodes[endX][endY];
		
		if (expectedSuccess && !start.isFree()) {
			System.out.println("[ERROR] Start[" + startX + "," + startY + "] is not a free point!");
			throw new RuntimeException("[ERROR] Start[" + startX + "," + startY + "] is not a free point!");
		}
		if (expectedSuccess && !end.isFree()) {
			System.out.println("[ERROR] Start[" + endX + ", " + endY + "] is not a free point!");
			throw new RuntimeException("[ERROR] Start[" + endX + ", " + endY + "] is not a free point!");
		}
		
		System.out.println("[INFO] Invoking AStar!");
		StopWatch watch = new StopWatch();
		AStarResult<MazeNode> result = AStar.aStar(maze, new MazeHeuristic(end), start, end);
		System.out.println("[INFO] AStar time:  " + watch.stopStr() + " ms");
		
		if (expectedSuccess != result.success) {
			if (!result.success) {
				System.out.println("[ERROR] Path not found! Can't be! Either someone passed wrong maze.png or AStar has failed!");
				throw new RuntimeException("Path not found! Can't be! Either someone passed wrong maze.png or AStar has failed!");
			} else {
				System.out.println("[ERROR] Path found! Should not exist!");
				throw new RuntimeException("Path found! Should not exist!");
			}
		}
		if (result.success) {
			System.out.println("[INFO] Path found!");
			System.out.println("[INFO] Path length: " + result.getPath().size());
			maze.output(result, num);
		} else {
			System.out.println("[INFO] Path does not exist! (Expected == correct)");
		}
		
		System.out.println("---/// TEST OK ///---");
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
		test(num, start.x, start.y, goal.x, goal.y, true);
	}
	
	private void testNegativeRandom(int num) {
		MazeNode start = getRandomNode(true);
		MazeNode goal = getRandomNode(false);
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
