package cz.cuni.amis.pathfinding;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.imageio.ImageIO;

import cz.cuni.amis.pathfinding.map.IPFKnownMap;

public class Maze implements IPFKnownMap<MazeNode> {
	
	public boolean[][] maze = null; // false == wall, true == empty space
	
	public MazeNode[][] nodes = null;

	public int width;

	public int height;

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
				nodes[i][j] = new MazeNode(this, i, j);
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
	public int getArcCost(MazeNode nodeFrom, MazeNode nodeTo) {			
		return Math.abs(nodeFrom.x - nodeTo.x) + Math.abs(nodeFrom.y - nodeTo.y) ;
	}

	@Override
	public Collection<MazeNode> getNeighbors(MazeNode node) {
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
	
	public void output(List<MazeNode> result, String name, int number) {
		if (result == null || result.size() == 0) {
			System.out.println("[WARNING] cannot output result ... result is EMPTY !!! name='" + name + "', number='" + number + "'");
			return;
		}
		MazeNode start = result.get(0);
		MazeNode end = result.get(result.size()-1);
		int r = 100;
		int g = 100;
		int b = 100;
		int i = 0;
		for (MazeNode node : result) {
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
		File out = new File(imagePath.substring(0, imagePath.lastIndexOf(separ)) + separ + "maze-result-" + name + "-" + (number > 9 ? number : "0" + number) + ".bmp");
		try {
			ImageIO.write(image, "bmp", out);
		} catch (IOException e) {
			throw new RuntimeException("Could not write BMP output with maze-result into " + out.getAbsolutePath(), e);
		}
		System.out.println("[INFO] result saved into " + out.getAbsolutePath());
		
		restoreRectangle(start.x, start.y);	
		restoreRectangle(end.x, end.y);
		for (MazeNode node : result) {
			image.setRGB(node.x, node.y, white);
		}
	}

	@Override
	public List<MazeNode> getNodes() {
		List<MazeNode> nodesList = new ArrayList(width * height);
		for (int i = 0; i < width; ++i) {
			for (int j = 0; j < height; ++j) {
				if (maze[i][j]) {
					nodesList.add(nodes[i][j]);
				}
			}
		}
		return nodesList;
	}

	@Override
	public int getNodeCost(MazeNode node) {
		return 0;
	}
	
}