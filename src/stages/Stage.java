package stages;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;

import gui.JDungeonPanel;
import progress.ProgressData;
import work.Mouse;
import work.Pack;

public abstract class Stage {

	private Manager manager;
	private Stage last;
	
	public void setLast(Stage last) {
		this.last = last;
	}
	
	public void setManager(Manager manager) {
		this.manager = manager;
	}
	
	public Stage getLast() {
		return last;
	}
	
	public Manager getManager() {
		return manager;
	}

	public JDungeonPanel getPanel() {
		return getManager().getPanel();
	}

	public int getFrameW() {
		return getPanel().getFrameW();
	}
	
	public int getFrameH() {
		return getPanel().getFrameH();
	}

	public int getGameWidth() {
		return getPanel().getGameWidth();
	}
	
	public int getGameHeight() {
		return getPanel().getGameHeight();
	}
	
	public double getQuality() {
		return getPanel().quality;
	}
	
	public double getScale() {
		return getPanel().getScale();
	}
	
	public Pack getPack() {
		return manager.getPack();
	}
	
	public ProgressData getData() {
		return manager.getData();
	}
	
	public Mouse getMouse() {
		return manager.getMouse();
	}
	
	
	public abstract void update();
	public abstract void draw(Graphics2D g, Graphics2D fg);

	public abstract void keyPressed(KeyEvent e);
	public abstract void keyReleased(KeyEvent e);
}
