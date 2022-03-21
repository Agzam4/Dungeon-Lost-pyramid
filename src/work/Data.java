package work;

import java.awt.event.KeyEvent;

public class Data {

	public static final int KEY_PAUSE = 0;
	public static final int KEY_JUMP = 1;
	public static final int KEY_LEFT = 2;
	public static final int KEY_DOWN = 3;
	public static final int KEY_RIGHT = 4;
	
	public static final int KEY_SLOWTIME = 5;

	public static final int KEY_ESC = 6;
	public static final int KEY_MAP = 7;
	
	public static int[] control = {
			KeyEvent.VK_ESCAPE,
			KeyEvent.VK_UP,
			KeyEvent.VK_LEFT,
			KeyEvent.VK_DOWN,
			KeyEvent.VK_RIGHT,
			KeyEvent.VK_SPACE,
			KeyEvent.VK_ESCAPE,
			KeyEvent.VK_M
	};
}
