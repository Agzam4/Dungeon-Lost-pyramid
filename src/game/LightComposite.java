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
            int w = Math.min(src.getWidth(), dstIn.getWidth());
            int h = Math.min(src.getHeight(), dstIn.getHeight());

            int[] srcRgba = new int[4];
            int[] dstRgba = new int[4];

            for (int x = 0; x < w; x++) {
                for (int y = 0; y < h; y++) {
                    src.getPixel(x, y, srcRgba);
                    dstIn.getPixel(x, y, dstRgba);
                    for (int i = 0; i < 2; i++) {
                        dstRgba[i] = dstRgba[i] + srcRgba[i];
                        if(dstRgba[i] > 255) {
                        	dstRgba[i] = 255;
                        }
                        if(dstRgba[i] < 0) {
                        	dstRgba[i] = 0;
                        }
                    }
                    dstOut.setPixel(x, y, dstRgba);
                }
            }
        }

        @Override
        public void dispose() {
        }
    }
}