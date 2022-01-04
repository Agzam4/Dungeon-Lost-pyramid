package stages;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;

import gui.JDungeonPanel;

public class Manager {

	Stage stage;
	JDungeonPanel panel;
	
	public Manager(JDungeonPanel panel) {
		this.panel = panel;
		stage = new SGame();
		stage.setManager(this);
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
}
