package work;

import java.awt.Panel;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.lang.annotation.Retention;

import javax.swing.JPanel;

import gui.JDungeonPanel;

public class Mouse {

	private JDungeonPanel panel;
	private boolean isMousePressed;
	
	private int mouseX, mouseY;
	
	public Mouse(JDungeonPanel panel) {
		this.panel = panel;
		panel.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
				isMousePressed = false;
				resetMouseDragg();
				reClick();
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
				isMousePressed = true;
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				isMousePressed = false;
				reClick();
				mouseX = -1;
				mouseY = -1;
//				resetMouseDragg();
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				isMouseOneClick = true;
			}
		});
		
		panel.addMouseMotionListener(new MouseMotionListener() {
			
			@Override
			public void mouseMoved(MouseEvent e) {
				mouseX = e.getX();
				mouseY = e.getY();
				mouseDraggStartX = e.getX();
				mouseDraggStartY = e.getY();
				resetMouseDragg();
			}
			
			@Override
			public void mouseDragged(MouseEvent e) {
				mouseX = e.getX();
				mouseY = e.getY();

				mouseDraggMoveX = mouseX - mouseDraggStartX;
				mouseDraggMoveY = mouseY - mouseDraggStartY;
			}
		});
	}

	private int mouseDraggStartX, mouseDraggStartY;
	private int mouseDraggMoveX, mouseDraggMoveY;
	
	public int getMouseDraggMoveX() {
		return mouseDraggMoveX;
	}
	
	public int getMouseDraggMoveY() {
		return mouseDraggMoveY;
	}
	
	public int getFrameMouseX() {
		return mouseX;
	}
	
	public int getFrameMouseY() {
		return mouseY;
	}
	
//	public int getMouseX() {
//		return (int) (mouseX/getSQ());
//	}
//	
//	public int getMouseY() {
//		return (int) (mouseY/getSQ());
//	}

	public int getGameMouseX() {
		return (int) (((mouseX-panel.getFrameW()/2)/panel.getScale()+panel.getGameWidth()/2));
	}
	
	public int getGameMouseY() {
		return (int) (((mouseY-panel.getFrameH()/2)/panel.getScale()+panel.getGameHeight()/2));
	}
	
	public boolean isMousePressed() {
		return isMousePressed;
	}
	
	public boolean isMouseClicked() {
		return isMousePressed && mouseDraggMoveX*mouseDraggMoveY == 0;
	}
	
	boolean isMouseOneClick;
	
	public boolean isMouseOneClick() {
		if(isMouseOneClick) {
			reClick();
			return true;
		}
		return isMouseOneClick;
	}

	public void reClick() {
		isMouseOneClick = false;
	}
	
	public void release() {
		isMousePressed = false;
	}
	
	public void resetMouseDragg() {
		mouseDraggStartX = mouseX;
		mouseDraggStartY = mouseY;
		mouseDraggMoveX = 0;
		mouseDraggMoveY = 0;
	}
}
