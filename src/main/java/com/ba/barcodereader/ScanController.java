package com.ba.barcodereader;

import com.ba.barcodereader.helper.FileHelper;
import com.ba.barcodereader.helper.ImageHelper;
import com.ba.barcodereader.service.ImageService;
import com.ba.barcodereader.service.ScannerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
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
    @RequestMapping("/scan-file")
    public String scanFile() {
        scannerService.scanFileFromScanner();
        return "scan file completed";
    }

    @GetMapping("/scan-file/t")
    @ResponseBody
    public List<String> scanAndReadByTesseract() {

        scannerService.scanFileFromScanner();
        List<String> datas = imageService.readBarcodeWithTesseractFromScannedImageVia();

        log.info("scan-file/t result : {} ", datas);

        return datas;
    }

    @GetMapping("/scan-file/gv")
    @ResponseBody
    public List<String> scanAndReadByGoogleVision() {

        scannerService.scanFileFromScanner();

        List<String> datas = imageService.readBarcodeWithGoogleVisionFromScannedImage();
        log.info("scan-file/gv result : {} ", datas);

        return datas;
    }

    @GetMapping("/scan-file/zx")
    @ResponseBody
    public List<String> scanAndReadByZebraCrossing() {

        scannerService.scanFileFromScanner();

        List<String> datas = imageService.readBarcodeWithZXingFromScannedImage();
        log.info("scan-file/zx result : {} ", datas);

        return datas;
    }
}
