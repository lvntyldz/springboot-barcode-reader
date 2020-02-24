package com.ba.barcodereader.service;


import com.ba.barcodereader.enums.Dimensions;
import com.ba.barcodereader.helper.FileHelper;
import com.ba.barcodereader.helper.ImageHelper;
import com.ba.barcodereader.props.Config;
import com.ba.barcodereader.util.ImageUtils;
import com.google.cloud.vision.v1.*;
import com.google.protobuf.ByteString;
import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.*;


@Service
public class ImageService {

    @Autowired
    ImageHelper imageHelper;

    @Autowired
    FileHelper fileHelper;

    private static Map<DecodeHintType, Object> hintsMap;

    public void readBarcodeWithTesseractFromScannedImageVia() throws Exception {
        BufferedImage subimage = imageHelper.readScannedImageGetTesseractPart();

        fileHelper.writeToTargetAsJpg(subimage, "croppedImage");
        //searchWhiteFrameInMainImage(image);
        imageToBlackWhite(subimage);

        String text = getStringFromImage(subimage);
        List<String> data = getFinalDatas(text);
        System.out.println("final Data : " + data);
    }

    private static void imageToBlackWhite(BufferedImage subimage) {
        for (int xx = 0; xx < subimage.getWidth(); xx++) {
            for (int yy = 0; yy < subimage.getHeight(); yy++) {

                int clr = subimage.getRGB(xx, yy);
                int red = (clr & 0x00ff0000) >> 16;
                int green = (clr & 0x0000ff00) >> 8;
                int blue = clr & 0x000000ff;

                if (red > Dimensions.RGB_THRESHOLD.getVal() && green > Dimensions.RGB_THRESHOLD.getVal() && blue > Dimensions.RGB_THRESHOLD.getVal()) {
                    subimage.setRGB(xx, yy, Dimensions.WHITE_COLOR.getVal());
                    continue;
                }
                subimage.setRGB(xx, yy, Dimensions.BLACK_COLOR.getVal());
            }
        }
    }

    private static List<String> getFinalDatas(String text) {
        List<String> data = new ArrayList<>();

        String[] datasByNewLine = text.split("\n");

        for (String row : datasByNewLine) {
            String[] allData = row.split(" ");
            for (String s : allData) {
                s = s.replaceAll("\\D+", "");//remmove nonDigitData
                if (s.length() == 11 || s.length() == 12 || s.length() == 13) {
                    data.add(s);
                }
            }
        }

        return data;
    }

    private String getStringFromImage(BufferedImage subimage) throws TesseractException {
        Tesseract tesseract = new Tesseract();
        tesseract.setDatapath(Config.DATA_FOLDER);
        return tesseract.doOCR(subimage);
    }


    private void detectText(String filePath, PrintStream out) throws Exception, IOException {
        List<AnnotateImageRequest> requests = new ArrayList<>();

        ByteString imgBytes = ByteString.readFrom(new FileInputStream(filePath));

        Image img = Image.newBuilder().setContent(imgBytes).build();
        Feature feat = Feature.newBuilder().setType(Feature.Type.TEXT_DETECTION).build();
        AnnotateImageRequest request =
                AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img).build();
        requests.add(request);

        try (ImageAnnotatorClient client = ImageAnnotatorClient.create()) {
            BatchAnnotateImagesResponse response = client.batchAnnotateImages(requests);
            List<AnnotateImageResponse> responses = response.getResponsesList();

            for (AnnotateImageResponse res : responses) {
                if (res.hasError()) {
                    out.printf("Error: %s\n", res.getError().getMessage());
                    return;
                }

                // For full list of available annotations, see http://g.co/cloud/vision/docs
                for (EntityAnnotation annotation : res.getTextAnnotationsList()) {
                    out.printf("Text: %s\n", annotation.getDescription());
                    out.printf("Position : %s\n", annotation.getBoundingPoly());
                }
            }
        }
    }


    public void readBarcodeWithGoogleVisionFromScannedImage() throws Exception {
        BufferedImage image = imageHelper.readScannedImageGetHeaderPart();

        fileHelper.writeToTargetAsJpg(image, "croppedImage");
        //searchWhiteFrameInMainImage(image);

        detectText(Config.SCANNED_FILE_PATH, new PrintStream(Config.TEMP_DIR + "googleVisionOutput.txt"));//TODO:Development[name prepare method]
    }

    public void readBarcodeWithZXingFromScannedImage() throws Exception {
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
}
