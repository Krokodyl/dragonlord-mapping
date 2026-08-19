package main;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Utils {


    public static byte b(String s) {
        return (byte) Integer.parseInt(s,16);
    }

    public static int x(String s) {
        return Integer.parseInt(s,16);
    }

    public static String h(int i) {
        return padLeft(Integer.toHexString(i).toUpperCase(), '0',5);
    }

    public static String h(long i) {
        return padLeft(Long.toHexString(i).toUpperCase(), '0',5);
    }

    public static String h4(int i) {
        return padLeft(Integer.toHexString(i).toUpperCase(), '0',4);
    }

    public static String h2(int i) {
        return padLeft(Integer.toHexString(i).toUpperCase(), '0',2);
    }

    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 3];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 3] = HEX_ARRAY[v >>> 4];
            hexChars[j * 3 + 1] = HEX_ARRAY[v & 0x0F];
            hexChars[j * 3 + 2] = ' ';
        }
        return new String(hexChars);
    }

    public static String padLeft(String s,char c,int length) {
        while (s.length()<length) {
            s=c+s;
        }
        return s;
    }

    public static List<String> loadTextFile(String filename) {
        List<String> lines = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(
                            Objects.requireNonNull(Utils.class.getClassLoader().getResourceAsStream(filename)), StandardCharsets.UTF_8));
            String line = br.readLine();
            while (line!=null) {
                lines.add(line);
                line = br.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }

    public static void saveData(String output, byte[] data) {
        System.out.println("Saving data : "+output);
        FileOutputStream stream = null;
        try {
            stream = new FileOutputStream(output);
            stream.write(data);
            stream.flush();
            stream.close();
        } catch (IOException ex) {
            Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (stream != null) {
                try {
                    stream.flush();
                    stream.close();
                } catch (IOException ex) {
                    Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    public static String getHexString(byte[] bytes) {
        if (bytes==null) return "";
        char[] hexChars = new char[bytes.length * 3];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 3] = HEX_ARRAY[v >>> 4];
            hexChars[j * 3 + 1] = HEX_ARRAY[v & 0x0F];
            hexChars[j * 3 + 2] = ' ';
        }
        return new String(hexChars);
    }

    /**
     * Parses a space separated hexadecimal representation of a byte array
     * @param hexValues
     * @return
     */
    public static byte[] parseHex(String hexValues) {
        String[] s1 = hexValues.split(" ");
        byte[] bytes = new byte[s1.length];
        for (int i = 0; i < s1.length; i++) {
            bytes[i] = (byte) (Integer.parseInt(s1[i],16) & 0xFF);
        }
        return bytes;
    }

    public static void writeBytes(byte[] source, byte[] target, int targetOffset) {
        for (byte b : source) {
            target[targetOffset++]=b;
        }
    }
    
}
