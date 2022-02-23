package stages;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import game.SecretItem;
import gameGUI.BlockButton;
import gameGUI.Button;
import work.Mouse;
import work.Pack;

public class SAbilitiesTree extends Stage {

	int camX, camY;
	int icoSize = 1;
	
	private BlockButton back 	= new BlockButton("Back");

	private BlockButton uSelect = new BlockButton("Select");
	private BlockButton uUpdate = new BlockButton("Update");
	private BlockButton uBack 	= new BlockButton("Back");
	
	@Override
	public void update() {
		imgSize = getFrameH()/75;
		distanceSizeX = imgSize*5;//getPack().getItemSize()*2+2;
		distanceSizeY = imgSize*5;//getPack().getItemSize()*2+14;
		
		icoSize = getPack().getItemFrame(0).getHeight();
		treeWidth = getFrameH();//(int) (getPack().getCount(Pack.GOLD_ITEMS)*imgSize)*2;
		treeHeight = getFrameH();
		
		camX += getMouse().getMouseDraggMoveX()*getQuality()/(getScale()*getQuality());
		camY += getMouse().getMouseDraggMoveY()*getQuality()/(getScale()*getQuality());
		getMouse().resetMouseDragg();

		if(camX > treeWidth) camX = treeWidth;
		if(-camX > treeWidth) camX = -treeWidth;
		
		if(camY > treeHeight) camY = treeHeight;
		if(-camY > treeHeight) camY = -treeHeight;

		back.setX(imgSize);
		back.setY(getFrameH()-imgSize*6);
		
		back.setWidth(imgSize*15);
		back.setHeight(imgSize*5);
		back.setSize(imgSize*3);
		
		back.update(getMouse());
		
		uSelect.update(getMouse());
		uUpdate.update(getMouse());
		uBack.update(getMouse());

		if(back.isClicked()) {
			getManager().setStage(new SMenu());
		}
		
		if(uSelect.isClicked()) {
			getData().setSelectedItem(selectedID, selectedITEM_ID);
			uSelect.reclick();
			getMouse().release();
		}
		if(uUpdate.isClicked()) {
			getData().updateItem(2-selectedID, selectedITEM_ID);
			uUpdate.reclick();
			getMouse().release();
		}
		if(uBack.isClicked()) {
			selectedID = -1;
			selectedITEM_ID = -1;
			uBack.reclick();
			getMouse().release();
		}
	}

	@Override
	public void draw(Graphics2D g, Graphics2D fg) {
		
//		fg.setColor(Color.BLACK);
//		fg.fillRect(0,0,getFrameW(), getFrameH());

//		int q = (int) (getQuality() * 5);
//		for (int y = -2; y < getFrameH()/q + 2; y++) {
//			for (int x = -2; x < getFrameW()/q + 2; x++) {
//				if((x+y) % 2 == 0)
//					fg.setColor(new Color(25, 34, 51));
//				else
//					fg.setColor(new Color(44, 60, 90));
//				fg.fillRect(x*q + (camX/2)%(2*q), y*q + (camY/2)%(2*q), q, q);
//			}
//		}
		
		// Draw Abilities Tree

		fg.setFont(new Font(Font.SANS_SERIF, Font.BOLD, (int) (getPanel().getScalefull()*10)));
		
		back.draw(fg);
		
		
		coinsY = 1;
		for (int i = 2; i > -1; i--) {
			drawCoins(fg, i, getData().getCoins(2-i));
		}
		
		if(selectedID == -1) {
			uSelect.hide();
			uUpdate.hide();
			uBack.hide();
			drawSecretItem(fg, getData().getSecretItem(), 0, 0, 0, 0, 0);
		}else {
			uSelect.show();
			uUpdate.show();
			uBack.show();
			drawUpdateAbilities(fg, selectedID,
					selectedITEM_ID,
					0,
					0);
		}
		//*/
		
		int count = 26;
		int q = getFrameH()/count;
		for (int y = -2; y < getFrameH()/q + 4; y++) {
			for (int x = -2; x < getFrameW()/q + 4; x++) {
				if((x+y) % 2 == 0)
					fg.setColor(new Color(25, 34, 51));
				else
					fg.setColor(new Color(44, 60, 90));
				fg.fillRect(x*q + (camX/2)%(2*q), y*q + (camY/2)%(2*q), q, q);
			}
		}
		
	}
	
	private void drawUpdateAbilities(Graphics2D g, int id, int itemID, int x, int y) {
		BufferedImage ico = getPack().getItemFrame(id);

		int drawX = x+camX + (getFrameW())/2; //- (ico.getWidth()*imgSize - getFrameW())/2 + getIQuality();
		int drawY = y+camY + (getFrameH())/2; //- (ico.getHeight()*imgSize - getFrameH())/2 + getIQuality();

		
		int progressW = 50*imgSize - 3*getIQuality();
		
		long need = getData().getNeedItemCount(2-id, itemID);
		long items = getData().getItemCount(2-id, itemID);
		int size = imgSize;
		
		int allDrawX = x + ico.getWidth()*imgSize/2 + size*3;

		if(getData().getSelectedID() == id && getData().getSelectedITEM_ID() == itemID) {
			drawImageAt(g, getPack().getFrame(id), x, y);
		}
		
//		if(checkMouse(drawX, drawY, (ico.getWidth()-2)*imgSize)) {
//			fillIcoRect(g, drawX, drawY, ico);
//			if(getMouse().isMouseClicked()) {
//				getMouse().reClick();
//				g.setColor(new Color(255,255,255,150));
//				selectedID = -1;
//				selectedITEM_ID = -1;
//				getMouse().release();
//			}else {
//				g.setColor(new Color(255,255,255,50));
//			}
//		}

		drawImageAt(g, getPack().getItem(id, itemID), x, y);

		drawImageAt(g, ico, x, y);

		g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, size*4));
		
		g.setColor(Color.WHITE);

		g.drawString(getData().getItemName(2-id, itemID),
				drawX - ico.getWidth()*imgSize/2 + size,
				drawY - ico.getHeight()*imgSize/2 - size*3);

		g.drawString("LVL " + getData().getItemLevel(2-id, itemID),
				drawX - ico.getWidth()*imgSize/2 + size,
				drawY + ico.getHeight()*imgSize/2 + size*6);
		
		SecretItem item = getData().getItemForName(getData().getItemName(2-id, itemID));
		
		g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, size*3)); // TODO
		
		descriptionY = 0;
		
		drawItemDescription(g,
				item,
				drawX - ico.getWidth()*imgSize/2 + size,
				drawY + ico.getHeight()*imgSize/2 + size*10);
		
		
		
		
		g.setColor(Color.GREEN);
		fillRect(g,
				allDrawX + size/2,
				y - ico.getHeight()*imgSize/2 + size/2,
				(int) (progressW * Math.min(items,need) / need),
				size);
		
		g.setColor(Color.DARK_GRAY);
		fillRect(g, 
				allDrawX + size/2,
				y - ico.getHeight()*imgSize/2 + size/2, 
				progressW,
				size);

		g.setColor(Color.BLACK);
		fillRect(g, 
				allDrawX,
				y - ico.getHeight()*imgSize/2, 
				progressW + size,
				size*2);
		
		g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, size*2));
		g.setColor(Color.WHITE);
		g.drawString(Math.min(items,need) + "/" + need,
				drawX + ico.getWidth()*imgSize/2 + size*3 + size/2,
				drawY - ico.getHeight()*imgSize/2 + size*4
				);
		
		String c = getData().getCoins(2-id) + "/" + getData().getNeedItemCoins(2-id, itemID);
		
		int drawCoinsX = drawX + ico.getWidth()*imgSize/2 + size + size/2
				+ progressW ;
		int padW = g.getFontMetrics().charWidth('w');
		g.drawString(c,
				(int) (drawCoinsX -  g.getFontMetrics().stringWidth(c) - padW*1.5 + size*2),
				drawY - ico.getHeight()*imgSize/2 + size*4);

		int fs = g.getFont().getSize();
		BufferedImage img = getPack().getCoins(id);
//		fs = (int) (fs/1.25);
		int imgW = fs*img.getWidth()/img.getHeight();
		g.drawImage(img, 
				drawCoinsX - padW + size*2,
				(int) ((drawY - ico.getHeight()*imgSize/2 + size*4) - fs + size/2),
				imgW, fs, null);
		
		/*
		 * 50 - 2 = 48 			48 / 3 = 16
		 * ^^   ^   				 ^	 ^
		 * s    p   				 c	 b
		 * i    a  					 o	 t
		 * z    d  					 u	 n
		 * e    d  					 n	 S
		 *      i  					 t	 i
		 *      n  					 	 z
		 *      g  						 e
		 */
		
		uSelect.setX(drawX + ico.getWidth()*imgSize/2 	+ size*3);
		uUpdate.setX(drawX + ico.getWidth()*imgSize/2 	+ size*3 + size*16);
		uBack.setX(drawX + ico.getWidth()*imgSize/2 	+ size*3 + size*32);
		
		uSelect.setY(drawY + ico.getHeight()*imgSize/2 - size*5);
		uUpdate.setY(drawY + ico.getHeight()*imgSize/2 - size*5);
		uBack.setY(drawY + ico.getHeight()*imgSize/2 - size*5);

		uSelect.setWidth(size*15);
		uUpdate.setWidth(size*15);
		uBack.setWidth(size*15);

		uSelect.setHeight(size*5);
		uUpdate.setHeight(size*5);
		uBack.setHeight(size*5);

		uSelect.setSize(size*3);
		uUpdate.setSize(size*3);
		uBack.setSize(size*3);
		
		uSelect.draw(g);
		uUpdate.draw(g);
		uBack.draw(g);
	}
	
	private boolean drawButton(Graphics2D g, String text, int x, int y, int x2, int w) {
		boolean isClicked = false;
		if(checkMouse(x + x2*imgSize, y, w*imgSize - 2*getIQuality(), 7*imgSize - 2*getIQuality())) {
			g.setColor(Color.DARK_GRAY);
			if(getMouse().isMouseOneClick()) {
				getMouse().reClick();
				isClicked = true;
			}
		}else {
			g.setColor(Color.BLACK);
		}
		fillRect(g, x + x2*imgSize, y, w, 7);
		g.setColor(Color.WHITE);
		g.drawString(text,
				x + x2*imgSize + w*imgSize/2 - getIQuality() - g.getFontMetrics().stringWidth(text)/2,
				y + (7*imgSize)/2);
		return isClicked;
	}

	int selectedID = -1;
	int selectedITEM_ID = -1;
	
	int coinsY;
	
	int descriptionY = 0;
	
	private void drawItemDescription(Graphics2D g, SecretItem secretItem, int x, int y) {
		if(secretItem == null) return;
		g.setColor(getPack().getItemsColor(2-secretItem.getId()));
		g.drawString(getData().getItemDescription(secretItem.getId(), secretItem.getItemID()), x, y);
		y += imgSize*4;
		
		for (int i = 0; i < secretItem.getSecretItemsCount(); i++) {
			drawItemDescription(
					g,
					secretItem.getSecretItems()[i],
					x,
					y + imgSize*4*i*(1+secretItem.getSecretItems()[i].getSecretItemsCount()));
			
//			SecretItem item = secretItem.getSecretItems()[i];
//			g.setColor(getPack().getItemsColor(item.getId()));
//			g.drawString(getData().getItemDescription(item.getId(), item.getItemID()), x, y);
//			y += imgSize*2;
		}
	}
	
	private void drawCoins(Graphics2D g, int id, long coins) {
		int padding = g.getFontMetrics().charWidth('_');
		g.setColor(getPack().getItemsColor(id).darker().brighter());
		int fs = g.getFont().getSize();
		int y = (int) (fs*1.1)*coinsY + padding/2;
		String c = " " + coins;
		int w = g.getFontMetrics().stringWidth(c + ".");

		BufferedImage img = getPack().getCoins(id);
		fs = (int) (fs/1.25);
		int imgW = fs*img.getWidth()/img.getHeight();
		g.drawString(c, getPanel().getFrameW()-w-imgW-padding, y);
		g.drawImage(img, getPanel().getFrameW()-imgW-padding, y-fs, imgW, fs, null);
		
		/*
		 * FONT SIZE	IMG H
		 * ?			IMG W
		 */
		
		coinsY++;
	}
	
	private void drawWayLine(Graphics2D g, int x1, int y1, ArrayList<Integer> xs, int y2,
			Color c1, Color c2, ArrayList<Boolean> bs) {
		int y12 = y1 - (y1-y2)/2;

		if(bs.contains(true)) {
			g.setColor(waylinkColor.darker().darker().darker().darker().darker());
		}else {
			g.setColor(waylinkColor);
		}
		
		drawLine(g, x1-1, y12-1, 3, 1);
		drawLine(g, x1-1, y12-1, 1, 3);
		drawLine(g, x1-1, y12+1, 3, 1);
		drawLine(g, x1+1, y12-1, 1, 3);
		
		for (int i = 0; i < xs.size(); i++) {
			if(bs.get(i)) {
				g.setColor(c2.darker().darker().darker());
			}else {
				g.setColor(c2);
			}
			if(xs.get(i) > x1) {
				drawLine(g, x1+1, y12, xs.get(i)-x1, 1);
			}else if(xs.get(i) < x1){
				drawLine(g, xs.get(i), y12, x1-xs.get(i)-1, 1);
			}
			
			drawLine(g, xs.get(i), y12+1, 1, y2-y12-1);
		}
		if(bs.contains(true)) {
			g.setColor(c1.darker().darker().darker());
		}else {
			g.setColor(c1);
		}
		drawLine(g, x1, y1, 1, y12-y1);
	}
	
	Color waylinkColor = Color.WHITE;//new Color(84,84,84);
	
	private void drawLine(Graphics2D g, int x1, int y1, int w, int h) {
		g.fillRect(
				camX + (int) ((-.5d + x1)*getQuality()) + getFrameW()/2,
				camY + (int) ((-.5d + y1)*getQuality()) + getFrameH()/2,
				(int) (w*getQuality()),
				(int) (h*getQuality())
				);
	}
	
	
	private int calcDeDistance(int count) {
		return (int) (getQuality()*count*(imgSize + distanceSizeX)/-2);
	}
	
	private void drawSecretItem(Graphics2D g, SecretItem secretItem, int x, int y, int id, int iid, int pow) {
		int drawX = x + getPack().getPositionX(id, pow)*distanceSizeX/2;
		int drawY = y + (id-1)*distanceSizeY;

		ArrayList<Integer> xs = new ArrayList<Integer>();
		ArrayList<Boolean> bs = new ArrayList<Boolean>();
		for (int i = 0; i < secretItem.getSecretItemsCount(); i++) {
			xs.add(x + getPack().getPositionX(id+1, pow*secretItem.getSecretItemsCount()+i)*distanceSizeX/2);
			bs.add(secretItem.getSecretItems()[i].isLock());
		}
		
		drawAbilities(g, 2-id,
				pow,
				(int) (drawX*getQuality()),
				(int) (drawY*getQuality()),
				secretItem.isLock());
		
		for (int i = 0; i < secretItem.getSecretItemsCount(); i++) {
			drawSecretItem(
					g, secretItem.getSecretItems()[i],
					x,
					y,
					id+1,
					i,
					pow*secretItem.getSecretItemsCount()+i);
		}
		
		if(id < 2) {
			drawWayLine(g, drawX, drawY, xs, drawY + distanceSizeY,
					getPack().getItemsColor(2-id), getPack().getItemsColor(2-id - 1), bs);
		}
		
		/*
		 * POW		X
		 * COUNT	WIDTH
		 * 
		 * X = POW*WIDTH/COUNT
		 */
	}

	int treeWidth = 0;
	int treeHeight = 0;
	
	int imgSize = 2;
	int distanceSizeX = 2;
	int distanceSizeY = 7;
	
	private void drawAbilities(Graphics2D g, int id, int itemID, int x, int y, boolean isLocked) {
		BufferedImage ico = getPack().getItemFrame(id);
		
		int drawX = x+camX - (ico.getWidth()*imgSize - getFrameW())/2 + getIQuality();
		int drawY = y+camY - (ico.getHeight()*imgSize - getFrameH())/2 + getIQuality();
		

		if(getData().getSelectedID() == id && getData().getSelectedITEM_ID() == itemID) {
			drawImageAt(g, getPack().getFrame(id), x, y);
		}
		
		if(isLocked) {
			g.setColor(new Color(0,0,0,150));
			fillIcoRect(g, x, y, ico);
		}else {
			if(checkMouse(drawX, drawY, (ico.getWidth()-2)*imgSize)) {
				if(getMouse().isMousePressed()) {
					getMouse().reClick();
					g.setColor(new Color(255,255,255,150));
					selectedID = id;
					selectedITEM_ID = itemID;
				}else {
					g.setColor(new Color(255,255,255,50));
				}
				fillIcoRect(g, x, y, ico);
				fillIcoRect(g, x, y, ico);
			}
		}
		
		drawImageAt(g, isLocked ? getPack().getItemLock(id) : getPack().getItem(id, itemID), x, y);
		drawImageAt(g, ico, x, y);
	}
	
	private void fillIcoRect(Graphics2D g, int x, int y, BufferedImage ico) {
//		g.fillRect(
//				drawX,
//				drawY,
//				ico.getWidth()*imgSize - 2*getIQuality(),
//				ico.getHeight()*imgSize - 2*getIQuality());
//		
		g.fillRect(
				x+camX - (ico.getWidth()*imgSize - getFrameW())/2 + imgSize,
				y+camY - (ico.getHeight()*imgSize - getFrameH())/2 + imgSize,
				(ico.getWidth()-2)*imgSize,
				(ico.getHeight()-2)*imgSize
				);
	}

	
	private void drawImageAt(Graphics2D g, BufferedImage img, int x, int y) {
		if(img == null) {
			g.fillRect(x+camX - (getPack().getItemSize()*imgSize - getFrameW())/2,
					y+camY - (getPack().getItemSize()*imgSize - getFrameH())/2,
					getPack().getItemSize()*imgSize,
					getPack().getItemSize()*imgSize);
			return;
		}
		g.drawImage(
				img,
				x+camX - (img.getWidth()*imgSize - getFrameW())/2,
				y+camY - (img.getHeight()*imgSize - getFrameH())/2,
				img.getWidth()*imgSize,
				img.getHeight()*imgSize,
				null);
	}
	
	private void fillRect(Graphics2D g, int x, int y, int w, int h) {
		g.fillRect(
				x+camX + (getFrameW())/2,
				y+camY + (getFrameH())/2,
				w,
				h);
	}
	
//	private void fillRect(Graphics2D g, int drawX, int drawY, int w, int h) {
//		g.fillRect(
//				drawX,
//				drawY,
//				w*imgSize - 2*getIQuality(),
//				h*imgSize - 2*getIQuality());
//	}
	
	private boolean checkMouse(int x, int y, int size) {
//		System.out.println("[SAbilitiesTree] " + (x+getCenterX() - getMouse().getMouseX()));
		return getMouse().getFrameMouseX() >= x && getMouse().getFrameMouseX() <= x+size
				&& getMouse().getFrameMouseY() >= y && getMouse().getFrameMouseY() <= y+size;
				//Math.abs(x - getMouse().getMouseX()) <= size && 
			   //Math.abs(y - getMouse().getMouseY()) <= size;
	}
	
	private boolean checkMouse(int x, int y, int w, int h) {
		return getMouse().getGameMouseX() >= x && getMouse().getGameMouseX() <= x+w
				&& getMouse().getGameMouseY() >= y && getMouse().getGameMouseY() <= y+h;
	}
	
	private double getCenterX() {
		return (getFrameW()/2)/getQuality();
	}
	
	private double getCenterY() {
		return (getFrameH()/2)/getQuality();
	}
	
	private void fillRectAt(Graphics2D g, BufferedImage img, int x, int y) {
		g.fillRect(
				x+camX - (img.getWidth()*imgSize - getFrameW())/2,
				y+camY - (img.getHeight()*imgSize - getFrameH())/2,
				img.getWidth()*imgSize,
				img.getHeight()*imgSize
				);
	}
	
	public int getIQuality() {
		return (int) (2*getQuality());
	}

	@Override
	public void keyPressed(KeyEvent e) {
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		
	}
}
