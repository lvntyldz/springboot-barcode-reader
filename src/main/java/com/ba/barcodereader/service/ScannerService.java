package com.ba.barcodereader.service;

import com.ba.barcodereader.helper.ScannerHelper;
import org.springframework.stereotype.Service;

@Service
public class ScannerService {

    public void scanFileFromScanner() throws InterruptedException {
        ScannerHelper.scanFile();
    }
}
