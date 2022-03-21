package game;

import java.awt.Graphics2D;

import gui.JDungeonPanel;
import stages.Manager;
import stages.SGame;

public abstract class GameObject {

	public double x, y;
	
	public abstract void update();
	public abstract void draw(Graphics2D g, Graphics2D fg);
	
	SGame game;
	
	public void setGame(SGame game) {
		this.game = game;
	}
	
	public SGame getGame() {
		return game;
	}
	
	Manager manager;
	
	public void setManager(Manager manager) {
		this.manager = manager;
	}
	
	public Manager getManager() {
		return manager;
	}
	
	public JDungeonPanel getPanel() {
		return manager.getPanel();
	}

	
	public void setPosition(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public void setX(double x) {
		this.x = x;
	}
	
	public void setY(double y) {
		this.y = y;
	}
	
	public double getX() {
		return x;
	}
	
	public double getY() {
		return y;
	}
}
