package com.ba.barcodereader.helper;

import com.ba.barcodereader.enums.Dimensions;
import com.ba.barcodereader.props.Config;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;

@Component
public class ImageHelper {

    public BufferedImage readScannedImageGetBarcodePart() throws Exception {
        BufferedImage image = ImageIO.read(new FileInputStream(Config.SCANNED_FILE_PATH));

        //image = imageService.rotateImage(image, 90);
        //fileHelper.writeToTargetAsJpg(image, "rotatedImage");

        if (Dimensions.BARCODE_FRAME_X.getVal() + Dimensions.BARCODE_FRAME_W.getVal() > image.getWidth() || Dimensions.BARCODE_FRAME_Y.getVal() + Dimensions.BARCODE_FRAME_H.getVal() > image.getHeight()) {
            throw new Exception("Aranacak barcode ölçüleri resimden daha büyük!");//TODO
        }

        image = image.getSubimage(Dimensions.BARCODE_FRAME_X.getVal(), Dimensions.BARCODE_FRAME_Y.getVal(), Dimensions.BARCODE_FRAME_W.getVal(), Dimensions.BARCODE_FRAME_H.getVal());
        //imageHelper.displayScrollableImage(image);
        return image;
    }

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