package gameGUI;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import work.Mouse;

public class Button {

	// 0 - default
	public static final int ALIGN_LEFT = -1;
	public static final int ALIGN_TOP = -1;
	public static final int ALIGN_CENTER = 0;
	public static final int ALIGN_BOTTOM = 1;
	public static final int ALIGN_RIGHT = 1;

	public boolean clickable = true;
	protected boolean isClicked = false;
	protected boolean isHover = false;
	
	protected boolean isVisible = true;
	
	public boolean isResizable = true;
	
	protected String text = "";
	protected int x, y;
	protected int alignX, alignY;

	protected int size = 25;
	protected int sizeHover = 25;
	protected double visibleSize = 25;
	
	public Button() {
	}
	
	public Button(String text) {
		this.text = text;
	}

	public void update(Mouse mouse) {
		if(!isVisible) return;
		if(g == null) return;
		isHover = getLeftX(g) < mouse.getFrameMouseX() && mouse.getFrameMouseX() < getRightX(g)
				&& getTopY(g) < mouse.getFrameMouseY() && mouse.getFrameMouseY() < getBottomY(g);
		
		if(isHover || isClicked) {
			if(mouse.isMouseClicked()) {
				isClicked = true;
				if(isResizable) {
					visibleSize = size/1.5;
				}
			}
		}
		
		if(isResizable) {
			if(isHover) {
				visibleSize = (visibleSize - sizeHover)/5 + sizeHover;
			}else {
				visibleSize = (visibleSize - size)/5 + size;
			}
		}else {
			visibleSize = size;
		}
		
		if(!mouse.isMouseClicked()) {
			isClicked = false;
		}
	}
	
	private Graphics2D g;
	
	public void draw(Graphics2D g) {
		this.g = g;
		g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, (int) visibleSize));
		
		drawButton(g);
	}
	
	protected void drawButton(Graphics2D g) {
		g.setColor(Color.WHITE);
		drawAt(g, getLeftX(g), getBottomY(g));
		int shadowSize = size/10;
		g.setColor(Color.BLACK);
		for (int angle = 0; angle < 360; angle+=36) {
			double a = Math.toRadians(angle);
			drawAt(g, (int) (getLeftX(g) + Math.cos(a)*shadowSize), (int) (getBottomY(g) + Math.sin(a)*shadowSize));
		}
	}

	protected void drawAt(Graphics2D g, int x, int y) {
		g.drawString(text, x, y);
	}

	public int getLeftX(Graphics2D g) {
		if(alignX == ALIGN_LEFT) return x;
		if(alignX == ALIGN_RIGHT) return x-getFontWidth(g);
		return x - getFontWidth(g)/2;
	}
	
	public int getRightX(Graphics2D g) {
		return getLeftX(g) + getFontWidth(g);
	}

	public int getTopY(Graphics2D g) {
		if(alignY == ALIGN_TOP) return y;
		if(alignY == ALIGN_BOTTOM) return y-getFontSize(g);
		return y-getFontSize(g)/2;
	}
	
	public int getBottomY(Graphics2D g) {
		return getTopY(g) + getFontSize(g);
	}

	public int getFontWidth(Graphics2D g) {
		return g.getFontMetrics().stringWidth(text);
	}
	
	public int getFontSize(Graphics2D g) {
		return g.getFont().getSize();
	}
	
	
	public void setX(int x) {
		this.x = x;
	}
	
	public void setY(int y) {
		this.y = y;
	}
	
	public void setAlignX(int alignX) {
		this.alignX = alignX;
	}
	
	public void setAlignY(int alignY) {
		this.alignY = alignY;
	}
	
	public void setSize(int size) {
		this.size = size;
		sizeHover = (int) (size*1.25);
	}
	
	public boolean isClicked() {
		return isClicked && isVisible;
	}
	
	public void reclick() {
		isClicked = false;
	}

	public void hide() {
		isVisible = false;
	}
	
	public void show() {
		isVisible = true;
	}
	
	public void clickEffect() {
		visibleSize = size/2;
	}
}
