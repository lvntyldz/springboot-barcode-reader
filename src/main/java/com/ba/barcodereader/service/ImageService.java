package com.ba.barcodereader.service;


import com.ba.barcodereader.enums.Dim;
import com.ba.barcodereader.util.FileUtils;
import com.ba.barcodereader.util.ImageUtils;
import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
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

    private static Map<DecodeHintType, Object> hintsMap;

    public void searchWhiteFrameInMainImage(BufferedImage image) throws IOException {
        int height = image.getHeight();
        int width = image.getWidth();

        for (int y = 0; y < height; y = y + 15) {
            for (int x = 0; x < width; x = x + 15) {

                if (x + Dim.SUB_IMAGE_DIMENSION.getVal() > width || y + Dim.SUB_IMAGE_DIMENSION.getVal() > height) {
                    continue;
                }

                BufferedImage subimage = image.getSubimage(x, y, Dim.SUB_IMAGE_DIMENSION.getVal(), Dim.SUB_IMAGE_DIMENSION.getVal());
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

                if ((i == 0 || j == 0 || i + 1 == subimageWidth || j + 1 == subimageHeight) && !ImageUtils.isWhiteColor(subimage, i, j, Dim.RGB_WHITE_THRESHOLD.getVal())) {
                    isHasWhiteFrame = false;
                    break;
                }
            }
        }

        if (isHasWhiteFrame) {
            ImageUtils.displayScrollableImage(subimage);
            String subImagePath = FileUtils.writeToTargetAsJpg(subimage, "image-" + x + "-" + y);
            tryReadDataMatrix(subImagePath);
        }
    }

    private void tryReadDataMatrix(String filePath) {

        if (1 == 1) {
            //return;
        }

        //TODO:Remove
        //filePath = "/Users/leventyildiz/development/git/tofas/barcodeDetection/target/image-45-360.jpg";

        Map<DecodeHintType, Object> hintsMap;
        BufferedImage before = null;
        hintsMap = new EnumMap<DecodeHintType, Object>(DecodeHintType.class);
        hintsMap.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
        hintsMap.put(DecodeHintType.POSSIBLE_FORMATS, EnumSet.allOf(BarcodeFormat.class));
        //hintsMap.put(DecodeHintType.POSSIBLE_FORMATS, EnumSet.of(BarcodeFormat.DATA_MATRIX));
        //hintsMap.put(DecodeHintType.PURE_BARCODE, Boolean.FALSE);
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

            //tmpBfrImage = tmpBfrImage.getSubimage(200, 100, 800, 800);
        } catch (IOException tmpIoe) {
            tmpIoe.printStackTrace();
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

}
