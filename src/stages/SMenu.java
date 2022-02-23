package stages;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RadialGradientPaint;
import java.awt.event.KeyEvent;

import gameGUI.BlockButton;
import gameGUI.Button;
import gameGUI.TextDrawer;

public class SMenu extends Stage {

	private Button[] buttons;
	
	private String[] buttonsNames = {
			"Play",
			"Player",
			"Settings",
			"Exit"
	};
	
	private static final int CLICK_ACTION_PLAY 		= 0;
	private static final int CLICK_ACTION_PLAYER 	= 1;
	private static final int CLICK_ACTION_SETTINGS 	= 2;
	private static final int CLICK_ACTION_EXIT 		= 3;
	
	public SMenu() {
		buttons = new Button[buttonsNames.length];
		for (int i = 0; i < buttons.length; i++) {
			buttons[i] = new Button(buttonsNames[i]);
		}
	}
	
	@Override
	public void update() {
		for (int i = 0; i < buttons.length; i++) {
			buttons[i].update(getMouse());
			if(buttons[i].isClicked()) {
				if(i == CLICK_ACTION_PLAY) {
					getManager().setStage(new SGame());
				}
				if(i == CLICK_ACTION_PLAYER) {
					getManager().setStage(new SAbilitiesTree());
				}
				if(i == CLICK_ACTION_SETTINGS) {
					// TODO: settings
				}
				if(i == CLICK_ACTION_EXIT) {
					getManager().gameExit();
				}
			}
		}
	}

	float padding = 0.1F;
	float buttonSpace = 0.75F;

	@Override
	public void draw(Graphics2D g, Graphics2D fg) {
		int fh = getPanel().getFrameH();
		int pad = (int) (fh*padding);
		int panelH = fh-pad*2;

		int buttonH = (int) (panelH*buttonSpace);
		int titleH = (int) (panelH-buttonH);

		g.setColor(new Color(52, 48, 26));
		g.fillRect(0, 0, getGameWidth(), getGameHeight());
		
		drawTitle(fg, titleH, pad);
		
		int step = buttonH/buttons.length;
		for (int i = 0; i < buttons.length; i++) {
			int y =  pad + titleH + (int) (step*(i+0.5));
			buttons[i].setSize(step/2);
			buttons[i].setX(getPanel().getFrameW()/2);
			buttons[i].setY(y);
			buttons[i].draw(fg);
		}
		
		fg.setPaint(getGradient());
		fg.fillRect(0, 0, getPanel().getFrameW(), getPanel().getFrameH());
	}
	
	private void drawTitle(Graphics2D fg, int titleH, int pad) {
		int titlePad = (int) (titleH*padding);
		int titleSize = titleH - titlePad*2;
		int y = pad - titlePad + titleSize;
		fg.setColor(Color.WHITE);
		fg.setFont(new Font(Font.SANS_SERIF, Font.BOLD, titleSize));
		TextDrawer.drawCenter(fg, "Dungeon II", getPanel().getFrameW()/2, y, titleSize/20);
	}

	@Override
	public void keyPressed(KeyEvent e) {
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}
	
	
	protected RadialGradientPaint getGradient() {
		int newR = (int) (getPanel().getGameHeight()/2f * 1.5);
		if(newR <= 0)
			newR = 1;
		return new RadialGradientPaint(
				new Point(getPanel().getFrameW()/2 , getPanel().getFrameH()/2),
				newR,
				new float[] { .2f,1f},
				new Color[] {new Color(0,0,0,0), new Color(0,0,0)});
	}
}
