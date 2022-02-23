package stages;

import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RadialGradientPaint;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import debug.Debug;
import game.Box;
import game.LightComposite;
import game.Player;
import game.Tile;
import gameGUI.Button;
import gameGUI.ImageButton;
import generator.Blocks;
import generator.LevelGenerator;
import generator.MinimapGenerator;
import work.Data;
import work.Pack;

public class SGame extends Stage {

	/**
	 * DEBUG
	 */
	
	boolean hitboxMode = false;
	
	int map[][];
	Tile[][] tiles;

	Player player;

	int width;
	int height;
	
	int mapCenterX, mapCenterY;
	int mapTilesWidth, mapTilesHeight;
	
	
	int dungeonLevel;

	public static float lightRadius = 2;
	public static float effectLight = 1;

	private Button gameOverMenu;
	private Button gameOverNext;
	
	ImageButton minimapButton;

	public SGame() {

		gameOverMenu = new Button("Menu");
		gameOverNext = new Button("Next");
		gameOverMenu.hide();
		gameOverNext.hide();
		
		
		dungeonLevel = Debug.isSecretRoomTesting ? 3 : 0;
		gameOver = null;
		
		player = new Player(this);
		player.setGame(this);

		needNextDungeon = false;
		generate();
		
		blackScreenAlpha = -1;
		effectBlackScreenAlpha = -1;
	}
	
	BufferedImage minimap = null;
	boolean isMinimpOpen = false;
	double minimapSize = 0;
	double minimapDark = 0;
	
	private void generate() {
		LevelGenerator generator = new LevelGenerator();
		generator.setLevel(dungeonLevel);
		generator.generate(25 + dungeonLevel, 25 + dungeonLevel);
		
		minimap = MinimapGenerator.generate(generator.getMap());
		
		/*
		try {
			ImageIO.write(generator.draw(), "png", new File("map.png"));
			ImageIO.write(generator.drawID(), "png", new File("mapID.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		//*/
		
		width = generator.w;
		height = generator.h;

		mapTilesWidth = width*Tile.tilesize;
		mapTilesHeight = height*Tile.tilesize;
		
		mapCenterX = mapTilesWidth/2;
		mapCenterY = mapTilesHeight/2;

		map = generator.getMap();
		
		tiles = new Tile[map.length][map[0].length];
		for (int x = 0; x < map.length; x++) {
			for (int y = 0; y < map[x].length; y++) {
				tiles[x][y] = new Tile(map[x][y]);
				tiles[x][y].setPosition((x)*Tile.tilesize, (y)*Tile.tilesize);
				tiles[x][y].setGame(this);
			}
		}
		player.setPosition(
				mapCenterX - player.getHeight()/2,
				mapCenterY - Tile.tilesize/2 - player.getHeight());
		
		dungeonTitleTime = 1500;
		isSecretRoomOpened = Debug.isSecretRoomTesting;
		
		effectBlackScreenAlpha = -1;
		effectLight = 1f;
		isSecretChestLooted = false;
		
		dungeonLevel++;
		needNextDungeon = false;
	}

	int timer = 0;
	
	@Override
	public void update() {
		timer++;
		
		if(getManager() != null && minimapButton == null) {
			minimapButton = new ImageButton(getPack().minimapIco);
		}
		
		if(needNextDungeon) {
			if(blackScreenAlpha < 240) {
				blackScreenAlpha = (blackScreenAlpha - 255)*0.85 + 255;
			}else {
				blackScreenAlpha = 255;
				generate();
			}
		} else if(gameOver != null) {
			if(blackScreenAlpha < 240) {
				blackScreenAlpha = (blackScreenAlpha - 255)*0.85 + 255;
			}
		}else {
			blackScreenAlpha *= 0.8;
		}
		
		if(isGameOver()) {
			gameOverMenu.update(getMouse());
			gameOverNext.update(getMouse());

			if(gameOverMenu.isClicked()) {
				getManager().setStage(new SMenu());
			}
			
			if(gameOverNext.isClicked()) {
				getManager().setStage(new SGame());
			}
		}else {
			if(minimapButton != null) {
				minimapButton.update(getMouse());
				if(minimapButton.isClicked()) {
					isMinimpOpen = !isMinimpOpen;
					minimapButton.clickEffect();
					minimapButton.reclick();
					getMouse().release();
				}
			}
			player.update();
		}

		if(player.getX() < mapCenterX) {
			player.setX(player.getX() + mapTilesWidth);
		}
		if(player.getY() < mapCenterY) {
			player.setY(player.getY() + mapTilesHeight);
		}

		for (int x = 0; x < map.length; x++) {
			for (int y = 0; y < map[x].length; y++) {
				tiles[x][y].update();
			}
		}
		
		if(isSecretChestLooted) {
			effectLight = (effectLight-0.6f)/1.5f + 0.6f;
			effectBlackScreenAlpha = 255*Math.cos(timer/20d);
		}
	}


	public double mapX;
	public double mapY;

	public double sMapX;
	public double sMapY;

	
	public int dungeonTitleTime;
	
	private void drawTitle(Graphics2D fg, String title) {
		int titleH = (int) (getPanel().scalefull*25);
		fg.setFont(new Font(Font.SANS_SERIF, Font.BOLD, titleH));
		int titleW = fg.getFontMetrics().stringWidth(title);

		int moveTitle = titleH/15;

		int titleX = (getPanel().getFrameW()-titleW)/2;
		int titleY = (getPanel().getFrameH()-titleH)/2;
		fg.setColor(new Color(255,255,255, Math.min(255, dungeonTitleTime)));
		fg.drawString(title, titleX, titleY);
		
		fg.setColor(new Color(50,50,50, Math.min(255, dungeonTitleTime)));
		fg.drawString(title, titleX + moveTitle, titleY + moveTitle);

		fg.setColor(new Color(0,0,0, Math.min(255, dungeonTitleTime)/5));
		fg.fillRect(0, 0, getPanel().getFrameW(), getPanel().getFrameH());
	}
	
	private void drawSubTitle(Graphics2D fg, String title) {
		int titleH = (int) (getPanel().scalefull*25/2);
		fg.setFont(new Font(Font.DIALOG_INPUT, Font.PLAIN, titleH));
		int titleW = fg.getFontMetrics().stringWidth(title);

		int moveTitle = titleH/15;

		int titleX = (getPanel().getFrameW()-titleW)/2;
		int titleY = (getPanel().getFrameH()+titleH)/2;
		fg.setColor(new Color(225,225,225, Math.min(255, dungeonTitleTime)));
		fg.drawString(title, titleX, titleY);
		
		fg.setColor(new Color(50,50,50, Math.min(255, dungeonTitleTime)));
		fg.drawString(title, titleX + moveTitle, titleY + moveTitle);

		fg.setColor(new Color(0,0,0, Math.min(255, dungeonTitleTime)/5));
		fg.fillRect(0, 0, getPanel().getFrameW(), getPanel().getFrameH());
	}
	
	@Override
	public void draw(Graphics2D g, Graphics2D fg) {
		getPanel().clear(fg);
		
		if(isGameOver()) {
			if(dungeonTitleTime < 255) {
				dungeonTitleTime += 50;
			}
			
			drawTitle(fg, "Dungeon " + dungeonLevel);
			drawSubTitle(fg, "You lose from " + gameOver);

			gameOverMenu.show();
			gameOverNext.show();

			gameOverMenu.setSize(getFrameH()/14);
			gameOverNext.setSize(getFrameH()/14);
			
			gameOverMenu.setY(getFrameH() - getFrameH()/7);
			gameOverNext.setY(getFrameH() - getFrameH()/7);

			gameOverMenu.setX(getFrameW()/3*1);
			gameOverNext.setX(getFrameW()/3*2);
			
			gameOverMenu.draw(fg);
			gameOverNext.draw(fg);
		}else {
			if(minimapButton != null) {
				minimapButton.show();
				minimapButton.setSize(getFrameH()/14);
				minimapButton.setY(minimapButton.getHeight());
				minimapButton.setX(getFrameW() - minimapButton.getWidth());
				minimapButton.draw(fg);
			}

			if(dungeonTitleTime > 0) {
				dungeonTitleTime -= 50;
				drawTitle(fg, "Dungeon " + dungeonLevel);
			}else {
				dungeonTitleTime = 0;
			}

			if(minimap != null) {
				int size = Math.min(getFrameW(), getFrameH());
				if(isMinimpOpen) {
					minimapSize = (minimapSize-size)/2+size;
					
					minimapDark = (minimapDark-100)/2+100;
					if(minimapDark > 100) minimapDark = 100;
				}else {
					if(minimapSize > size) {
						minimapSize = size-6;
					}
					minimapSize -= (size-minimapSize/1.05);
					if(minimapSize < 0) {
						minimapSize = 0;
					}
					minimapDark -= (100-minimapDark/1.05);
					if(minimapDark < 0) minimapDark = 0;
				}
				if(minimapSize > 1) {
					fg.drawImage(minimap,
							(int) ((getFrameW()-minimapSize)/2),
							(int) ((getFrameH()-minimapSize)/2),
							(int) minimapSize,
							(int) minimapSize,
							null);
				}
			}
		}
		
		if(blackScreenAlpha+effectBlackScreenAlpha > 0) {
			fg.setColor(new Color(0,0,0, (int) Math.min(255, blackScreenAlpha+effectBlackScreenAlpha)));
			fg.fillRect(0, 0, getPanel().getFrameW(), getPanel().getFrameH());
		}
		
		if(minimapDark >= 1) {
			fg.setColor(new Color(0,0,0, (int) Math.min(255, minimapDark*2)));
			fg.fillRect(0, 0, getPanel().getFrameW(), getPanel().getFrameH());
		}
		
		mapX = (mapX - player.getX()) * 0.0 + player.getX() + sMapX;
		mapY = (mapY - player.getY()) * 0.0 + player.getY() + sMapY;
		
		g.setColor(new Color(52, 48, 26));
		g.fillRect(0, 0, getGameWidth(), getGameHeight());
		
//		g.translate(getGameWidth()/2, getGameHeight()/2);

		/*
		 * int visibleX = (int) (getPanel().getGameWidth()/getQuality()/Tile.tilesize);
		int visibleY = (int) (getPanel().getGameHeight()/getQuality()/Tile.tilesize);
		
		double scrollX = -mapX%Tile.tilesize;
		double scrollY = -mapY%Tile.tilesize;

		int tileX = (int) (mapX/Tile.tilesize);
		int tileY = (int) (mapY/Tile.tilesize);
		
		for (int x = -visibleX; x < visibleX*2; x++) {
			for (int y = -visibleY; y < visibleY*2; y++) {
				
				double px = (x*Tile.tilesize+scrollX);
				double py = (y*Tile.tilesize+scrollY);
				
				Box hitbox = tiles[(x + tileX)%width][(y + tileY)%height].getHitbox();


				g.setColor(Color.DARK_GRAY);
				g.fillRect((int)((hitbox.x+px)*getQuality()),
						(int)((hitbox.y+py)*getQuality()),
						(int)(hitbox.w*getQuality()),
						(int)(hitbox.h*getQuality())
						);
				
				g.setColor(Color.WHITE);
				g.drawRect((int)((hitbox.x+px)*getQuality()),
						(int)((hitbox.y+py)*getQuality()),
						(int)(hitbox.w*getQuality()),
						(int)(hitbox.h*getQuality())
						);
			}
		}*/


		boolean needFire = false;
		/*/ - Draw Tiles - /*/
		int tileX = (int) -(getPanel().getGameWidth()/2/getQuality()/Tile.tilesize);
		int tileY = (int) -(getPanel().getGameHeight()/2/getQuality()/Tile.tilesize);

		for (int y = (int) (-mapY%Tile.tilesize); y < getPanel().getGameHeight()/getPanel().quality+Tile.tilesize; y+=Tile.tilesize) {
			for (int x = (int) (-mapX%Tile.tilesize); x < getPanel().getGameWidth()/getPanel().quality; x+=Tile.tilesize) {
				int bx = ((int) ((mapX+x)/Tile.tilesize) + tileX)%width;
				int by = ((int) ((y+mapY)/Tile.tilesize) + tileY)%height;
//				int block = map[bx][by];

				int px = (int) ((x - player.getWidth()/2)*getPanel().quality + getQuality()*Tile.tilesize/2);
				int py = (int) ((y - player.getHeight()/2)*getPanel().quality);

				if(bx < 0) continue;
				if(by < 0) continue;
				
				Tile tile = tiles[bx][by];
				
				int type = map[bx][by];
				Box hitbox = tiles[bx][by].getHitbox();
				Box redbox = tiles[bx][by].getRedzone();
				Box ladder = tiles[bx][by].getLadder();
				Box drawbox = tiles[bx][by].getDrawBox();
				
				
				if(hitboxMode) {
					g.setColor(Color.ORANGE.darker());
					g.fillRect((int)(ladder.x*getQuality() + px),
							(int)(ladder.y*getQuality() + py),
							(int)(ladder.w*getQuality()),
							(int)(ladder.h*getQuality())
							);
					
					g.setColor(Color.ORANGE);
					g.drawRect((int)(ladder.x*getQuality() + px),
							(int)(ladder.y*getQuality() + py),
							(int)(ladder.w*getQuality()),
							(int)(ladder.h*getQuality())
							);

					
					
					g.setColor(Color.RED.darker());
					g.fillRect((int)(redbox.x*getQuality() + px),
							(int)(redbox.y*getQuality() + py),
							(int)(redbox.w*getQuality()),
							(int)(redbox.h*getQuality())
							);
					
					g.setColor(Color.RED);
					g.drawRect((int)(redbox.x*getQuality() + px),
							(int)(redbox.y*getQuality() + py),
							(int)(redbox.w*getQuality()),
							(int)(redbox.h*getQuality())
							);
					
					
					
					g.setColor(Color.DARK_GRAY);
					g.fillRect((int)(hitbox.x*getQuality() + px),
							(int)(hitbox.y*getQuality() + py),
							(int)(hitbox.w*getQuality()),
							(int)(hitbox.h*getQuality())
							);

					g.setColor(Color.LIGHT_GRAY);
					g.drawRect((int)(hitbox.x*getQuality() + px),
							(int)(hitbox.y*getQuality() + py),
							(int)(hitbox.w*getQuality()),
							(int)(hitbox.h*getQuality())
							);
					

					g.setColor(new Color(255,255,255,25));
					g.fillRect((int)(drawbox.x*getQuality() + px),
							(int)(drawbox.y*getQuality() + py),
							(int)(drawbox.w*getQuality()),
							(int)(drawbox.h*getQuality())
							);
				}else {
					if(tile.isSecretRoomBlock() && !isSecretRoomOpened()) {
						drawImage(g, drawbox, Blocks.WALL.ordinal(), px, py, 0, 0);
					}else if(tile.isFire()) {
						drawImage(g, drawbox, Blocks.SECRET_AIR.ordinal(), px, py, 0, 0);
						needFire = true;
					}else if(tile.isSand()) {
						drawImage(g, drawbox, type, px, py, 0, tile.getTrapRedzoneInt());
						drawImage(g, drawbox, Blocks.SAND_HEAD.ordinal(),
								px, py, 0, tile.getTrapRedzoneInt()-Tile.tilesize);
						drawImage(g, drawbox, Blocks.SAND_DOWN.ordinal(),
								px, py, 0, 0);
					}else if (tile.isCrusherAir()) {
						drawImage(g, drawbox, type, px, py, 0, -Tile.tilesize+tile.getTrapRedzoneInt());
						drawImage(g, drawbox, Blocks.WALL.ordinal(), px, py, 0, -Tile.tilesize);
					}else if (tile.isLadderTrapLeft()) {
						drawImage(g, drawbox, Blocks.LADDER.ordinal(), px, py, Tile.tilesize, 0);
						drawImage(g, drawbox, Blocks.LADDER_TRAP_LADDER.ordinal(), px, py, tile.getTrapRedzoneInt()/2, 0);
						drawImage(g, drawbox, Blocks.LADDER_TRAP_LEFT.ordinal(), px, py, 0, 0);
					}else if (tile.isLadderTrap()) {
					}else if (tile.isLadderTrapRight()) {
						g.drawImage(getPack().getTile(Blocks.LADDER_TRAP_LADDER.ordinal()),
							(int)((drawbox.x-tile.getTrapRedzoneInt()/2+Tile.tilesize)*getQuality() + px),
							(int)(drawbox.y*getQuality() + py),
							(int)(-drawbox.w*getQuality()),
							(int)(drawbox.h*getQuality()), null);
						drawImage(g, drawbox, Blocks.LADDER_TRAP_RIGHT.ordinal(), px, py, 0, 0);
					}else {
						drawImage(g, drawbox, type, px, py, 0, 0);
					}
//					g.drawImage(pack.getTile(type), (int)(drawbox.x*getQuality() + px),
//							(int)(drawbox.y*getQuality() + py),
//							(int)(drawbox.w*getQuality()),
//							(int)(drawbox.h*getQuality()), null);
					
//					if(tiles[bx][by].isSand()) {
//						drawImage(fg, drawbox, type, px, py, 0, 0);
//						g.drawImage(pack.getTile(Blocks.SAND_HEAD.ordinal()), (int)(drawbox.x*getQuality() + px),
//								(int)((drawbox.y
//										- Tile.tilesize - tiles[bx][by].getTrapRedzoneInt()
//										)*getQuality() + py),
//								(int)(drawbox.w*getQuality()),
//								(int)(drawbox.h*getQuality()), null);
//					}
				}
				
				

				if(hitboxMode && bx == debug$tileX && by == debug$tileY) {
					g.setColor(Color.GREEN.darker());
					g.fillRect(px,py,
							(int)(Tile.tilesize*getQuality()),
							(int)(Tile.tilesize*getQuality())
							);
					g.setColor(Color.GREEN);
					g.drawRect(px,py,
							(int)(Tile.tilesize*getQuality()),
							(int)(Tile.tilesize*getQuality())
							);
				}
			}
		}
		
		player.draw(g, fg);

		if(needFire) {
			for (int y = (int) (-mapY%Tile.tilesize); y < getPanel().getGameHeight()/getPanel().quality+Tile.tilesize; y+=Tile.tilesize) {
				for (int x = (int) (-mapX%Tile.tilesize); x < getPanel().getGameWidth()/getPanel().quality; x+=Tile.tilesize) {
					int bx = ((int) ((mapX+x)/Tile.tilesize) + tileX)%width;
					int by = ((int) ((y+mapY)/Tile.tilesize) + tileY)%height;

					int px = (int) ((x - player.getWidth()/2)*getPanel().quality + getQuality()*Tile.tilesize/2);
					int py = (int) ((y - player.getHeight()/2)*getPanel().quality);

					if(bx < 0) continue;
					if(by < 0) continue;

					Tile tile = tiles[bx][by];

					int type = map[bx][by];
					if(tile.isFire() && !isSecretChestLooted()) {
						Composite composite = g.getComposite();
						g.setComposite(LightComposite.INSTANCE);
						int size = (int) (Tile.tilesize*getQuality());
						int moveX = (int) (1*Math.cos(timer/3d) * getQuality());
						
						/*
						double lightSmooth = 20;
						g.setColor(new Color(25,15, 0));
						for (int i = 1; i < lightSmooth; i++) {
							int r = (int) (size*2*i/lightSmooth);
							//g.setPaint(getFireRadialGradient(px + size/2, (int) (py + size/1.5), Tile.tilesize*2));
							//g.fillOval(px, py+size/4, size, size);
							g.fillOval(px - r + size/2, (int) (py-r+size/1.5), (int)(r*2), (int)(r*2));
						}
						
						/*/
						
						g.setPaint(getFireRadialGradient(px + size/2, (int) (py + size/1.5), Tile.tilesize*2));
						g.fillRect(px-size*2, py-size*2, size*5, size*5);
						
						//g.setPaint(getFireRadialGradient(px + size/2, (int) (py + size/1.5), Tile.tilesize*2 + moveX));
						//g.fillRect(px-size*2 - moveX, py-size*2 - moveX, size*5 + moveX*2, size*5 + moveX*2);
						
						//*/
						
						
						Box drawbox = tile.getDrawBox();
						drawImage(g, drawbox, type, px, py, 0, 0);
						moveX = (int) (2*Math.cos(timer/7d) * getQuality());
						drawImage(g, drawbox, type, px + moveX, py, 0, 0);
						drawImage(g, drawbox, type, px - moveX, py, 0, 0);
						drawImage(g, drawbox, type, px - moveX/2, py, 0, 0);
						
						g.setComposite(composite);
					}
				}
			}
		}
		

		g.setPaint(getGradient());
		g.fillRect(0, 0, getPanel().getGameWidth(), getPanel().getGameHeight());
//		mapX += 5.0;
//		mapY += 0.5;
	}
	
	
	public RadialGradientPaint getFireRadialGradient(int x, int y, double r) {
		return new RadialGradientPaint(
				new Point(x, y),
				(float) (getQuality() * r),
				new float[] { .50f, 1f},
				new Color[] {new Color(255,150,0,0), new Color(0,0,0,0)});
	}
	
	protected RadialGradientPaint getGradient() {
		if(!(effectLight > 0)) {
			effectLight = 0.1f;
		}
		if(!(lightRadius > 0)) {
			lightRadius = 0.1f;
		}
		int newR = (int) (getPanel().getGameHeight()/2f * lightRadius * effectLight + (int)(4*getQuality()*Math.cos(timer/10d)));
		if(newR <= 0)
			newR = 1;
		int g = (int)(5 + 5*Math.cos(timer/10d));
		return new RadialGradientPaint(
				new Point(getPanel().getGameWidth()/2,getPanel().getGameHeight()/2),
				newR,
				new float[] { .2f,1f},
				new Color[] {new Color(0,0,0,0), new Color(g,g,g)});
	}
	
	
	private void drawImage(Graphics2D g, Box drawbox, int type, int px, int py, int x, int y) {
		g.drawImage(getPack().getTile(type),
				(int)((drawbox.x+x)*getQuality() + px),
				(int)((drawbox.y+y)*getQuality() + py),
				(int)(drawbox.w*getQuality()),
				(int)(drawbox.h*getQuality()),
				null);
	}
	
	boolean isReloadMiniMapKey = true;

	@Override
	public void keyPressed(KeyEvent e) {
		setKeys(e.getKeyCode(), true);

		if(isReloadMiniMapKey) {
			if(e.getKeyCode() == Data.control[Data.KEY_MAP]) {
				isMinimpOpen = !isMinimpOpen;
				isReloadMiniMapKey = false;
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		setKeys(e.getKeyCode(), false);
		if(e.getKeyCode() == Data.control[Data.KEY_MAP]) {
			isReloadMiniMapKey = true;
		}
	}
	
	private void setKeys(int key, boolean b) {
		if(key == Data.control[Data.KEY_JUMP])
			player.up(b);
		if(key == Data.control[Data.KEY_DOWN])
			player.down(b);
		if(key == Data.control[Data.KEY_RIGHT])
			player.right(b);
		if(key == Data.control[Data.KEY_LEFT])
			player.left(b);
//		if(key == Data.control[Data.KEY_LEAP])
//			player.leap(b);
	}

	int debug$tileX, debug$tileY;
	
	public Box getHitbox(int x, int y, int type) {
		int tp = x*Tile.tilesize, ty = y*Tile.tilesize;
		if(x < 0) x = width-((-x)%width);
		if(y < 0) y = height-((-y)%height);
		x = x%width;
		y = y%height;
		tiles[x][y].setPosition(tp, ty);

				
		switch (type) {
		case Tile.HITBOX_WALL:
			return tiles[x][y].getHitbox();
		case Tile.HITBOX_REDZONE:
			return tiles[x][y].getRedzone();
		case Tile.HITBOX_LADDER:
			return tiles[x][y].getLadder();
		case Tile.HITBOX_TOUCHBOX:
			return tiles[x][y].getTouchzone();
		default:
			return tiles[x][y].getHitbox();
		}
	}
	
	public Tile getTile(int x, int y) {
		if(x < 0) x = width-((-x)%width);
		if(y < 0) y = height-((-y)%height);
		x = x%width;
		y = y%height;
		return tiles[x][y];
	}

	public void destroyTrap(int x, int y) {
		if(x < 0) x = width-((-x)%width);
		if(y < 0) y = height-((-y)%height);
		x = x%width;
		y = y%height;
		if(tiles[x][y].isTrap()) {
			if(tiles[x][y].isLadderTrap()) {
				map[x][y] = Blocks.LADDER.ordinal();
				setBlock(Blocks.WALL.ordinal(), x+1, y);
				setBlock(Blocks.WALL.ordinal(), x-1, y);
			}else {
				map[x][y] = Blocks.AIR.ordinal();
			}
			tiles[x][y] = new Tile(map[x][y]);
			tiles[x][y].setPosition((x)*Tile.tilesize, (y)*Tile.tilesize);
			tiles[x][y].setGame(this);
		}
	}
	
	private void setBlock(int type, int x, int y) {
		if(x < 0) x = width-((-x)%width);
		if(y < 0) y = height-((-y)%height);
		x = x%width;
		y = y%height;
		map[x][y] = type;
		tiles[x][y] = new Tile(map[x][y]);
		tiles[x][y].setPosition((x)*Tile.tilesize, (y)*Tile.tilesize);
		tiles[x][y].setGame(this);

	}
	
	public int getMapTilesWidth() {
		return mapTilesWidth;
	}
	
	public int getMapTilesHeight() {
		return mapTilesHeight;
	}

	private double blackScreenAlpha;
	private double effectBlackScreenAlpha;
	private boolean needNextDungeon;
	
	public void nextDungeon() {
		if(!needNextDungeon) {
			blackScreenAlpha = 0;
			needNextDungeon = true;
		}
	}
	
	private String gameOver;
	
	public void gameOver(String text) {
		if(!isGameOver()) { //  !gameOver.equals(text)
			dungeonTitleTime = 0;
			gameOver = text;
			blackScreenAlpha = 0;
		}
	}
	
	public boolean isGameOver() {
		return gameOver != null;
	}
	
//	public Pack getPack() {
//		return getManager().getPack();
//	}

	private boolean isSecretRoomOpened;
	private boolean isSecretChestLooted;
	
	public void openSecretRoom() {
		isSecretRoomOpened = true;
	}
	
	public boolean isSecretRoomOpened() {
		return isSecretRoomOpened;
	}
	
	public void chestLooted() {
		isSecretChestLooted = true;
		effectBlackScreenAlpha = 255;
	}
	
	public boolean isSecretChestLooted() {
		return isSecretChestLooted;
	}
	
	public double getQsMapX() {
		return sMapX*getQuality();
	}
	
	public double getQsMapY() {
		return sMapY*getQuality();
	}
}
