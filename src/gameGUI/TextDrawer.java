package gameGUI;

import java.awt.Color;
import java.awt.Graphics2D;

public class TextDrawer {

	public static void draw(Graphics2D g, String text, int x, int y, int border) {
		g.setColor(Color.WHITE);
		g.drawString(text, x, y);
		g.setColor(Color.BLACK);
		for (int angle = 0; angle < 360; angle+=36) {
			double a = Math.toRadians(angle);
			g.drawString(text, (int) (x + Math.cos(a)*border), (int) (y + Math.sin(a)*border));
		}
	}
	
	public static void drawCenter(Graphics2D g, String text, int x, int y, int border) {
		draw(g, text, x - g.getFontMetrics().stringWidth(text)/2, y, border);
	}
}
