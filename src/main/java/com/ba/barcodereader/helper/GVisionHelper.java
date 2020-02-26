package com.ba.barcodereader.helper;

import com.ba.barcodereader.exception.SystemException;
import com.google.cloud.vision.v1.*;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class GVisionHelper {

    private GVisionHelper() {
    }

    public static String detectAllTextFromGivenImage(String filePath) {

        StringBuilder stringBuilder = new StringBuilder();
        List<AnnotateImageRequest> requests = new ArrayList<>();
        Image img = ImageHelper.getImageFromGivenPath(filePath);

        Feature feat = Feature.newBuilder().setType(Feature.Type.TEXT_DETECTION).build();
        AnnotateImageRequest request = AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img).build();
        requests.add(request);

        return getStringFromImageAnnotatorClient(stringBuilder, requests);
    }

    private static String getStringFromImageAnnotatorClient(StringBuilder stringBuilder, List<AnnotateImageRequest> requests) {
        try (ImageAnnotatorClient client = ImageAnnotatorClient.create()) {
            BatchAnnotateImagesResponse response = client.batchAnnotateImages(requests);
            List<AnnotateImageResponse> responses = response.getResponsesList();

            for (AnnotateImageResponse res : responses) {
                if (res.hasError()) {
                    log.error("ResponseList has error! error : {} ", res.getError().getMessage());
                    return null;
                }

                appendResponseTextToResult(stringBuilder, res);
            }

        } catch (IOException e) {
            log.error("Something went wrong while reading image with google cloud vision!  e: {} ", e);
            throw new SystemException("Google vision not read file!");
        }

        return stringBuilder.toString();
    }

    private static void appendResponseTextToResult(StringBuilder stringBuilder, AnnotateImageResponse res) {
        for (EntityAnnotation annotation : res.getTextAnnotationsList()) {
            String textFormat = String.format("Text: %s\n", annotation.getDescription());
            String ppositionFormat = String.format("Position : %s\n", annotation.getBoundingPoly());

            stringBuilder.append(textFormat);
            stringBuilder.append(ppositionFormat);
        }
    }
}
