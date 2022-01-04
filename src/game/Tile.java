package game;

import generator.Blocks;
import stages.SGame;

public class Tile {

	public static final int tilesize = 32;

	public static final int HITBOX_WALL = 0;
	public static final int HITBOX_REDZONE = 1;
	public static final int HITBOX_LADDER = 2;
	public static final int HITBOX_TOUCHBOX = 3;
	
	private enum Hitboxes {

		// WALL
		AIR (new Box(0,0,0,0)),
		BOX (new Box(0,0, tilesize, tilesize)),
		UP_HALF (new Box(0,0, tilesize, tilesize/2)),
		DOWN_HALF (new Box(0,tilesize/2, tilesize, tilesize/2)),
		STICK (new Box(tilesize/2 - tilesize/8, 0, tilesize/4, tilesize)),
		SPIKES (new Box(0, 5, tilesize, tilesize-5)),
		CRUSHER_AIR (new Box(0, 0, tilesize, 0)),
		LADDER_TRAP (new Box(0, 0, tilesize, tilesize)),
		
		// REDZONE
		RCRUSHER_AIR (new Box(1, 0, tilesize-2, 0)),
		RSPIKES (new Box(1, 1, tilesize-2, tilesize-2)),
		RSAND (new Box(0, 0, tilesize, tilesize-1)),
		RFINISH (new Box(2, 8, 28, 24)),
		
		// LADDER
		LADDER (new Box(2, 0, tilesize-4, tilesize)),
		
		NULL (null);
		
		Box hitbox;
		private Hitboxes(Box hitbox) {
			this.hitbox = hitbox;
		}
		
		public Box getHitbox() {
			return hitbox.clone();
		}
	}

	SGame game;
	private Box hitbox, redzone, ladder, drawBox, touchzone;
	private int type;
	
	public Tile(int type) {
		this.type = type;
		
		if(type == Blocks.AIR.ordinal()) hitbox = Hitboxes.AIR.getHitbox();
		
		if(type == Blocks.CRUSHER.ordinal()) hitbox = Hitboxes.BOX.getHitbox();
		if(type == Blocks.CRUSHER_AIR.ordinal()) hitbox = Hitboxes.CRUSHER_AIR.getHitbox();
		if(type == Blocks.CRUSHER_DOWN.ordinal()) hitbox = Hitboxes.BOX.getHitbox();

		if(type == Blocks.HALF_STICK_UP.ordinal()) hitbox = Hitboxes.UP_HALF.getHitbox();

		if(type == Blocks.LADDER.ordinal()) hitbox = Hitboxes.AIR.getHitbox();
		if(type == Blocks.LADDER_TRAP_LADDER.ordinal()) hitbox = Hitboxes.AIR.getHitbox();
		if(type == Blocks.LADDER_TRAP_LEFT.ordinal()) hitbox = Hitboxes.BOX.getHitbox();
		if(type == Blocks.LADDER_TRAP_RIGHT.ordinal()) hitbox = Hitboxes.BOX.getHitbox();

		if(type == Blocks.SAND.ordinal()) hitbox = Hitboxes.DOWN_HALF.getHitbox();
		if(type == Blocks.SANDWALL.ordinal()) hitbox = Hitboxes.BOX.getHitbox();
		
		if(type == Blocks.SPIKES.ordinal()) hitbox = Hitboxes.SPIKES.getHitbox();

		if(type == Blocks.START_WALL.ordinal()) hitbox = Hitboxes.BOX.getHitbox();
		if(type == Blocks.STICK.ordinal()) hitbox = Hitboxes.STICK.getHitbox();
		if(type == Blocks.WALL.ordinal()) hitbox = Hitboxes.BOX.getHitbox();
		if(type == Blocks.CRACKED_WALL.ordinal()) hitbox = Hitboxes.BOX.getHitbox();

		if(type == Blocks.FINISH.ordinal()) hitbox = Hitboxes.AIR.getHitbox();

		if(type == Blocks.SECRET_DOOR.ordinal()) hitbox = Hitboxes.BOX.getHitbox();
		if(type == Blocks.SECRET_AIR.ordinal()) hitbox = Hitboxes.BOX.getHitbox();
		if(type == Blocks.SECRET_WALL.ordinal()) hitbox = Hitboxes.BOX.getHitbox();
		if(type == Blocks.SECRET_CHEST.ordinal()) hitbox = Hitboxes.BOX.getHitbox();
		if(type == Blocks.SECRET_COLUMN.ordinal()) hitbox = Hitboxes.BOX.getHitbox();
		if(type == Blocks.SECRET_CARVED_BLOCK.ordinal()) hitbox = Hitboxes.BOX.getHitbox();
		if(type == Blocks.SECRET_COLUMN_FIRE.ordinal()) hitbox = Hitboxes.AIR.getHitbox();
		
		redzone = Hitboxes.AIR.getHitbox();

		if(type == Blocks.CRUSHER_AIR.ordinal()) redzone = Hitboxes.RCRUSHER_AIR.getHitbox();
		if(type == Blocks.LADDER_TRAP_LADDER.ordinal()) redzone = Hitboxes.LADDER_TRAP.getHitbox();
		if(type == Blocks.SAND.ordinal()) redzone = Hitboxes.RSAND.getHitbox();
		if(type == Blocks.SPIKES.ordinal()) redzone = Hitboxes.RSPIKES.getHitbox();
		if(type == Blocks.FINISH.ordinal()) redzone = Hitboxes.RFINISH.getHitbox();
		

		ladder = Hitboxes.AIR.getHitbox();
		if(type == Blocks.LADDER.ordinal()) ladder = Hitboxes.LADDER.getHitbox();
		if(type == Blocks.LADDER_TRAP_LADDER.ordinal()) ladder = Hitboxes.LADDER.getHitbox();
		
		drawBox = Hitboxes.BOX.getHitbox();

		touchzone = Hitboxes.AIR.getHitbox();
		if(type == Blocks.SECRET_DOOR.ordinal()) touchzone = Hitboxes.BOX.getHitbox();
		if(type == Blocks.SECRET_CHEST.ordinal()) touchzone = Hitboxes.RFINISH.getHitbox();
	}
	
	public void setGame(SGame game) {
		this.game = game;
	}
	
	public void setPosition(int px, int py) {
//		System.out.println("[Tile.setPosition()] " + px + " " + py);
		hitbox.setPosition(px, py);
	}
	
	int trapTime = 0;
	double trapRedzone = 0;

	int sTouchTime = 0;
	
	
	public void updateTouch() {
		System.out.println(sTouchTime);
		if(isSecretDoor()) {
			sTouchTime++;
			if(sTouchTime > 10) {
				hitbox.y = Math.max(0, (sTouchTime-10));
				drawBox.y = Math.max(0, (sTouchTime-10));
				if(sTouchTime > tilesize + 10) {
					game.openSecretRoom();
					hitbox.h = 0;
					drawBox.h = 0;
				}
			}
		}else if(isSecretChest()) {
			game.chestLooted();
		}
	}
	
	public void update() {
		if(game.isSecretRoomOpened()) {
			if(isSecretAir() || isSecretDoor() || isSecretChest()) {
				hitbox.w = 0;
			}
		}
		if(isCrusherAir() || isLadderTrap() || isLadderTrapRight() || isLadderTrapLeft()) {
			trapTime++;
			if(trapTime > 50) {
				
				//////////////
				 trapTime = 0;
				//////////////
				
			}else if(trapTime < 15) {
				trapRedzone *= 0.75;
			}else if(trapTime < 35) {
				trapRedzone = 0;
			}else if(trapTime < 60) {
				trapRedzone = (trapRedzone-Tile.tilesize)/2+Tile.tilesize;
			}else if(trapTime < 50) {
				trapRedzone = Tile.tilesize;
			}
			if(isCrusherAir()) {
				redzone.h = (int) trapRedzone;
				hitbox.h = (int) trapRedzone;
			}
			if(isLadderTrap()) {
				redzone.w = (int) trapRedzone;
			}
//			if(isLadderTrapLeft()) {
//				drawBox.x = (int) trapRedzone/2;
//			}
//			if(isLadderTrapRight()) {
//				drawBox.x = (int) trapRedzone/-2;
//			}
		}else if(isSand()) {
			trapTime++;
			trapRedzone = -(tilesize/5 + tilesize/5*Math.cos(trapTime/15d));
		}
	}

	public boolean isSecretRoomBlock() {
		return isSecretDoor() || isSecretAir() || isSecretChest() 
				|| isSecretColumn() || isSecretCarvedBlock() || isSecretColumnFire();
	}
	
	public boolean isSecretDoor() {
		return type == Blocks.SECRET_DOOR.ordinal();
	}
	public boolean isSecretAir() {
		return type == Blocks.SECRET_AIR.ordinal();
	}
	public boolean isSecretChest() {
		return type == Blocks.SECRET_CHEST.ordinal();
	}
	public boolean isSecretColumn() {
		return type == Blocks.SECRET_COLUMN.ordinal();
	}
	public boolean isSecretColumnFire() {
		return type == Blocks.SECRET_COLUMN_FIRE.ordinal();
	}
	public boolean isSecretCarvedBlock() {
		return type == Blocks.SECRET_CARVED_BLOCK.ordinal();
	}
	
	public boolean isFire() {
		return isSecretColumnFire(); // || ...
	}
	
	public boolean isCrusherAir() {
		return type == Blocks.CRUSHER_AIR.ordinal();
	}
	public boolean isLadderTrap() {
		return type == Blocks.LADDER_TRAP_LADDER.ordinal();
	}
	public boolean isLadderTrapRight() {
		return type == Blocks.LADDER_TRAP_RIGHT.ordinal();
	}
	public boolean isLadderTrapLeft() {
		return type == Blocks.LADDER_TRAP_LEFT.ordinal();
	}
	public boolean isSand() {
		return type == Blocks.SAND.ordinal();
	}

	public boolean isSpikes() {
		return type == Blocks.SPIKES.ordinal();
	}
	public int getTrapRedzoneInt() {
		return (int) trapRedzone;
	}
	
	public Box getHitbox() {
		return hitbox;
	}
	
	public Box getRedzone() {
		return redzone;
	}
	
	public Box getLadder() {
		return ladder;
	}
	
	public Box getDrawBox() {
		return drawBox;
	}
	
	public Box getTouchzone() {
		return touchzone;
	}
	
	public int getType() {
		return type;
	}

	public boolean isTrap() {
		return isCrusherAir() || isLadderTrap() || isSpikes();
	}
}
