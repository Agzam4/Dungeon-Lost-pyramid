package gameGUI;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class ImageButton extends Button {

	BufferedImage ico;
	
	public ImageButton(BufferedImage ico) {
		this.ico = ico;
	}

	@Override
	protected void drawButton(Graphics2D g) {
		int width = (int) (visibleSize*ico.getHeight()/ico.getWidth());
		int height = (int) visibleSize;
		g.drawImage(ico, x-width/2, y-height/2, width, height, null);
		super.drawButton(g);
	}
	
	@Override
	public int getFontSize(Graphics2D g) {
		return (int) visibleSize;
	}

	@Override
	public int getFontWidth(Graphics2D g) {
		return  (int) (visibleSize*ico.getHeight()/ico.getWidth());
	}

	public int getWidth() {
		return  (int) (size*ico.getHeight()/ico.getWidth());
	}
	
	public int getHeight() {
		return  (int) (size);
	}
	
	@Override
	public int getLeftX(Graphics2D g) {
		return x-getFontWidth(g)/2;
	}
	
	@Override
	public int getTopY(Graphics2D g) {
		return y-getFontSize(g)/2;
	}
	
	public BufferedImage getIco() {
		return ico;
	}
	
	public void setIco(BufferedImage ico) {
		this.ico = ico;
	}
}
