package game;

import java.awt.Graphics2D;

import generator.Blocks;
import stages.SGame;

public class Player extends GameObject {

	/* 
	 * TODO:
	 * 
	 * Classes of players:
	 * > Tank
	 * > Lucky
	 * > Reacher
	 * > ...
	 * 
	 */
	
	public double speed = 8;
	public double slow = 2D; // 0.5D

	public static final int g = Tile.tilesize/17;
	public static final int jumpPower = (int) Math.sqrt(2*g*Tile.tilesize*3); // v = sqrt(2gh)
	
	SGame game;

	Box hitbox;
	Box groundBox;
	
	public Player(SGame game) {
		this.game = game;
		hitbox = new Box(0, 0, 20, 20);
		groundBox = new Box(0, 0, 20, 40);
	}
	
	/**
	 * Center X & Y
	 */
	int cx, cy;
	
	int HP = 0; // FIXME

	boolean isGrounded;
	
	boolean isTouchTrap;
	
	@Override
	public void update() {
		
		cx = (int) (hitbox.w/2d*getGame().getQuality());
		cy = (int) (hitbox.h/2d*getGame().getQuality());

		updatePosition();
		boolean isLadder = isLadder();
		updateTouch();
		
		if(isRIGHT) vx=speed;
		if(isLEFT) vx=-speed;
		
		if(isLadder) {
			if(isUP) vy=-speed;
			if(isDOWN) vy=speed;
		}else {
			if(isUP && isGrounded) vy=-jumpPower;
		}
		
		isTouchTrap = isTrap();
		if(isTouchTrap) {
			int id = getTrapID();
			if(id != -1) {
				if(id == Blocks.FINISH.ordinal()) {
					getGame().nextDungeon();
				}else {
					if(HP > 0) {
						HP--;
						destroyTraps();
					}else {
						if (id == Blocks.SPIKES.ordinal()) {
							getGame().gameOver("spikes");
						}else if (id == Blocks.CRUSHER_AIR.ordinal()) {
							getGame().gameOver("crusher");
						}else if (id == Blocks.SAND.ordinal()) {
							getGame().gameOver("quicksand");
						}else if (id == Blocks.LADDER_TRAP_LADDER.ordinal()) {
							getGame().gameOver("crusher");
						}
						return;
					}
				}
			}
		}

		x += vx;
		updatePosition();
		int intersectionX = getIntersectionX(Tile.HITBOX_WALL);
		if(intersectionX != 0) {
			x += intersectionX;
			vx = 0;
		}

//		y += vy;
//		updatePosition();
//		if(isWall(Tile.HITBOX_WALL)) {
//			y += getIntersectionY(Tile.HITBOX_WALL);
//			vy = 0;
//		}

		isGrounded = false;
		if(vy > 0) {
			for (double vectorY = vy; vectorY > 0; vectorY-=getHeight()) {
				if(vectorY > getHeight()) {
					y+=getHeight();
				}else {
					y+=vectorY%getHeight();
				}
				updatePosition();
				int intersectionY = getIntersectionY(Tile.HITBOX_WALL);
				if(intersectionY != 0) {
					y += intersectionY;
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
		
		if(isLadder) {
			if(vy > slow) {
				vy-=slow;
			}else if(vy < -slow) {
				vy+=slow;
			}else {
				vy = 0;
			}
		}else {
			vy += g;
		}
	}
	
	private void destroyTraps() {
		int left = (int)((x)/Tile.tilesize);
		int right = (int)((x+hitbox.w)/Tile.tilesize);
		int top = (int)((y)/Tile.tilesize);
		int bottom = (int)((y+hitbox.h)/Tile.tilesize);

		getGame().destroyTrap(left, top);
		getGame().destroyTrap(left, bottom);
		getGame().destroyTrap(right, top);
		getGame().destroyTrap(right, bottom);
	}

	private int getTrapID() {
		Tile tile = getTrap();
		if(tile == null) return -1;
		return tile.getType();
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
	
	private Tile getTrap() {
		int left = (int)((x)/Tile.tilesize);
		int right = (int)((x+hitbox.w)/Tile.tilesize);
		int top = (int)((y)/Tile.tilesize);
		int bottom = (int)((y+hitbox.h)/Tile.tilesize);

		if(checkTrap(left, top)) return getGame().getTile(left, top);
		if(checkTrap(left, bottom)) return getGame().getTile(left, bottom);
		if(checkTrap(right, top)) return getGame().getTile(right, top);
		if(checkTrap(right, bottom)) return getGame().getTile(right, bottom);
		
		return null;
	}
	
	private boolean checkTrap(int tx, int ty) {
		return getGame().getHitbox(tx, ty, Tile.HITBOX_REDZONE).intersects((int)x,(int)y);
	}
	
//	private boolean isGrounded() {
//		return checkTiles(hitbox, Tile.HITBOX_WALL, x, y+1);
//	}

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
	
	private int getIntersectionY(int hbType) {
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

	private void updatePosition() {
		hitbox.setPosition((int)x, (int)y);
	}
	
//	private boolean isWall(int hbType) {
//		return checkTiles(hitbox, hbType, x, y);
//	}
	
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
				
//		return /**/	getGame().getHitbox(left, top, hbType).intersects(box) 
//				|| 	getGame().getHitbox(left, bottom, hbType).intersects(box) 
//				|| 	getGame().getHitbox(right, top, hbType).intersects(box)
//				|| 	getGame().getHitbox(right, bottom, hbType).intersects(box)
//			   /**/;
	}
	
	public boolean checkTile(Box box) {
		return box.intersects(hitbox);
	}
	
	double vx, vy;

	@Override
	public void draw(Graphics2D g, Graphics2D fg) {
		int q = (int) getGame().getQuality();
//		int nx = (int) (x-getGame().mapX -Tile.tilesize + cx);
//		int ny = (int) (y-getGame().mapY -Tile.tilesize + cy);
		int nx = (int) ((x-getGame().mapX + getGame().getGameWidth()/q/2)*q - cx
				- getGame().getQsMapX());
		int ny = (int) (y-getGame().mapY + getGame().getGameHeight()/2  - cy
				- getGame().getQsMapY());
		int nd = (int) (hitbox.w*getGame().getQuality());
//		
//		g.setColor(isTouchTrap ? Color.RED.darker() : Color.LIGHT_GRAY);
//		g.drawRect(nx, ny, nd, nd);
		g.drawImage(getGame().getPack().player, nx, ny, nd, nd, null);
	}

	
	
	boolean isUP;
	boolean isDOWN;

	boolean isRIGHT;
	boolean isLEFT;
	
	public void up(boolean b) {
		isUP = b;
	}
	public void down(boolean b) {
		isDOWN = b;
	}
	public void right(boolean b) {
		isRIGHT = b;
	}
	public void left(boolean b) {
		isLEFT = b;
	}

	public int getWidth() {
		return hitbox.w;
	}
	
	public int getHeight() {
		return hitbox.h;
	}
}
