package main;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.List;

import static main.Utils.*;

public class Main {
    
    // unpacked disk files
    public static String GAME_DISK = "D:\\emulation\\atari\\dls\\AtariMania dumps\\SCP DISK";
    
    public static void main(String[] args) throws IOException {

        /*generateTiles(1, "background");
        generateTiles(1, "foreground");
        generateTiles(2, "background");
        generateTiles(2, "foreground");
        generateTiles(3, "background");
        generateTiles(3, "foreground");
        generateTiles(4, "background");
        generateTiles(4, "foreground");*/
        
        //generateLevel(1);
        //generateLevel(2);
        //generateLevel(3);
        //generateLevel(4);

        //generateTiles(0, "dragon", 320/16, 200/8, 16,8);
        //generateTiles(0, "ending", 320/16, 200/8, 16,8);
        //generateTiles(99, "ending", 320/16, 200/8, 16,8);

        //generateTiles(0, "title", 320/16, 200/8, 16,8);

        //generateTiles(1, "items", 320/16, 256/8, 16,8);
        //generateTiles(2, "items", 320/16, 256/8, 16,8);
        //generateTiles(3, "items", 320/16, 256/8, 16,8);
        //generateTiles(4, "items", 320/16, 256/8, 16,8);

        //stxPatch();

        //generateTitleFont();
        //introText();
        //parseTrace("D:\\emulation\\atari\\emulators\\Steem.SSE.4.2.1.Win64.D3D\\TRACE-entering-level-two.txt");
        //parseTrace("D:\\emulation\\atari\\emulators\\Steem.SSE.4.2.1.Win64.D3D\\TRACE-entering-level-four.txt");
    }
    
    public static void generateLevel(int level) throws IOException {
        String datFile = String.format("PLAN%d", level);
        int screenId = 1;
        for (screenId=0;screenId<30;screenId++) {
            BufferedImage image = getScreenImageFromDatFile(
                    screenId,
                    String.format("%s\\%s.DAT", GAME_DISK, datFile), level);
            new File(String.format("output/level%d", level)).mkdirs();
            ImageIO.write(image, "png", new File(String.format("output/level%d/%02d.png", level, screenId)));
        }
    }

    public static BufferedImage generateTiles(int id, String type) throws IOException {
        return generateTiles(id, type, 20, 12, 16, 16);
    }

    public static BufferedImage generateTiles(int id, String type, int w, int h, int tw, int th) throws IOException {
        byte[] imageData = Main.class.getClassLoader()
                .getResourceAsStream(String.format("tiles-%s-%02d.bin", type, id)).readAllBytes();
        byte[] palData = Main.class.getClassLoader()
                .getResourceAsStream(String.format("tiles-%s-%02d.pal", type, id)).readAllBytes();

        int width = w;
        int height = h;
        final int TILE_WIDTH = tw;
        final int TILE_HEIGHT = th;

        ColorMap cmap = new ColorMap(ColorDepth._4BPP);
        cmap.loadColorMap(palData, 0);
        IndexColorModel colorModel = cmap.getIndexColorModel();

        BufferedImage out = new BufferedImage(width*TILE_WIDTH, height*TILE_HEIGHT, BufferedImage.TYPE_BYTE_INDEXED, cmap.getIndexColorModel());
        Graphics2D g2d = out.createGraphics();
        int i = 0;
        int offset = 0;
        int x = 0;
        int y = 0;
        while (x<out.getWidth() && y<out.getHeight()) {

            for (int half = 0; half <=1; half++) {
                byte b1 = imageData[offset + half];
                byte b2 = imageData[offset + half + 2];
                byte b3 = imageData[offset + half + 4];
                byte b4 = imageData[offset + half + 6];

                int bit = 7;
                int colorIndex = 0;
                while (bit>=0) {
                    colorIndex |= ((b4 & (1 << bit)) >> bit) << 3;
                    colorIndex |= ((b3 & (1 << bit)) >> bit) << 2;
                    colorIndex |= ((b2 & (1 << bit)) >> bit) << 1;
                    colorIndex |= (b1 & (1 << bit)) >> bit;

                    out.getRaster().setPixel(x, y, new int[]{colorIndex});
                    x++;
                    if (x>=out.getWidth()) {
                        x=0;
                        y++;
                    }
                    bit--;
                    colorIndex = 0;
                }

            }
            offset += 8;
        }
        g2d.dispose();

        ImageIO.write(out, "png", new File(String.format("output/res/tiles-%s-%02d.png", type, id)));

        return out;
    }

    public static BufferedImage generateTilesPalette(String image, String palette, String output) throws IOException {
        byte[] imageData = Main.class.getClassLoader()
                .getResourceAsStream(image).readAllBytes();
        byte[] palData = Main.class.getClassLoader()
                .getResourceAsStream(palette).readAllBytes();

        int width = 20;
        int height = 12;
        final int TILE_WIDTH = 16;
        final int TILE_HEIGHT = 16;

        ColorMap cmap = new ColorMap(ColorDepth._4BPP);
        cmap.loadColorMap(palData, 0);
        IndexColorModel colorModel = cmap.getIndexColorModel();

        BufferedImage out = new BufferedImage(width*TILE_WIDTH, height*TILE_HEIGHT, BufferedImage.TYPE_BYTE_INDEXED, cmap.getIndexColorModel());
        Graphics2D g2d = out.createGraphics();
        int i = 0;
        int offset = 0;
        int x = 0;
        int y = 0;
        while (offset<imageData.length) {

            for (int half = 0; half <=1; half++) {
                byte b1 = imageData[offset + half];
                byte b2 = imageData[offset + half + 2];
                byte b3 = imageData[offset + half + 4];
                byte b4 = imageData[offset + half + 6];

                int bit = 7;
                int colorIndex = 0;
                while (bit>=0) {
                    colorIndex |= ((b4 & (1 << bit)) >> bit) << 3;
                    colorIndex |= ((b3 & (1 << bit)) >> bit) << 2;
                    colorIndex |= ((b2 & (1 << bit)) >> bit) << 1;
                    colorIndex |= (b1 & (1 << bit)) >> bit;

                    out.getRaster().setPixel(x, y, new int[]{colorIndex});
                    x++;
                    if (x>=out.getWidth()) {
                        x=0;
                        y++;
                    }
                    bit--;
                    colorIndex = 0;
                }

            }
            offset += 8;
        }
        g2d.dispose();

        ImageIO.write(out, "png", new File(output));

        return out;
    }
    
    public static BufferedImage getScreenImageFromDatFile(int screenId, String datFile, int level) throws IOException {
        int width = 20;
        int height = 11;
        final int TILE_WIDTH = 16;
        final int TILE_HEIGHT = 16;
        
        BufferedImage tilesForeground = ImageIO.read(Objects.requireNonNull(Main.class.getClassLoader().getResourceAsStream(String.format("tiles-foreground-%02d.png", level))));
        BufferedImage tilesBackground = ImageIO.read(Objects.requireNonNull(Main.class.getClassLoader().getResourceAsStream(String.format("tiles-background-%02d.png", level))));
        tilesForeground = getImageRGBMode(tilesForeground, Color.BLACK);
        
        byte[] tilemap = Files.readAllBytes(new File(datFile).toPath());
        
        int w = width;
        int h = height;
        BufferedImage out = new BufferedImage(w*TILE_WIDTH, h*TILE_HEIGHT, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = out.createGraphics();

        int baseOffset = screenId * ((width*height)*2) + (screenId * 8 * 20);
        System.out.printf("Base offset: %d\t%s%n", screenId, Integer.toHexString(baseOffset));
        //baseOffset = 0x258;
        
        for (int y = 0; y < h; y++)
            for (int x = 0; x < w; x++) {

                int offsetBg = baseOffset + y * w + x;
                int offsetFg = (baseOffset + 0xDC) + y * w + x;
                
                int b = tilemap[offsetBg] & 0xFF;
                
                if (b<0xFF) {
                    BufferedImage tile = tilesBackground.getSubimage((b % 20) * TILE_WIDTH, (b / 20) * TILE_HEIGHT, TILE_WIDTH, TILE_HEIGHT);
                    g2d.drawImage(tile, x * (TILE_WIDTH), y * TILE_HEIGHT, TILE_WIDTH, TILE_HEIGHT, null);
                }
                b = tilemap[offsetFg] & 0xFF;
                if (b>0) {
                    BufferedImage tile = tilesForeground.getSubimage((b % 20) * TILE_WIDTH, (b / 20) * TILE_HEIGHT, TILE_WIDTH, TILE_HEIGHT);
                    
                    g2d.drawImage(tile, x * (TILE_WIDTH), y * TILE_HEIGHT, TILE_WIDTH, TILE_HEIGHT, null);
                }
            }

        g2d.dispose();
        return out;
    }
    
    public static BufferedImage getImageRGBMode(BufferedImage imageIndexedColorMode, Color transparentColor) {
        BufferedImage dest = new BufferedImage(
                imageIndexedColorMode.getWidth(), imageIndexedColorMode.getHeight(),
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = dest.createGraphics();
        g2.drawImage(imageIndexedColorMode, 0, 0, null);
        g2.dispose();
        return addTransparentColor(dest, transparentColor);
    }

    private static BufferedImage addTransparentColor(BufferedImage image, Color transparentColor)
    {
        ImageFilter filter = new RGBImageFilter()
        {
            public final int filterRGB(int x, int y, int rgb)
            {
                if (rgb == transparentColor.getRGB()) return (rgb << 8) & 0xFF000000;
                return rgb;
            }
        };

        ImageProducer ip = new FilteredImageSource(image.getSource(), filter);
        return imageToBufferedImage(Toolkit.getDefaultToolkit().createImage(ip), image.getWidth(), image.getHeight());
    }

    private static BufferedImage imageToBufferedImage(Image image, int width, int height)
    {
        BufferedImage dest = new BufferedImage(
                width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = dest.createGraphics();
        g2.drawImage(image, 0, 0, null);
        g2.dispose();
        return dest;
    }

    public static void testAllPalettes(String image) {
        try {
            Files.list(Path.of(Main.class.getClassLoader().getResource("pal").toURI())).forEach(
                    i -> {
                        try {
                            generateTilesPalette(image, "pal/"+i.getFileName(), "output/test/"+image+"-"+i.getFileName()+".png");
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
            );
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void generateTitleFont() throws IOException {
        BufferedImage titleFont = getTitleFont(Color.WHITE, Color.BLACK);
        ImageIO.write(titleFont, "png", new File("output/font.png"));
    }
    
    public static BufferedImage getTitleFont(Color textColor, Color backgroundColor) throws IOException {
        byte[] data = Files.readAllBytes(new File(GAME_DISK+"\\PROGPRI2.PRG").toPath());
        int characterCount = 54;
        int size = 32;
        int start = 0x4C90;
        int end = start + characterCount*0x80;

        BufferedImage out = new BufferedImage(characterCount*size, size, BufferedImage.TYPE_INT_ARGB);
        
        int offset = start;
        int character = 0;
        while (offset<end) {
            System.out.printf("Char %d\t%s%n", character, Integer.toHexString(offset));
            int line = 0;
            while (line<size) {
                long l = readBytesBE(data, offset);
                String binary = Long.toBinaryString(l);
                while (binary.length()<size) binary = "0"+binary;
                int x = character*size;
                int y = line;
                for (char c : binary.toCharArray()) {
                    if (c=='1') {
                        out.setRGB(x, y, textColor.getRGB());
                    } else
                        out.setRGB(x, y, backgroundColor.getRGB());
                    x++;
                }
                line++;
                offset+=4;
            }
            character++;
        }
        //System.out.println(Long.toBinaryString(l));
        
        
        //out.setRGB();

        
        return out;
    }
    
    public static long readBytesBE(byte[] data, int offset) {
        return (data[offset+3] & 0xFF) + ((data[offset+2] & 0xFF)*0x100) + ((data[offset+1] & 0xFF)*0x10000) + ((data[offset] & 0xFF)* 0x1000000L);
    }
    
    public static void stxPatch() throws IOException {
        String input = "D:\\emulation\\atari\\dls\\AtariMania dumps\\Dragon Lord - 16-32 Diffusion.stx";

        byte[] data = Files.readAllBytes(new File(input).toPath());
        
        //fix bug SEVEN, EIGHT, NINE, TEN
        String hex = "0D 00 04 08 06 07 13 00 0D 08 0D 04 13 04 0D 00 04 0B 04 15 04 0D 13 16 04 0B 15 04";
        writeBytes(Utils.parseHex(hex), data, 0x2190A);
        
        //fix bug "THE END"
        hex = "4E 71";
        writeBytes(Utils.parseHex(hex), data, 0x12765A);


        //bug palette final stage
        int offsetJSR = 0x121BC;
        hex = "4E B9 00 01 19 A2 4E 71 4E 71";
        writeBytes(Utils.parseHex(hex), data, offsetJSR);
        int offsetCode = 0x12D431; //Atari mem: 119A2
        hex = "41 F9 00 05 E9 A0 22 7C 00 04 EF A0 4E 75";
        writeBytes(Utils.parseHex(hex), data, offsetCode);

        //bug palette ending
        offsetCode += 0x12; //Atari mem: 119A2 + 0x12 = 119B4
        hex = "48 79 00 07 7F E0 3F 3C 00 06 4E 4E 5C 8F 43 F9 00 03 BB 28 4E 75";
        writeBytes(Utils.parseHex(hex), data, offsetCode);
        offsetJSR = 0x127588;
        hex = "4E B9 00 01 19 B4";
        writeBytes(Utils.parseHex(hex), data, offsetJSR);
        
        
        //bug transition stage 3-4
        hex = "0C 04 00 05"; // CMPI.B #$5, D4
        writeBytes(Utils.parseHex(hex), data, 0x1277D8);
        
        String output = "D:\\emulation\\atari\\dragonlord-mapping\\output\\stx\\DragonLord Fix Final.stx";
        
        saveData(output, data);
    }
    
    public static void introText() throws IOException {
        final int TILE_WIDTH = 32;
        final int TILE_HEIGHT = 32;

        BufferedImage font = getTitleFont(Color.WHITE, Color.BLACK);

        List<String> textFile = loadTextFile("intro.txt");
        int longestLine = 0;
        for (String s : textFile) {
            longestLine = Math.max(longestLine, s.length());
        }
        
        BufferedImage out = new BufferedImage((longestLine)*TILE_WIDTH, textFile.size()*TILE_HEIGHT, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = out.createGraphics();

        int y = 0;
        for (String line : textFile) {
            int x = 0;
            for (char c : line.toCharArray()) {
                    int i = asciiToDragonlord(c);
                    BufferedImage tile = font.getSubimage(i * 32, 0, TILE_WIDTH, TILE_HEIGHT);
                    g2d.drawImage(tile, x * TILE_WIDTH, y * TILE_HEIGHT, TILE_WIDTH, TILE_HEIGHT, null);
                    x++;
            }
            y++;
            x=0;
        }
        

        g2d.dispose();
        ImageIO.write(out, "png", new File("output/intro.png"));
    }
    
    public static int asciiToDragonlord(int i) {
        if (i<=0x09) return (i);
        if (i>=0x41 && i<=0x5A) return (i-0x37);
        switch (i) {
            case 0x2E: return 0x25;
            case 0x3A: return 0x26;
            case 0x2C: return 0x27;
            case 0x3B: return 0x28;
            case 0x21: return 0x29;
            case 0x28: return 0x2A;
            case 0x29: return 0x2B;
            case 0x3C: return 0x2C;
            case 0x3E: return 0x2D;
            case 0x2B: return 0x2E;
            case 0x2D: return 0x2F;
            case 0x2A: return 0x30;
            case 0x2F: return 0x31;
            case 0x27: return 0x32;
            case 0x3F: return 0x33;
            case 0x34: return 0x34; // fnac logo - unused
            case 0x35: return 0x35; // Thy & Jo logo
        }
        return 0x24;
    } 
    
    public static void parseTrace(String file) throws IOException {
        System.out.println("PARSING TRACE FILE "+file);
        BufferedReader br = new BufferedReader(new FileReader(file));

        /*String[] skip = {
        "jsr $fc9eae",
        "jsr $fca694",
        "jsr (A0)",
                "jsr $fc7d44",
                "jsr $fc6bfe",
                "jsr $fc58ac",
                "jsr $fc91fe"
        };*/
        
        String last = "";
        int count = 0;
        
        Map<String, Integer> totalCount = new HashMap<>();
        
        String line = "";
        while ((line=br.readLine())!=null) {
            int i = line.indexOf("jsr");
            if (i>0) {
                String jsr = line.substring(i);
                if (totalCount.containsKey(jsr)) totalCount.put(jsr, totalCount.get(jsr)+1);
                else totalCount.put(jsr, 1);
                //if (Arrays.asList(skip).contains(jsr)) continue;
                if (last.isEmpty()) {
                    last = jsr;
                    count = 1;
                } else if (jsr.equals(last)) count++;
                else {
                    //System.out.printf("%s\t\t%d%n", last, count);
                    last = jsr;
                    count = 1;
                }
            }
        }

        for (Map.Entry<String, Integer> e : totalCount.entrySet()) {
            System.out.println(e.getKey()+"\t\t"+e.getValue());
        }

    }

}
