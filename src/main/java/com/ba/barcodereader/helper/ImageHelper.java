package com.ba.barcodereader.helper;

import com.ba.barcodereader.enums.Dimensions;
import com.ba.barcodereader.props.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;

@Component
public class ImageHelper {

    @Autowired
    FileHelper fileHelper;

    public BufferedImage rotateImage(BufferedImage image, double degrees) {

        final double rads = Math.toRadians(90);
        final double sin = Math.abs(Math.sin(rads));
        final double cos = Math.abs(Math.cos(rads));
        final int w = (int) Math.floor(image.getWidth() * cos + image.getHeight() * sin);
        final int h = (int) Math.floor(image.getHeight() * cos + image.getWidth() * sin);
        final BufferedImage rotatedImage = new BufferedImage(w, h, image.getType());
        final AffineTransform at = new AffineTransform();
        at.translate(w / 2, h / 2);
        at.rotate(rads, 0, 0);
        at.translate(-image.getWidth() / 2, -image.getHeight() / 2);
        final AffineTransformOp rotateOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
        BufferedImage finalBufferedImage = rotateOp.filter(image, rotatedImage);

        return finalBufferedImage;
    }

    public BufferedImage readScannedImageGetBarcodePart() throws Exception {
        BufferedImage image = ImageIO.read(new FileInputStream(Config.SCANNED_FILE_PATH));
        fileHelper.writeToTargetAsJpg(image, "originalImage");

        //image = rotateImage(image, 90);
        //fileHelper.writeToTargetAsJpg(image, "rotatedImage");

        if (Dimensions.BARCODE_FRAME_X.getVal() + Dimensions.BARCODE_FRAME_W.getVal() > image.getWidth() || Dimensions.BARCODE_FRAME_Y.getVal() + Dimensions.BARCODE_FRAME_H.getVal() > image.getHeight()) {
            throw new Exception("Aranacak barcode ölçüleri resimden daha büyük!");//TODO
        }

        image = image.getSubimage(Dimensions.BARCODE_FRAME_X.getVal(), Dimensions.BARCODE_FRAME_Y.getVal(), Dimensions.BARCODE_FRAME_W.getVal(), Dimensions.BARCODE_FRAME_H.getVal());
        //imageHelper.displayScrollableImage(image);
        return image;
    }

    public BufferedImage readScannedImageGetHeaderPart() throws Exception {
        BufferedImage image = ImageIO.read(new FileInputStream(Config.SCANNED_FILE_PATH));
        fileHelper.writeToTargetAsJpg(image, "originalImage");

        //image = rotateImage(image, 90);
        //fileHelper.writeToTargetAsJpg(image, "rotatedImage");

        if (Dimensions.HEADER_FRAME_X.getVal() + Dimensions.HEADER_FRAME_W.getVal() > image.getWidth() || Dimensions.HEADER_FRAME_Y.getVal() + Dimensions.HEADER_FRAME_H.getVal() > image.getHeight()) {
            throw new Exception("Aranacak barcode ölçüleri resimden daha büyük!");//TODO
        }

        image = image.getSubimage(Dimensions.HEADER_FRAME_X.getVal(), Dimensions.HEADER_FRAME_Y.getVal(), Dimensions.HEADER_FRAME_W.getVal(), Dimensions.HEADER_FRAME_H.getVal());
        //imageHelper.displayScrollableImage(image);
        return image;
    }

    public BufferedImage readScannedImageGetTesseractPart() throws Exception {
        BufferedImage image = ImageIO.read(new FileInputStream(Config.SCANNED_FILE_PATH));
        fileHelper.writeToTargetAsJpg(image, "originalImage");

        image = rotateImage(image, 90);
        fileHelper.writeToTargetAsJpg(image, "rotatedImage");

        if (Dimensions.TESSERACT_FRAME_X.getVal() + Dimensions.TESSERACT_FRAME_W.getVal() > image.getWidth() || Dimensions.TESSERACT_FRAME_Y.getVal() + Dimensions.TESSERACT_FRAME_H.getVal() > image.getHeight()) {
            throw new Exception("Aranacak TESSERACT ölçüleri resimden daha büyük!");//TODO
        }

        image = image.getSubimage(Dimensions.TESSERACT_FRAME_X.getVal(), Dimensions.TESSERACT_FRAME_Y.getVal(), Dimensions.TESSERACT_FRAME_W.getVal(), Dimensions.TESSERACT_FRAME_H.getVal());
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