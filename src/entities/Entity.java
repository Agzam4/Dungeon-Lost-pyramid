package entities;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import game.Box;
import game.GameObject;
import game.Tile;
import stages.SGame;

public abstract class Entity extends GameObject {

	protected double speed = 8D;
	protected double slow = 2D;
	protected double g = Tile.tilesize/17d;
	protected double jumpPower = Math.sqrt(2*g*Tile.tilesize*3);

	protected Box hitbox;
	
	protected BufferedImage img = null;
	
	public Entity(SGame game) {
		hitbox = new Box(0, 0, 20, 20);
		setGame(game);
		setManager(game.getManager());
	}

	protected abstract void ai();
	protected abstract BufferedImage image();
	
	

	protected int HP = 1;
	private boolean isAlive = true;
	
	protected int cx, cy;
	protected double vx, vy;

	private double effect = 0;
	protected double effectMove = 0;
	private boolean isTouchTrap;

	protected boolean isLocked = false;
	protected boolean isTouchable = true;

	protected boolean isGrounded;
	
	@Override
	public void update() {
		cx = (int) (hitbox.w/2d*getGame().getQuality());
		cy = (int) (hitbox.h/2d*getGame().getQuality());

		updatePosition();
		boolean isLadder = isLadder();
		updateTouch();
		
		double eff = 0;
		
		ai();
		
		isTouchTrap = isTrap();
		if(isTouchTrap) {
			HP--;
			if(HP < 1) {
				isAlive = false;
			}
		}
		if(isLocked) {
			vx = 0;
			vy = 0;
		}
		x += vx;
		updatePosition();
		int intersectionX = getIntersectionX(Tile.HITBOX_WALL);
		if(intersectionX != 0) {
			x += intersectionX;
			vx = 0;
			effectMove = 0;
		}

		isGrounded = false;
		if(vy > 0) {
			for (double vectorY = vy; vectorY > 0; vectorY-=getHeight()) {
				if(vectorY > getHeight()) {
					y+=getHeight();
				}else {
					y+=vectorY%getHeight();
				}
				updatePosition();
				if(isTouchable) {
					int intersectionPlayerY = getPlayerIntersectionY(Tile.HITBOX_WALL);
					if(intersectionPlayerY != 0) {
						y += intersectionPlayerY;
						if(vy > g) {
							effect = -vy/20;
						}
						vy = 0;
						isGrounded = true;
						break;
					}
				}
				
				int intersectionY = getIntersectionY(Tile.HITBOX_WALL);
				if(intersectionY != 0) {
					y += intersectionY;
					if(vy > g) {
						effect = -vy/20;
					}
					vy = 0;
					isGrounded = true;
					break;
				}
			}
		}else if(vy < 0) {
			for (double vectorY = -vy; vectorY > 0; vectorY-=getHeight()) {
				if(vectorY > getHeight()) {
					y-=getHeight();
				}else {
					y-=vectorY%getHeight();
				}
				updatePosition();
				int intersectionY = getIntersectionY(Tile.HITBOX_WALL);
				if(intersectionY != 0) {
					y += intersectionY;
					vy = 0;
					eff = 0;
					break;
				}
			}
		}

		if(vx > slow) {
			vx-=slow;
		}else if(vx < -slow) {
			vx+=slow;
		}else {
			vx = 0;
		}
		
		if(effectMove > slow) {
			effectMove-=slow;
		}else if(effectMove < -slow) {
			effectMove+=slow;
		}else {
			effectMove = 0;
		}
		
		if(isLadder) {
			eff += Math.abs(vy)/50;
			if(vy > slow) {
				vy-=slow;
			}else if(vy < -slow) {
				vy+=slow;
			}else {
				vy = 0;
			}
		}else {
			eff += vy > 0 ? 0 : -vy/50;
			vy += g;
		}
		
		if(Math.abs(effectMove) < speed) {
			eff += Math.abs(effectMove)/15;
		}
		
		effect = (effect-eff)/2+eff;
	}
	
	public void destroyTraps() {
		int left = (int)((x)/Tile.tilesize);
		int right = (int)((x+hitbox.w)/Tile.tilesize);
		int top = (int)((y)/Tile.tilesize);
		int bottom = (int)((y+hitbox.h)/Tile.tilesize);

		getGame().destroyTrap(left, top);
		getGame().destroyTrap(left, bottom);
		getGame().destroyTrap(right, top);
		getGame().destroyTrap(right, bottom);
	}

	private void updateTouch() {
		Tile tile = getTouch();
		if(tile == null) return;
		tile.updateTouch();
	}
	
	private boolean checkTouch(int tx, int ty) {
		return getGame().getHitbox(tx, ty, Tile.HITBOX_TOUCHBOX).intersects((int)x,(int)y);
	}
	
	private Tile getTouch() {
		int left = (int)((x-1)/Tile.tilesize);
		int right = (int)((x+hitbox.w)/Tile.tilesize);
		int top = (int)((y-1)/Tile.tilesize);
		int bottom = (int)((y+hitbox.h)/Tile.tilesize);

		if(checkTouch(left, top)) return getGame().getTile(left, top);
		if(checkTouch(left, bottom)) return getGame().getTile(left, bottom);
		if(checkTouch(right, top)) return getGame().getTile(right, top);
		if(checkTouch(right, bottom)) return getGame().getTile(right, bottom);
		
		return null;
	}

	private boolean isLadder() {
		return checkTiles(hitbox, Tile.HITBOX_LADDER, x, y);
	}
	
	private boolean isTrap() {
		return checkTiles(hitbox, Tile.HITBOX_REDZONE, x, y);
	}
	
	private int getIntersectionX(int hbType) {
		int left = (int)((x)/Tile.tilesize);
		int right = (int)((x+getWidth())/Tile.tilesize);
		int top = (int)((y)/Tile.tilesize);
		int bottom = (int)((y+getHeight())/Tile.tilesize);
		
		int sum = 0;
		
		if(vx <= 0) {
			int iLeft = Math.max(
					hitbox.getIntersectionLeft(getGame().getHitbox(left, top, hbType)),
					hitbox.getIntersectionLeft(getGame().getHitbox(left, bottom, hbType)));
			if(iLeft > 0) sum += iLeft;
		}
		if(vx >= 0) {
			int iRight = Math.max(
					hitbox.getIntersectionRight(getGame().getHitbox(right, top, hbType)),
					hitbox.getIntersectionRight(getGame().getHitbox(right, bottom, hbType)));
			if(iRight > 0) sum -= iRight;
		}
		return sum;
	}
	
	private int getIntersectionY(int hbType) { // TODO
		int left = (int)((x)/Tile.tilesize);
		int right = (int)((x+getWidth())/Tile.tilesize);
		int top = (int)((y)/Tile.tilesize);
		int bottom = (int)((y+getHeight())/Tile.tilesize);
		
		int sum = 0;

		if(vy <= 0) {
			int iTop = Math.max(
					hitbox.getIntersectionTop(getGame().getHitbox(right, top, hbType)),
					hitbox.getIntersectionTop(getGame().getHitbox(left, top, hbType)));
			if(iTop > 0) sum += iTop;
		}
		if(vy >= 0) {
			int iBottom = Math.max(
					hitbox.getIntersectionBottom(getGame().getHitbox(right, bottom, hbType)),
					hitbox.getIntersectionBottom(getGame().getHitbox(left, bottom, hbType)));
			if(iBottom > 0) sum -= iBottom;
		}
		return sum;
	}
	
	private int getPlayerIntersectionY(int hbType) {
		int left = (int)((x)/Tile.tilesize);
		int right = (int)((x+getWidth())/Tile.tilesize);
		int top = (int)((y)/Tile.tilesize);
		int bottom = (int)((y+getHeight())/Tile.tilesize);
		
		int sum = 0;

		if(vy > 0) {
			int iBottom = getHitbox().getIntersectionBottom(getGame().getPlayer().getHitbox());
			if(iBottom > 6) {
				if(iBottom > 0) sum -= iBottom;
			}
		}
		return sum;
	}

	private void updatePosition() {
		hitbox.setPosition((int)x, (int)y);
	}
	
	private boolean checkTiles(Box box, int hbType, double x, double y) {
		int left = (int)((x)/Tile.tilesize);
		int right = (int)((x+box.w)/Tile.tilesize);
		int top = (int)((y)/Tile.tilesize);
		int bottom = (int)((y+box.h)/Tile.tilesize);
		
		return /**/ getGame().getHitbox(left, top, hbType)		.intersects((int)x, 		(int)y)
				||  getGame().getHitbox(left, bottom, hbType)	.intersects((int)x, 		(int)(y+box.h))
				||  getGame().getHitbox(right, top, hbType)		.intersects((int)(x+box.w), (int)y)
				||  getGame().getHitbox(right, bottom, hbType)	.intersects((int)(x+box.w), (int)(y+box.h))
			   /**/;
	}
	
	public boolean checkTile(Box box) {
		return box.intersects(hitbox);
	}
	
	@Override
	public void draw(Graphics2D g, Graphics2D fg) {
		if(img != null) {
			int q = (int) getGame().getQuality();
			int nx = (int) ((x - getGame().mapX%(getGame().getWidth()*Tile.tilesize)-10)*getGame().getQuality() + getGame().getGameWidth()/2);
			int ny = (int) ((y - getGame().mapY%(getGame().getHeight()*Tile.tilesize)-10)*getGame().getQuality() + getGame().getGameHeight()/2);

//			int nx = (int) ((x-getGame().mapX + getGame().getGameWidth()/q/2)*q - cx
//					- getGame().getQsMapX());
//			int ny = (int) (y-getGame().mapY + getGame().getGameHeight()/2  - cy
//					- getGame().getQsMapY());

			int nw = (int) (hitbox.w*getGame().getQuality());
			int nh = (int) (hitbox.h*getGame().getQuality());
			
			int eff = (int) (effect*nw/2);
			g.drawImage(img, nx + eff/2, ny - eff, nw - eff, nh + eff, null);
		}else {
			img = image();
		}
	}

	public int getWidth() {
		return hitbox.w;
	}
	
	public int getHeight() {
		return hitbox.h;
	}
	
	public boolean isAlive() {
		return isAlive;
	}
	
	protected double getDistanceToPlayer() {
		return Math.hypot(
				x - getGame().mapX%(getGame().getTWidth()),
				y - getGame().mapY%(getGame().getTHeight())
				);
	}
	
	public Box getHitbox() {
		return new Box(
				(int)(x%getGame().getTWidth() + getGame().getTWidth())%getGame().getTWidth(),
				(int)(y%getGame().getTHeight() + getGame().getTHeight())%getGame().getTHeight(),
				hitbox.w,
				hitbox.h);
	}
}
