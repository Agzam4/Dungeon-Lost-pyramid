package game;

import java.awt.Color;
import java.awt.Composite;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import stages.SGame;
import work.LevelLoader;

public class DrawEffects {
	
	private SGame game;
	
	public DrawEffects(SGame game) {
		this.game = game;
	}

	private Polygon level10$light = new Polygon(
			new int[] {
					44*Tile.tilesize,
					65*Tile.tilesize,
					65*Tile.tilesize,
					49*Tile.tilesize,
					47*Tile.tilesize,
					46*Tile.tilesize,
					45*Tile.tilesize + Tile.tilesize/2,
					43*Tile.tilesize - Tile.tilesize/2,
					
					43*Tile.tilesize,
					
					43*Tile.tilesize,
					44*Tile.tilesize
			},
			new int[] {
					19*Tile.tilesize,
					19*Tile.tilesize,
					30*Tile.tilesize,
					30*Tile.tilesize,
					34*Tile.tilesize,
					34*Tile.tilesize,
					35*Tile.tilesize,
					35*Tile.tilesize,
					
					34*Tile.tilesize,
					
					33*Tile.tilesize,
					31*Tile.tilesize
					},
			11
			);
	

	private Polygon level10$light2 = new Polygon(
			new int[] {
					44*Tile.tilesize+3,
					65*Tile.tilesize+3,
					65*Tile.tilesize+3,
					49*Tile.tilesize+3,
					47*Tile.tilesize+3,
					46*Tile.tilesize,
					45*Tile.tilesize + Tile.tilesize/2+3,
					43*Tile.tilesize - Tile.tilesize/2+3,
					
					43*Tile.tilesize+3,
					
					43*Tile.tilesize+3,
					44*Tile.tilesize+3
			},
			new int[] {
					19*Tile.tilesize,
					19*Tile.tilesize,
					30*Tile.tilesize,
					30*Tile.tilesize,
					34*Tile.tilesize,
					34*Tile.tilesize,
					35*Tile.tilesize,
					35*Tile.tilesize,
					
					34*Tile.tilesize,
					
					33*Tile.tilesize,
					31*Tile.tilesize
					},
			11
			);
	BufferedImage level10$0 = loadImage(10, 0);
	BufferedImage level10$1 = loadAImage(10, 1);

	private BufferedImage loadAImage(int level, int id) {
		try {
			return ImageIO.read(LevelLoader.class.getResourceAsStream("/effects/level" + level + "_" + id + ".png"));
		} catch (IOException e) {
			System.err.println("[DrawEffects] Image not loaded");
			return new BufferedImage(1, 1, 1);
		}
	}
	
	private BufferedImage loadImage(int level, int id) {
		try {
			BufferedImage loaded = ImageIO.read(LevelLoader.class.getResourceAsStream("/effects/level" + level + "_" + id + ".png"));
			BufferedImage newImage = new BufferedImage(loaded.getWidth(), loaded.getHeight(), BufferedImage.TYPE_INT_RGB);
			Graphics2D g = (Graphics2D) newImage.getGraphics();
			g.drawImage(loaded, 0, 0, null);
			g.dispose();
			return newImage;
		} catch (IOException e) {
			System.err.println("[DrawEffects] Image not loaded");
			return new BufferedImage(1, 1, 1);
		}
	}

	public void drawEffects(Graphics2D g, int level) {
		level10(g);
	}
	
	public void drawBackgroundEffects(Graphics2D g, int level) {
		level10BG(g);
	}
	private void level10BG(Graphics2D g) {
		g.drawImage(level10$1,
				x(19*Tile.tilesize), y(46*Tile.tilesize),
				(int) (level10$1.getWidth()*game.getQuality()*2),
				(int) (level10$1.getHeight()*game.getQuality()*2),
				null);
	}
	
	private void level10(Graphics2D g) {
		Composite composite = g.getComposite();
		g.setComposite(LightComposite.INSTANCE);
		g.drawImage(level10$0,
				x(43*Tile.tilesize), y(27*Tile.tilesize),
				(int) (level10$0.getWidth()*game.getQuality()),
				(int) (level10$0.getHeight()*game.getQuality()),
				null);
//		g.drawImage(level10$1,
//				x(43*Tile.tilesize), y(27*Tile.tilesize),
//				(int) (level10$1.getWidth()*game.getQuality()),
//				(int) (level10$1.getHeight()*game.getQuality()),
//				null);
		g.setComposite(composite);
		
//		g.setColor(new Color(63,63,45));
//		GradientPaint gradientPaint = new GradientPaint(
//				x(0), y(19*Tile.tilesize), new Color(255,255,255),
//				x(0), y(34*Tile.tilesize), new Color(150,150,75));
//		g.setPaint(gradientPaint);
//		g.fill(fixPolygon(level10$light));
//		g.fill(fixPolygon(level10$light2));
//		int px = ;
//		int py = (int) ((y - 0/2 - game.mapY) * game.getQuality() + game.getGameHeight()/2);
	}
	
	private Polygon fixPolygon(Polygon polygon) {
		int[] xp = new int[polygon.xpoints.length];
		int[] yp = new int[polygon.ypoints.length];

		for (int x = 0; x < xp.length; x++) {
			xp[x] = x(polygon.xpoints[x]);
		}
		for (int y = 0; y < yp.length; y++) {
			yp[y] = y(polygon.ypoints[y]);
		}
		
		Polygon newPolygon = new Polygon(xp, yp, polygon.npoints);
		return newPolygon;
	}

	private int x(int x) {
		return (int) ((x - game.mapX%(game.getWidth()*Tile.tilesize)-10)*game.getQuality() + game.getGameWidth()/2);
	}
	
	private int y(int y) {
		return (int) ((y - game.mapY%(game.getHeight()*Tile.tilesize)-10)*game.getQuality() + game.getGameHeight()/2);
	}
}
