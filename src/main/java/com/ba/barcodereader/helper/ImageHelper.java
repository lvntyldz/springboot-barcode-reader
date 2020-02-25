package com.ba.barcodereader.helper;

import com.ba.barcodereader.enums.Dimensions;
import com.ba.barcodereader.exception.SystemException;
import com.ba.barcodereader.model.DimensionModel;
import com.ba.barcodereader.props.Config;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;

@Component
@Slf4j
public class ImageHelper {

    @Autowired
    FileHelper fileHelper;

    public BufferedImage readScannedImageGetHeaderPart(final boolean rotate) {

        BufferedImage image = getReadAndRotateImage(rotate);
        DimensionModel dim = prepareHeaderImageDimensionBy(image);

        image = image.getSubimage(dim.getXPoint(), dim.getYPoint(), dim.getWidth(), dim.getHeight());

        return image;
    }

    private BufferedImage getReadAndRotateImage(boolean rotate) {
        BufferedImage image = null;

        try {
            image = ImageIO.read(new FileInputStream(Config.SCANNED_FILE_PATH));
            fileHelper.writeToTempAsJpg(image, "originalImage");

            if (rotate) {
                image = rotateImage(image, 90);
                fileHelper.writeToTempAsJpg(image, "rotatedImage");
            }
        } catch (IOException e) {
            log.error("File could not not read! e:{} ", e);
            throw new SystemException("Could not read file");
        }
        return image;
    }

    private DimensionModel prepareHeaderImageDimensionBy(BufferedImage image) {

        int x = Dimensions.HEADER_FRAME_X.getVal();
        int w = Dimensions.HEADER_FRAME_W.getVal();
        int y = Dimensions.HEADER_FRAME_Y.getVal();
        int h = Dimensions.HEADER_FRAME_H.getVal();

        return getImageDimensionBy(image, x, w, y, h);
    }

    private DimensionModel prepareBarcodeImageDimensionBy(BufferedImage image) {

        int x = Dimensions.BARCODE_FRAME_X.getVal();
        int w = Dimensions.BARCODE_FRAME_W.getVal();
        int y = Dimensions.BARCODE_FRAME_Y.getVal();
        int h = Dimensions.BARCODE_FRAME_H.getVal();

        return getImageDimensionBy(image, x, w, y, h);
    }

    private DimensionModel prepareTesseractImageDimensionBy(BufferedImage image) {

        int x = Dimensions.TESSERACT_FRAME_X.getVal();
        int w = Dimensions.TESSERACT_FRAME_W.getVal();
        int y = Dimensions.TESSERACT_FRAME_Y.getVal();
        int h = Dimensions.TESSERACT_FRAME_H.getVal();

        return getImageDimensionBy(image, x, w, y, h);
    }

    private DimensionModel getImageDimensionBy(BufferedImage image, int x, int w, int y, int h) {
        if (x + w > image.getWidth() && y + h > image.getHeight()) {
            log.warn("Aranan resim genişliği ve yüksekliği mevcut resimden daha büyük!");
            return new DimensionModel(x, y, Math.abs((image.getWidth() - x)), Math.abs((image.getHeight() - y)));
        }

        if (x + w > image.getWidth()) {
            log.warn("Aranan resim genişliği mevcut resimden daha büyük!");
            return new DimensionModel(x, y, Math.abs((image.getWidth() - x)), h);
        }

        if (y + h > image.getHeight()) {
            log.warn("Aranan resim yükseklği mevcut resimden daha büyük!");
            return new DimensionModel(x, y, w, Math.abs((image.getHeight() - y)));
        }

        return new DimensionModel(x, y, w, h);
    }

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

    public BufferedImage readScannedImageGetBarcodePart(final boolean rotate) {

        BufferedImage image = getReadAndRotateImage(rotate);
        DimensionModel dim = prepareBarcodeImageDimensionBy(image);

        image = image.getSubimage(dim.getXPoint(), dim.getYPoint(), dim.getWidth(), dim.getHeight());
        //imageHelper.displayScrollableImage(image);

        return image;
    }

    public BufferedImage readScannedImageGetTesseractPart(boolean rotate) {

        BufferedImage image = getReadAndRotateImage(rotate);
        DimensionModel dim = prepareTesseractImageDimensionBy(image);

        image = image.getSubimage(dim.getXPoint(), dim.getYPoint(), dim.getWidth(), dim.getHeight());

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
