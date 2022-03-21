package work;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import game.Tile;

public class Pack {

	public BufferedImage player;
	public BufferedImage minimapIco;
	
	public int tilesetTileSize = Tile.tilesize;

	private BufferedImage[] tileset;

	public static final int DIAMOND_ITEMS = 2;
	public static final int AMETHYST_ITEMS = 1;
	public static final int GOLD_ITEMS = 0;

	private static final int ITEM_FRAME = 0;
	private static final int ITEM_LOCK = 1;

	private BufferedImage[][] items;
	private int[][] positionsX = new int[][] {
		{0},
		{-3, 3},
		{-5, -3, -1, 1, 3, 5}
	};
	private Color[] itemsColors;
	

	private BufferedImage[] coins;
	private BufferedImage[] itemsFrames;
	

	private BufferedImage[] particlesCrusher;

	private BufferedImage entityChest;
	private BufferedImage entityBlue;
	
	public void load() throws IOException {
		player = loadImage("/tileset/player.png");
		System.out.println("[Pack] Loaded Player");
		minimapIco = loadImage("/ico/minimap_ico.png");
		System.out.println("[Pack] Loaded Icos");
		loadGameTileset(loadImage("/tileset/Dungeon_Lost_pyramid.png"));
		System.out.println("[Pack] Loaded Tilest (length: " + tileset.length + ")");
		loadItemsTileset(loadImage("/tileset/items.png"));
		System.out.println("[Pack] Loaded Items (length: " + items.length + ")");
		loadItemsCoinsTileset(loadImage("/tileset/coins.png"));
		System.out.println("[Pack] Loaded Coins (length: " + coins.length + ")");
		loadItemsFramesTileset(loadImage("/tileset/itemsFrames.png"));
		System.out.println("[Pack] Loaded Frames (length: " + itemsFrames.length + ")");
		
		// Entity
		
		entityChest = loadImage("/tileset/entities/chest.png");
		entityBlue = loadImage("/tileset/entities/blue.png");
		
		// Particles

		particlesCrusher = loadParticles("/obj/particles_crusher.png", 4);
		System.out.println("[Pack] Loaded particles");
	}

	private BufferedImage loadImage(String path) throws IOException {
		return ImageIO.read(Pack.class.getResourceAsStream(path));
	}
	
	private BufferedImage[] loadParticles(String path, int count) {
		try {
			BufferedImage particlesImage = loadImage(path);
			BufferedImage[] particles = new BufferedImage[count];
			int size = particlesImage.getWidth()/count;
			for (int i = 0; i < particles.length; i++) {
				particles[i] = particlesImage.getSubimage(i*size, 0, size, particlesImage.getHeight());
			}
			return particles;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public BufferedImage[] getParticlesCrusher() {
		return particlesCrusher;
	}
	
	public void loadGameTileset(BufferedImage image) {
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
	
	int itemSize;
	int maxItems;
	
	public void loadItemsTileset(BufferedImage image) {
		int tilesize = image.getHeight()/3;
		itemSize = tilesize;
		itemsColors = new Color[3];
		for (int i = 0; i < itemsColors.length; i++) {
			itemsColors[i] = new Color(image.getRGB(0, tilesize*i));
		}
		
		image = image.getSubimage(1, 0, image.getWidth()-1, image.getHeight());
		
		items = new BufferedImage[3][];
		maxItems = 0;
		
		for (int i = 0; i < items.length; i++) {
			int count;
			for (count = 0; count < image.getWidth()/tilesize; count++) {
				if(image.getRGB(count*tilesize, i*tilesize) == itemsColors[i].getRGB()) {
					break;
				}
			}
			if(count > maxItems) maxItems = count;
			System.out.println("[Pack] " + (i+1) + ") " + count);
			items[i] = new BufferedImage[count];
			for (int x = 0; x < count; x++) {
				items[i][x] = image.getSubimage(x*tilesize, i*tilesize, tilesize, tilesize);
			}
		}
		
//		int w = image.getWidth()/tilesetTileSize;
//		int h = image.getHeight()/tilesetTileSize;
//		tileset = new BufferedImage[w*h];
//		int i = 0;
//		for (int y = 0; y < h; y++) {
//			for (int x = 0; x < w; x++) {
//				tileset[i] = image.getSubimage(
//						x*tilesetTileSize, y*tilesetTileSize,
//						tilesetTileSize, tilesetTileSize
//						);
//				i++;
//			}
//		}
	}
	
	public void loadItemsCoinsTileset(BufferedImage image) {
		int tilesize = image.getWidth()/3;
		coins = new BufferedImage[3];
		for (int i = 0; i < coins.length; i++) {
			coins[i] = image.getSubimage(i*tilesize, 0, tilesize, image.getHeight());
		}
	}
	
	private void loadItemsFramesTileset(BufferedImage image) {
		int tilesize = image.getWidth()/3;
		itemsFrames = new BufferedImage[3];
		for (int i = 0; i < itemsFrames.length; i++) {
			itemsFrames[i] = image.getSubimage(i*tilesize, 0, tilesize, image.getHeight());
		}
	}

	public BufferedImage getCoins(int id) {
		return coins[id];
	}
	
	public BufferedImage getFrame(int id) {
		return itemsFrames[id];
	}
	
	public BufferedImage getTile(int id) {
		if(tileset == null) return null;
		return tileset[id];
	}

	public BufferedImage getItemFrame(int id) {
		return items[id][ITEM_FRAME];
	}

	public BufferedImage getItemLock(int id) {
		return items[id][ITEM_LOCK];
	}
	
	public BufferedImage getItem(int id, int itemId) {
		return items[id][itemId+2];
	}
	
	public int getPositionX(int id, int itemId) {
		return positionsX[id][itemId];
	}
	
	public int getCount(int id) {
		return items[id].length;
	}
	
	public int getItemSize() {
		return itemSize;
	}
	
	public int getMaxItems() {
		return maxItems;
	}
	
	public Color getItemsColor(int id) {
		return itemsColors[id];
	}

	public BufferedImage getEntityChest() {
		return entityChest;
	}

	public BufferedImage getEntityBlue() {
		return entityBlue;
	}
}
