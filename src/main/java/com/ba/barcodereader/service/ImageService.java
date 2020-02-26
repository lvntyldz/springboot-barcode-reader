package com.ba.barcodereader.service;


import com.ba.barcodereader.dto.BarcodeDTO;
import com.ba.barcodereader.helper.*;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;


@Service
public class ImageService {

    public List<String> readBarcodeWithTesseractFromScannedImageVia() {
        BufferedImage subimage = ImageHelper.readScannedImageGetTesseractPart(false);
        ImageHelper.convertToBlackWhite(subimage);

        String text = TesseractHelper.getTextFromImage(subimage);
        String actualData = RegexHelper.findCartNumberWithRegex(text);

        if (actualData != null) {
            return Arrays.asList(actualData);
        }

        List<String> dataList = TesseractHelper.getFinalDataByLength(text);

        return dataList;
    }

    public List<String> readBarcodeWithGoogleVisionFromScannedImage() {
        ImageHelper.readScannedImageGetHeaderPart(false);

        String imageTexts = GVisionHelper.detectAllTextFromGivenImage(FileHelper.getCroppedImgPath());

        String cardNumber = RegexHelper.findCartNumberWithRegex(imageTexts);

        return Arrays.asList(cardNumber);
    }

    public List<String> readBarcodeWithZXingFromScannedImage() {
        BufferedImage image = ImageHelper.readScannedImageGetBarcodePart(false);
        BarcodeDTO response = ZXingHelper.searchWhiteFrameInMainImage(image);
        return response.getDataList();
    }
}
