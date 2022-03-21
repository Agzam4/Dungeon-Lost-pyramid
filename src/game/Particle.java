package game;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import stages.Manager;

public class Particle extends GameObject {

	public static int PARTICLE_CRUSHER = 0;
	
	double rotate;
	int type;
	double vx, vy;
	double x, y;
	
	int imgId = 0;
	
	public Particle(int type, Manager manager, double rotate, double vx, double vy, double x, double y) {
		this.manager = manager;
		this.rotate = rotate;
		this.type = type;
		imgId = (int) (Math.random()*getImages().length);
		this.vx = vx;
		this.vy = vy;
		this.x = x;
		this.y = y;
	}
	
	@Override
	public void update() {
		x+=vx;
		y+=vy;
		
//		vx/=1.1;
		vy+=1;

		if(vx > 0) rotate += Math.abs(Math.atan2(vx, vy)/2d);
		if(vx < 0) rotate -= Math.abs(Math.atan2(vx, vy)/2d);
	}

	@Override
	public void draw(Graphics2D g, Graphics2D fg) {
		int px = (int) ((x - 0/2 - mapX) * getQuality() + getGame().getGameWidth()/2);/*  */
		int py = (int) ((y - 0/2 - mapY) * getQuality() + getGame().getGameHeight()/2);
		int drawX = (int) (px);
		int drawY = (int) (py);
		int drawW = (int) (getImg().getWidth()*getQuality());
		int drawH = (int) (getImg().getHeight()*getQuality());
    
		System.out.println("MAP: " + mapX);
		System.out.println("X1: " + drawX);
		System.out.println("X2: " + (drawX+getTQGameWidth()));
		
		drawImage(g, drawX, drawY, drawW, drawH);
		
		drawImage(g, drawX - getTQGameWidth(), drawY, drawW, drawH);
		drawImage(g, drawX + getTQGameWidth(), drawY, drawW, drawH);
		
		drawImage(g, drawX, drawY + getTQGameHeight(), drawW, drawH);
		drawImage(g, drawX, drawY - getTQGameHeight(), drawW, drawH);

		drawImage(g, drawX - getTQGameWidth(), drawY + getTQGameHeight(), drawW, drawH);
		drawImage(g, drawX - getTQGameWidth(), drawY - getTQGameHeight(), drawW, drawH);
		drawImage(g, drawX + getTQGameWidth(), drawY + getTQGameHeight(), drawW, drawH);
		drawImage(g, drawX + getTQGameWidth(), drawY - getTQGameHeight(), drawW, drawH);
	}
	
	private int getTQGameWidth() {
		return (int) (getGame().getWidth()*Tile.tilesize*getQuality());
	}
	private int getTQGameHeight() {
		return (int) (getGame().getHeight()*Tile.tilesize*getQuality());
	}
	
	private void drawImage(Graphics2D g, int drawX, int drawY, int drawW, int drawH) {
		g.rotate(rotate, drawX + drawW/2, drawY + drawH/2);
		g.drawImage(getImg(),
				drawX,
				drawY,
				drawW,
				drawH,
				null);
		g.rotate(-rotate, drawX + drawW/2, drawY + drawH/2);
	}
	
	public BufferedImage getImg() {
		return getImages()[imgId];
	}
	
	private double getQuality() {
		return getManager().getPanel().quality;
	}
	
	private BufferedImage[] getImages() {
		if(type == PARTICLE_CRUSHER) return getManager().getPack().getParticlesCrusher();
		return null;
	}
	
	double mapX, mapY;
	
	public void setCam(double mapX, double mapY) {
		this.mapX = mapX;
		this.mapY = mapY;
	}
}
