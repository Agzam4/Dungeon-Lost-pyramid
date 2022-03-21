package entities;

import java.awt.image.BufferedImage;

import game.Tile;
import stages.SGame;

public class Chest extends Entity {

	public Chest(SGame game) {
		super(game);
		hitbox.w = 28;
		hitbox.h = 24;
		speed = 10D;
		if(game.getDungeonLevel() == 10) {
			isLocked = true;
			isTouchable = false;
		}
	}

	@Override
	protected void ai() {
		if(getGame().getDungeonLevel() == 11) {
//			System.out.println("[Chest] LevelEventID: " + getGame().getLevelEventID());
			
			if(getGame().getLevelEventID() == 1) {
				isLocked = false;
				g *= 1.5;
				jumpPower = Math.sqrt(2*g*Tile.tilesize*3);
				vx = -speed;
				effectMove=speed;
				vy=-jumpPower/3d;
				getGame().nextLevelEvent();
			}else if(getGame().getLevelEventID() == 2) {
				vx -= speed/3d;
				if(isGrounded) vy=-jumpPower/2d;
			}
		}
//		if(isGrounded) vy=-jumpPower;
//		System.out.println(x-(getGame().mapX%(getGame().getWidth()*Tile.tilesize)-10) 
//				+ " " + (y-getGame().mapY%(getGame().getHeight()*Tile.tilesize)-10));
	}

	@Override
	protected BufferedImage image() {
		return getGame().getPack().getEntityChest();
	}
}
