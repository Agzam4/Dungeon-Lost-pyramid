package gui;

import javax.swing.JPanel;

import game.Tile;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;

import stages.Manager;

public class JDungeonPanel extends JPanel implements KeyListener {

	private static final long serialVersionUID = -3211693771382043045L;

	Manager manager;
	
	private static long sleep = 50; // 1000 / FPS;
	
	public int frameW;
	public int frameH;
	
	public double scale;
	public double scalefull;
	
	public double quality = 5;
	
	public int gameX;
	public int gameY;
	
	public JDungeonPanel() {
		manager = new Manager(this);
		
		addComponentListener(new ComponentListener() {
			
			@Override
			public void componentResized(ComponentEvent e) {
				calculateScreen();
			}

			@Override public void componentShown(ComponentEvent e) {}
			@Override public void componentMoved(ComponentEvent e) {}
			@Override public void componentHidden(ComponentEvent e) {}
		});
		
		setFocusable(true);
	}
	
	private boolean isRunning;
	
	public void go() {
		calculateScreen();
		
		isRunning = true;
		Thread update = new Thread(() -> {
			long start;
			long wait;
			while (isRunning) {
				start = System.nanoTime();
				manager.update();
				draw();
				wait = sleep - (System.nanoTime() - start)/1_000_000;
				if(wait < 0) wait = 5;
				try {
					Thread.sleep(wait);
				} catch (InterruptedException e) {
				}
			}
		});
		update.start();
		
		addKeyListener(this);
	}

	private BufferedImage all;
	private BufferedImage game;
	private BufferedImage gamefull;
	
	private void draw() {
		Graphics2D panelGraphics2d = (Graphics2D) getGraphics();
		
		Graphics2D g = (Graphics2D) game.getGraphics();
		Graphics2D gf = (Graphics2D) gamefull.getGraphics();
		
		Render.addRenderingHints(g);
		Render.addRenderingHints(gf);
		
		gf.setColor(new Color(0,0,0,0));
		gf.fillRect(0, 0, frameW, frameH);
		gf.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, (int) (5*scalefull)));
		manager.draw(g, gf);

		Graphics2D a = (Graphics2D) all.getGraphics();
		a.drawImage(game, gameX, gameY,
				(int)(game.getWidth()*scale), (int) (game.getHeight()*scale), null);
		a.drawImage(gamefull, 0, 0, null);
		a.dispose();
		
		panelGraphics2d.drawImage(all, 0, 0, null);
	}
	
	public void calculateScreen() {
		frameW = getWidth();
		frameH = getHeight();
		double tq = quality*Tile.tilesize;
		scalefull = (int) Math.floor(frameH/200);
		scale = (int) Math.ceil(frameH/16f/11f)/quality;
		game = new BufferedImage((int) ((int) Math.ceil(frameW/scale/tq/2f)*tq*2f + tq),
				(int) (Math.ceil(16*11*quality/tq)*tq), BufferedImage.TYPE_INT_RGB);
		
		/**
		 * TYPE_INT_RGB
		 * 
		 * TODO
		 * 
		 * TYPE_USHORT_555_RGB	// ROUND
		 * TYPE_BYTE_INDEXED	// PIXEL
		 * TYPE_BYTE_GRAY		// GRAY
		 * TYPE_BYTE_BINARY		// W&B
		 */
				
		gamefull = new BufferedImage(frameW, frameH, BufferedImage.TYPE_INT_ARGB);
		all = new BufferedImage(frameW, frameH, BufferedImage.TYPE_INT_RGB);
		gameX = (int) (frameW - Math.floor(game.getWidth()/16)*scale*16)/2;
		gameY = (int) (frameH - Math.floor(game.getHeight()/16)*scale*16)/2;

		System.out.println("[JDungeonPanel] Frame: " + frameW + "x" + frameH);
		System.out.println("[JDungeonPanel] Game: " + getGameWidth() + "x" + getGameHeight());
		System.out.println("[JDungeonPanel] GameTiles: " + getCountWidth() + "x" + getCountHeight());
		System.out.println("[JDungeonPanel] Scale: " + scale);
		System.out.println("[JDungeonPanel] ScaleFull: " + scalefull);
		System.out.println("[JDungeonPanel] Quality: " + quality);
	}
	
	public int getGameWidth() {
		return game.getWidth();
	}
	
	public int getGameHeight() {
		return game.getHeight();
	}

	public int getCountWidth() {
		return (int) (Math.floor(game.getWidth()/16f)/quality);
	}
	public int getCountHeight() {
		return (int) (Math.floor(game.getHeight()/16f)/quality);
	}
	
	public int getFrameW() {
		return frameW;
	}
	
	public int getFrameH() {
		return frameH;
	}
	
	public double getScalefull() {
		return scalefull;
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
		manager.keyPressed(e);
	}

	@Override
	public void keyReleased(KeyEvent e) {
		manager.keyReleased(e);
	}

	private transient final AlphaComposite alpha = AlphaComposite.getInstance(AlphaComposite.DST_IN, 1f);
	private transient final AlphaComposite normal = AlphaComposite.getInstance(AlphaComposite.DST_OVER, 1f);
	
	public void clear(Graphics2D g) {
		g.setComposite(alpha);
		g.setColor(new Color(0,0,0,0));
		g.fillRect(0, 0, frameW, frameH);
		g.setComposite(normal);
	}
	
	public AlphaComposite getNormalComposite() {
		return normal;
	}
}
