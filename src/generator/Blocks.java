package generator;

public enum Blocks {

	WALL, // Must be first
	CRACKED_WALL,
	AIR,
	START_WALL,
	LADDER,
	SPIKES,
	
	CRUSHER,
	CRUSHER_AIR,
	CRUSHER_DOWN,
	
	HALF_STICK_UP,
	STICK,

//	MOSSY_STONE,
//	DART,
//	PLATE,
//	PLATE_ACTIVATE,
//	STONE_STICK,
//	STONE_HALF,
//	STONE_DOOR,
//	SPIKY_ONE,
	
	SAND,
	SANDWALL,
	
	LADDER_TRAP_LEFT,
	LADDER_TRAP_LADDER,
	LADDER_TRAP_RIGHT,

	SAND_HEAD,
	SAND_DOWN,
	
	FINISH,

	SECRET_DOOR,
	SECRET_WALL,
	SECRET_AIR,
	SECRET_CHEST,
	SECRET_COLUMN,
	SECRET_CARVED_BLOCK,
	SECRET_COLUMN_FIRE,
	
	COLUMN,
	COLUMN_FIRE,
	;

	String name;

	private Blocks() {
		char[] cs = name().replaceFirst("_", " ").toLowerCase().toCharArray();
		cs[0] = Character.toUpperCase(cs[0]);
		this.name = new String(cs);
	}

	@Override
	public String toString() {
		return " " + name + " ";
	}

	public int getID() {
		return ordinal();
	}
}

/*
	 BLOCK_AIR = 1;
	 BLOCK_STONE = 2;
	 BLOCK_MOSSY_STONE = 3;

	 BLOCK_DART = 4;
	 BLOCK_PLATE = 5;
	 BLOCK_PLATE_ACTIVATE = 6;
	 BLOCK_SPIKY = 7;
	 BLOCK_SPIKY_OFF = 8;
	 BLOCK_LADDER = 9;
	 BLOCK_STONE_STICK = 10;
	 BLOCK_STONE_HALF = 11;
	 BLOCK_STONE_DOOR = 12;
	 BLOCK_SPIKY_ONE = 13;
	 BLOCK_NULL = 14;
	 BLOCK_SOLID = 15;
	 BLOCK_CHEST = 16;

	 BLOCK_TNT= 17;
	 BLOCK_SPIKY_UP = 18;
	 BLOCK_DOOR_OPEN_DOWN = 19;
	 BLOCK_DOOR_OPEN_UP = 20;
	 BLOCK_GOLD = 21;
	 BLOCK_DIAMOND = 22;
	 BLOCK_BOOM = 23;
	 BLOCK_NULL2 = 24;
	 BLOCK_NULL3 = 25;
	 BLOCK_SPIKY_FALL = 26;
	 BLOCK_IRON = 27;
	 BLOCK_STICK = 28;
	 BLOCK_STICK_BLOCK = 29;
	 BLOCK_LAVA = 30;
	 BLOCK_MAGMA = 31;
	 BLOCK_MAGMA_HALF = 32;
	 BLOCK_WATER = 33;
	 BLOCK_SAW = 34;
	 BLOCK_SAW_LINE = 35;
 */
