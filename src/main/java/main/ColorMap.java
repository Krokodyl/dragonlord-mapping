package main;

import java.awt.image.IndexColorModel;

public class ColorMap {

    ColorDepth depth;
    private IndexColorModel colorModel;

    ColorMap(ColorDepth depth) {
        this.depth = depth;
    }
    
    public void loadColorMap(byte[] data, int offset) {
        int colors = depth.getColorPerPalette();
        byte[] r = new byte[getDepth().getColorPerPalette()];
        byte[] g = new byte[getDepth().getColorPerPalette()];
        byte[] b = new byte[getDepth().getColorPerPalette()];

        int k = 0;
        int end = offset + colors*2;
        while (offset<end) {
            int red = data[offset] & 0x07;
            int green = (data[offset+1] & 0x070) >> 4;
            int blue = data[offset+1] & 0x07;
            //int rgb = (data[offset++] & 0xFF) * 0x10000 + (data[offset++] & 0xFF) * 0x100 + (data[offset++] & 0xFF);
            r[k] = (byte) ((red * 32) & 0xFF);
            g[k] = (byte) ((green * 32) & 0xFF);
            b[k] = (byte) ((blue * 32) & 0xFF);
            k++;
            offset += 2;
        }
        
        IndexColorModel colorModel = new IndexColorModel(
                8, getDepth().getColorPerPalette(), r, g, b
        );
        this.colorModel = colorModel;
    }
    
    public IndexColorModel getIndexColorModel() {
        return colorModel;
    }


    public ColorDepth getDepth() {
        return depth;
    }
    
}
