package com.ba.barcodereader;

import com.ba.barcodereader.enums.Dimensions;
import com.ba.barcodereader.helper.FileHelper;
import com.ba.barcodereader.helper.ImageHelper;
import com.ba.barcodereader.props.Config;
import com.ba.barcodereader.service.ImageService;
import com.ba.barcodereader.service.ScannerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;

@RestController
@RequestMapping("/scanner")
public class ScanController {

    @Autowired
    private ScannerService scannerService;

    @Autowired
    ImageService imageService;

    @Autowired
    ImageHelper imageHelper;

    @Autowired
    FileHelper fileHelper;

    @GetMapping
    public String hello() {
        return "Hello World.";
    }

    @GetMapping
    @RequestMapping("/scan-file")
    public String scanFile() {
        scannerService.scanFileFromScanner();
        return "scan file completed";
    }

    @GetMapping
    @RequestMapping("/scan-file/t")
    public String scanAndReadByTesseract() {
        return "scan file and read barcode with tesseract completed";
    }

    @GetMapping
    @RequestMapping("/scan-file/gv")
    public String scanAndReadByGoogleVision() {
        return "scan file and read barcode with Google Vision Api completed";
    }

    @GetMapping
    @RequestMapping("/scan-file/zx")
    public String scanAndReadByZebraCrossing() throws Exception {

        BufferedImage image = ImageIO.read(new FileInputStream(Config.SCANNED_FILE_PATH));

        //image = imageService.rotateImage(image, 90);
        //fileHelper.writeToTargetAsJpg(image, "rotatedImage");

        if (Dimensions.BARCODE_FRAME_X.getVal() + Dimensions.BARCODE_FRAME_W.getVal() > image.getWidth() || Dimensions.BARCODE_FRAME_Y.getVal() + Dimensions.BARCODE_FRAME_H.getVal() > image.getHeight()) {
            throw new Exception("Aranacak barcode ölçüleri resimden daha büyük!");//TODO
        }

        image = image.getSubimage(Dimensions.BARCODE_FRAME_X.getVal(), Dimensions.BARCODE_FRAME_Y.getVal(), Dimensions.BARCODE_FRAME_W.getVal(), Dimensions.BARCODE_FRAME_H.getVal());
        //imageHelper.displayScrollableImage(image);

        fileHelper.writeToTargetAsJpg(image, "croppedImage");
        imageService.searchWhiteFrameInMainImage(image);

        System.out.println("bitti!");

        return "scan file and read barcode with Google Zebra Crossing API completed";
    }
}