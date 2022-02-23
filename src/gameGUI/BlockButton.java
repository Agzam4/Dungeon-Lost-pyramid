package gameGUI;

import java.awt.Color;
import java.awt.Graphics2D;

import work.Mouse;

public class BlockButton extends Button {

	public BlockButton() {
		isResizable = false;
	}

	protected double gray = 0;
	
	@Override
	public void update(Mouse mouse) {
		super.update(mouse);
		if(isHover) {
			gray = (gray - 150)/1.5 + 150;
		}else {
			gray /= 1.5;
		}
	}
	
	public BlockButton(String text) {
		isResizable = false;
		this.text = text;
		alignX = ALIGN_LEFT;
		alignY = ALIGN_TOP;
	}

//	protected int paddingX = 30;
//	protected int paddingY = 10;

	protected int width = 50;
	protected int height = 50;
	
	@Override
	protected void drawButton(Graphics2D g) {
		g.setColor(Color.WHITE);
		drawAt(g, getLeftX(g) + width/2 - super.getFontWidth(g)/2, y + height - super.getFontSize(g)/2);

		g.setColor(new Color((int) gray,(int) gray,(int) gray));
		g.fillRect(getLeftX(g), getTopY(g),
				getFontWidth(g), getFontSize(g));
	}
	
	@Override
	public int getFontWidth(Graphics2D g) {
		return width;//(width > 0 ? width : super.getFontWidth(g));
	}
	
	@Override
	public int getFontSize(Graphics2D g) {
		return height;//(height > 0 ? height : super.getFontSize(g));
	}
	
	public void setWidth(int width) {
		this.width = width;
	}
	
	public void setHeight(int height) {
		this.height = height;
	}
	
	@Override
	public void show() {
		if(!isVisible) gray = 0;
		super.show();
	}
}
