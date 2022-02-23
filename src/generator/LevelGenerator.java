package generator;

import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

import debug.Debug;

public class LevelGenerator {

	private long seed;
	private Random random;
	
	public int w, h;
	private int mapTraps[][];
	private int map[][];

	private static enum TrapsID {
		NONE,			// 0
		START,			// 1
		FINISH,			// 2
		TUNEL,			// 3
		TUNEL_BOUNDS,	// 4
		SPIKES,			// 5
		CRUSHER,		// 6
		LADDER_TRAP,	// 7
		SAND,			// 8
		SECRET;			// 9
	}

	public LevelGenerator() {
		Random r = new Random();
		init(r.nextLong());
	}

	public LevelGenerator(long seed) {
		init(seed);
	}

	private void init(long seed) {
		System.out.println("[LevelGenerator] Seed: " + seed);
		this.seed = seed;
		random = new Random(seed);
	}

	public void generate() {
		generate(50, 50);
	}

	public void generate(int w, int h) {
		this.w = w;
		this.h = h;
		mapTraps = new int[w][h];
		map = new int[w][h];
		tunelsCount = w*h/20;
		System.out.println("[LevelGenerator] Generating tunels...");
		generateAllTunels();
		System.out.println("[LevelGenerator] Generating traps...");
		generateAllTraps();
		System.out.println("[LevelGenerator] Fixing...");
		System.out.println("[LevelGenerator] Secret Room...");
		generateSecretRoom();
		
		if(Debug.isSecretRoomTesting) setSecretRoomAt(w/2+2, h/2 - 2, true);
		
		MinimapGenerator.generate(map);
		// TODO: fix ladders
	}
	
	private float secretRoomChance = 0.52f;
	
	private void generateSecretRoom() {
		System.out.println("[LevelGenerator] LVL " + dungeonLevel);
		if(dungeonLevel < 2) return;
		/*
		 *         1 5
		 *   ###############
		 *   # |         | #
		 * 5 #  @   ^   @  # 5
		 * 	 [  !  _#_  !  ]
		 *   ###############
		 *         1 5
		 */
		
		double randomD = Math.random();
		System.out.println("[LevelGenerator] random: " + randomD);
		if(randomD < secretRoomChance) {
			System.out.println("[LevelGenerator] S.R.");
			secretRoomPoints.clear();
			secretRoomDirs.clear();
			for (int y = 0; y < h; y++) {
				for (int x = 0; x < w; x++) {
					secretRoom(x, y);
				}
			}
			System.out.println(secretRoomPoints.size());
			if(secretRoomPoints.size() > 0) {
				int randID = new Random().nextInt(secretRoomPoints.size());
				Point randomPoint = secretRoomPoints.get(randID);
				setSecretRoomAt(randomPoint.x, randomPoint.y, secretRoomDirs.get(randID));
			}
		}
	}
	
	private void secretRoom(int x, int y) {
		if ((getTrapID(x-1, y+3) == TrapsID.TUNEL_BOUNDS.ordinal() && getBlock(x-1, y+3) == Blocks.WALL.ordinal())) { 
			for (int sy = 0; sy < 5; sy++) {
				for (int sx = 0; sx < 15; sx++) {
					if(getTrapID(sx+x, sy+y) != TrapsID.NONE.ordinal() || !isWallBlock(x+sx, y+sy)) {
						return;
					}
				}
			}
			secretRoomPoints.add(new Point(x, y));
			secretRoomDirs.add(true);
		}
		
		if ((getTrapID(x+15, y+3) == TrapsID.TUNEL_BOUNDS.ordinal() && getBlock(x+15, y+3) == Blocks.WALL.ordinal())) { 
			for (int sy = 0; sy < 5; sy++) {
				for (int sx = 0; sx < 15; sx++) {
					if(getTrapID(sx+x, sy+y) != TrapsID.NONE.ordinal() || !isWallBlock(x+sx, y+sy)) {
						return;
					}
				}
			}
			secretRoomPoints.add(new Point(x, y));
			secretRoomDirs.add(false);
		}
	}

	ArrayList<Point> secretRoomPoints = new ArrayList<Point>();
	ArrayList<Boolean> secretRoomDirs = new ArrayList<Boolean>();
	
	private void setSecretRoomAt(int x, int y, boolean k) {
		for (int sy = 0; sy < 5; sy++) {
			for (int sx = 0; sx < 15; sx++) {
				setBlock(x+sx, y+sy, Blocks.SECRET_WALL.ordinal());
				setTrapID(x+sx, y+sy, TrapsID.SECRET.ordinal());
			}
		}
		for (int sy = 1; sy < 4; sy++) {
			for (int sx = 1; sx < 14; sx++) {
				setBlock(x+sx, y+sy, Blocks.SECRET_AIR.ordinal());
			}
		}

		if(k) {
			setBlock(x-1, y+3, Blocks.SECRET_DOOR.ordinal());
			setTrapID(x-1, y+3, TrapsID.SECRET.ordinal());
			setBlock(x, y+3, Blocks.SECRET_AIR.ordinal());
			setTrapID(x, y+3, TrapsID.SECRET.ordinal());
		}else {
			setBlock(x+14, y+3, Blocks.SECRET_AIR.ordinal());
			setTrapID(x+14, y+3, TrapsID.SECRET.ordinal());
			
			setBlock(x+15, y+3, Blocks.SECRET_DOOR.ordinal());
			setTrapID(x+15, y+3, TrapsID.SECRET.ordinal());
		}

		setBlock(x+7, y+2, Blocks.SECRET_CHEST.ordinal());
		setBlock(x+7, y+3, Blocks.SECRET_CARVED_BLOCK.ordinal());
		setBlock(x+3, y+3, Blocks.SECRET_COLUMN.ordinal());
		setBlock(x+3, y+2, Blocks.SECRET_COLUMN_FIRE.ordinal());
		
		setBlock(x+11, y+3, Blocks.SECRET_COLUMN.ordinal());
		setBlock(x+11, y+2, Blocks.SECRET_COLUMN_FIRE.ordinal());
	}

	private void generateAllTraps() {
		// TODO: upper spikes, dart
		sand(0.5f);
		//			fallSand(0.25f);
		spikes(0.5f);
		crusher(0.5f);
		// Ladder Traps
		lcrusher(0.5f);
	}

	private void sand(float rare) {
		/**
		 * ###########
		 * #    _ _  #
		 *    _ | |  
		 * ##~|~|~|~##
		 * ##_|_|_|_##
		 * |- Random -|
		 */
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				if(getTrapID(x, y) == TrapsID.TUNEL.ordinal() && getBlock(x, y) == Blocks.AIR.ordinal()) { 
					int lakeW = -1;
					for (int w = 0; w < 10; w++) {
						if(!(isWallBlock(x+w, y-2) && isWallBlock(x+w, y-1)
								&& getTrapID(x+w, y) == TrapsID.TUNEL.ordinal()
								&& getBlock(x, y) == Blocks.AIR.ordinal()
								&& isWallBlock(x+w, y+2) && isWallBlock(x+w, y+1))) {
							lakeW = w-1;
							break;
						}
					}
					if(lakeW%2 != 0) lakeW--;
					if(lakeW > 5) {
						for (int w = 1; w < lakeW; w++) {
							setBlock(x+w, y-2, Blocks.AIR.ordinal());
							setBlock(x+w, y-1, Blocks.AIR.ordinal());
							if(w%2 == 0) {
								boolean b = random.nextBoolean();
								setBlock(x+w, y, (b ? Blocks.HALF_STICK_UP : Blocks.AIR).ordinal());
								setBlock(x+w, y+1, (b ? Blocks.STICK : Blocks.HALF_STICK_UP).ordinal());
							}else {
								setBlock(x+w, y, Blocks.AIR.ordinal());
								setBlock(x+w, y+1, Blocks.AIR.ordinal());
							}
							setBlock(x+w, y+2, Blocks.SAND.ordinal());
						}
						setBlock(x, y-2, Blocks.AIR.ordinal());
						setBlock(x, y-1, Blocks.AIR.ordinal());
						setBlock(x, y, Blocks.AIR.ordinal());
						setBlock(x, y+1, Blocks.SANDWALL.ordinal());
						setBlock(x, y+2, Blocks.SANDWALL.ordinal());

						setBlock(x+lakeW, y-2, Blocks.AIR.ordinal());
						setBlock(x+lakeW, y-1, Blocks.AIR.ordinal());
						setBlock(x+lakeW, y, Blocks.AIR.ordinal());
						setBlock(x+lakeW, y+1, Blocks.SANDWALL.ordinal());
						setBlock(x+lakeW, y+2, Blocks.SANDWALL.ordinal());

						for (int w = 0; w < lakeW+1; w++) {
							for (int yy = -2; yy < 3; yy++) {
								setTrapID(x+w, y+yy, TrapsID.SAND.ordinal());
							}
						}
					}
				}
			}
		}
	}

	private void lcrusher(float rare) {
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				if(getTrapID(x, y) == TrapsID.TUNEL.ordinal() && getBlock(x, y) == Blocks.LADDER.ordinal()) {
					if(isLadderBlock(x, y-1) && isLadderBlock(x, y) && isLadderBlock(x, y+1)) {
						if(isWallBlock(x-1, y-1) && isWallBlock(x-1, y) && isWallBlock(x-1, y+1)) {
							if(isWallBlock(x+1, y-1) && isWallBlock(x+1, y) && isWallBlock(x+1, y+1)) {
								setBlock(x-1, y, Blocks.LADDER_TRAP_LEFT.ordinal());
								setBlock(x, y, Blocks.LADDER_TRAP_LADDER.ordinal());
								setBlock(x+1, y, Blocks.LADDER_TRAP_RIGHT.ordinal());
								for (int yy = -1; yy < 2; yy++) {
									for (int xx = -1; xx < 2; xx++) {
										setTrapID(x+xx, y+yy, TrapsID.LADDER_TRAP.ordinal());
									}
								}
							}
						}
					}
				}
			}
		}
	}

	private void spikes(float rare) {
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				if(getTrapID(x, y) == TrapsID.TUNEL.ordinal()) {
					if(random.nextFloat() < rare) {
						if(isTunelBlock(x-1, y) && isTunelBlock(x, y) && isTunelBlock(x+1, y)) {
							if(isWallBlock(x-1, y+1) && isWallBlock(x, y+1) && isWallBlock(x+1, y+1)) {
								if(isWallBlock(x-1, y-1) && isWallBlock(x, y-1) && isWallBlock(x+1, y-1)) {
									if(isWallBlock(x-1, y-2) && isWallBlock(x, y-2) && isWallBlock(x+1, y-2)) {
										if(isWallBlock(x, y+2)) {
											setBlock(x-1, y-1, Blocks.AIR.ordinal());
											setBlock(x, y-1, Blocks.AIR.ordinal());
											setBlock(x+1, y-1, Blocks.AIR.ordinal());
											setBlock(x, y+1, Blocks.SPIKES.ordinal());
											for (int yy = -2; yy < 3; yy++) {
												for (int xx = -1; xx < 2; xx++) {
													setTrapID(x+xx, y+yy, TrapsID.SPIKES.ordinal());
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}

	private void crusher(float rare) {
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				if(getTrapID(x, y) == TrapsID.TUNEL.ordinal()) {
					if(random.nextFloat() < rare) {
						if(isTunelBlock(x-1, y) && isTunelBlock(x, y) && isTunelBlock(x+1, y)) {
							if(isWallBlock(x-1, y-1) && isWallBlock(x, y-1) && isWallBlock(x+1, y-1)) {
								if(isWallBlock(x-1, y+1) && isWallBlock(x, y+1) && isWallBlock(x+1, y+1)) {
									if(isWallBlock(x, y-2)) {
										setBlock(x, y-1, Blocks.CRUSHER.ordinal());
										setBlock(x, y, Blocks.CRUSHER_AIR.ordinal());
										setBlock(x, y+1, Blocks.CRUSHER_DOWN.ordinal());
										for (int yy = -2; yy < 3; yy++) {
											for (int xx = -1; xx < 2; xx++) {
												setTrapID(x+xx, y+yy, TrapsID.CRUSHER.ordinal());
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}

//	private boolean isNoneID(int x, int y) {
//		return getTrapID(x, y) == TrapsID.NONE.ordinal();
//	}

	private boolean isLadderBlock(int x, int y) {
		return getBlock(x, y) == Blocks.LADDER.ordinal() && getTrapID(x, y) == TrapsID.TUNEL.ordinal();
	}

	private boolean isTunelBlock(int x, int y) {
		return getBlock(x, y) == Blocks.AIR.ordinal() && getTrapID(x, y) == TrapsID.TUNEL.ordinal();
	}
	private boolean isWallBlock(int x, int y) {
		return getBlock(x, y) == Blocks.WALL.ordinal() 
				&& (getTrapID(x, y) == TrapsID.TUNEL_BOUNDS.ordinal() || getTrapID(x, y) == TrapsID.NONE.ordinal());
	}

	public int getBlock(int x, int y) {
		x = w-(-x%w);
		y = h-(-y%h);
		x = x%w;
		y = y%h;
		return map[x][y];
	}

	public void setBlock(int x, int y, int type) {
		x = w-(-x%w);
		y = h-(-y%h);
		x = x%w;
		y = y%h;
		map[x][y] = type;
	}

	public void setTrapID(int x, int y, int type) {
		x = w-(-x%w);
		y = h-(-y%h);
		x = x%w;
		y = y%h;
		mapTraps[x][y] = type;
	}

	public int getTrapID(int x, int y) {
		x = w-(-x%w);
		y = h-(-y%h);
		x = x%w;
		y = y%h;
		return mapTraps[x][y];
	}
	
	int finishX, finishY;

	private void generateAllTunels() {
		int sx = w/2;
		int sy = h/2;

		double randomDir = Math.toRadians(random.nextInt(360));
		int distanceToFinish = Math.min(w, h)/2;
		finishX = (int) (distanceToFinish*Math.cos(randomDir));
		finishY = (int) (distanceToFinish*Math.sin(randomDir));
		
		// Finish
		for (int yy = -3; yy < 4; yy++) {
			for (int xx = -3; xx < 4; xx++) {
				setTrapID(finishX+xx, finishY+yy, TrapsID.FINISH.ordinal());
			}
		}
				
		generateTunels(sx, sy);

		// Start
		for (int yy = -3; yy < 4; yy++) {
			for (int xx = -3; xx < 4; xx++) {
				setTrapID(sx+xx, sy+yy, TrapsID.START.ordinal());
			}
		}
		for (int yy = -1; yy < 2; yy++) {
			for (int xx = -1; xx < 2; xx++) {
				setBlock(sx+xx, sy+yy, Blocks.AIR.ordinal());
			}
		}
		setBlock(sx, sy, Blocks.START_WALL.ordinal());

		// Tunnel to Finish

		int x = finishX, y = finishY;
		do {
			if(y == sy) {
				setBlock(x, y, Blocks.AIR.ordinal());
				if(getBlock(x, y+1) != Blocks.WALL.ordinal()) {
					setBlock(x, y, Blocks.LADDER.ordinal());
				}
			}else {
				setBlock(x, y, Blocks.LADDER.ordinal());
			}
			setTrapID(x+1, y, TrapsID.TUNEL_BOUNDS.ordinal());
			setTrapID(x-1, y, TrapsID.TUNEL_BOUNDS.ordinal());
			setTrapID(x, y+1, TrapsID.TUNEL_BOUNDS.ordinal());
			setTrapID(x, y-1, TrapsID.TUNEL_BOUNDS.ordinal());
			setTrapID(x, y, TrapsID.TUNEL.ordinal());
			
			if(y < sy) {
				y++;
			}else if(y > sy){
				y--;
			}else {
				if(x > sx) {
					x--;
				}else {
					x++;
				}
			}
		} while (getBlock(x, y) != Blocks.AIR.ordinal() && getBlock(x, y) != Blocks.LADDER.ordinal());

		for (int yy = -3; yy < 4; yy++) {
			for (int xx = -3; xx < 4; xx++) {
				setTrapID(finishX+xx, finishY+yy, TrapsID.FINISH.ordinal());
			}
		}
		
		for (int yy = -1; yy < 2; yy++) {
			for (int xx = -1; xx < 2; xx++) {
				setBlock(finishX+xx, finishY+yy, Blocks.LADDER.ordinal());
			}
		}
		setBlock(finishX, finishY, Blocks.START_WALL.ordinal());
		setBlock(finishX, finishY-1, Blocks.FINISH.ordinal());
	}

	ArrayList<Point> nextStep = new ArrayList<Point>();

	private static final int UP  	= 0;
	private static final int DOWN 	= 1;
	private static final int LEFT 	= 2;
	private static final int RIGHT 	= 3;

	private void generateTunels(int x, int y) {
		ArrayList<Integer> dirs = new ArrayList<Integer>();
		if(isFree(x+1, y))
			dirs.add(RIGHT);
		if(isFree(x-1, y))
			dirs.add(LEFT);
		if(isFree(x, y+1))
			dirs.add(DOWN);
		if(isFree(x, y-1))
			dirs.add(UP);

		if(dirs.size() < 1) {
			return;
		}

		int newTunelsCount = 1+random.nextInt(dirs.size());

		for (int i = 0; i < newTunelsCount; i++) {
			int randomIndex = random.nextInt(dirs.size());
			generateTunel(x, y, dirs.get(randomIndex));
			dirs.remove(randomIndex);
		}
	}

	int tunelsCount = 150;

	private void generateTunel(int x, int y, int dir) {
		if(tunelsCount < 1) return;
		tunelsCount--;
		int tunelSize = 3 + random.nextInt((dir == RIGHT || dir == LEFT) ? 7 : 2);
		switch (dir) {
		case RIGHT:
			for (int i = 0; i < tunelSize; i++) {
				if(!isFree(x+1, y)) {
					if(i == 0) tunelsCount++;
					break;
				}
				setBlock(x, y, Blocks.AIR.ordinal());
				setTrapID(x, y, TrapsID.TUNEL.ordinal());
				if(isFree(x, y+1))
					setTrapID(x, y+1, TrapsID.TUNEL_BOUNDS.ordinal());
				if(isFree(x, y-1))
					setTrapID(x, y-1, TrapsID.TUNEL_BOUNDS.ordinal());
				x++;
			}
			break;
		case LEFT:
			for (int i = 0; i < tunelSize; i++) {
				if(!isFree(x-1, y)) {
					if(i == 0) tunelsCount++;
					break;
				}
				setBlock(x, y, Blocks.AIR.ordinal());
				setTrapID(x, y, TrapsID.TUNEL.ordinal());
				if(isFree(x, y+1))
					setTrapID(x, y+1, TrapsID.TUNEL_BOUNDS.ordinal());
				if(isFree(x, y-1))
					setTrapID(x, y-1, TrapsID.TUNEL_BOUNDS.ordinal());
				x--;
			}
			break;
		case DOWN:
			for (int i = 0; i < tunelSize; i++) {
				if(!isFree(x, y+1))  {
					if(i == 0) tunelsCount++;
					break;
				}
				setBlock(x, y, Blocks.LADDER.ordinal());
				setTrapID(x, y, TrapsID.TUNEL.ordinal());
				if(isFree(x+1, y))
					setTrapID(x+1, y, TrapsID.TUNEL_BOUNDS.ordinal());
				if(isFree(x-1, y))
					setTrapID(x-1, y, TrapsID.TUNEL_BOUNDS.ordinal());
				y++;
			}
			break;
		case UP:
			for (int i = 0; i < tunelSize; i++) {
				if(!isFree(x, y-1))  {
					if(i == 0) tunelsCount++;
					break;
				}
				setBlock(x, y, Blocks.LADDER.ordinal());
				setTrapID(x, y, TrapsID.TUNEL.ordinal());
				if(isFree(x+1, y))
					setTrapID(x+1, y, TrapsID.TUNEL_BOUNDS.ordinal());
				if(isFree(x-1, y))
					setTrapID(x-1, y, TrapsID.TUNEL_BOUNDS.ordinal());
				y--;
			}
			break;
		default:
			break;
		}

		try {
			generateTunels(x, y);

		} catch (StackOverflowError e) {
			// TODO: handle exception
		}
	}

	private boolean isFree(int x, int y) {
		return getBlock(x, y) == Blocks.WALL.ordinal() && getTrapID(x, y) == TrapsID.NONE.ordinal();
	}

	public void print() {
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				char c = '?';
				switch (map[x][y]) {
				case 0:
					c = '#';
					break;
				case 1:
					c = '#';
					break;
				case 2:
					c = ' ';
					break;
				case 3:
					c = '=';
					break;
				case 4:
					c = 'S';
					break;
				case 5:
					c = 'C';
					break;
				case 6:
					c = '%';
					break;
				case 7:
					c = ':';
					break;
				case 8:
					c = '<';
					break;
				case 9:
					c = '>';
					break;
				case 10:
					c = '-';
					break;
				case 11:
					c = '$';
					break;
				case 12:
					c = '~';
					break;
				case 13:
					c = '|';
					break;
				case 14:
					c = '_';
					break;
				case 15:
					c = '@';
					break;
				default:
					break;
				}
				System.out.print(c);
				System.out.print(c);
			}
			System.out.println();
		}
	}

	public BufferedImage draw() {
		BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				Color c = Color.CYAN;
				if(map[x][y] == Blocks.AIR.ordinal()) c = 				Color.WHITE;
				if(map[x][y] == Blocks.WALL.ordinal()) c = 				Color.BLACK;
				if(map[x][y] == Blocks.START_WALL.ordinal()) c = 		Color.CYAN;
				if(map[x][y] == Blocks.LADDER.ordinal()) c = 			Color.ORANGE;
				if(map[x][y] == Blocks.SPIKES.ordinal()) c = 			Color.BLACK;
				
				if(map[x][y] == Blocks.CRUSHER.ordinal()) c = 			Color.ORANGE;
				if(map[x][y] == Blocks.CRUSHER_AIR.ordinal()) c = 		Color.WHITE;
				if(map[x][y] == Blocks.CRUSHER_DOWN.ordinal()) c = 		Color.DARK_GRAY;

				if(map[x][y] == Blocks.HALF_STICK_UP.ordinal()) c = 	Color.BLACK;
				if(map[x][y] == Blocks.STICK.ordinal()) c = 			Color.BLACK;
				
				if(map[x][y] == Blocks.SAND.ordinal()) c = 				Color.RED;
				if(map[x][y] == Blocks.SANDWALL.ordinal()) c = 			Color.BLACK;

				if(map[x][y] == Blocks.LADDER_TRAP_LEFT.ordinal()) c = 	Color.YELLOW;
				if(map[x][y] == Blocks.LADDER_TRAP_LADDER.ordinal()) c =Color.WHITE;
				if(map[x][y] == Blocks.LADDER_TRAP_RIGHT.ordinal()) c = Color.YELLOW;

				if(map[x][y] == Blocks.CRACKED_WALL.ordinal()) c = Color.MAGENTA.brighter();
				

				if(map[x][y] == Blocks.SECRET_CHEST.ordinal()) c = Color.BLUE.brighter();
				if(map[x][y] == Blocks.SECRET_AIR.ordinal()) c = Color.MAGENTA.darker().darker();
				
//				else c = new Color(0, 255*map[x][y]/Blocks.values().length, 0);
				image.setRGB(x, y, c.getRGB());
			}
		}
		return image;
	}

	public BufferedImage drawID() {
		BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				Color c = Color.CYAN;
				switch (mapTraps[x][y]) {
				case 0:
					c = Color.BLACK;
					break;
				case 1:
					c = new Color(0, (int) (125 + 125*Math.cos(x/2d)), (int) (125 + 125*Math.cos(y/2d)));
					break;
				case 3:
					c = Color.WHITE;
					break;
				case 4:
					c = Color.GRAY;
					break;
				case 5:
					c = Color.RED;
					break;
				case 6:
					c = Color.GREEN;
					break;
				case 7:
					c = Color.YELLOW;
					break;
				case 8:
					c = new Color(255, 180, 0);
					break;
				default:
					break;
				}
				image.setRGB(x, y, c.getRGB());
			}
		}
		return image;
	}
	
	public int[][] getMap() {
		return map;
	}

	public long getSeed() {
		return seed;
	}

	int dungeonLevel;
	public void setLevel(int dungeonLevel) {
		this.dungeonLevel = dungeonLevel;
	}
}
