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

    public List<String> readBarcodeWithGoogleVisionFromScannedImage() {
        ImageHelper.readScannedImageGetHeaderPart(false);

        String imageTexts = GVisionHelper.detectAllTextFromGivenImage(FileHelper.getCroppedImgPath());

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
