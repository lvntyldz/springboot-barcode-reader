package com.ba.barcodereader.helper;

import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

@Component
public class ImageHelper {

    public void displayScrollableImage(BufferedImage image) {
        JFrame frame = new JFrame();
        ImageIcon ii = new ImageIcon(image);
        JScrollPane jsp = new JScrollPane(new JLabel(ii));
        frame.getContentPane().add(jsp);
        frame.setSize(800, 800);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    public void displayImage(BufferedImage image) {

        JFrame frame = new JFrame();
        frame.getContentPane().setLayout(new FlowLayout());
        frame.getContentPane().add(new JLabel(new ImageIcon(image)));
        frame.pack();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}