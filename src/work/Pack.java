package work;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import game.Tile;

public class Pack {

	public BufferedImage player;
	
	public int tilesetTileSize = Tile.tilesize;

	private BufferedImage[] tileset;
	
	public void load() throws IOException {
		player = loadImage("/tileset/player.png");
	}
	
	private BufferedImage loadImage(String path) throws IOException {
		return ImageIO.read(Pack.class.getResourceAsStream(path));
	}
	
	public void loadTileset(BufferedImage image) {
		int w = image.getWidth()/tilesetTileSize;
		int h = image.getHeight()/tilesetTileSize;
		tileset = new BufferedImage[w*h];
		int i = 0;
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				tileset[i] = image.getSubimage(
						x*tilesetTileSize, y*tilesetTileSize,
						tilesetTileSize, tilesetTileSize
						);
				i++;
			}
		}
	}
	
	public BufferedImage getTile(int id) {
		if(tileset == null) return null;
		return tileset[id];
	}
}
