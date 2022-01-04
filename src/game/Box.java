package game;

import java.awt.Point;

public class Box implements Cloneable {

	/**
	 * x, y - Position in tile
	 */
	public int x, y, w, h;
	
	/**
	 * Position of tile
	 */
	public int px, py;
	
	public Box(int x, int y, int w, int h) {
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
	}
	
	public int getLeftX() {
		return x + px;
	}
	
	public int getTopY() {
		return y + py;
	}
	
	public int getW() {
		return w;
	}
	
	public int getH() {
		return h;
	}

	public int getRightX() {
		return getLeftX() + w;
	}
	
	public int getBottomY() {
		return getTopY() + h;
	}
	
	public void setPosition(int px, int py) {
		this.px = px;
		this.py = py;
	}
	
	/* - 0 +
	 * 
	 *   T		-
	 * L + R    0
	 *   B		+
	 */
	
	
	public boolean intersects(Box box) {
//		System.out.println("[Box.intersects()] " + box.getRightX() + " > " + getLeftX());
//		System.out.println("[Box.intersects()] " + box.getLeftX() + " < " + getRightX());
		if(box.getW() == 0 || getW() == 0) return false;
		if(box.getH() == 0 || getH() == 0) return false;
		return box.getRightX() >= getLeftX() && box.getLeftX() <= getRightX()
				 && box.getBottomY() >= getTopY() && box.getTopY() <= getBottomY();
	}

	public boolean intersects(Point point) {
		return point.x > getLeftX() && point.x < getRightX()
				&& point.y < getBottomY() && point.y > getTopY();
	}

	public boolean intersects(int x, int y) {
		if(getW() == 0 || getH() == 0) return false;
//		System.out.println("[Box.intersects(x,y)]");
//		System.out.println(" > " + w + "x" + h);
//		System.out.println(" > " + px + ";" + py);
//		System.out.println(" > " + getLeftX() + " <= " + x + " <= " + getRightX());
//		System.out.println(" > " + getTopY() + " <= " + y + " <= " + getBottomY());
		
		//*
		return getLeftX() <= x%Tile.tilesize && x%Tile.tilesize <= getRightX()
				&& getTopY() <= y%Tile.tilesize  && y%Tile.tilesize <= getBottomY();
				/*/
		return getLeftX() <= x && x <= getRightX()
				&& getTopY() <= y  && y <= getBottomY();
		//*/
	}
	
	public int getIntersectionLeft(Box box) {
		if(checkbox(box)) return 0;
		return box.getW() - (getLeftX() - box.getLeftX());
	}
	
	public int getIntersectionRight(Box box) {
		if(checkbox(box)) return 0;
		return getW() - (box.getLeftX() - getLeftX());
	}

	public int getIntersectionTop(Box box) {
		if(checkbox(box)) return 0;
		return box.getH() - (getTopY() - box.getTopY());
	}
	
	public int getIntersectionBottom(Box box) {
		if(checkbox(box)) return 0;
		return getH() - (box.getTopY() - getTopY());
	}
	
	private boolean checkbox(Box box) {
		return box.getBottomY() <= getTopY() 	|| 	box.getTopY() 	>= getBottomY() ||
				box.getRightX() <= getLeftX() 	|| 	box.getLeftX() 	>= getRightX()
				|| box.getW() == 0 || getW() == 0 || box.getH() == 0 || getH() == 0;
	}
	
	@Override
	protected Box clone() {
		try {
			return (Box) super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}
}
