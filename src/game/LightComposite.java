package game;

import java.awt.Composite;
import java.awt.CompositeContext;
import java.awt.RenderingHints;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

public class LightComposite implements Composite {

    public static LightComposite INSTANCE = new LightComposite();

    private LightContext context = new LightContext();

    @Override
    public CompositeContext createContext(ColorModel srcColorModel,
            ColorModel dstColorModel, RenderingHints hints) {
        return context;
    }
    
    
    private static class LightContext implements CompositeContext {

        @Override
        public void compose(Raster src, Raster dstIn, WritableRaster dstOut) {
        	
//            WritableRaster src;
//            int w;
//            int h;
//
//            if (src2 != dst) {
//                dst.setDataElements(0, 0, src2);
//            }
//            
//
//            
//            // REMIND: We should be able to create a SurfaceData from just
//            // a non-writable Raster and a ColorModel.  Since we need to
//            // create a SurfaceData from a BufferedImage then we need to
//            // make a WritableRaster since it is needed to construct a
//            // BufferedImage.
//            if (src1 instanceof WritableRaster) {
//                src = (WritableRaster) src1;
//            } else {
//                src = src1.createCompatibleWritableRaster();
//                src.setDataElements(0, 0, src1);
//            }
//
//            w = Math.min(src.getWidth(), src2.getWidth());
//            h = Math.min(src.getHeight(), src2.getHeight());
//            
//            
//            BufferedImage srcImg = new BufferedImage(src., src,
//                                                    	true,
//                                                     null);
//            BufferedImage dstImg = new BufferedImage(new Co, dst,
//            											true,
//                                                     null);
//
//            SurfaceData srcData = BufImgSurfaceData.createData(srcImg);
//            SurfaceData dstData = BufImgSurfaceData.createData(dstImg);
//            Blit blit = Blit.getFromCache(srcData.getSurfaceType(),
//                                          comptype,
//                                          dstData.getSurfaceType());
//            blit.Blit(srcData, dstData, composite, null, 0, 0, 0, 0, w, h);
//            
//            CompositeType.
        	
//            int w = Math.min(src1.getWidth(), src2.getWidth());
//            int h = Math.min(src1.getHeight(), src2.getHeight());
//
//            int[] srcRgba = new int[4];
//            int[] dstRgba = new int[4];
            
//            int x = 0;
//            int y = 0;
//            for (int x = 0; x < w; x++) {
//                src.getDataElements(0, y, width, 1, srcPixels);
//                dstIn.getDataElements(0, y, width, 1, dstPixels);
//                for (int y = 0; y < h; y++) {
//                    //src1.getPixel(x, y, srcRgba);
//                    //src2.getPixel(x, y, dstRgba);
//                    for (int i = 0; i < 2; i++) {
//                        dstRgba[i] = dstRgba[i] + srcRgba[i];
//                        if(dstRgba[i] > 255) {
//                        	dstRgba[i] = 255;
//                        }
//                        if(dstRgba[i] < 0) {
//                        	dstRgba[i] = 0;
//                        }
//                    }
//                    dst.setPixel(x, y, dstRgba);
//                }
        	//            }

        	int width = Math.min(src.getWidth(), dstIn.getWidth());
        	int height = Math.min(src.getHeight(), dstIn.getHeight());

        	float alpha = 1;

        	int[] result = new int[4];
        	int[] srcPixel = new int[4];
        	int[] dstPixel = new int[4];
        	int[] srcPixels = new int[width];
        	int[] dstPixels = new int[width];

        	for (int y = 0; y < height; y++) {
        		src.getDataElements(0, y, width, 1, srcPixels);
        		dstIn.getDataElements(0, y, width, 1, dstPixels);
        		for (int x = 0; x < width; x++) {
        			int pixel = srcPixels[x];
        			srcPixel[0] = (pixel >> 16) & 0xFF;
        			srcPixel[1] = (pixel >>  8) & 0xFF;
        			srcPixel[2] = (pixel      ) & 0xFF;
        			srcPixel[3] = (pixel >> 24) & 0xFF;

        			pixel = dstPixels[x];
        			dstPixel[0] = (pixel >> 16) & 0xFF;
        			dstPixel[1] = (pixel >>  8) & 0xFF;
        			dstPixel[2] = (pixel      ) & 0xFF;
        			dstPixel[3] = (pixel >> 24) & 0xFF;

        			blend(srcPixel, dstPixel, result);

        			dstPixels[x] = ((int) (dstPixel[3] + (result[3] - dstPixel[3]) * alpha) & 0xFF) << 24 |
        					((int) (dstPixel[0] + (result[0] - dstPixel[0]) * alpha) & 0xFF) << 16 |
        					((int) (dstPixel[1] + (result[1] - dstPixel[1]) * alpha) & 0xFF) <<  8 |
        					(int) (dstPixel[2] + (result[2] - dstPixel[2]) * alpha) & 0xFF;
        		}
        		dstOut.setDataElements(0, y, width, 1, dstPixels);
        	}
        }
        
        public void blend(int[] src, int[] dst, int[] result) {
            result[0] = Math.min(255, src[0] + dst[0]);
            result[1] = Math.min(255, src[1] + dst[1]);
            result[2] = Math.min(255, src[2] + dst[2]);
            result[3] = Math.min(255, src[3] + dst[3]);
        }

        @Override
        public void dispose() {
        }
    }
}