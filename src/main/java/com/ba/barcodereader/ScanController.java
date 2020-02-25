package com.ba.barcodereader;

import com.ba.barcodereader.helper.FileHelper;
import com.ba.barcodereader.helper.ImageHelper;
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
    private ScannerService scannerService;

    @Autowired
    ImageService imageService;

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
        List<String> datas = imageService.readBarcodeWithTesseractFromScannedImageVia();

        return prepareResponseEntity(datas);
    }

    @GetMapping("/scan-file/gv")
    @ResponseBody
    public ResponseEntity<ResponseModel> scanAndReadByGoogleVision() {

        scannerService.scanFileFromScanner();

        List<String> datas = imageService.readBarcodeWithGoogleVisionFromScannedImage();
        log.info("scan-file/gv result : {} ", datas);

        return prepareResponseEntity(datas);
    }

    @GetMapping("/scan-file/zx")
    @ResponseBody
    public ResponseEntity<ResponseModel> scanAndReadByZebraCrossing() {

        scannerService.scanFileFromScanner();

        List<String> datas = imageService.readBarcodeWithZXingFromScannedImage();
        log.info("scan-file/zx result : {} ", datas);

        return prepareResponseEntity(datas);
    }

    private ResponseEntity<ResponseModel> prepareResponseEntity(List<String> datas) {
        ResponseModel response = new ResponseModel(datas, Arrays.asList(Config.SCANNED_FILE_PATH));
        return new ResponseEntity(response, HttpStatus.OK);
    }
}
