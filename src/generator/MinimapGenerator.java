package generator;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import work.Pack;

public class MinimapGenerator {

	public static final BufferedImage finish = loadImage("finish");
	
	private static BufferedImage loadImage(String path) {
		try {
			return ImageIO.read(Pack.class.getResourceAsStream("/minimap/" + path + ".png"));
		} catch (IOException e) {
			return new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		}
	}
	
	public static BufferedImage generate(int[][] map) {
		int w = map.length, h = map[0].length;
		int size = 10;
		int width = w*size;
		int height = h*size;
		
		Point finishPoint = null;
		
		BufferedImage minimap = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = (Graphics2D) minimap.getGraphics();
		g.setColor(new Color(233, 193, 131));
		g.setColor(new Color(233-25, 193-25, 131-25));
		g.fillRect(0, 0, width, height);
		
		int border = 1;
		
		for (int x = 0; x < w; x++) {
			for (int y = 0; y < h; y++) {
				int id = map[x][y];
				if(id == Blocks.FINISH.ordinal()) {
					finishPoint = new Point(x, y+1);
				}else if(id == Blocks.START_WALL.ordinal()) {
				}else if(id == Blocks.LADDER.ordinal() 
					|| id == Blocks.AIR.ordinal() || id == Blocks.LADDER_TRAP_LADDER.ordinal()
							|| id == Blocks.CRUSHER_AIR.ordinal()) {
				
				}else {
					g.setColor(new Color(160, 111, 62));
					g.fillRect(x*size, y*size, size, size);
					g.setColor(new Color(12, 8, 4));
					for (int yy = 0; yy < size; yy++) {
						for (int xx = 0; xx < size; xx++) {
							if((x*size+xx)%4 == 0 && (y*size+yy)%4 == 0)
							g.fillRect(x*size+xx, y*size+yy, 1, 1);
							if((x*size+xx)%4 == 2 && (y*size+yy)%4 == 2)
							g.fillRect(x*size+xx, y*size+yy, 1, 1);
						}
					}
					
					if(x+1 < w && isAirBlock(map[x+1][y])) {
						g.fillRect((x+1)*size-border, y*size, border, size);
						for (int i = 0; i < 4; i++) {
							for (int yy = i%2*(i+1)/2; yy < size; yy+=2 + 2*Math.floor(i/2d)) {
								g.fillRect((x+1)*size-border-i-1, y*size + yy, 1, 1);
							}
						}
					}
					if(x > 0 && isAirBlock(map[x-1][y])) {
						g.fillRect(x*size, y*size, border, size);
						for (int i = 0; i < 4; i++) {
							for (int yy = i%2*(i+1)/2; yy < size; yy+=2 + 2*Math.floor(i/2d)) {
								g.fillRect(x*size+border+i, y*size + yy, 1, 1);
							}
						}
					}
					if(y+1 < h && isAirBlock(map[x][y+1])) {
						g.fillRect(x*size,(y+1)*size-border, size, border);
						for (int i = 0; i < 4; i++) {
							for (int xx = i%2*(i+1)/2; xx < size; xx+=2 + 2*Math.floor(i/2d)) {
								g.fillRect(x*size+xx, (y+1)*size-border-i-1, 1, 1);
							}
						}
					}
					if(y > 0 && isAirBlock(map[x][y-1])) {
						g.fillRect(x*size, y*size, size, border);
						for (int i = 0; i < 4; i++) {
							for (int xx = i%2*(i+1)/2; xx < size; xx+=2 + 2*Math.floor(i/2d)) {
								g.fillRect(x*size+xx, y*size+border+i, 1, 1);
							}
						}
					}
				}
			}
		}
		
		if(finishPoint != null)
		g.drawImage(finish,
				finishPoint.x*size-finish.getWidth()/2 + size/2,
				finishPoint.y*size-finish.getHeight()/2 + size/2,
				null);
		
		g.dispose();

		cut(minimap);
		effectTo(minimap, createEffectMap(minimap.getWidth(), minimap.getHeight()));
		
//		try {
//			ImageIO.write(minimap, "png", new File("debug/minimap.png"));
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		return minimap;
	}
	
	private static boolean isAirBlock(int id) {
		return id == Blocks.LADDER.ordinal() 
				|| id == Blocks.AIR.ordinal() 
				|| id == Blocks.LADDER_TRAP_LADDER.ordinal()
				|| id == Blocks.CRUSHER_AIR.ordinal() 
				|| id == Blocks.START_WALL.ordinal()
				|| id == Blocks.FINISH.ordinal();
	}
	
	public static void shift(BufferedImage img) {
		BufferedImage newImg = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());

		int w = img.getWidth();
		int h = img.getHeight();

		int shift = 0;
		
		for (int y = 0; y < h; y++) {
			if(shift > 0) shift = randomInt(1);
			else if(shift == 0) shift = randomInt(2)-1;
			else if(shift < 0) shift = -randomInt(1);
			
			for (int x = 0; x < w; x++) {
				newImg.setRGB(x, y,
						averageRGB(
								newImg.getRGB(x, y),
								img.getRGB((x+shift+2)%w, y))
						);
			}
		}

		img = newImg;
		newImg = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());
		
		shift = 0;

		for (int x = 0; x < w; x++) {
			if(shift > 0) shift = randomInt(1);
			else if(shift == 0) shift = randomInt(2)-1;
			else if(shift < 0) shift = -randomInt(1);

			for (int y = 0; y < h; y++) {
				newImg.setRGB(x, y, img.getRGB(x, (y+shift+2)%h));
			}
		}
		
		img = newImg;
//		try {
//			ImageIO.write(newImg, "png", new File("debug/shift minimap.png"));
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
	}
	
	private static int averageRGB(int rgb1, int rgb2) {
		Color c1 = new Color(rgb1);
		Color c2 = new Color(rgb2);
		return new Color(
				c1.getRed()+c2.getRed()/2,
				c1.getGreen()+c2.getGreen()/2,
				c1.getBlue()+c2.getBlue()/2
				).getRGB();
	}
	
	private static void cut(BufferedImage img) {
		BufferedImage cut = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);

		int[] lholesTop = new int[img.getWidth()];
		int[] lholesBottom = new int[img.getWidth()];
		int[] lholesRight = new int[img.getHeight()];
		int[] lholesLeft = new int[img.getHeight()];
		
//		for (int i = 0; i < img.getWidth()/5; i++) {
//			int randomX = randomInt(img.getWidth()-2) + 1;
//			holes[randomX]+=5;
//			holes[randomX]+=20;
//			holes[randomX]+=5;
//		}
		
		int[][] cutMap = createEffectMap(img.getWidth(), img.getHeight());
		
		Graphics2D g = (Graphics2D) img.getGraphics();

		int maxTop = 1;
		int maxBottom = 1;
		int maxRight = 1;
		int maxLeft = 1;
		
		for (int x = 0; x < cutMap.length; x++) {
			for (int y = 1; y < cutMap[x].length; y++) {
				if(cutMap[x][y] < 10) {
					lholesTop[x] = y;
					if(maxTop < y) maxTop = y;
					break;
				}
			}
		}
		for (int y = 0; y < cutMap.length; y++) {
			for (int x = 1; x < cutMap[y].length; x++) {
				if(cutMap[x][y] < 10) {
					lholesLeft[y] = x;
					if(maxLeft < x) maxLeft = x;
					break;
				}
			}
		}
		for (int x = 0; x < cutMap.length; x++) {
			for (int y = cutMap[x].length-2; y > 0; y--) {
				if(cutMap[x][y] < 10) {
					lholesBottom[x] = y;
					if(maxBottom < y) maxBottom = y;
					break;
				}
			}
		}
		for (int y = 0; y < cutMap.length; y++) {
			for (int x = cutMap[y].length-2; x > 0; x--) {
				if(cutMap[x][y] < 10) {
					lholesRight[y] = x;
					if(maxRight < x) maxRight = x;
					break;
				}
			}
		}

		int[] holesTop = new int[img.getWidth()];
		int[] holesBottom = new int[img.getWidth()];
		int[] holesRight = new int[img.getWidth()];
		int[] holesLeft = new int[img.getWidth()];


		int size = img.getWidth()/33;
		int size2 = size-1;

		
		for (int i = 0; i < size; i++) {
			holesTop[i] = 0;
			holesLeft[i] = 0;
			
			holesTop[holesTop.length-i-1] = 0;
			holesLeft[holesLeft.length-i-1] = 0;
			
			holesRight[i] = holesRight.length-1;
			holesBottom[i] = holesBottom.length-1;
			
			holesRight[holesRight.length-i-1] = holesRight.length-1;
			holesBottom[holesBottom.length-i-1] = holesBottom.length-1;
		}

		for (int x = 1; x < lholesBottom.length-1; x++) {
			holesTop[x] = 	 (lholesTop[x-1] 	+ lholesTop[x] 		+ lholesTop[x+1])/3;
			holesBottom[x] = (lholesBottom[x-1] + lholesBottom[x] 	+ lholesBottom[x+1])/3;
			holesRight[x] =  (lholesRight[x-1] 	+ lholesRight[x] 	+ lholesRight[x+1])/3;
			holesLeft[x] = 	 (lholesLeft[x-1] 	+ lholesLeft[x] 	+ lholesLeft[x+1])/3;
		}
		
//		for (int i = 1; i < holesTop.length-1; i++) {
//			if(holesTop[i] > i) 
//				holesTop[i] = i;
//			
//			if(holesLeft[i] > i) 
//				holesLeft[i] = i;
//			if(holesLeft[holesLeft.length-i-1] > holesLeft.length-i-1) 
//				holesLeft[holesLeft.length-i-1] = holesLeft.length-i;
//		}

		int maxRight2 = 1;
		int maxBottom2 = 1;
		for (int i = 0; i < lholesBottom.length; i++) {
			holesRight[i] = holesRight.length - holesRight[i];
			holesBottom[i] = holesBottom.length - holesBottom[i];
			if(maxRight2 < holesRight[i]) maxRight2 = holesRight[i];
			if(maxBottom2 < holesBottom[i]) maxBottom2 = holesBottom[i];
		}
		System.out.println(maxRight2);
		
		g.setColor(new Color(0,0,0,0));
		for (int i = 0; i < holesTop.length; i++) {
			g.setColor(new Color(160, 111, 62));
			g.fillRect(i, 0, 1, holesTop[i]*size/maxTop+3);
			g.fillRect(0, i, holesLeft[i]*size/maxLeft+3, 1);
			g.fillRect(img.getWidth() - holesRight[i]*size/maxRight-3, i, holesRight[i]*size/maxRight+3, 1);
			g.fillRect(i, img.getHeight() - holesBottom[i]*size/maxLeft-3, 1, holesBottom[i]*size/maxBottom+3);
			
			g.setColor(new Color(80, 54, 30));
			g.fillRect(i, 0, 1, holesTop[i]*size2/maxTop+2);
			g.fillRect(0, i, holesLeft[i]*size2/maxLeft+2, 1);
			g.fillRect(img.getWidth() - holesRight[i]*size2/maxRight-2, i, holesRight[i]*size2/maxRight+2, 1);
			g.fillRect(i, img.getHeight() - holesBottom[i]*size2/maxBottom-2, 1, holesBottom[i]*size2/maxBottom+2);
		}
		g.dispose();
		

		g = (Graphics2D) img.getGraphics();
		g.setComposite(alpha);
		for (int i = 0; i < holesTop.length; i++) {
			g.setColor(new Color(0,0,0,0));
			g.fillRect(i, 0, 1, holesTop[i]*size2/maxTop+1);
			g.fillRect(0, i, holesLeft[i]*size2/maxLeft+1, 1);
			g.fillRect(img.getWidth() - holesRight[i]*size2/maxRight-1, i, holesRight[i]*size2/maxRight+1, 1);
			g.fillRect(i, img.getHeight() - holesBottom[i]*size2/maxBottom-1, 1, holesBottom[i]*size2/maxBottom+1);
//			g.fillRect(0, i, holesLeft[i]*14/maxLeft+1, 1);
		}
		g.setComposite(normal);
		g.dispose();
	}
	
	public static int[][] createEffectMap(int w, int h) {
		int eff[][] = new int[w][h];
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				eff[x][y] = randomInt(20)*20;
			}
		}
		
		for (int i = 0; i < 3; i++) {
			for (int y = 1; y < h-1; y++) {
				for (int x = 1; x < w-1; x++) {
					int e = eff[x][y];
					for (int yy = -1; yy < 2; yy++) {
						for (int xx = -1; xx < 2; xx++) {
							e += eff[x+xx][y+yy];
						}
					}
					e /= 100;
					e *= 10;
					eff[x][y] = e;
				}
			}
		}
		
		for (int x = 0; x < w; x++) {
			for (int y = 1; y < h-1; y++) {
				eff[x][y] /= 25;
				eff[x][y] *= 25;
				eff[x][y] /= 2;
				eff[x][y] -= 50;
			}
		}
		return eff;
	}
	
	private static void effectTo(BufferedImage img, int map[][]) {
		for (int y = 1; y < img.getHeight()-1; y++) {
			for (int x = 1; x < img.getWidth()-1; x++) {
				Color c = new Color(img.getRGB(x, y));
				if(c.getRGB() != new Color(0,0,0).getRGB())
				img.setRGB(x, y, new Color(
						Math.max(0,(Math.min(255, c.getRed() + map[x][y]))),
						Math.max(0,(Math.min(255, c.getGreen() + map[x][y]))),
						Math.max(0,(Math.min(255, c.getBlue() + map[x][y])))
					).getRGB());
//				else
//					img.setRGB(x, y, new Color(255,0,255).getRGB());
			}
		}
		
		
//		BufferedImage newImg = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());
//		for (int y = 1; y < img.getHeight()-1; y++) {
//			for (int x = 1; x < img.getWidth()-1; x++) {
//				int value = new Color(img.getRGB(x, y)).getRed();
//				
//				//Color c = new Color(img.getRGB(x, y));
//				//int r = c.getRed(), g = c.getGreen(), b = c.getBlue();
//				for (int yy = -1; yy < 2; yy++) {
//					for (int xx = -1; xx < 2; xx++) {
//						value += new Color(img.getRGB(x+xx, y+yy)).getRed();
////						Color cc = new Color(img.getRGB(x+xx, y+yy));
////						r += cc.getRed();
////						g += cc.getGreen();
////						b += cc.getBlue();
//					}
//				}
//				value /= 100;
//				value *= 10;
////				r /= 200;
////				g /= 200;
////				b /= 200;
////				r *= 20;
////				g *= 20;
////				b *= 20;
//				try {
//					Color c = new Color(160 + value, 111 + value, 62 + value);
//					newImg.setRGB(x, y, c.getRGB());
//				} catch (Exception e) {
//					System.out.println(value);
//				}
//			}
//		}
//		try {
//			ImageIO.write(newImg, "png", new File("debug/minimap_effect.png"));
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
	}
	
//	public static void main(String[] args) {
//		LevelGenerator generator = new LevelGenerator();
//		generator.setLevel(1);
//		generator.generate(26, 26);
////		try {
////			ImageIO.write(generator.draw(), "png", new File("debug/map.png"));
////		} catch (IOException e) {
////			e.printStackTrace();
////		}
//	}
	
	private static int randomInt(int i) {
		return (int) (Math.random()*i);
	}
	
	private transient static final AlphaComposite alpha = AlphaComposite.getInstance(AlphaComposite.DST_IN, 1f);
	private transient static final AlphaComposite normal = AlphaComposite.getInstance(AlphaComposite.DST_OVER, 1f);
	
	public static void clear(BufferedImage img) {
		Graphics2D g = (Graphics2D) img.getGraphics();
		g.setComposite(alpha);
		g.setColor(new Color(0,0,0,0));
		g.fillRect(0, 0, img.getWidth()/2, img.getHeight());
		g.setComposite(normal);
	}
}
