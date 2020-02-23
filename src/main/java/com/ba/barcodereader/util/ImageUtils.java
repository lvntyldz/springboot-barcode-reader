package com.ba.barcodereader.util;

import java.awt.image.BufferedImage;

public class ImageUtils {

    public static boolean isWhiteColor(BufferedImage subimage, int x, int y, int rgbThreshold) {
        int clr = subimage.getRGB(x, y);
        int red = (clr & 0x00ff0000) >> 16;
        int green = (clr & 0x0000ff00) >> 8;
        int blue = clr & 0x000000ff;

        return isWhiteColorRange(red, green, blue, rgbThreshold);
    }

    private static boolean isWhiteColorRange(int red, int green, int blue, int rgbThreshold) {
        return (red > rgbThreshold && green > rgbThreshold && blue > rgbThreshold);
    }
}
