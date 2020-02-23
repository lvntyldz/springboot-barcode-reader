package com.ba.barcodereader;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/scanner")
public class ScanController {

    @GetMapping
    public String hello() {
        return "Hello World.";
    }

    @GetMapping
    @RequestMapping("/scan-file")
    public String scanFile() {
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
    public String scanAndReadByZebraCrossing() {
        return "scan file and read barcode with Google Zebra Crossing API completed";
    }
}