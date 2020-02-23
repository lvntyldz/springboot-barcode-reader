package com.ba.barcodereader.service;


import com.ba.barcodereader.enums.Dimensions;
import com.ba.barcodereader.helper.FileHelper;
import com.ba.barcodereader.helper.ImageHelper;
import com.ba.barcodereader.util.ImageUtils;
import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;

@Service
public class ImageService {

    @Autowired
    ImageHelper imageHelper;

    @Autowired
    FileHelper fileHelper;

    private static Map<DecodeHintType, Object> hintsMap;

    public void readBarcodeFromScannedImage() throws Exception {
        BufferedImage image = imageHelper.readScannedImageGetBarcodePart();

        fileHelper.writeToTargetAsJpg(image, "croppedImage");
        searchWhiteFrameInMainImage(image);
    }

    private void searchWhiteFrameInMainImage(BufferedImage image) throws IOException {
        int height = image.getHeight();
        int width = image.getWidth();

        for (int y = 0; y < height; y = y + 5) {
            for (int x = 0; x < width; x = x + 5) {

                if (x + Dimensions.SUB_IMAGE_DIMENSION.getVal() > width || y + Dimensions.SUB_IMAGE_DIMENSION.getVal() > height) {
                    continue;
                }

                BufferedImage subimage = image.getSubimage(x, y, Dimensions.SUB_IMAGE_DIMENSION.getVal(), Dimensions.SUB_IMAGE_DIMENSION.getVal());
                checkWhiteFrameAndwriteToFile(x, y, subimage);
            }
        }
    }

    private void checkWhiteFrameAndwriteToFile(int x, int y, BufferedImage subimage) throws IOException {

        boolean isHasWhiteFrame = true;
        int subimageHeight = subimage.getHeight();
        int subimageWidth = subimage.getWidth();

        for (int i = 0; i < subimageWidth; i++) {
            for (int j = 0; j < subimageHeight; j++) {

                if ((i == 0 || j == 0 || i + 1 == subimageWidth || j + 1 == subimageHeight) && !ImageUtils.isWhiteColor(subimage, i, j, Dimensions.RGB_WHITE_THRESHOLD.getVal())) {
                    isHasWhiteFrame = false;
                    break;
                }
            }
        }

        if (isHasWhiteFrame) {
            //imageHelper.displayScrollableImage(subimage);
            String subImagePath = fileHelper.writeToTargetAsJpg(subimage, "image-" + x + "-" + y);
            tryReadDataMatrix(subImagePath);
        }
    }

    private void tryReadDataMatrix(String filePath) {

        Map<DecodeHintType, Object> hintsMap;
        BufferedImage before = null;
        hintsMap = new EnumMap<DecodeHintType, Object>(DecodeHintType.class);
        hintsMap.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
        hintsMap.put(DecodeHintType.POSSIBLE_FORMATS, EnumSet.allOf(BarcodeFormat.class));

        try {
            before = ImageIO.read(new File(filePath));
            decode(before);
            for (int i = -100; i < 100; i++) {
                AffineTransform transform = new AffineTransform();
                double rad = (double) i / 100;
                double scale = (double) i / 100;
                System.out.println("rad " + scale);
                transform.rotate(rad, before.getWidth() / 2, before.getHeight() / 2);
                //transform.scale(scale, scale);
                BufferedImage after = new BufferedImage(before.getWidth(), before.getHeight(), BufferedImage.TYPE_INT_ARGB);
                AffineTransformOp op = new AffineTransformOp(transform, AffineTransformOp.TYPE_BILINEAR);
                after = op.filter(before, after);
                decode(after);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void decode(BufferedImage tmpBfrImage) {
        if (tmpBfrImage == null)
            throw new IllegalArgumentException("Could not decode image.");
        LuminanceSource tmpSource = new BufferedImageLuminanceSource(tmpBfrImage);
        BinaryBitmap tmpBitmap = new BinaryBitmap(new HybridBinarizer(tmpSource));
        MultiFormatReader tmpBarcodeReader = new MultiFormatReader();

        Result tmpResult;
        String tmpFinalResult;
        try {
            if (hintsMap != null && !hintsMap.isEmpty())
                tmpResult = tmpBarcodeReader.decode(tmpBitmap, hintsMap);
            else
                tmpResult = tmpBarcodeReader.decode(tmpBitmap);
            // setting results.
            tmpFinalResult = String.valueOf(tmpResult.getText());
            System.out.println(tmpFinalResult);
            System.exit(0);
            ;
        } catch (Exception tmpExcpt) {
            tmpExcpt.printStackTrace();
        }
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


}
