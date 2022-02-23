package stages;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.io.IOException;

import javax.imageio.ImageIO;

import gui.JDungeonPanel;
import progress.ProgressData;
import work.Mouse;
import work.Pack;

public class Manager {

	Stage stage;
	Mouse mouse;
	JDungeonPanel panel;
	ProgressData data;
	
	Pack pack;
	
	public Manager(JDungeonPanel panel) {
		this.panel = panel;
		stage = new SMenu();
		stage.setManager(this);
		
		pack = new Pack();
		data = new ProgressData();
		try {
			pack.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setStage(Stage stage) {
		this.stage = stage;
		this.stage.setManager(this);
	}
	
	public void back() {
		Stage last = stage.getLast();
		if(last != null) {
			stage = last;
		}
	}
	
	public void update() {
		stage.update();
	}
	
	public void draw(Graphics2D g, Graphics2D fg) {
		stage.draw(g, fg);
	}
	
	public JDungeonPanel getPanel() {
		return panel;
	}

	public void keyPressed(KeyEvent e) {
		stage.keyPressed(e);
	}

	public void keyReleased(KeyEvent e) {
		stage.keyReleased(e);
	}
	
	public Pack getPack() {
		return pack;
	}
	
	public ProgressData getData() {
		return data;
	}
	
	public void setMouse(Mouse mouse) {
		this.mouse = mouse;
	}
	
	public Mouse getMouse() {
		return mouse;
	}
	
	public double getScale() {
		return panel.getScale();
	}

	public void gameExit() {
		// TODO
		System.exit(0);
	}
}
