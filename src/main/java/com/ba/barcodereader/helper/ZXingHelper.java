package com.ba.barcodereader.helper;

import com.ba.barcodereader.enums.Dimensions;
import com.ba.barcodereader.dto.BarcodeDTO;
import com.ba.barcodereader.props.Config;
import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;

import javax.imageio.ImageIO;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;

public class ZXingHelper {

    private ZXingHelper() {
    }

    public static BarcodeDTO searchWhiteFrameInMainImage(BufferedImage image) {
        int height = image.getHeight();
        int width = image.getWidth();
        BarcodeDTO response = new BarcodeDTO();

        outerloop:
        for (int y = 0; y < height; y = y + 10) {
            for (int x = 0; x < width; x = x + 10) {

                if (x + Dimensions.SUB_IMAGE_DIMENSION.getVal() > width || y + Dimensions.SUB_IMAGE_DIMENSION.getVal() > height) {
                    continue;
                }

                BufferedImage subimage = image.getSubimage(x, y, Dimensions.SUB_IMAGE_DIMENSION.getVal(), Dimensions.SUB_IMAGE_DIMENSION.getVal());
                response = checkWhiteFrameAndwriteToFile(subimage);
                if (response.isReadSuccessfully()) {
                    break outerloop;
                }
            }
        }

        return response;
    }

    private static BarcodeDTO checkWhiteFrameAndwriteToFile(BufferedImage subimage) {

        boolean isHasWhiteFrame = true;
        int subimageHeight = subimage.getHeight();
        int subimageWidth = subimage.getWidth();

        for (int i = 0; i < subimageWidth; i++) {
            for (int j = 0; j < subimageHeight; j++) {

                if ((i == 0 || j == 0 || i + 1 == subimageWidth || j + 1 == subimageHeight) && !ImageHelper.isWhiteColor(subimage, i, j, Dimensions.RGB_WHITE_THRESHOLD.getVal())) {
                    isHasWhiteFrame = false;
                    break;
                }
            }
        }

        BarcodeDTO response = new BarcodeDTO();

        if (isHasWhiteFrame) {
            String subImagePath = FileHelper.writeToTempAsJpg(subimage, Config.WHITE_FRAME_IMG_NAME);
            response = tryReadDataMatrix(subImagePath);
        }

        return response;

    }

    private static BarcodeDTO tryReadDataMatrix(String filePath) {
        BarcodeDTO response = new BarcodeDTO();
        BufferedImage before = null;
        Map<DecodeHintType, Object> hintsMap;
        hintsMap = new EnumMap<DecodeHintType, Object>(DecodeHintType.class);
        hintsMap.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
        hintsMap.put(DecodeHintType.POSSIBLE_FORMATS, EnumSet.allOf(BarcodeFormat.class));

        try {
            before = ImageIO.read(new File(filePath));
            response = decode(before, hintsMap);
            if (response.isReadSuccessfully()) {
                return response;
            }

            for (int i = -100; i < 100; i++) {
                AffineTransform transform = new AffineTransform();
                double rad = (double) i / 100;
                transform.rotate(rad, before.getWidth() / 2, before.getHeight() / 2);
                BufferedImage after = new BufferedImage(before.getWidth(), before.getHeight(), BufferedImage.TYPE_INT_ARGB);
                AffineTransformOp op = new AffineTransformOp(transform, AffineTransformOp.TYPE_BILINEAR);
                after = op.filter(before, after);
                response = decode(after, hintsMap);
                if (response.isReadSuccessfully()) {
                    break;
                }
            }

        } catch (IOException e) {
            //log.error("Something went wrong while rotating datamatrix! e:{}", e);
        }
        return response;
    }

    private static BarcodeDTO decode(BufferedImage tmpBfrImage, Map<DecodeHintType, Object> hintsMap) {
        if (tmpBfrImage == null)
            throw new IllegalArgumentException("Could not decode image.");

        LuminanceSource tmpSource = new BufferedImageLuminanceSource(tmpBfrImage);
        BinaryBitmap tmpBitmap = new BinaryBitmap(new HybridBinarizer(tmpSource));
        MultiFormatReader tmpBarcodeReader = new MultiFormatReader();

        Result tmpResult;
        String tmpFinalResult;
        BarcodeDTO barcodeDto = new BarcodeDTO();

        try {

            if (hintsMap != null && !hintsMap.isEmpty()) {
                tmpResult = tmpBarcodeReader.decode(tmpBitmap, hintsMap);
            } else {
                tmpResult = tmpBarcodeReader.decode(tmpBitmap);
            }

            // setting results.
            tmpFinalResult = String.valueOf(tmpResult.getText());
            barcodeDto = new BarcodeDTO(true, Arrays.asList(tmpFinalResult));

        } catch (Exception e) {
            //log.error("Something went wrong while decoding datamatrix! e:{} ", e);
        }

        return barcodeDto;
    }

}
