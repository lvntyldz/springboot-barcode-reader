package com.ba.barcodereader;

import com.ba.barcodereader.model.ResponseModel;
import com.ba.barcodereader.props.Config;
import com.ba.barcodereader.service.ImageService;
import com.ba.barcodereader.service.ScannerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/scanner")
public class ScanController {

    @Autowired
    ImageService imageService;
    @Autowired
    private ScannerService scannerService;

    @GetMapping
    @RequestMapping("/scan-file")
    public String scanFile() {
        scannerService.scanFileFromScanner();
        return "scan file completed";
    }

    @GetMapping("/scan-file/t")
    @ResponseBody
    public ResponseEntity<ResponseModel> scanAndReadByTesseract() {
        scannerService.scanFileFromScanner();
        return prepareResponseEntity(imageService.readBarcodeWithTesseractFromScannedImageVia());
    }

    @GetMapping("/scan-file/gv")
    @ResponseBody
    public ResponseEntity<ResponseModel> scanAndReadByGoogleVision() {
        scannerService.scanFileFromScanner();
        return prepareResponseEntity(imageService.readBarcodeWithGoogleVisionFromScannedImage());
    }

    @GetMapping("/scan-file/zx")
    @ResponseBody
    public ResponseEntity<ResponseModel> scanAndReadByZebraCrossing() {
        scannerService.scanFileFromScanner();
        return prepareResponseEntity(imageService.readBarcodeWithZXingFromScannedImage());
    }

    private ResponseEntity<ResponseModel> prepareResponseEntity(List<String> dataList) {
        ResponseModel response = new ResponseModel(dataList, Arrays.asList(Config.SCANNED_FILE_PATH));
        return new ResponseEntity(response, HttpStatus.OK);
    }
}
