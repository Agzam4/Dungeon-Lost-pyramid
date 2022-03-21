package entities;

import java.awt.image.BufferedImage;

import game.Player;
import game.Tile;
import stages.SGame;

public class Blue extends Entity {

	public static final String name = "Winfred";
	
	public Blue(SGame game) {
		super(game);
		if(game.getDungeonLevel() == 10) {
			isLocked = true;
		}
		g = Player.g;
		jumpPower = Player.jumpPower;
		isTouchable = false;
	}
	
	private boolean level10$jumpOnPlayer = false;
	
	
	@Override
	protected void ai() {
//		System.out.println(getGame().getDungeonLevel());
		if(getGame().getDungeonLevel() == 11) {
			if(getGame().getLevelEventID() == 2) {
				isLocked = false;
				if(getDistanceToPlayer() < Tile.tilesize*1.5) {
					getGame().addDialog(name, new String[] {
							"Oh... Hi, I'm " + name + ".",
							"You fell into this trap too?",
							"I know how to get out",
							"but...",
							"I need help",
							"Stand in the left corner"
					});
					getGame().setLevelEventID(3);
				}
			}
			if(getGame().getLevelEventID() == 3) {
				if(getGame().isDialogEnd()) {
					getGame().setLevelEventID(4);
				}
			}
			if(level10$jumpOnPlayer) {
				isTouchable = true;
				vx = -speed;
				if(isGrounded) vy=-jumpPower;
				if(940 > x) {
					level10$jumpOnPlayer = false;
					getGame().setLevelEventID(5);
				}
			}

			if(getGame().getLevelEventID() == 6) {
				if(x <= 1024) {
					vx = speed;
					isTouchable = false;
				}else {
					getGame().setLevelEventID(7);
				}
			}
			if(getGame().getLevelEventID() == 7) {
				if(x >= 670) {
					vx = -speed;
				}else {
					getGame().setLevelEventID(8);
				}
			}
			if(getGame().getLevelEventID() == 8) {
				if(getDistanceToPlayer() < Tile.tilesize*1.5) {
					getGame().addDialog(name, new String[] {
							"Fantastic! This is an amazing place!",
							"It's not like Egyptian architecture"
					});
					getGame().setLevelEventID(9);
				}
			}
		}
	}

	@Override
	protected BufferedImage image() {
		return getGame().getPack().getEntityBlue();
	}
	
	
	public void level10$jumpOnPlayer() {
		level10$jumpOnPlayer = true;
//		if(isGrounded) vy=-jumpPower;
	}

}
