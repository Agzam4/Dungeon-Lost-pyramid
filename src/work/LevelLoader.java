package work;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import generator.Blocks;
import generator.LevelGenerator;

public class LevelLoader {

	private static final int AIR = new Color(255,255,255).getRGB();
	private static final int LADDER = new Color(255,255,0).getRGB();
	private static final int COLUMN = new Color(100,100,100).getRGB();
	private static final int START_WALL = new Color(255,0,255).getRGB();
	private static final int COLUMN_FIRE = new Color(0,255,255).getRGB();
	private static final int CRUSHER_AIR = new Color(255,100,0).getRGB();
	private static final int CRUSHER_DOWN = new Color(50,50,50).getRGB();

	public static int lastW = 0;
	public static int lastH = 0;
	
	
	public static int[][] loadLevel(int level) throws IOException {
		BufferedImage loaded = loadImage("level" + level + ".png");
		
		int map[][] = new int[loaded.getWidth()][loaded.getHeight()];
		lastW = loaded.getWidth();
		lastH = loaded.getHeight();
		for (int y = 0; y < loaded.getHeight(); y++) {
			for (int x = 0; x < loaded.getWidth(); x++) {
				int type = 0;
				int rgb = loaded.getRGB(x, y);
				if(rgb == AIR) type = Blocks.AIR.ordinal();
				if(rgb == LADDER) type = Blocks.LADDER.ordinal();
				if(rgb == START_WALL) type = Blocks.START_WALL.ordinal();
				if(rgb == CRUSHER_AIR) type = Blocks.CRUSHER_AIR.ordinal();
				if(rgb == CRUSHER_DOWN) type = Blocks.CRUSHER_DOWN.ordinal();
				if(rgb == COLUMN) type = Blocks.COLUMN.ordinal();
				if(rgb == COLUMN_FIRE) type = Blocks.COLUMN_FIRE.ordinal();
				
				map[x][y] = type;
			}
		}
		return map;
	}
	
	private static BufferedImage loadImage(String path) throws IOException {
		return ImageIO.read(LevelLoader.class.getResourceAsStream("/levels/" + path));
	}
}
