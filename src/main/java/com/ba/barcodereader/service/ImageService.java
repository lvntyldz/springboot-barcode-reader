package com.ba.barcodereader.service;


import com.ba.barcodereader.exception.SystemException;
import com.ba.barcodereader.helper.*;
import com.ba.barcodereader.model.BarcodeModel;
import com.google.cloud.vision.v1.*;
import com.google.protobuf.ByteString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


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

        List<String> dataList = TesseractHelper.getFinalDataByLength(text);
        log.info("Final dataList : {}", dataList);

        return dataList;
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
        ImageHelper.readScannedImageGetHeaderPart(false);

        String imageTexts = detectAllTextFromGivenImage(FileHelper.getCroppedImgPath());

        String cardNumber = RegexHelper.findCartNumberWithRegex(imageTexts);

        log.info("Final data : {} ", cardNumber);

        return Arrays.asList(cardNumber);
    }

    public List<String> readBarcodeWithZXingFromScannedImage() {
        BufferedImage image = ImageHelper.readScannedImageGetBarcodePart(false);
        BarcodeModel response = ZXingHelper.searchWhiteFrameInMainImage(image);
        return response.getDataList();
    }
}
