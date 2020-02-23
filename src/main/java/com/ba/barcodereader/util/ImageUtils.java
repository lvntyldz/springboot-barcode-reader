package com.ba.barcodereader.util;


import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class ImageUtils {

    public static void displayScrollableImage(BufferedImage image) {
        if(1==1){
            return;
        }

        JFrame frame = new JFrame();
        ImageIcon ii = new ImageIcon(image);
        JScrollPane jsp = new JScrollPane(new JLabel(ii));
        frame.getContentPane().add(jsp);
        frame.setSize(800, 800);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    public static void displayImage(BufferedImage image) {

        JFrame frame = new JFrame();
        frame.getContentPane().setLayout(new FlowLayout());
        frame.getContentPane().add(new JLabel(new ImageIcon(image)));
        frame.pack();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

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
