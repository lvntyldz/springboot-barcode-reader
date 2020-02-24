package com.ba.barcodereader;

import com.ba.barcodereader.helper.FileHelper;
import com.ba.barcodereader.helper.ImageHelper;
import com.ba.barcodereader.service.ImageService;
import com.ba.barcodereader.service.ScannerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
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
    public String scanFile() throws InterruptedException {
        scannerService.scanFileFromScanner();
        return "scan file completed";
    }

    @GetMapping
    @RequestMapping("/scan-file/t")
    public String scanAndReadByTesseract() throws Exception {

        scannerService.scanFileFromScanner();
        imageService.readBarcodeWithTesseractFromScannedImageVia();

        return "scan file and read barcode with tesseract completed";
    }

    @GetMapping
    @RequestMapping("/scan-file/gv")
    public String scanAndReadByGoogleVision() throws Exception {

        scannerService.scanFileFromScanner();
        imageService.readBarcodeWithGoogleVisionFromScannedImage();

        return "scan file and read barcode with Google Vision Api completed";
    }

    @GetMapping
    @RequestMapping("/scan-file/zx")
    public String scanAndReadByZebraCrossing() throws Exception {

        scannerService.scanFileFromScanner();
        List<String> datas = imageService.readBarcodeWithZXingFromScannedImage();
        log.info("result : {} ", datas);

        return "scan file and read barcode with Google Zebra Crossing API completed";
    }
}