package com.ba.barcodereader.service;


import com.ba.barcodereader.enums.Dimensions;
import com.ba.barcodereader.exception.SystemException;
import com.ba.barcodereader.helper.FileHelper;
import com.ba.barcodereader.helper.ImageHelper;
import com.ba.barcodereader.helper.RegexHelper;
import com.ba.barcodereader.helper.TesseractHelper;
import com.ba.barcodereader.model.BarcodeModel;
import com.ba.barcodereader.props.Config;
import com.ba.barcodereader.util.ImageUtils;
import com.google.cloud.vision.v1.*;
import com.google.protobuf.ByteString;
import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;


@Service
@Slf4j
public class ImageService {

    public List<String> readBarcodeWithTesseractFromScannedImageVia() {
        BufferedImage subimage = ImageHelper.readScannedImageGetTesseractPart(false);
        ImageHelper.convertToBlackWhite(subimage);

        String text = TesseractHelper.getTextFromImage(subimage);
        String actualData = RegexHelper.findCartNumberWithRegex(text);
        log.info("Final Actual Data : {}", actualData);

        if (actualData != null) {
            return Arrays.asList(actualData);
        }

        List<String> datas = TesseractHelper.getFinalDataByLength(text);
        log.info("Final datas : {}", datas);

        return datas;
    }

    private String detectAllTextFromGivenImage(String filePath) {

        StringBuilder stringBuilder = new StringBuilder();

        List<AnnotateImageRequest> requests = new ArrayList<>();

        ByteString imgBytes = null;
        try {
            imgBytes = ByteString.readFrom(new FileInputStream(filePath));
        } catch (IOException e) {
            log.error("Something went wrong while reading image file! File path : {} - e: {} ", filePath, e);
            throw new SystemException("Read image file failed!");
        }

        Image img = Image.newBuilder().setContent(imgBytes).build();
        Feature feat = Feature.newBuilder().setType(Feature.Type.TEXT_DETECTION).build();
        AnnotateImageRequest request = AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img).build();
        requests.add(request);

        try (ImageAnnotatorClient client = ImageAnnotatorClient.create()) {
            BatchAnnotateImagesResponse response = client.batchAnnotateImages(requests);
            List<AnnotateImageResponse> responses = response.getResponsesList();

            for (AnnotateImageResponse res : responses) {
                if (res.hasError()) {
                    log.error("ResponseList has error! error : {} ", res.getError().getMessage());
                    return null;
                }

                // For full list of available annotations, see http://g.co/cloud/vision/docs
                for (EntityAnnotation annotation : res.getTextAnnotationsList()) {
                    String textFormat = String.format("Text: %s\n", annotation.getDescription());
                    String ppositionFormat = String.format("Position : %s\n", annotation.getBoundingPoly());

                    stringBuilder.append(textFormat);
                    stringBuilder.append(ppositionFormat);
                }
            }
        } catch (IOException e) {
            log.error("Something went wrong while reading image with google cloud vision!  e: {} ", e);
            throw new SystemException("Google vision not read file!");
        }

        return stringBuilder.toString();
    }

    public List<String> readBarcodeWithGoogleVisionFromScannedImage() {
        BufferedImage image = ImageHelper.readScannedImageGetHeaderPart(false);

        FileHelper.writeToTempAsJpg(image, Config.CROP_IMG_NAME);

        String imageTexts = detectAllTextFromGivenImage(FileHelper.getCroppedImgPath());

        String cardNumber = RegexHelper.findCartNumberWithRegex(imageTexts);

        log.info("Final data : {} ", cardNumber);

        return Arrays.asList(cardNumber);
    }

    public List<String> readBarcodeWithZXingFromScannedImage() {

        BufferedImage image = ImageHelper.readScannedImageGetBarcodePart(false);

        FileHelper.writeToTempAsJpg(image, Config.CROP_IMG_NAME);
        BarcodeModel response = searchWhiteFrameInMainImage(image);
        return response.getDataList();
    }

    private BarcodeModel searchWhiteFrameInMainImage(BufferedImage image) {
        int height = image.getHeight();
        int width = image.getWidth();
        BarcodeModel response = new BarcodeModel();

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

    private BarcodeModel checkWhiteFrameAndwriteToFile(BufferedImage subimage) {

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

        BarcodeModel response = new BarcodeModel();

        if (isHasWhiteFrame) {
            String subImagePath = FileHelper.writeToTempAsJpg(subimage, Config.WHITE_FRAME_IMG_NAME);
            response = tryReadDataMatrix(subImagePath);
        }

        return response;

    }

    private BarcodeModel tryReadDataMatrix(String filePath) {
        BarcodeModel response = new BarcodeModel();
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
                log.info("Rotating by rad value. rad : {}", rad);
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
            log.error("Something went wrong while rotating datamatrix! e:{}", e);
        }
        return response;
    }

    public static BarcodeModel decode(BufferedImage tmpBfrImage, Map<DecodeHintType, Object> hintsMap) {
        if (tmpBfrImage == null)
            throw new IllegalArgumentException("Could not decode image.");

        LuminanceSource tmpSource = new BufferedImageLuminanceSource(tmpBfrImage);
        BinaryBitmap tmpBitmap = new BinaryBitmap(new HybridBinarizer(tmpSource));
        MultiFormatReader tmpBarcodeReader = new MultiFormatReader();

        Result tmpResult;
        String tmpFinalResult;
        BarcodeModel barcodeModel = new BarcodeModel();

        try {

            if (hintsMap != null && !hintsMap.isEmpty()) {
                tmpResult = tmpBarcodeReader.decode(tmpBitmap, hintsMap);
            } else {
                tmpResult = tmpBarcodeReader.decode(tmpBitmap);
            }

            // setting results.
            tmpFinalResult = String.valueOf(tmpResult.getText());
            barcodeModel = new BarcodeModel(true, Arrays.asList(tmpFinalResult));

        } catch (Exception e) {
            log.error("Something went wrong while decoding datamatrix! e:{} ", e);
        }

        return barcodeModel;
    }
}
